package com.example.tracksolutions.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.tracksolutions.data.entity.ClienteEntity
import com.example.tracksolutions.data.entity.PaisEntity

data class ClienteConPaisZona(
    @Embedded val cliente: ClienteEntity,

    // Trae el País del cliente y, dentro, su Zona (anidado)
    @Relation(
        parentColumn = "idPais",
        entityColumn = "idPais",
        entity = PaisEntity::class
    )
    val paisConZona: PaisConZona
) {
    // Accesos de conveniencia (opcional)
    val pais get() = paisConZona.pais
    val zona get() = paisConZona.zona
}
