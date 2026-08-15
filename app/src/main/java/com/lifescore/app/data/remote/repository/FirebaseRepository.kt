package com.lifescore.app.data.remote.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.lifescore.app.data.remote.model.*
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.LifeTask
import com.lifescore.app.domain.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.IOException

interface FirebaseRepository {
    suspend fun saveUser(user: UserProfile, email: String = "", uid: String? = null)
    suspend fun getUser(uid: String): UserProfile?
    suspend fun updateUserScore(uid: String, score: Int, xp: Int, level: Int)
    suspend fun saveDimension(uid: String, dimension: DimensionDocument)
    suspend fun getDimensions(uid: String): List<DimensionDocument>
    suspend fun saveTask(uid: String, task: LifeTask)
    suspend fun getTasks(uid: String, date: String): List<LifeTask>
    suspend fun completeTask(uid: String, taskId: String, isCompleted: Boolean)
    suspend fun saveStreak(uid: String, streak: StreakDocument)
    suspend fun getStreak(uid: String): StreakDocument?
    suspend fun saveChallenge(challenge: ChallengeDocument)
    suspend fun joinChallenge(uid: String, challengeId: String)
    suspend fun getLeaderboard(): List<UserDocument>
    fun observeGlobalLeaderboard(limit: Long = 100): Flow<List<UserDocument>>
    fun observeDimensionLeaderboard(dimension: DimensionType, limit: Long = 100): Flow<List<DimensionDocument>>
    fun observeFriendsLeaderboard(friendUids: List<String>): Flow<List<UserDocument>>
    suspend fun sponsorUser(sponsorUid: String, sponsorName: String, recipientEmail: String, message: String): String
    fun observeGuardianWall(): Flow<List<GuardianSponsorshipDocument>>
    suspend fun updateShieldCount(uid: String, shieldsCount: Int)
    suspend fun updateSubscriptionStatus(uid: String, isPremium: Boolean, tier: String, expiryDate: Long? = null)
    suspend fun saveAssessmentResult(result: AssessmentResultDocument)
    suspend fun getAssessmentResult(uid: String): AssessmentResultDocument?
}

class FirebaseRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : FirebaseRepository {

    private val usersCollection = firestore.collection("users")
    private val dimensionsCollection = firestore.collection("dimensions")
    private val tasksCollection = firestore.collection("tasks")
    private val streaksCollection = firestore.collection("streaks")
    private val challengesCollection = firestore.collection("challenges")
    private val userChallengesCollection = firestore.collection("userChallenges")
    private val guardiansCollection = firestore.collection("guardians")

    override suspend fun saveUser(user: UserProfile, email: String, uid: String?): Unit = withRetry {
        val targetUid = uid ?: user.id.toString()
        val userDoc = UserDocument(
            uid = targetUid,
            email = email,
            displayName = user.name,
            totalScore = 500,
            level = user.currentLevel,
            currentXp = user.currentXp,
            streak = user.currentStreakDays,
            isPremium = user.isPremium
        )
        usersCollection.document(targetUid)
            .set(userDoc, SetOptions.merge())
            .await()
    }

    override suspend fun getUser(uid: String): UserProfile? = withRetry {
        val snapshot = usersCollection.document(uid).get().await()
        val doc = snapshot.toObject<UserDocument>() ?: return@withRetry null
        UserProfile(
            id = doc.uid.hashCode().toLong(),
            name = doc.displayName,
            currentXp = doc.currentXp,
            currentLevel = doc.level,
            currentStreakDays = doc.streak,
            isPremium = doc.isPremium,
            title = doc.archetype
        )
    }

    override suspend fun updateUserScore(uid: String, score: Int, xp: Int, level: Int): Unit = withRetry {
        val updates = mapOf(
            "totalScore" to score,
            "currentXp" to xp,
            "level" to level,
            "lastActive" to com.google.firebase.firestore.FieldValue.serverTimestamp()
        )
        usersCollection.document(uid).update(updates).await()
    }

    override suspend fun saveDimension(uid: String, dimension: DimensionDocument): Unit = withRetry {
        val docId = "${uid}_${dimension.dimensionId}"
        dimensionsCollection.document(docId)
            .set(dimension.copy(id = docId, uid = uid), SetOptions.merge())
            .await()
    }

    override suspend fun getDimensions(uid: String): List<DimensionDocument> = withRetry {
        val snapshot = dimensionsCollection.whereEqualTo("uid", uid).get().await()
        snapshot.toObjects<DimensionDocument>()
    }

    override suspend fun saveTask(uid: String, task: LifeTask): Unit = withRetry {
        val taskId = if (task.id == 0L) tasksCollection.document().id else task.id.toString()
        val taskDoc = TaskDocument(
            taskId = taskId,
            uid = uid,
            dimensionId = task.dimension.name,
            title = task.title,
            isCompleted = task.isCompleted,
            points = task.pointsReward,
            date = com.lifescore.app.core.util.DateUtils.getTodayIso(),
            completedAt = task.completedAt,
            createdAt = task.createdAt
        )
        tasksCollection.document(taskId).set(taskDoc, SetOptions.merge()).await()
    }

    override suspend fun getTasks(uid: String, date: String): List<LifeTask> = withRetry {
        val snapshot = tasksCollection
            .whereEqualTo("uid", uid)
            .whereEqualTo("date", date)
            .get()
            .await()

        snapshot.toObjects<TaskDocument>().map { doc ->
            LifeTask(
                id = doc.taskId.hashCode().toLong(),
                title = doc.title,
                dimension = try { DimensionType.valueOf(doc.dimensionId) } catch (e: Exception) { DimensionType.HEALTH },
                pointsReward = doc.points,
                isCompleted = doc.isCompleted,
                completedAt = doc.completedAt,
                createdAt = doc.createdAt
            )
        }
    }

    override suspend fun completeTask(uid: String, taskId: String, isCompleted: Boolean): Unit = withRetry {
        val updates = mapOf(
            "isCompleted" to isCompleted,
            "completedAt" to if (isCompleted) System.currentTimeMillis() else null
        )
        tasksCollection.document(taskId).update(updates).await()
    }

    override suspend fun saveStreak(uid: String, streak: StreakDocument): Unit = withRetry {
        streaksCollection.document(uid).set(streak.copy(uid = uid), SetOptions.merge()).await()
    }

    override suspend fun getStreak(uid: String): StreakDocument? = withRetry {
        val snapshot = streaksCollection.document(uid).get().await()
        snapshot.toObject<StreakDocument>()
    }

    override suspend fun saveChallenge(challenge: ChallengeDocument): Unit = withRetry {
        challengesCollection.document(challenge.challengeId)
            .set(challenge, SetOptions.merge())
            .await()
    }

    override suspend fun joinChallenge(uid: String, challengeId: String): Unit = withRetry {
        val docId = "${uid}_${challengeId}"
        val userChallenge = UserChallengeDocument(
            id = docId,
            uid = uid,
            challengeId = challengeId,
            progress = 0f,
            currentDay = 1,
            isCompleted = false,
            startedAt = System.currentTimeMillis()
        )
        userChallengesCollection.document(docId).set(userChallenge, SetOptions.merge()).await()
    }

    override suspend fun getLeaderboard(): List<UserDocument> = withRetry {
        val snapshot = usersCollection
            .orderBy("totalScore", Query.Direction.DESCENDING)
            .limit(50)
            .get()
            .await()
        snapshot.toObjects<UserDocument>()
    }

    override fun observeGlobalLeaderboard(limit: Long): Flow<List<UserDocument>> = callbackFlow {
        val listener = usersCollection
            .orderBy("totalScore", Query.Direction.DESCENDING)
            .limit(limit)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val users = snapshot?.toObjects<UserDocument>() ?: emptyList()
                trySend(users)
            }
        awaitClose { listener.remove() }
    }

    override fun observeDimensionLeaderboard(dimension: DimensionType, limit: Long): Flow<List<DimensionDocument>> = callbackFlow {
        val listener = dimensionsCollection
            .whereEqualTo("dimensionId", dimension.name)
            .orderBy("score", Query.Direction.DESCENDING)
            .limit(limit)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val docs = snapshot?.toObjects<DimensionDocument>() ?: emptyList()
                trySend(docs)
            }
        awaitClose { listener.remove() }
    }

    override fun observeFriendsLeaderboard(friendUids: List<String>): Flow<List<UserDocument>> = callbackFlow {
        if (friendUids.isEmpty()) {
            trySend(emptyList())
            awaitClose { }
            return@callbackFlow
        }
        val listener = usersCollection
            .whereIn("uid", friendUids.take(10))
            .orderBy("totalScore", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val users = snapshot?.toObjects<UserDocument>() ?: emptyList()
                trySend(users)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun sponsorUser(sponsorUid: String, sponsorName: String, recipientEmail: String, message: String): String = withRetry {
        val docRef = guardiansCollection.document()
        val sponsorship = GuardianSponsorshipDocument(
            id = docRef.id,
            sponsorUid = sponsorUid,
            sponsorName = sponsorName,
            recipientEmail = recipientEmail,
            message = message,
            sponsoredDate = System.currentTimeMillis()
        )
        docRef.set(sponsorship).await()
        docRef.id
    }

    override fun observeGuardianWall(): Flow<List<GuardianSponsorshipDocument>> = callbackFlow {
        val listener = guardiansCollection
            .orderBy("sponsoredDate", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.toObjects<GuardianSponsorshipDocument>() ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun updateShieldCount(uid: String, shieldsCount: Int): Unit = withRetry {
        usersCollection.document(uid)
            .update("shieldsRemaining", shieldsCount)
            .await()
    }

    override suspend fun updateSubscriptionStatus(
        uid: String,
        isPremium: Boolean,
        tier: String,
        expiryDate: Long?
    ): Unit = withRetry {
        val updates = mutableMapOf<String, Any>(
            "isPremium" to isPremium,
            "subscriptionTier" to tier
        )
        if (expiryDate != null) {
            updates["subscriptionExpiryDate"] = expiryDate
        }
        usersCollection.document(uid)
            .set(updates, SetOptions.merge())
            .await()
    }

    override suspend fun saveAssessmentResult(result: AssessmentResultDocument): Unit = withRetry {
        val docRef = if (result.id.isNotBlank()) {
            firestore.collection("assessment_results").document(result.id)
        } else {
            firestore.collection("assessment_results").document()
        }
        val finalResult = result.copy(id = docRef.id)
        docRef.set(finalResult, SetOptions.merge()).await()

        // Also update user's primary profile archetype and scores
        if (result.uid.isNotBlank()) {
            usersCollection.document(result.uid)
                .set(
                    mapOf(
                        "archetype" to result.archetypeName,
                        "overallScore" to result.overallScore,
                        "topRiasecCode" to result.topRiasecCode
                    ),
                    SetOptions.merge()
                )
                .await()
        }
    }

    override suspend fun getAssessmentResult(uid: String): AssessmentResultDocument? = withRetry {
        val snapshot = firestore.collection("assessment_results")
            .whereEqualTo("uid", uid)
            .orderBy("completedAt", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .await()

        snapshot.documents.firstOrNull()?.toObject<AssessmentResultDocument>()
    }

    /**
     * Exponential backoff retry wrapper for robust network handling.
     */
    private suspend fun <T> withRetry(
        maxRetries: Int = 3,
        initialDelayMs: Long = 500,
        block: suspend () -> T
    ): T = withContext(Dispatchers.IO) {
        var currentDelay = initialDelayMs
        repeat(maxRetries - 1) {
            try {
                return@withContext block()
            } catch (e: Exception) {
                delay(currentDelay)
                currentDelay *= 2
            }
        }
        return@withContext block() // Last attempt
    }
}
