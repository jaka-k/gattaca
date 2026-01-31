package com.gattaca.domain.ports.inbound

import com.gattaca.City

interface ICityService {
    suspend fun createCity(city: City): Int
    suspend fun getCity(id: Int): City
    suspend fun updateCity(id: Int, city: City)
    suspend fun removeCity(id: Int)
}
