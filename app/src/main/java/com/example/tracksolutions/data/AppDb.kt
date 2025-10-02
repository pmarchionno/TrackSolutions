package com.example.tracksolutions.data

import android.content.Context
import android.util.Log
import androidx.room.*
import androidx.room.migration.Migration
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//private object PrepopulateCallback : RoomDatabase.Callback() {
//    override fun onCreate(db: SupportSQLiteDatabase) {
//        super.onCreate(db)
//        // Cargar el archivo .sql (léelo a String) y ejecutar línea por línea:
//        val ctx = /* contexto */
//        val sql = ctx.assets.open("inserts_from_vPedidos.sql")
//            .bufferedReader(Charsets.UTF_8).use { it.readText() }
//
//        db.beginTransaction()
//        try {
//            sql.split(";")
//                .map { it.trim() }
//                .filter { it.isNotEmpty() }
//                .forEach { stmt -> db.execSQL("$stmt;") }
//            db.setTransactionSuccessful()
//        } finally {
//            db.endTransaction()
//        }
//    }
////    override fun onCreate(db: SupportSQLiteDatabase) {
////        super.onCreate(db)
////        Log.d("DB", "onCreate() de Room: pre-poblando…")
////
////        // 1) Insert ZONAS
////        db.execSQL("INSERT INTO zona(idZona, zona) VALUES (1,'LatAm');")
////        db.execSQL("INSERT INTO zona(idZona, zona) VALUES (2,'Norteamérica');")
////        db.execSQL("INSERT INTO zona(idZona, zona) VALUES (3,'Europa');")
////
////        // 2) Insert PAISES (ajusta nombres/columnas si tu Entity difiere)
////        db.execSQL("INSERT INTO pais(idPais, pais, idZona) VALUES (1,'Argentina',1);")
////        db.execSQL("INSERT INTO pais(idPais, pais, idZona) VALUES (2,'Brasil',1);")
////        db.execSQL("INSERT INTO pais(idPais, pais, idZona) VALUES (3,'Chile',1);")
////        db.execSQL("INSERT INTO pais(idPais, pais, idZona) VALUES (4,'Uruguay',1);")
////        db.execSQL("INSERT INTO pais(idPais, pais, idZona) VALUES (5,'Paraguay',1);")
////        db.execSQL("INSERT INTO pais(idPais, pais, idZona) VALUES (6,'Bolivia',1);")
////        db.execSQL("INSERT INTO pais(idPais, pais, idZona) VALUES (7,'Perú',1);")
////        db.execSQL("INSERT INTO pais(idPais, pais, idZona) VALUES (8,'México',1);")
////        db.execSQL("INSERT INTO pais(idPais, pais, idZona) VALUES (9,'Colombia',1);")
////        db.execSQL("INSERT INTO pais(idPais, pais, idZona) VALUES (10,'Estados Unidos',2);")
////        db.execSQL("INSERT INTO pais(idPais, pais, idZona) VALUES (11,'España',3);")
////
////        // 3) Verificar con COUNT
////        db.query("SELECT COUNT(*) FROM zona").use { c ->
////            c.moveToFirst()
////            Log.d("DB", "Zonas insertadas: ${c.getInt(0)}")
////        }
////        db.query("SELECT COUNT(*) FROM pais").use { c ->
////            c.moveToFirst()
////            Log.d("DB", "Países insertados: ${c.getInt(0)}")
////        }
////
////        Log.d("DB", "Pre-poblado finalizado.")
////    }
//}

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

    companion object {
        @Volatile private var INSTANCE: AppDb? = null
        fun get(ctx: Context): AppDb =
            INSTANCE ?: synchronized(this) {
                val appCtx = ctx.applicationContext
                val callback = object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Leer el SQL de assets
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
                }

                val instance = Room.databaseBuilder(appCtx, AppDb::class.java, "apptrack.db")
                    .fallbackToDestructiveMigration()
                    .addCallback(callback)                // 👈 ahora sí tiene ctx
                    .build()

                INSTANCE = instance
                instance
            }

//        fun get(ctx: Context): AppDb =
//            INSTANCE ?: synchronized(this) {
//                INSTANCE ?: Room.databaseBuilder(ctx, AppDb::class.java, "apptrack.db")
//                    .fallbackToDestructiveMigration()
//                    .addCallback(PrepopulateCallback)
////                    .addCallback(object : RoomDatabase.Callback() {
////                        override fun onCreate(db: SupportSQLiteDatabase) {
////                            super.onCreate(db)
////                            // ⚠️ Usar corrutina IO para llamar a métodos suspend
////                            CoroutineScope(Dispatchers.IO).launch {
////                                val database = get(ctx)
////
////                                // 1) Sembrar Zonas (si corresponde)
////                                val zonaDao = database.zonaDao()
////                                zonaDao.insertAll(
////                                    listOf(
////                                        ZonaEntity(idZona = 0, zona = "LatAm"),
////                                        ZonaEntity(idZona = 1, zona = "Norteamérica"),
////                                        ZonaEntity(idZona = 2, zona = "Europa")
////                                    )
////                                )
//////                                val zonas = listOf(
//////                                    ZonaEntity(idZona = 0, zona = "LatAm"),
//////                                    ZonaEntity(idZona = 1, zona = "Norteamérica"),
//////                                    ZonaEntity(idZona = 2, zona = "Europa")
//////                                )
////                                val zonas = zonaDao.listar()
////                                // upsert/insert según tu DAO:
////                                zonas.forEach { z -> zonaDao.insert(z) } // o zonaDao.insertAll(zonas)
////
////                                // Busca idZona para asignar a países (ejemplo: LatAm = 1)
////                                val zonasActuales = zonaDao.listar()
////                                val idLatAm = zonasActuales.firstOrNull { it.zona == "LatAm" }?.idZona ?: 1
////
////                                // 2) Sembrar Países
////                                val paisDao = database.paisDao()
////                                paisDao.insertAll(
////                                    listOf(
////                                        PaisEntity(0, "Argentina", idLatAm),
////                                        PaisEntity(0, "Brasil", idLatAm),
////                                        PaisEntity(0, "Chile", idLatAm),
////                                        PaisEntity(0, "Uruguay", idLatAm),
////                                        PaisEntity(0, "Paraguay", idLatAm),
////                                        PaisEntity(0, "Bolivia", idLatAm),
////                                        PaisEntity(0, "Perú", idLatAm),
////                                        PaisEntity(0, "México", idLatAm),
////                                        PaisEntity(0, "Colombia", idLatAm),
////                                        // si tenés otras zonas, asigná su idZona correspondiente:
////                                        PaisEntity(0, "Estados Unidos", zonasActuales.firstOrNull { it.zona == "Norteamérica" }?.idZona ?: idLatAm),
////                                        PaisEntity(0, "España", zonasActuales.firstOrNull { it.zona == "Europa" }?.idZona ?: idLatAm)
////                                    )
////                                )
////                            }
////                        }
////                    })
//                    .addMigrations(MIG_1_2)
//                    .build().also { INSTANCE = it }
//            }

        // Ejemplo de esqueleto de migración (si querés conservar datos)
        val MIG_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Drop/crear tablas nuevas. Si querés migrar datos reales, agregá INSERT SELECT.
                db.execSQL("DROP TABLE IF EXISTS notes")
                // ... execSQL con todos los CREATE TABLE de arriba (idénticos a los de Room) ...
            }
        }
    }
}

