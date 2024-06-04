package com.gotravel.clienteMovil.network

import com.gotravel.clienteMovil.data.Usuario
import com.gotravel.clienteMovil.data.Viaje
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface Service {

    @GET("usuario")
    suspend fun findUsuarioById(@Query("id") id: Int): Usuario

    @GET("viajes")
    suspend fun findViajesFromUserId(@Query("idUsuario") usuarioId: Int): List<Viaje>

    @POST("/saveViaje")
    suspend fun saveViaje(@Body viaje: Viaje, @Query("usuarioId") usuarioId: Int) : Boolean

}