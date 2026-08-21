package com.lifescore.app.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

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

    // Add image sharing support
    fun shareLifeScoreCard(
        context: Context,
        score: Int,
        level: Int,
        streakDays: Int,
        archetype: String,
        imageBitmap: Bitmap
    ) {
        val imageUri = saveImageToCache(context, imageBitmap)
        val shareText = "🏆 My LifeScore is $score/1000 (Level $level $archetype)! Active Streak: $streakDays days 🔥"

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            putExtra(Intent.EXTRA_STREAM, imageUri)
            type = "image/png"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Your Journey"))
    }

    // Add share with both text and image
    fun shareWithImageAndText(
        context: Context,
        text: String,
        imageBitmap: Bitmap
    ) {
        val imageUri = saveImageToCache(context, imageBitmap)
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_STREAM, imageUri)
            type = "image/png"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share"))
    }

    // Helper to save image to cache
    private fun saveImageToCache(context: Context, bitmap: Bitmap): Uri {
        val cachePath = File(context.cacheDir, "images")
        if (!cachePath.exists()) {
            cachePath.mkdirs()
        }
        val file = File(cachePath, "share_image_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
}
