package com.gattaca.infrastructure

import com.gattaca.domain.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.Statement
import java.util.UUID as JavaUuid
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

object DatabaseInitializer {
    fun initialize(connection: Connection) {
        connection.createStatement().use { stmt ->
            // H2 and Postgres support UUID type. 
            // Using UUID type for columns.
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS organizations (id UUID PRIMARY KEY, name VARCHAR(255))")
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (id UUID PRIMARY KEY, organization_id UUID, name VARCHAR(255), email VARCHAR(255), password_hash VARCHAR(255))")
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS exercises (id UUID PRIMARY KEY, creator_id UUID, title VARCHAR(255), description TEXT)")
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS candidates (id UUID PRIMARY KEY, name VARCHAR(255), email VARCHAR(255), github_profile VARCHAR(255))")
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS evaluations (id UUID PRIMARY KEY, candidate_id UUID, exercise_id UUID, score INT, feedback TEXT)")
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS sessions (session_id VARCHAR(255) PRIMARY KEY, user_id VARCHAR(255), access_token TEXT)")
        }
    }
}

class JdbcOrganizationRepository(private val connection: Connection) : OrganizationRepository {
    override suspend fun save(org: Organization): Uuid = withContext(Dispatchers.IO) {
        val newId = org.id ?: Uuid.random()
        connection.prepareStatement("INSERT INTO organizations (id, name) VALUES (?, ?)").use { stmt ->
            stmt.setObject(1, newId.toJavaUuid())
            stmt.setString(2, org.name)
            stmt.executeUpdate()
        }
        newId
    }
    override suspend fun findById(id: Uuid): Organization? = withContext(Dispatchers.IO) {
        connection.prepareStatement("SELECT * FROM organizations WHERE id = ?").use { stmt ->
            stmt.setObject(1, id.toJavaUuid())
            val rs = stmt.executeQuery()
            if (rs.next()) Organization(
                (rs.getObject("id") as JavaUuid).toKotlinUuid(),
                rs.getString("name")
            ) else null
        }
    }
    override suspend fun findAll(): List<Organization> = withContext(Dispatchers.IO) {
        connection.createStatement().use { stmt ->
            val rs = stmt.executeQuery("SELECT * FROM organizations")
            val list = mutableListOf<Organization>()
            while (rs.next()) list.add(Organization(
                (rs.getObject("id") as JavaUuid).toKotlinUuid(),
                rs.getString("name")
            ))
            list
        }
    }
    override suspend fun update(org: Organization): Boolean = withContext(Dispatchers.IO) {
        connection.prepareStatement("UPDATE organizations SET name = ? WHERE id = ?").use { stmt ->
            stmt.setString(1, org.name)
            stmt.setObject(2, org.id?.toJavaUuid() ?: throw IllegalArgumentException("Organization ID cannot be null for update"))
            stmt.executeUpdate() > 0
        }
    }
    override suspend fun delete(id: Uuid): Boolean = withContext(Dispatchers.IO) {
        connection.prepareStatement("DELETE FROM organizations WHERE id = ?").use { stmt ->
            stmt.setObject(1, id.toJavaUuid())
            stmt.executeUpdate() > 0
        }
    }
}

class JdbcUserRepository(private val connection: Connection) : UserRepository {
    override suspend fun save(user: User): Uuid = withContext(Dispatchers.IO) {
        val newId = user.id ?: Uuid.random()
        connection.prepareStatement("INSERT INTO users (id, organization_id, name, email, password_hash) VALUES (?, ?, ?, ?, ?)").use { stmt ->
            stmt.setObject(1, newId.toJavaUuid())
            stmt.setObject(2, user.organizationId.toJavaUuid())
            stmt.setString(3, user.name)
            stmt.setString(4, user.email)
            stmt.setString(5, user.passwordHash)
            stmt.executeUpdate()
        }
        newId
    }
    override suspend fun findById(id: Uuid): User? = withContext(Dispatchers.IO) {
        connection.prepareStatement("SELECT * FROM users WHERE id = ?").use { stmt ->
            stmt.setObject(1, id.toJavaUuid())
            val rs = stmt.executeQuery()
            if (rs.next()) mapRow(rs) else null
        }
    }
    override suspend fun findAll(): List<User> = withContext(Dispatchers.IO) {
        connection.createStatement().use { stmt ->
            val rs = stmt.executeQuery("SELECT * FROM users")
            val list = mutableListOf<User>()
            while (rs.next()) list.add(mapRow(rs))
            list
        }
    }
    override suspend fun findByOrganizationId(orgId: Uuid): List<User> = withContext(Dispatchers.IO) {
        connection.prepareStatement("SELECT * FROM users WHERE organization_id = ?").use { stmt ->
            stmt.setObject(1, orgId.toJavaUuid())
            val rs = stmt.executeQuery()
            val users = mutableListOf<User>()
            while (rs.next()) {
                users.add(mapRow(rs))
            }
            users
        }
    }
    override suspend fun findByEmail(email: String): User? = withContext(Dispatchers.IO) {
        connection.prepareStatement("SELECT * FROM users WHERE email = ?").use { stmt ->
            stmt.setString(1, email)
            val rs = stmt.executeQuery()
            if (rs.next()) mapRow(rs) else null
        }
    }
    override suspend fun update(user: User): Boolean = withContext(Dispatchers.IO) {
        connection.prepareStatement("UPDATE users SET organization_id = ?, name = ?, email = ?, password_hash = ? WHERE id = ?").use { stmt ->
            stmt.setObject(1, user.organizationId.toJavaUuid())
            stmt.setString(2, user.name)
            stmt.setString(3, user.email)
            stmt.setString(4, user.passwordHash)
            stmt.setObject(5, user.id?.toJavaUuid() ?: throw IllegalArgumentException("User ID cannot be null for update"))
            stmt.executeUpdate() > 0
        }
    }
    override suspend fun delete(id: Uuid): Boolean = withContext(Dispatchers.IO) {
        connection.prepareStatement("DELETE FROM users WHERE id = ?").use { stmt ->
            stmt.setObject(1, id.toJavaUuid())
            stmt.executeUpdate() > 0
        }
    }

    private fun mapRow(rs: java.sql.ResultSet): User {
        return User(
            (rs.getObject("id") as JavaUuid).toKotlinUuid(),
            (rs.getObject("organization_id") as JavaUuid).toKotlinUuid(),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("password_hash")
        )
    }
}

class JdbcExerciseRepository(private val connection: Connection) : ExerciseRepository {
    override suspend fun save(exercise: Exercise): Uuid = withContext(Dispatchers.IO) {
        val newId = exercise.id ?: Uuid.random()
        connection.prepareStatement("INSERT INTO exercises (id, creator_id, title, description) VALUES (?, ?, ?, ?)").use { stmt ->
            stmt.setObject(1, newId.toJavaUuid())
            stmt.setObject(2, exercise.creatorId.toJavaUuid())
            stmt.setString(3, exercise.title)
            stmt.setString(4, exercise.description)
            stmt.executeUpdate()
        }
        newId
    }
    override suspend fun findById(id: Uuid): Exercise? = withContext(Dispatchers.IO) {
        connection.prepareStatement("SELECT * FROM exercises WHERE id = ?").use { stmt ->
            stmt.setObject(1, id.toJavaUuid())
            val rs = stmt.executeQuery()
            if (rs.next()) Exercise(
                (rs.getObject("id") as JavaUuid).toKotlinUuid(),
                (rs.getObject("creator_id") as JavaUuid).toKotlinUuid(),
                rs.getString("title"),
                rs.getString("description")
            ) else null
        }
    }
    override suspend fun findAll(): List<Exercise> = withContext(Dispatchers.IO) {
        connection.createStatement().use { stmt ->
            val rs = stmt.executeQuery("SELECT * FROM exercises")
            val list = mutableListOf<Exercise>()
            while (rs.next()) list.add(Exercise(
                (rs.getObject("id") as JavaUuid).toKotlinUuid(),
                (rs.getObject("creator_id") as JavaUuid).toKotlinUuid(),
                rs.getString("title"),
                rs.getString("description")
            ))
            list
        }
    }
    override suspend fun update(exercise: Exercise): Boolean = withContext(Dispatchers.IO) {
        connection.prepareStatement("UPDATE exercises SET creator_id = ?, title = ?, description = ? WHERE id = ?").use { stmt ->
            stmt.setObject(1, exercise.creatorId.toJavaUuid())
            stmt.setString(2, exercise.title)
            stmt.setString(3, exercise.description)
            stmt.setObject(4, exercise.id?.toJavaUuid() ?: throw IllegalArgumentException("Exercise ID cannot be null for update"))
            stmt.executeUpdate() > 0
        }
    }
    override suspend fun delete(id: Uuid): Boolean = withContext(Dispatchers.IO) {
        connection.prepareStatement("DELETE FROM exercises WHERE id = ?").use { stmt ->
            stmt.setObject(1, id.toJavaUuid())
            stmt.executeUpdate() > 0
        }
    }
}

class JdbcCandidateRepository(private val connection: Connection) : CandidateRepository {
    override suspend fun save(candidate: Candidate): Uuid = withContext(Dispatchers.IO) {
        val newId = candidate.id ?: Uuid.random()
        connection.prepareStatement("INSERT INTO candidates (id, name, email, github_profile) VALUES (?, ?, ?, ?)").use { stmt ->
            stmt.setObject(1, newId.toJavaUuid())
            stmt.setString(2, candidate.name)
            stmt.setString(3, candidate.email)
            stmt.setString(4, candidate.githubProfile)
            stmt.executeUpdate()
        }
        newId
    }
    override suspend fun findById(id: Uuid): Candidate? = withContext(Dispatchers.IO) {
        connection.prepareStatement("SELECT * FROM candidates WHERE id = ?").use { stmt ->
            stmt.setObject(1, id.toJavaUuid())
            val rs = stmt.executeQuery()
            if (rs.next()) mapRow(rs) else null
        }
    }
    override suspend fun findAll(): List<Candidate> = withContext(Dispatchers.IO) {
        connection.createStatement().use { stmt ->
            val rs = stmt.executeQuery("SELECT * FROM candidates")
            val list = mutableListOf<Candidate>()
            while (rs.next()) list.add(mapRow(rs))
            list
        }
    }
    override suspend fun findByEmail(email: String): Candidate? = withContext(Dispatchers.IO) {
        connection.prepareStatement("SELECT * FROM candidates WHERE email = ?").use { stmt ->
            stmt.setString(1, email)
            val rs = stmt.executeQuery()
            if (rs.next()) mapRow(rs) else null
        }
    }
    override suspend fun update(candidate: Candidate): Boolean = withContext(Dispatchers.IO) {
        connection.prepareStatement("UPDATE candidates SET name = ?, email = ?, github_profile = ? WHERE id = ?").use { stmt ->
            stmt.setString(1, candidate.name)
            stmt.setString(2, candidate.email)
            stmt.setString(3, candidate.githubProfile)
            stmt.setObject(4, candidate.id?.toJavaUuid() ?: throw IllegalArgumentException("Candidate ID cannot be null for update"))
            stmt.executeUpdate() > 0
        }
    }
    override suspend fun delete(id: Uuid): Boolean = withContext(Dispatchers.IO) {
        connection.prepareStatement("DELETE FROM candidates WHERE id = ?").use { stmt ->
            stmt.setObject(1, id.toJavaUuid())
            stmt.executeUpdate() > 0
        }
    }

    private fun mapRow(rs: java.sql.ResultSet): Candidate {
        return Candidate(
            (rs.getObject("id") as JavaUuid).toKotlinUuid(),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("github_profile")
        )
    }
}

class JdbcEvaluationRepository(private val connection: Connection) : EvaluationRepository {
    override suspend fun save(evaluation: Evaluation): Uuid = withContext(Dispatchers.IO) {
        val newId = evaluation.id ?: Uuid.random()
        connection.prepareStatement("INSERT INTO evaluations (id, candidate_id, exercise_id, score, feedback) VALUES (?, ?, ?, ?, ?)").use { stmt ->
            stmt.setObject(1, newId.toJavaUuid())
            stmt.setObject(2, evaluation.candidateId.toJavaUuid())
            stmt.setObject(3, evaluation.exerciseId.toJavaUuid())
            stmt.setInt(4, evaluation.score)
            stmt.setString(5, evaluation.feedback)
            stmt.executeUpdate()
        }
        newId
    }
    override suspend fun findByCandidateId(candidateId: Uuid): List<Evaluation> = withContext(Dispatchers.IO) {
        connection.prepareStatement("SELECT * FROM evaluations WHERE candidate_id = ?").use { stmt ->
            stmt.setObject(1, candidateId.toJavaUuid())
            val rs = stmt.executeQuery()
            val list = mutableListOf<Evaluation>()
            while (rs.next()) {
                list.add(Evaluation(
                    (rs.getObject("id") as JavaUuid).toKotlinUuid(),
                    (rs.getObject("candidate_id") as JavaUuid).toKotlinUuid(),
                    (rs.getObject("exercise_id") as JavaUuid).toKotlinUuid(),
                    rs.getInt("score"),
                    rs.getString("feedback")
                ))
            }
            list
        }
    }
}

class JdbcSessionRepository(private val connection: Connection) : SessionRepository {
    override suspend fun save(session: UserSession) = withContext(Dispatchers.IO) {
        connection.prepareStatement(
            "MERGE INTO sessions (session_id, user_id, access_token) KEY(session_id) VALUES (?, ?, ?)"
        ).use { stmt ->
            stmt.setString(1, session.sessionId)
            stmt.setString(2, session.userId)
            stmt.setString(3, session.accessToken)
            stmt.executeUpdate()
        }
        Unit
    }

    override suspend fun findById(sessionId: String): UserSession? = withContext(Dispatchers.IO) {
        connection.prepareStatement("SELECT * FROM sessions WHERE session_id = ?").use { stmt ->
            stmt.setString(1, sessionId)
            val rs = stmt.executeQuery()
            if (rs.next()) {
                UserSession(
                    sessionId = rs.getString("session_id"),
                    userId = rs.getString("user_id"),
                    accessToken = rs.getString("access_token")
                )
            } else null
        }
    }

    override suspend fun delete(sessionId: String) = withContext(Dispatchers.IO) {
        connection.prepareStatement("DELETE FROM sessions WHERE session_id = ?").use { stmt ->
            stmt.setString(1, sessionId)
            stmt.executeUpdate()
        }
        Unit
    }
}