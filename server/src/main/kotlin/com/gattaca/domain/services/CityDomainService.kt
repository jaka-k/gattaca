package com.gattaca.domain.services

import com.gattaca.City
import com.gattaca.domain.ports.inbound.ICityService
import com.gattaca.domain.ports.outbound.CityRepository

class CityDomainService(private val cityRepository: CityRepository) : ICityService {
    override suspend fun createCity(city: City): Int = cityRepository.save(city)
    override suspend fun getCity(id: Int): City = cityRepository.findById(id)
    override suspend fun updateCity(id: Int, city: City) = cityRepository.update(id, city)
    override suspend fun removeCity(id: Int) = cityRepository.delete(id)
}
