package com.lifescore.app.core.util

import com.lifescore.app.domain.model.*
import java.util.UUID

object DailyVlogStitcher {

    const val MAX_GROUP_MEMBERS = 12
    const val MAX_DAILY_CLIPS = 30
    const val CLIP_DURATION_SECONDS = 2.0
    const val MAX_DAILY_DURATION_SECONDS = 60.0

    /**
     * Stitches up to 30 2-second clips into a 60-second Daily Vlog compilation.
     */
    fun stitchClipsIntoDailyVlog(
        ownerUid: String,
        ownerName: String,
        date: String,
        clips: List<MicroVlog>
    ): DailyStitchedVlog {
        val validClips = clips.filter { it.isRecorded }.take(MAX_DAILY_CLIPS)
        val totalDuration = (validClips.size * CLIP_DURATION_SECONDS).coerceAtMost(MAX_DAILY_DURATION_SECONDS)
        val dominantDimension = findDominantDimension(validClips)

        return DailyStitchedVlog(
            id = "vlog_${ownerUid}_$date",
            date = date,
            ownerUid = ownerUid,
            ownerName = ownerName,
            clips = validClips,
            totalDurationSeconds = totalDuration,
            dominantDimension = dominantDimension,
            reactionCounts = mapOf("🔥" to 12, "👏" to 8, "🚀" to 15, "❤️" to 6, "🤯" to 4, "⚡" to 9),
            comments = listOf(
                VlogComment(
                    id = "c1",
                    vlogId = "vlog_${ownerUid}_$date",
                    authorUid = "u_sarah",
                    authorName = "Sarah Chen",
                    authorEmoji = "🚀",
                    text = "Insane consistency today! That 5am deep work block was inspiring 🔥",
                    timestamp = System.currentTimeMillis() - 3600000
                ),
                VlogComment(
                    id = "c2",
                    vlogId = "vlog_${ownerUid}_$date",
                    authorUid = "u_alex",
                    authorName = "Alex Rivera",
                    authorEmoji = "⚔️",
                    text = "60 seconds of pure discipline. Let's keep the streak alive!",
                    timestamp = System.currentTimeMillis() - 1800000
                )
            ),
            xpEarned = 150
        )
    }

    private fun findDominantDimension(clips: List<MicroVlog>): DimensionType {
        if (clips.isEmpty()) return DimensionType.FITNESS
        return clips.groupingBy { it.dimension }
            .eachCount()
            .maxByOrNull { it.value }?.key ?: DimensionType.FITNESS
    }

    /**
     * Generates a new 12-member maximum Log Group with a unique 6-character invite code.
     */
    fun createLogGroup(
        name: String,
        description: String,
        adminUid: String,
        adminName: String,
        adminEmoji: String = "👑"
    ): LogGroup {
        val inviteCode = "LOGS-" + (100..999).random() + ('A'..'Z').random()
        val adminMember = LogGroupMember(
            uid = adminUid,
            displayName = adminName,
            avatarEmoji = adminEmoji,
            streakDays = 7,
            todayClipsCount = 3,
            hasRecordedToday = true,
            lastRecordedAt = System.currentTimeMillis()
        )

        return LogGroup(
            id = "grp_" + UUID.randomUUID().toString().take(8),
            name = name,
            description = description,
            inviteCode = inviteCode,
            maxMembers = MAX_GROUP_MEMBERS,
            members = listOf(adminMember),
            adminUid = adminUid,
            themeColorHex = 0xFF6366F1
        )
    }

    /**
     * Validates and joins a user to a Log Group, strictly enforcing the 12-member ceiling.
     */
    fun joinLogGroup(group: LogGroup, newMember: LogGroupMember): Pair<LogGroup?, String?> {
        if (group.members.size >= MAX_GROUP_MEMBERS) {
            return Pair(null, "Group is at maximum capacity (12 members). Create or join another group!")
        }
        if (group.members.any { it.uid == newMember.uid }) {
            return Pair(null, "You are already a member of this Log Group.")
        }
        val updatedGroup = group.copy(members = group.members + newMember)
        return Pair(updatedGroup, null)
    }

    /**
     * Toggles an emoji reaction on a daily vlog.
     */
    fun toggleReaction(vlog: DailyStitchedVlog, emoji: String): DailyStitchedVlog {
        val currentCounts = vlog.reactionCounts.toMutableMap()
        val userReactions = vlog.userReactions.toMutableSet()

        if (userReactions.contains(emoji)) {
            userReactions.remove(emoji)
            currentCounts[emoji] = (currentCounts[emoji] ?: 1) - 1
        } else {
            userReactions.add(emoji)
            currentCounts[emoji] = (currentCounts[emoji] ?: 0) + 1
        }

        return vlog.copy(
            reactionCounts = currentCounts,
            userReactions = userReactions
        )
    }

    /**
     * Adds a comment to a daily vlog.
     */
    fun addComment(vlog: DailyStitchedVlog, authorUid: String, authorName: String, text: String): DailyStitchedVlog {
        val newComment = VlogComment(
            id = "cmt_" + System.currentTimeMillis(),
            vlogId = vlog.id,
            authorUid = authorUid,
            authorName = authorName,
            text = text,
            timestamp = System.currentTimeMillis()
        )
        return vlog.copy(comments = vlog.comments + newComment)
    }

    /**
     * Formats shareable viral caption for social export.
     */
    fun generateVlogShareCaption(vlog: DailyStitchedVlog, groupInviteCode: String? = null): String {
        val groupSnippet = if (!groupInviteCode.isNullOrBlank()) {
            "\n👥 Join our 12-member accountability squad: https://lifescore.app/logs/join/$groupInviteCode"
        } else ""
        return "🎬 My 60s Daily LifeScore Vlog! Crushed ${vlog.clips.size} micro-habits today across ${vlog.dominantDimension.displayName}. +${vlog.xpEarned} XP!$groupSnippet\n#LifeScore #MicroVlogs #Setlog #DailyMontage #GamifyYourLife"
    }

    fun getDefaultMockGroups(): List<LogGroup> {
        val group1 = LogGroup(
            id = "grp_founders",
            name = "⚡ 5AM Founders Club",
            description = "High-output daily habits: Deep work, cold plunges & sprint shipping.",
            inviteCode = "FOUND-77X",
            maxMembers = 12,
            members = listOf(
                LogGroupMember("u1", "Marcus Aurelius", "👑", 14, 4, true),
                LogGroupMember("u2", "Elena Vance", "⚡", 9, 3, true),
                LogGroupMember("u3", "Devon Park", "🚀", 12, 2, true),
                LogGroupMember("u4", "Sora Takahashi", "⚔️", 5, 1, true)
            ),
            adminUid = "u1",
            themeColorHex = 0xFF6366F1
        )

        val group2 = LogGroup(
            id = "grp_iron_body",
            name = "🏋️ Peak Athletic 12",
            description = "Daily 2-second training proof: Weights, 10k steps, nutrition discipline.",
            inviteCode = "IRON-99B",
            maxMembers = 12,
            members = listOf(
                LogGroupMember("u5", "Coach Dave", "🏋️", 21, 5, true),
                LogGroupMember("u6", "Aria Stark", "🏃", 18, 3, true),
                LogGroupMember("u7", "Liam O'Connor", "💪", 8, 2, false)
            ),
            adminUid = "u5",
            themeColorHex = 0xFFEF4444
        )

        return listOf(group1, group2)
    }
}
