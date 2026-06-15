package com.barns.app.domain.repository

import com.barns.app.domain.model.User

interface AuthRepository {
    suspend fun currentUser(): User?
    suspend fun signInWithMockAccount(): User
    suspend fun signOut()
}
