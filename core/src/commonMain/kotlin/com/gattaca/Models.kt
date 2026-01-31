package com.gattaca

import kotlinx.serialization.Serializable

@Serializable
data class Organization(val id: Int? = null, val name: String)

@Serializable
data class User(val id: Int? = null, val organizationId: Int, val name: String, val email: String, val passwordHash: String? = null)

@Serializable
data class Exercise(val id: Int? = null, val creatorId: Int, val title: String, val description: String)

@Serializable
data class Candidate(val id: Int? = null, val name: String, val email: String)

@Serializable
data class Evaluation(
    val id: Int? = null, 
    val candidateId: Int, 
    val exerciseId: Int, 
    val score: Int, 
    val feedback: String
)
