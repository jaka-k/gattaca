package com.gattaca.config

import com.gattaca.infrastructure.*
import com.gattaca.service.*
import io.ktor.server.application.*
import java.sql.Connection
import java.sql.DriverManager

class AppDependencies(
    val authService: AuthService,
    val userService: UserService,
    val candidateService: CandidateService,
    val orgRepo: JdbcOrganizationRepository,
    val userRepo: JdbcUserRepository,
    val exerciseRepo: JdbcExerciseRepository,
    val candidateRepo: JdbcCandidateRepository,
    val evaluationRepo: JdbcEvaluationRepository,
    val sessionRepo: JdbcSessionRepository
)

fun Application.configureDatabases(): AppDependencies {
    val dbConnection: Connection = connectToPostgres(embedded = true)
    DatabaseInitializer.initialize(dbConnection)
    
    val orgRepo = JdbcOrganizationRepository(dbConnection)
    val userRepo = JdbcUserRepository(dbConnection)
    val exerciseRepo = JdbcExerciseRepository(dbConnection)
    val candidateRepo = JdbcCandidateRepository(dbConnection)
    val evaluationRepo = JdbcEvaluationRepository(dbConnection)
    val sessionRepo = JdbcSessionRepository(dbConnection)
    
    val passwordHasher = Sha256PasswordHasher()
    
    val authService = AuthService(userRepo, passwordHasher)
    val userService = UserService(userRepo, passwordHasher)
    val candidateService = CandidateService(candidateRepo)

    return AppDependencies(
        authService,
        userService,
        candidateService,
        orgRepo,
        userRepo,
        exerciseRepo,
        candidateRepo,
        evaluationRepo,
        sessionRepo
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