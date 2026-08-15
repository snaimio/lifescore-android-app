package com.lifescore.app

import com.lifescore.app.core.util.DailyVlogStitcher
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.LogGroupMember
import com.lifescore.app.domain.model.MicroVlog
import org.junit.Assert.*
import org.junit.Test

class DailyVlogStitcherTest {

    @Test
    fun testStitchMax30ClipsInto60SecondVlog() {
        val clips = (1..35).map { i ->
            MicroVlog(
                id = "c_$i",
                date = "2026-08-14",
                dayOfWeek = "Day $i",
                dimension = DimensionType.FITNESS,
                caption = "Habit #$i",
                durationSeconds = 2.0,
                isRecorded = true
            )
        }

        val vlog = DailyVlogStitcher.stitchClipsIntoDailyVlog(
            ownerUid = "u_hero",
            ownerName = "Champion Hero",
            date = "2026-08-14",
            clips = clips
        )

        // Should cap at 30 clips
        assertEquals(30, vlog.clips.size)
        // Should equal 60.0s total duration
        assertEquals(60.0, vlog.totalDurationSeconds, 0.01)
        assertEquals(DimensionType.FITNESS, vlog.dominantDimension)
        assertEquals(150, vlog.xpEarned)
    }

    @Test
    fun testLogGroup12MembersCapEnforcement() {
        val group = DailyVlogStitcher.createLogGroup(
            name = "Iron 12 Squad",
            description = "Daily 2s fitness proof",
            adminUid = "admin_1",
            adminName = "Leader"
        )

        assertEquals(12, group.maxMembers)
        assertEquals(1, group.members.size)
        assertFalse(group.isFull)
        assertEquals(11, group.availableSlots)

        // Add 11 members up to 12
        var currentGroup = group
        for (i in 2..12) {
            val member = LogGroupMember("u_$i", "Member $i")
            val (updated, err) = DailyVlogStitcher.joinLogGroup(currentGroup, member)
            assertNotNull(updated)
            assertNull(err)
            currentGroup = updated!!
        }

        assertEquals(12, currentGroup.members.size)
        assertTrue(currentGroup.isFull)
        assertEquals(0, currentGroup.availableSlots)

        // Attempt to add 13th member -> must be rejected
        val excessMember = LogGroupMember("u_13", "Excess Member")
        val (rejected, errMsg) = DailyVlogStitcher.joinLogGroup(currentGroup, excessMember)
        assertNull(rejected)
        assertNotNull(errMsg)
        assertTrue(errMsg!!.contains("maximum capacity"))
    }

    @Test
    fun testEmojiReactionToggle() {
        val vlog = DailyVlogStitcher.stitchClipsIntoDailyVlog(
            ownerUid = "u_hero",
            ownerName = "Champion Hero",
            date = "2026-08-14",
            clips = emptyList()
        )

        val initialFire = vlog.reactionCounts["🔥"] ?: 0

        // User reacts with 🔥
        val reactedVlog = DailyVlogStitcher.toggleReaction(vlog, "🔥")
        assertEquals(initialFire + 1, reactedVlog.reactionCounts["🔥"])
        assertTrue(reactedVlog.userReactions.contains("🔥"))

        // User toggles 🔥 off
        val unreactedVlog = DailyVlogStitcher.toggleReaction(reactedVlog, "🔥")
        assertEquals(initialFire, unreactedVlog.reactionCounts["🔥"])
        assertFalse(unreactedVlog.userReactions.contains("🔥"))
    }

    @Test
    fun testAddVlogComment() {
        val vlog = DailyVlogStitcher.stitchClipsIntoDailyVlog(
            ownerUid = "u_hero",
            ownerName = "Champion Hero",
            date = "2026-08-14",
            clips = emptyList()
        )

        val initialCommentsCount = vlog.comments.size
        val updated = DailyVlogStitcher.addComment(
            vlog = vlog,
            authorUid = "u_marcus",
            authorName = "Marcus",
            text = "Great 2s clip! Keep the fire burning!"
        )

        assertEquals(initialCommentsCount + 1, updated.comments.size)
        assertEquals("Great 2s clip! Keep the fire burning!", updated.comments.last().text)
    }

    @Test
    fun testVlogShareCaptionFormatting() {
        val vlog = DailyVlogStitcher.stitchClipsIntoDailyVlog(
            ownerUid = "u_hero",
            ownerName = "Champion Hero",
            date = "2026-08-14",
            clips = listOf(
                MicroVlog("1", "2026-08-14", "Mon", DimensionType.CAREER, "Deep work block", isRecorded = true)
            )
        )

        val caption = DailyVlogStitcher.generateVlogShareCaption(vlog, "LOGS-88X")
        assertTrue(caption.contains("My 60s Daily LifeScore Vlog!"))
        assertTrue(caption.contains("+150 XP"))
        assertTrue(caption.contains("LOGS-88X"))
        assertTrue(caption.contains("#MicroVlogs"))
    }
}
