package com.example.tracksolutions.data.sync

import com.example.tracksolutions.data.dao.ClienteDao
import com.example.tracksolutions.data.remote.dto.ClienteDto
import com.example.tracksolutions.data.remote.dto.FirebaseModule
import com.example.tracksolutions.data.remote.dto.toDto
import com.example.tracksolutions.data.remote.dto.toEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ClienteSyncRepository(
    private val dao: ClienteDao
) {
    private val col = FirebaseModule.db.collection("clientes")

    /** Subir cambios locales marcados como pendingSync. */
    suspend fun push() {
        val pendientes = dao.pendientes()
        for (c in pendientes) {
            val dto = c.toDto()
            if (c.remoteId == null) {
                // crear en la nube
                val ref = col.add(dto.toMap()).await()
                // guardar remoteId y limpiar pending
                dao.upsert(c.copy(remoteId = ref.id, pendingSync = false))
            } else {
                // actualizar doc remoto si nuestra versión es más nueva
                val snap = col.document(c.remoteId).get().await()
                val remoto = snap.toObject(ClienteDto::class.java)
                if (remoto == null || c.updatedAt >= (remoto.updatedAt)) {
                    col.document(c.remoteId).set(dto.toMap()).await()
                    dao.upsert(c.copy(pendingSync = false))
                } else {
                    // remoto más nuevo → pull lo resolverá
                }
            }
        }
    }

    /** Bajar cambios de la nube (desde lastSyncAt si lo manejás). */
    suspend fun pull(lastSyncAt: Long? = null) {
        val q = if (lastSyncAt != null)
            col.whereGreaterThan("updatedAt", lastSyncAt)
        else col

        val snaps = q.get().await()
        for (doc in snaps.documents) {
            val dto = doc.toObject(ClienteDto::class.java) ?: continue
            val local = dao.porRemoteId(doc.id)
            when {
                local == null -> {
                    // no existe local → insertar
                    dao.upsert(
                        dto.toEntity(localId = 0, remoteId = doc.id)
                    )
                }
                dto.updatedAt > local.updatedAt -> {
                    // remoto gana
                    dao.upsert(
                        local.copy(
                            cliente = dto.nombre,
                            email = dto.email,
                            idPais = dto.idPais,
                            updatedAt = dto.updatedAt,
                            deleted = dto.deleted,
                            pendingSync = false
                        )
                    )
                }
                else -> {
                    // local gana o empate → no tocar (push ya lo subirá si pending)
                }
            }
        }
    }

    // Helpers
    private fun ClienteDto.toMap() = hashMapOf(
        "nombre" to nombre,
        "email" to email,
        "idPais" to idPais,
        "updatedAt" to updatedAt,
        "deleted" to deleted
    )
}

private fun FirebaseFirestore.collection(string: String) {
    TODO("Not yet implemented")
}
