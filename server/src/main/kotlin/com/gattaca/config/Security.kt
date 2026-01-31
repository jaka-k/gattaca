package com.gattaca.config

import com.gattaca.public.configurePublicClient
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.Serializable
import com.gattaca.domain.SessionRepository
import com.gattaca.domain.UserSession as DomainUserSession

@Serializable
data class KtorUserSession(val sessionId: String, val userId: String, val accessToken: String? = null)

class DbSessionStorage(private val repository: SessionRepository) : SessionStorage {
    override suspend fun write(id: String, value: String) {
        repository.save(DomainUserSession(sessionId = id, userId = "unknown", accessToken = value))
    }

    override suspend fun read(id: String): String {
        return repository.findById(id)?.accessToken ?: throw NoSuchElementException("Session $id not found")
    }

    override suspend fun invalidate(id: String) {
        repository.delete(id)
    }
}

fun Application.configureSecurity(sessionRepository: SessionRepository) {
    install(Sessions) {
        cookie<KtorUserSession>("USER_SESSION", DbSessionStorage(sessionRepository)) {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 3600
        }
    }

    authentication {
        oauth("auth-oauth-google") {
            urlProvider = { "http://localhost:8080/callback" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "google",
                    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                    accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
                    requestMethod = HttpMethod.Post,
                    clientId = System.getenv("GOOGLE_CLIENT_ID"),
                    clientSecret = System.getenv("GOOGLE_CLIENT_SECRET"),
                    defaultScopes = listOf("https://www.googleapis.com/auth/userinfo.profile")
                )
            }
            client = HttpClient(Apache) {
                configurePublicClient()
            }
        }
    }
    routing {
        authenticate("auth-oauth-google") {
            get("login") {
                call.respondRedirect("/callback")
            }
        
            get("/callback") {
                val principal: OAuthAccessTokenResponse.OAuth2? = call.authentication.principal()
                val sessionId = io.ktor.util.generateNonce()
                call.sessions.set(KtorUserSession(
                    sessionId = sessionId,
                    userId = "google-user", 
                    accessToken = principal?.accessToken.toString()
                ))
                call.respondRedirect("/hello")
            }
        }
    }
}
