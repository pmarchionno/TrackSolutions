package com.example.tracksolutions.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import net.sqlcipher.database.SQLiteDatabase

object CryptoHelper {

    private const val MASTER_KEY_ALIAS = "room_key_alias"
    private const val PREFS_NAME = "secure_prefs"
    private const val PREF_KEY_SECRET = "room_db_secret"

    /**
     * Obtiene (o crea si no existe) una passphrase persistente para cifrar SQLCipher.
     * Retorna el arreglo de bytes listo para usar en SupportFactory(passphrase).
     */
    fun getOrCreateDbPassphrase(context: Context): ByteArray {
        val appCtx = context.applicationContext

        // 1) Clave maestra en Android Keystore
        val masterKey = MasterKey.Builder(appCtx, MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        // 2) SharedPreferences cifradas con esa master key
        val prefs = EncryptedSharedPreferences.create(
            appCtx,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        // 3) Generar una secret si no existe y guardarla
        var secret = prefs.getString(PREF_KEY_SECRET, null)
        if (secret == null) {
            // Dos UUIDs concatenados = ~72 chars; suficiente entropía para derivar passphrase
            secret = "${java.util.UUID.randomUUID()}_${java.util.UUID.randomUUID()}"
            prefs.edit().putString(PREF_KEY_SECRET, secret).apply()
        }

        // 4) Convertir a passphrase para SQLCipher
        return SQLiteDatabase.getBytes(secret.toCharArray())
    }

    /** (Opcional) Permite rotar la clave local borrándola. Requiere migrar/rehacer la DB. */
    fun clearDbPassphrase(context: Context) {
        val appCtx = context.applicationContext
        val masterKey = MasterKey.Builder(appCtx, MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val prefs = EncryptedSharedPreferences.create(
            appCtx,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        prefs.edit().remove(PREF_KEY_SECRET).apply()
    }
}
