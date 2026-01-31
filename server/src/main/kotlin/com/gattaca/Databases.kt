package com.gattaca

import com.gattaca.adapters.outbound.persistence.*
import io.ktor.server.application.*
import java.sql.Connection
import java.sql.DriverManager

class AppRepositories(
    val orgRepo: JdbcOrganizationRepository,
    val userRepo: JdbcUserRepository,
    val exerciseRepo: JdbcExerciseRepository,
    val candidateRepo: JdbcCandidateRepository,
    val evaluationRepo: JdbcEvaluationRepository
)

fun Application.configureDatabases(): AppRepositories {
    val dbConnection: Connection = connectToPostgres(embedded = true)
    return AppRepositories(
        JdbcOrganizationRepository(dbConnection),
        JdbcUserRepository(dbConnection),
        JdbcExerciseRepository(dbConnection),
        JdbcCandidateRepository(dbConnection),
        JdbcEvaluationRepository(dbConnection)
    )
}

fun Application.connectToPostgres(embedded: Boolean): Connection {
    Class.forName("org.postgresql.Driver")
    return if (embedded) {
        log.info("Using embedded H2 database for testing")
        DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "root", "")
    } else {
        val url = environment.config.property("postgres.url").getString()
        val user = environment.config.property("postgres.user").getString()
        val password = environment.config.property("postgres.password").getString()
        DriverManager.getConnection(url, user, password)
    }
}
