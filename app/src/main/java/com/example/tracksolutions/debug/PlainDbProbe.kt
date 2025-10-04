package com.example.tracksolutions.debug

import android.content.Context
import android.util.Log
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.tracksolutions.security.CryptoHelper
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteDatabaseHook
import net.sqlcipher.database.SupportFactory

object PlainDbProbe {

    private const val TAG = "SqlcipherProbe"
    private const val APP_DB_NAME = "apptrack.db"
    private const val PROBE_DB_NAME = "cipher_probe.db"

    /**
     * Verifica que la BD principal (apptrack.db) esté cifrada:
     * 1) Abre con la passphrase real (CryptoHelper)
     * 2) Lee PRAGMA cipher_version e integrity_check
     * 3) Intenta abrir con clave incorrecta (debe FALLAR)
     */
    fun verifyAppDbIsEncrypted(context: Context, dbName: String = APP_DB_NAME) {
        SQLiteDatabase.loadLibs(context.applicationContext)

        // 1) Abrir con la clave correcta y leer PRAGMAs
        val correctPass = CryptoHelper.getOrCreateDbPassphrase(context)
        val goodDb = openCiphered(context, dbName, correctPass) ?: run {
            Log.e(TAG, "❌ No se pudo abrir $dbName con la clave correcta. ¿Existe el archivo?")
            return
        }
        try {
            pragmaCipherVersion(goodDb)
            pragmaIntegrity(goodDb)
        } finally {
            goodDb.close()
        }

        // 2) Intentar con clave incorrecta → debe fallar
        expectFailureWithWrongKey(context, dbName)
    }

    /**
     * Crea una BD CIFRADA de laboratorio (cipher_probe.db) y demuestra:
     * - PRAGMA cipher_version e integrity_check con clave correcta.
     * - Falla al abrir con clave incorrecta.
     * No toca tu apptrack.db real.
     */
    fun createEncryptedProbeAndVerify(context: Context) {
        SQLiteDatabase.loadLibs(context.applicationContext)

        // Elimina restos anteriores
        deleteIfExists(context, PROBE_DB_NAME)

        // Passphrase temporal (solo para este laboratorio)
        val probePass = SQLiteDatabase.getBytes("probe-${java.util.UUID.randomUUID()}".toCharArray())

        // Crea el archivo cifrado y una tabla mínima
        openCiphered(context, PROBE_DB_NAME, probePass, onCreate = { db ->
            db.execSQL("CREATE TABLE t(id INTEGER PRIMARY KEY, v TEXT NOT NULL);")
            db.execSQL("INSERT INTO t(id, v) VALUES (1,'ok');")
        })?.close()

        // Abre con clave correcta → PRAGMAs
        openCiphered(context, PROBE_DB_NAME, probePass)?.use { db ->
            pragmaCipherVersion(db)
            pragmaIntegrity(db)
        }

        // Con clave incorrecta → debe fallar
        expectFailureWithWrongKey(context, PROBE_DB_NAME)
    }

    // ---------- helpers cifrados (no “plain”) ----------

    private fun openCiphered(
        context: Context,
        dbName: String,
        passphrase: ByteArray,
        onCreate: ((SupportSQLiteDatabase) -> Unit)? = null
    ): SupportSQLiteDatabase? {
        return try {
            val hook = object : SQLiteDatabaseHook {
                override fun preKey(db: SQLiteDatabase?) {}
                override fun postKey(db: SQLiteDatabase?) {
                    db?.rawExecSQL("PRAGMA cipher_migrate;")
                }
            }
            val factory = SupportFactory(passphrase, hook)
            val config = SupportSQLiteOpenHelper.Configuration.builder(context)
                .name(dbName)
                .callback(object : SupportSQLiteOpenHelper.Callback(1) {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        onCreate?.invoke(db)
                    }
                    override fun onUpgrade(db: SupportSQLiteDatabase, oldV: Int, newV: Int) {}
                })
                .build()
            val helper = factory.create(config)
            helper.writableDatabase
        } catch (e: Exception) {
            Log.w(TAG, "No se pudo abrir $dbName con SQLCipher: ${e.message}")
            null
        }
    }

    private fun pragmaCipherVersion(db: SupportSQLiteDatabase) {
        try {
            db.query(androidx.sqlite.db.SimpleSQLiteQuery("PRAGMA cipher_version;")).use { c ->
                if (c.moveToFirst()) {
                    Log.i(TAG, "cipher_version = ${c.getString(0)}")
                } else {
                    Log.w(TAG, "cipher_version vacío")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error leyendo cipher_version: ${e.message}")
        }
    }

    private fun pragmaIntegrity(db: SupportSQLiteDatabase) {
        try {
            db.query(androidx.sqlite.db.SimpleSQLiteQuery("PRAGMA cipher_integrity_check;")).use { c ->
                if (c.moveToFirst()) {
                    Log.i(TAG, "cipher_integrity_check = ${c.getString(0)}") // "ok" si todo bien
                } else {
                    Log.w(TAG, "cipher_integrity_check vacío")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error en integrity_check: ${e.message}")
        }
    }

    private fun expectFailureWithWrongKey(context: Context, dbName: String) {
        try {
            val wrongPass = SQLiteDatabase.getBytes("clave-incorrecta".toCharArray())
            val wrong = openCiphered(context, dbName, wrongPass)
            // Si llegó a abrir, algo anda mal (no debería abrir con clave errónea)
            wrong?.close()
            Log.e(TAG, "❌ Se abrió $dbName con CLAVE INCORRECTA — revisar configuración")
        } catch (e: Exception) {
            Log.i(TAG, "✅ Falló con clave incorrecta (esperado): ${e.message}")
        }
    }

    private fun deleteIfExists(context: Context, name: String) {
        val f = context.getDatabasePath(name)
        listOf(f, java.io.File(f.path + "-wal"), java.io.File(f.path + "-shm")).forEach {
            if (it.exists()) it.delete()
        }
    }
}
