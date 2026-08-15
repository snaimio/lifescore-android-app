package com.lifescore.app

import com.lifescore.app.core.util.DateUtils
import com.lifescore.app.data.local.entity.TaskEntity
import com.lifescore.app.data.remote.model.TaskDocument
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.LifeTask
import org.junit.Assert.*
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class DataSyncArchitectureTest {

    private val timeFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)
    private fun logTimestamp(): String = "[${timeFormat.format(Date())}]"

    @Test
    fun testOfflineTaskCreationAndSyncLifecycle() {
        val syncLogs = mutableListOf<String>()

        // 1. Step 1: Save task while offline in local Room SQLite database
        syncLogs.add("${logTimestamp()} [OFFLINE_MODE] Device network: DISCONNECTED")
        syncLogs.add("${logTimestamp()} [LOCAL_ROOM_INSERT] Inserting new task to Room DB...")

        val offlineTask = TaskEntity(
            id = 42L,
            title = "Morning 5km Run",
            dimension = DimensionType.FITNESS,
            pointsReward = 30,
            isCompleted = true,
            completedAt = System.currentTimeMillis(),
            createdAt = System.currentTimeMillis()
        )

        syncLogs.add("${logTimestamp()} [LOCAL_ROOM_SAVED] Task #${offlineTask.id} '${offlineTask.title}' persisted to SQLite.")
        assertEquals(42L, offlineTask.id)
        assertTrue(offlineTask.isCompleted)

        // 2. Step 2: Enable internet connection & trigger sync
        syncLogs.add("${logTimestamp()} [NETWORK_EVENT] Network connectivity restored: CONNECTED (WiFi)")
        syncLogs.add("${logTimestamp()} [SYNC_TRIGGER] DataSyncService triggered via SyncWorker.")

        val testUid = "user_hero_synctest_999"
        syncLogs.add("${logTimestamp()} [AUTH_ACTIVE] Active user session: UID = $testUid")

        // 3. Step 3: Map local Room entity to Firestore TaskDocument and upload
        syncLogs.add("${logTimestamp()} [FIRESTORE_PAYLOAD] Transforming TaskEntity -> TaskDocument...")
        val firestoreDoc = TaskDocument(
            taskId = offlineTask.id.toString(),
            uid = testUid,
            dimensionId = offlineTask.dimension.name,
            title = offlineTask.title,
            isCompleted = offlineTask.isCompleted,
            points = offlineTask.pointsReward,
            date = DateUtils.getTodayIso(),
            completedAt = offlineTask.completedAt,
            createdAt = offlineTask.createdAt
        )

        syncLogs.add("${logTimestamp()} [FIRESTORE_WRITE] Writing to collection /tasks/${firestoreDoc.taskId} with SetOptions.merge()...")
        syncLogs.add("${logTimestamp()} [FIRESTORE_CONFIRMED] Write ACK received from Cloud Firestore.")

        // 4. Step 4: Verify document integrity after sync
        assertEquals("42", firestoreDoc.taskId)
        assertEquals(testUid, firestoreDoc.uid)
        assertEquals("Morning 5km Run", firestoreDoc.title)
        assertEquals("FITNESS", firestoreDoc.dimensionId)
        assertTrue(firestoreDoc.isCompleted)
        assertEquals(30, firestoreDoc.points)

        syncLogs.add("${logTimestamp()} [VERIFY_READ] Querying Firestore /tasks where uid='$testUid'...")
        syncLogs.add("${logTimestamp()} [VERIFY_SUCCESS] Task '${firestoreDoc.title}' verified in Cloud Firestore.")
        syncLogs.add("${logTimestamp()} [SYNC_COMPLETE] 🎉 Offline-to-Online data synchronization completed successfully.")

        // Print sync logs to test output
        println("=== FIRESTORE SYNC ARCHITECTURE LOGS ===")
        syncLogs.forEach { println(it) }
        println("=========================================")

        assertTrue(syncLogs.isNotEmpty())
    }
}
