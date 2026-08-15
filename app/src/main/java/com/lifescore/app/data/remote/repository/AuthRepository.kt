package com.lifescore.app.data.remote.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.lifescore.app.domain.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

interface AuthRepository {
    val currentUser: FirebaseUser?
    val authState: kotlinx.coroutines.flow.Flow<FirebaseUser?>
    suspend fun signInAnonymously(): Result<UserProfile>
    suspend fun signInWithGoogle(idToken: String): Result<UserProfile>
    suspend fun signUp(email: String, password: String, displayName: String): Result<UserProfile>
    suspend fun signIn(email: String, password: String): Result<UserProfile>
    fun signOut()
    fun getCurrentUser(): UserProfile?
}

class AuthRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firebaseRepository: FirebaseRepository
) : AuthRepository {

    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override val authState: kotlinx.coroutines.flow.Flow<FirebaseUser?> = kotlinx.coroutines.flow.callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun signInAnonymously(): Result<UserProfile> = withContext(Dispatchers.IO) {
        try {
            val result = auth.signInAnonymously().await()
            val user = result.user ?: return@withContext Result.failure(Exception("Anonymous login failed"))
            val profile = UserProfile(
                id = user.uid.hashCode().toLong(),
                name = "Guest Hero",
                currentXp = 0,
                currentLevel = 1,
                currentStreakDays = 0,
                isPremium = false,
                title = "Novice Seeker"
            )
            firebaseRepository.saveUser(profile, email = "", uid = user.uid)
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<UserProfile> = withContext(Dispatchers.IO) {
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user ?: return@withContext Result.failure(Exception("Google Sign-In failed"))
            
            val existing = firebaseRepository.getUser(user.uid)
            val profile = existing ?: UserProfile(
                id = user.uid.hashCode().toLong(),
                name = user.displayName ?: "Hero",
                currentXp = 0,
                currentLevel = 1,
                currentStreakDays = 0,
                isPremium = false,
                title = "Novice Seeker"
            )
            firebaseRepository.saveUser(profile, email = user.email ?: "", uid = user.uid)
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUp(email: String, password: String, displayName: String): Result<UserProfile> = withContext(Dispatchers.IO) {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: return@withContext Result.failure(Exception("Sign up failed"))
            
            // Set display name
            user.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(displayName).build()).await()

            val profile = UserProfile(
                id = user.uid.hashCode().toLong(),
                name = displayName,
                currentXp = 0,
                currentLevel = 1,
                currentStreakDays = 0,
                isPremium = false,
                title = "Novice Seeker"
            )
            firebaseRepository.saveUser(profile, email = email, uid = user.uid)
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signIn(email: String, password: String): Result<UserProfile> = withContext(Dispatchers.IO) {
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: return@withContext Result.failure(Exception("Sign in failed"))
            val profile = firebaseRepository.getUser(user.uid) ?: UserProfile(
                id = user.uid.hashCode().toLong(),
                name = user.displayName ?: "Hero",
                currentXp = 0,
                currentLevel = 1,
                currentStreakDays = 0
            )
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun signOut() {
        auth.signOut()
    }

    override fun getCurrentUser(): UserProfile? {
        val user = auth.currentUser ?: return null
        return UserProfile(
            id = user.uid.hashCode().toLong(),
            name = user.displayName ?: "Hero",
            currentXp = 0,
            currentLevel = 1,
            currentStreakDays = 0
        )
    }
}
