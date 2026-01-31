package com.gattaca.persistence

import com.gattaca.domain.*
import com.gattaca.domain.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.Statement

class JdbcOrganizationRepository(private val connection: Connection) : OrganizationRepository {
    init {
        connection.createStatement().use { 
            it.executeUpdate("CREATE TABLE IF NOT EXISTS organizations (id SERIAL PRIMARY KEY, name VARCHAR(255))")
        }
    }
    override suspend fun save(org: Organization): Int = withContext(Dispatchers.IO) {
        connection.prepareStatement("INSERT INTO organizations (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS).use { stmt ->
            stmt.setString(1, org.name)
            stmt.executeUpdate()
            val keys = stmt.generatedKeys
            if (keys.next()) keys.getInt(1) else throw Exception("Failed to save org")
        }
    }
    override suspend fun findById(id: Int): Organization? = withContext(Dispatchers.IO) {
        connection.prepareStatement("SELECT * FROM organizations WHERE id = ?").use { stmt ->
            stmt.setInt(1, id)
            val rs = stmt.executeQuery()
            if (rs.next()) Organization(rs.getInt("id"), rs.getString("name")) else null
        }
    }
    override suspend fun findAll(): List<Organization> = withContext(Dispatchers.IO) {
        connection.createStatement().use { stmt ->
            val rs = stmt.executeQuery("SELECT * FROM organizations")
            val list = mutableListOf<Organization>()
            while (rs.next()) list.add(Organization(rs.getInt("id"), rs.getString("name")))
            list
        }
    }
    override suspend fun update(org: Organization): Boolean = withContext(Dispatchers.IO) {
        connection.prepareStatement("UPDATE organizations SET name = ? WHERE id = ?").use { stmt ->
            stmt.setString(1, org.name)
            stmt.setInt(2, org.id ?: throw IllegalArgumentException("Organization ID cannot be null for update"))
            stmt.executeUpdate() > 0
        }
    }
    override suspend fun delete(id: Int): Boolean = withContext(Dispatchers.IO) {
        connection.prepareStatement("DELETE FROM organizations WHERE id = ?").use { stmt ->
            stmt.setInt(1, id)
            stmt.executeUpdate() > 0
        }
    }
}

class JdbcUserRepository(private val connection: Connection) : UserRepository {
    init {
        connection.createStatement().use { 
            it.executeUpdate("CREATE TABLE IF NOT EXISTS users (id SERIAL PRIMARY KEY, organization_id INT, name VARCHAR(255), email VARCHAR(255), password_hash VARCHAR(255))")
        }
    }
    override suspend fun save(user: User): Int = withContext(Dispatchers.IO) {
        connection.prepareStatement("INSERT INTO users (organization_id, name, email, password_hash) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS).use { stmt ->
            stmt.setInt(1, user.organizationId)
            stmt.setString(2, user.name)
            stmt.setString(3, user.email)
            stmt.setString(4, user.passwordHash)
            stmt.executeUpdate()
            val keys = stmt.generatedKeys
            if (keys.next()) keys.getInt(1) else throw Exception("Failed to save user")
        }
    }
    override suspend fun findById(id: Int): User? = withContext(Dispatchers.IO) {
        connection.prepareStatement("SELECT * FROM users WHERE id = ?").use { stmt ->
            stmt.setInt(1, id)
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
    override suspend fun findByOrganizationId(orgId: Int): List<User> = withContext(Dispatchers.IO) {
        connection.prepareStatement("SELECT * FROM users WHERE organization_id = ?").use { stmt ->
            stmt.setInt(1, orgId)
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
            stmt.setInt(1, user.organizationId)
            stmt.setString(2, user.name)
            stmt.setString(3, user.email)
            stmt.setString(4, user.passwordHash)
            stmt.setInt(5, user.id ?: throw IllegalArgumentException("User ID cannot be null for update"))
            stmt.executeUpdate() > 0
        }
    }
    override suspend fun delete(id: Int): Boolean = withContext(Dispatchers.IO) {
        connection.prepareStatement("DELETE FROM users WHERE id = ?").use { stmt ->
            stmt.setInt(1, id)
            stmt.executeUpdate() > 0
        }
    }

    private fun mapRow(rs: java.sql.ResultSet): User {
        return User(
            rs.getInt("id"),
            rs.getInt("organization_id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("password_hash")
        )
    }
}

class JdbcExerciseRepository(private val connection: Connection) : ExerciseRepository {
    init {
        connection.createStatement().use { 
            it.executeUpdate("CREATE TABLE IF NOT EXISTS exercises (id SERIAL PRIMARY KEY, creator_id INT, title VARCHAR(255), description TEXT)")
        }
    }
    override suspend fun save(exercise: Exercise): Int = withContext(Dispatchers.IO) {
        connection.prepareStatement("INSERT INTO exercises (creator_id, title, description) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS).use { stmt ->
            stmt.setInt(1, exercise.creatorId)
            stmt.setString(2, exercise.title)
            stmt.setString(3, exercise.description)
            stmt.executeUpdate()
            val keys = stmt.generatedKeys
            if (keys.next()) keys.getInt(1) else throw Exception("Failed to save exercise")
        }
    }
    override suspend fun findById(id: Int): Exercise? = withContext(Dispatchers.IO) {
        connection.prepareStatement("SELECT * FROM exercises WHERE id = ?").use { stmt ->
            stmt.setInt(1, id)
            val rs = stmt.executeQuery()
            if (rs.next()) Exercise(rs.getInt("id"), rs.getInt("creator_id"), rs.getString("title"), rs.getString("description")) else null
        }
    }
    override suspend fun findAll(): List<Exercise> = withContext(Dispatchers.IO) {
        connection.createStatement().use { stmt ->
            val rs = stmt.executeQuery("SELECT * FROM exercises")
            val list = mutableListOf<Exercise>()
            while (rs.next()) list.add(Exercise(rs.getInt("id"), rs.getInt("creator_id"), rs.getString("title"), rs.getString("description")))
            list
        }
    }
    override suspend fun update(exercise: Exercise): Boolean = withContext(Dispatchers.IO) {
        connection.prepareStatement("UPDATE exercises SET creator_id = ?, title = ?, description = ? WHERE id = ?").use { stmt ->
            stmt.setInt(1, exercise.creatorId)
            stmt.setString(2, exercise.title)
            stmt.setString(3, exercise.description)
            stmt.setInt(4, exercise.id ?: throw IllegalArgumentException("Exercise ID cannot be null for update"))
            stmt.executeUpdate() > 0
        }
    }
    override suspend fun delete(id: Int): Boolean = withContext(Dispatchers.IO) {
        connection.prepareStatement("DELETE FROM exercises WHERE id = ?").use { stmt ->
            stmt.setInt(1, id)
            stmt.executeUpdate() > 0
        }
    }
}

class JdbcCandidateRepository(private val connection: Connection) : CandidateRepository {
    init {
        connection.createStatement().use { 
            it.executeUpdate("CREATE TABLE IF NOT EXISTS candidates (id SERIAL PRIMARY KEY, name VARCHAR(255), email VARCHAR(255), github_profile VARCHAR(255))")
        }
    }
    override suspend fun save(candidate: Candidate): Int = withContext(Dispatchers.IO) {
        connection.prepareStatement("INSERT INTO candidates (name, email, github_profile) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS).use { stmt ->
            stmt.setString(1, candidate.name)
            stmt.setString(2, candidate.email)
            stmt.setString(3, candidate.githubProfile)
            stmt.executeUpdate()
            val keys = stmt.generatedKeys
            if (keys.next()) keys.getInt(1) else throw Exception("Failed to save candidate")
        }
    }
    override suspend fun findById(id: Int): Candidate? = withContext(Dispatchers.IO) {
        connection.prepareStatement("SELECT * FROM candidates WHERE id = ?").use { stmt ->
            stmt.setInt(1, id)
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
            stmt.setInt(4, candidate.id ?: throw IllegalArgumentException("Candidate ID cannot be null for update"))
            stmt.executeUpdate() > 0
        }
    }
    override suspend fun delete(id: Int): Boolean = withContext(Dispatchers.IO) {
        connection.prepareStatement("DELETE FROM candidates WHERE id = ?").use { stmt ->
            stmt.setInt(1, id)
            stmt.executeUpdate() > 0
        }
    }

    private fun mapRow(rs: java.sql.ResultSet): Candidate {
        return Candidate(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("github_profile")
        )
    }
}

class JdbcEvaluationRepository(private val connection: Connection) : EvaluationRepository {
    init {
        connection.createStatement().use { 
            it.executeUpdate("CREATE TABLE IF NOT EXISTS evaluations (id SERIAL PRIMARY KEY, candidate_id INT, exercise_id INT, score INT, feedback TEXT)")
        }
    }
    override suspend fun save(evaluation: Evaluation): Int = withContext(Dispatchers.IO) {
        connection.prepareStatement("INSERT INTO evaluations (candidate_id, exercise_id, score, feedback) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS).use { stmt ->
            stmt.setInt(1, evaluation.candidateId)
            stmt.setInt(2, evaluation.exerciseId)
            stmt.setInt(3, evaluation.score)
            stmt.setString(4, evaluation.feedback)
            stmt.executeUpdate()
            val keys = stmt.generatedKeys
            if (keys.next()) keys.getInt(1) else throw Exception("Failed to save evaluation")
        }
    }
    override suspend fun findByCandidateId(candidateId: Int): List<Evaluation> = withContext(Dispatchers.IO) {
        connection.prepareStatement("SELECT * FROM evaluations WHERE candidate_id = ?").use { stmt ->
            stmt.setInt(1, candidateId)
            val rs = stmt.executeQuery()
            val list = mutableListOf<Evaluation>()
            while (rs.next()) {
                list.add(Evaluation(rs.getInt("id"), rs.getInt("candidate_id"), rs.getInt("exercise_id"), rs.getInt("score"), rs.getString("feedback")))
            }
            list
        }
    }
}
