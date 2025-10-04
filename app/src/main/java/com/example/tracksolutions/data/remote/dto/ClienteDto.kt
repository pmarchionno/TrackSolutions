package com.example.tracksolutions.data.remote.dto

import com.example.tracksolutions.data.entity.ClienteEntity

// data/remote/dto/ClienteDto.kt
data class ClienteDto(
    val nombre: String = "",
    val email: String = "",
    val idPais: Int = 0,
    val updatedAt: Long = 0,
    val deleted: Boolean = false
)

fun ClienteEntity.toDto() = ClienteDto(
    nombre = cliente,
    email = email,
    idPais = idPais,
    updatedAt = updatedAt,
    deleted = deleted
)

fun ClienteDto.toEntity(
    localId: Int = 0,
    remoteId: String,
    // permitir conservar otros flags locales si querés
): ClienteEntity = ClienteEntity(
    idCliente = localId,
    cliente = nombre,
    email = email,
    idPais = idPais,
    remoteId = remoteId,
    updatedAt = updatedAt,
    pendingSync = false,
    deleted = deleted
)
