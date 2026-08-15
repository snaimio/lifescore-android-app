package com.lifescore.app.utils

import android.content.Context
import android.content.Intent

object ShareHelper {

    fun shareLifeScoreProgress(
        context: Context,
        score: Int,
        level: Int,
        streakDays: Int,
        archetype: String
    ) {
        val shareText = """
            🏆 My LifeScore is $score/1000 (Level $level $archetype)!
            🔥 Active Streak: $streakDays Days of balanced self-improvement.
            
            Can you beat my score? Download LifeScore and join my journey!
            📲 Get the app: https://play.google.com/store/apps/details?id=com.lifescore.app
        """.trimIndent()

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share My LifeScore Journey")
        context.startActivity(shareIntent)
    }

    fun shareReferralCode(
        context: Context,
        referralCode: String
    ) {
        val shareText = """
            🎁 Use my referral code [$referralCode] to unlock 1 Month of LifeScore Pro for free!
            Track your habits across 8 life dimensions with AI coaching:
            https://play.google.com/store/apps/details?id=com.lifescore.app?ref=$referralCode
        """.trimIndent()

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Invite Friends to LifeScore")
        context.startActivity(shareIntent)
    }
}
