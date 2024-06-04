package com.gotravel.clienteMovil.data

import com.gotravel.clienteMovil.network.Service

interface Repository {

    suspend fun findUsuarioById(id: Int): Usuario
    suspend fun findViajesFromUserId(usuarioId: Int): List<Viaje>
    suspend fun saveViaje(viaje: Viaje, usuarioId: Int): Boolean

}

class NetworkRepository(private val service: Service) : Repository {

    override suspend fun findUsuarioById(id: Int): Usuario = service.findUsuarioById(id)
    override suspend fun findViajesFromUserId(usuarioId: Int): List<Viaje> = service.findViajesFromUserId(usuarioId)
    override suspend fun saveViaje(viaje: Viaje, usuarioId: Int): Boolean = service.saveViaje(viaje, usuarioId)

}