package com.edutrack.app.repository

import com.edutrack.app.data.UserSessionManager
import com.edutrack.app.database.UserDao
import com.edutrack.app.model.User
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val isLoggedIn: StateFlow<Boolean>
    val currentUserName: StateFlow<String>
    val currentUserEmail: StateFlow<String>
    val currentUserRole: StateFlow<String>

    suspend fun register(name: String, email: String, password: String): Result<User>
    suspend fun login(email: String, password: String): Result<User>
    fun updateProfile(name: String, email: String, role: String)
    fun logout()
}

class AuthRepositoryImpl(
    private val userDao: UserDao,
    private val sessionManager: UserSessionManager
) : AuthRepository {

    override val isLoggedIn: StateFlow<Boolean> = sessionManager.isLoggedIn
    override val currentUserName: StateFlow<String> = sessionManager.userName
    override val currentUserEmail: StateFlow<String> = sessionManager.userEmail
    override val currentUserRole: StateFlow<String> = sessionManager.userRole

    override suspend fun register(name: String, email: String, password: String): Result<User> {
        val trimmedEmail = email.trim().lowercase()
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) return Result.failure(IllegalArgumentException("Name cannot be empty"))
        if (trimmedEmail.isEmpty()) return Result.failure(IllegalArgumentException("Email cannot be empty"))
        if (password.length < 4) return Result.failure(IllegalArgumentException("Password must be at least 4 characters"))

        val existing = userDao.getUserByEmail(trimmedEmail)
        if (existing != null) {
            return Result.failure(IllegalArgumentException("An account with this email already exists"))
        }

        val newUser = User(
            name = trimmedName,
            email = trimmedEmail,
            passwordHash = password // Clean in-app hash representation
        )
        val id = userDao.insertUser(newUser)
        val registeredUser = newUser.copy(id = id.toInt())
        sessionManager.login(name = registeredUser.name, email = registeredUser.email)
        return Result.success(registeredUser)
    }

    override suspend fun login(email: String, password: String): Result<User> {
        val trimmedEmail = email.trim().lowercase()
        val user = userDao.getUserByEmail(trimmedEmail)
            ?: return Result.failure(IllegalArgumentException("Invalid email or password"))

        if (user.passwordHash != password) {
            return Result.failure(IllegalArgumentException("Invalid email or password"))
        }

        sessionManager.login(name = user.name, email = user.email)
        return Result.success(user)
    }

    override fun updateProfile(name: String, email: String, role: String) {
        sessionManager.updateProfile(name, email, role)
    }

    override fun logout() {
        sessionManager.logout()
    }
}
