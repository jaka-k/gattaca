package com.gattaca.adapters.outbound.persistence

import com.gattaca.City
import com.gattaca.domain.ports.outbound.CityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.Statement

class JdbcCityRepository(private val connection: Connection) : CityRepository {
    companion object {
        private const val CREATE_TABLE_CITIES =
            "CREATE TABLE IF NOT EXISTS CITIES (ID SERIAL PRIMARY KEY, NAME VARCHAR(255), POPULATION INT);"
        private const val SELECT_CITY_BY_ID = "SELECT name, population FROM cities WHERE id = ?"
        private const val INSERT_CITY = "INSERT INTO cities (name, population) VALUES (?, ?)"
        private const val UPDATE_CITY = "UPDATE cities SET name = ?, population = ? WHERE id = ?"
        private const val DELETE_CITY = "DELETE FROM cities WHERE id = ?"
    }

    init {
        connection.createStatement().use { it.executeUpdate(CREATE_TABLE_CITIES) }
    }

    override suspend fun save(city: City): Int = withContext(Dispatchers.IO) {
        connection.prepareStatement(INSERT_CITY, Statement.RETURN_GENERATED_KEYS).use { statement ->
            statement.setString(1, city.name)
            statement.setInt(2, city.population)
            statement.executeUpdate()
            val generatedKeys = statement.generatedKeys
            if (generatedKeys.next()) generatedKeys.getInt(1) else throw Exception("Insert failed")
        }
    }

    override suspend fun findById(id: Int): City = withContext(Dispatchers.IO) {
        connection.prepareStatement(SELECT_CITY_BY_ID).use { statement ->
            statement.setInt(1, id)
            val resultSet = statement.executeQuery()
            if (resultSet.next()) {
                City(resultSet.getString("name"), resultSet.getInt("population"))
            } else throw Exception("Not found")
        }
    }

    override suspend fun update(id: Int, city: City) = withContext(Dispatchers.IO) {
        connection.prepareStatement(UPDATE_CITY).use { statement ->
            statement.setString(1, city.name)
            statement.setInt(2, city.population)
            statement.setInt(3, id)
            statement.executeUpdate()
        }
    }

    override suspend fun delete(id: Int) = withContext(Dispatchers.IO) {
        connection.prepareStatement(DELETE_CITY).use { statement ->
            statement.setInt(1, id)
            statement.executeUpdate()
        }
    }
}
