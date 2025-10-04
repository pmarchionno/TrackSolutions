package com.example.tracksolutions.data.sqlite

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.example.tracksolutions.security.CryptoHelper
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteDatabaseHook
import net.sqlcipher.database.SupportFactory

class DbProvider(context: Context) {

    private val helper: SupportSQLiteOpenHelper

    init {
        val appCtx = context.applicationContext

        // 1) Cargar librerías nativas de SQLCipher
        SQLiteDatabase.loadLibs(appCtx)

        // 2) Tomar la passphrase segura (misma que usa Room)
        val passphrase: ByteArray = CryptoHelper.getOrCreateDbPassphrase(appCtx)

        // 3) Factory de SQLCipher con migración de formato si aplica
        val hook = object : SQLiteDatabaseHook {
            override fun preKey(db: SQLiteDatabase?) { /* no-op */ }
            override fun postKey(db: SQLiteDatabase?) {
                db?.rawExecSQL("PRAGMA cipher_migrate;")
            }
        }
        val sqlcipherFactory = SupportFactory(passphrase, hook)

        // 4) Callback para crear/sembrar solo si esta BD es independiente
        val callback = object : SupportSQLiteOpenHelper.Callback(DB_VERSION) {
            override fun onCreate(db: SupportSQLiteDatabase) {
                // Si compartís archivo con Room, probablemente NO quieras crear nada aquí.
                // Si esta BD es independiente para reportes, podés mantener el esquema:
                db.execSQL(SqlSchema.CREATE_PRODUCTO)
                db.execSQL(SqlSchema.CREATE_DETALLE_PEDIDO)
                SqlSchema.INSERT_PRODUCTO.forEach(db::execSQL)
                SqlSchema.INSERT_DETALLE.forEach(db::execSQL)
            }

            override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {
                db.execSQL("DROP TABLE IF EXISTS DetallePedido;")
                db.execSQL("DROP TABLE IF EXISTS Producto;")
                onCreate(db)
            }
        }

        // 5) Config + creación del helper **cifrado**
        val config = SupportSQLiteOpenHelper.Configuration.builder(appCtx)
            .name(DB_NAME)        // Usa "apptrack.db" para compartir con Room
            .callback(callback)
            .build()

        helper = sqlcipherFactory.create(config)
    }

    /** Devuelve la conexión cifrada (SQLCipher) lista para usar. */
    fun open(): SupportSQLiteDatabase = helper.writableDatabase

    companion object {
        // 👉 Usa el mismo nombre que Room para una única BD cifrada:
        private const val DB_NAME = "apptrack.db"
        // Si querés mantenerla aparte para reportes, podés volver a "apptrack_local.db"

        private const val DB_VERSION = 1
    }
}
