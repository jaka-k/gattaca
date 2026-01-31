package com.gattaca.domain.ports.outbound

import com.gattaca.City

interface CityRepository {
    suspend fun save(city: City): Int
    suspend fun findById(id: Int): City
    suspend fun update(id: Int, city: City)
    suspend fun delete(id: Int)
}
