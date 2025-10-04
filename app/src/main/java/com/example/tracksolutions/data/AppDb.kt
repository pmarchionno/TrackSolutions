package com.example.tracksolutions.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.tracksolutions.data.dao.CatalogoDao
import com.example.tracksolutions.data.dao.ClienteDao
import com.example.tracksolutions.data.dao.DetallePedidoDao
import com.example.tracksolutions.data.dao.PedidoDao
import com.example.tracksolutions.data.dao.ProductoDao
import com.example.tracksolutions.data.dao.PaisDao
import com.example.tracksolutions.data.dao.TipoProductoDao
import com.example.tracksolutions.data.dao.ZonaDao
import com.example.tracksolutions.data.entity.CanalVentaEntity
import com.example.tracksolutions.data.entity.ClienteEntity
import com.example.tracksolutions.data.entity.DetallePedidoEntity
import com.example.tracksolutions.data.entity.PaisEntity
import com.example.tracksolutions.data.entity.PedidoEntity
import com.example.tracksolutions.data.entity.ProductoEntity
import com.example.tracksolutions.data.entity.TipoProductoEntity
import com.example.tracksolutions.data.entity.ZonaEntity
import com.example.tracksolutions.security.CryptoHelper
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteDatabaseHook
import net.sqlcipher.database.SupportFactory

@Database(
    version = 1,
    entities = [
        ZonaEntity::class, PaisEntity::class, ClienteEntity::class,
        TipoProductoEntity::class, ProductoEntity::class,
        CanalVentaEntity::class, PedidoEntity::class, DetallePedidoEntity::class
    ]
)
abstract class AppDb : RoomDatabase() {

    abstract fun productoDao(): ProductoDao
    abstract fun clienteDao(): ClienteDao
    abstract fun pedidoDao(): PedidoDao
    abstract fun detallePedidoDao(): DetallePedidoDao
    abstract fun catalogoDao(): CatalogoDao
    abstract fun paisDao(): PaisDao
    abstract fun zonaDao(): ZonaDao
    abstract fun tipoProductoDao(): TipoProductoDao

    /**
     * Exponer el SupportSQLiteDatabase cifrado por si necesitás pasarlo a repos “manuales”.
     */
    fun openSupportDb(): SupportSQLiteDatabase = openHelper.writableDatabase

    companion object {
        @Volatile private var INSTANCE: AppDb? = null
        private const val DB_NAME = "apptrack.db"
        private const val TAG = "AppDb"

        fun get(ctx: Context): AppDb =
            INSTANCE ?: synchronized(this) {
                val appCtx = ctx.applicationContext

                // 1) Cargar librerías nativas de SQLCipher ANTES de construir Room
                SQLiteDatabase.loadLibs(appCtx)

                // 2) Obtener passphrase segura desde CryptoHelper (ByteArray listo para SupportFactory)
                val passphrase: ByteArray = CryptoHelper.getOrCreateDbPassphrase(appCtx)

                // 3) Factory de SQLCipher con cipher_migrate para compatibilidad
                val hook = object : SQLiteDatabaseHook {
                    override fun preKey(db: SQLiteDatabase?) { /* no-op */ }
                    override fun postKey(db: SQLiteDatabase?) {
                        db?.rawExecSQL("PRAGMA cipher_migrate;")
                    }
                }
                val factory = SupportFactory(passphrase, hook)

                // 4) Callback de pre-poblado (lee el .sql de assets y lo ejecuta)
                val seedCallback = object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        val sql = appCtx.assets.open("inserts_from_vPedidos.sql")
                            .bufferedReader(Charsets.UTF_8)
                            .use { it.readText() }

                        db.beginTransaction()
                        try {
                            sql.split(";")
                                .map { it.trim() }
                                .filter { it.isNotEmpty() }
                                .forEach { stmt -> db.execSQL("$stmt;") }
                            db.setTransactionSuccessful()
                        } finally {
                            db.endTransaction()
                        }
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        // (Opcional) Diagnóstico de cifrado en debug
                        try {
                            val c = db.query(SimpleSQLiteQuery("PRAGMA cipher_version;"))
                            c.use {
                                if (it.moveToFirst()) {
                                    Log.i(TAG, "cipher_version = ${it.getString(0)}")
                                } else {
                                    Log.w(TAG, "cipher_version vacío (¿factory no aplicada?)")
                                }
                            }
                        } catch (e: Exception) {
                            Log.w(TAG, "No se pudo consultar cipher_version: ${e.message}")
                        }
                    }
                }

                // 5) Construir Room con la factory cifrada
                val instance = Room.databaseBuilder(appCtx, AppDb::class.java, DB_NAME)
                    .openHelperFactory(factory)               // ← SQLCipher activado
                    .fallbackToDestructiveMigration(false)    // no destruir por defecto
                    .addCallback(seedCallback)
                    // .setJournalMode(JournalMode.WRITE_AHEAD_LOGGING) // (Room ya suele usar WAL)
                    .build()

                INSTANCE = instance
                instance
            }

        // (Opcional) Ejemplo de migración esqueleto
        val MIG_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS notes")
            }
        }
    }
}
