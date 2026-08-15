package com.lifescore.app.core.util

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import androidx.core.content.FileProvider
import com.lifescore.app.domain.model.DimensionType
import java.io.File
import java.io.FileOutputStream
import kotlin.math.min

enum class CardTheme(
    val displayName: String,
    val topColor: Int,
    val bottomColor: Int,
    val accentColor: Int,
    val cardBgColor: Int
) {
    COSMIC_NIGHT(
        "Cosmic Night",
        0xFF0F172A.toInt(), // Deep Slate Navy
        0xFF1E1B4B.toInt(), // Midnight Indigo
        0xFF818CF8.toInt(), // Vibrant Violet Accent
        0x26FFFFFF          // 15% White overlay
    ),
    CYBER_NEON(
        "Cyber Neon",
        0xFF091E24.toInt(), // Dark Cyan Slate
        0xFF041318.toInt(), // Obsidian Teal
        0xFF2DD4BF.toInt(), // Electric Emerald
        0x26FFFFFF
    ),
    ROYAL_GOLD(
        "Royal Gold",
        0xFF1C1917.toInt(), // Obsidian
        0xFF2A1B05.toInt(), // Dark Bronze Amber
        0xFFFBBF24.toInt(), // Golden Amber
        0x26FFFFFF
    ),
    EMERALD_ZEN(
        "Emerald Zen",
        0xFF06281E.toInt(), // Deep Pine
        0xFF021610.toInt(), // Midnight Emerald
        0xFF34D399.toInt(), // Mint Green
        0x26FFFFFF
    )
}

data class ShareCardData(
    val userName: String,
    val score: Int,
    val level: Int,
    val streak: Int,
    val title: String,
    val dimensionScores: Map<DimensionType, Int>,
    val yearTag: String = "2026"
)

object CardGenerator {

    private const val WIDTH = 1080
    private const val HEIGHT = 1920

    fun generateCardBitmap(data: ShareCardData, theme: CardTheme = CardTheme.COSMIC_NIGHT): Bitmap {
        val bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Background Gradient
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, 0f, 0f, HEIGHT.toFloat(),
                intArrayOf(theme.topColor, theme.bottomColor),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, WIDTH.toFloat(), HEIGHT.toFloat(), bgPaint)

        // Decorative background glowing orbs
        val orbPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                WIDTH * 0.8f, HEIGHT * 0.2f, 450f,
                intArrayOf(theme.accentColor and 0x44FFFFFF, Color.TRANSPARENT),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(WIDTH * 0.8f, HEIGHT * 0.2f, 450f, orbPaint)

        val bottomOrbPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                WIDTH * 0.2f, HEIGHT * 0.85f, 400f,
                intArrayOf(theme.accentColor and 0x33FFFFFF, Color.TRANSPARENT),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(WIDTH * 0.2f, HEIGHT * 0.85f, 400f, bottomOrbPaint)

        // 2. Header App Branding
        val headerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 34f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            letterSpacing = 0.15f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("✨  L I F E S C O R E   R E P O R T  •  ${data.yearTag}", (WIDTH / 2).toFloat(), 130f, headerPaint)

        // Sub-Header Profile Pill
        val userPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = theme.accentColor
            textSize = 42f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("${data.userName}  •  Level ${data.level} ${data.title}", (WIDTH / 2).toFloat(), 200f, userPaint)

        // 3. Central Hero Score Dial
        val centerX = (WIDTH / 2).toFloat()
        val centerY = 510f
        val radius = 220f

        // Score Card Background Container
        val cardRect = RectF(70f, 260f, (WIDTH - 70).toFloat(), 760f)
        val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = theme.cardBgColor
            style = Paint.Style.FILL
        }
        val cardStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0x33FFFFFF
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }
        canvas.drawRoundRect(cardRect, 48f, 48f, cardPaint)
        canvas.drawRoundRect(cardRect, 48f, 48f, cardStrokePaint)

        // Dial Track
        val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0x22FFFFFF
            style = Paint.Style.STROKE
            strokeWidth = 24f
            strokeCap = Paint.Cap.ROUND
        }
        val arcRect = RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
        canvas.drawArc(arcRect, 135f, 270f, false, trackPaint)

        // Dial Active Progress Arc
        val progressSweep = (data.score.coerceIn(0, 1000) / 1000f) * 270f
        val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                centerX - radius, centerY, centerX + radius, centerY,
                intArrayOf(theme.accentColor, Color.WHITE),
                null,
                Shader.TileMode.CLAMP
            )
            style = Paint.Style.STROKE
            strokeWidth = 26f
            strokeCap = Paint.Cap.ROUND
        }
        canvas.drawArc(arcRect, 135f, progressSweep, false, progressPaint)

        // Inside Dial Score Number
        val scoreNumberPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 120f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("${data.score}", centerX, centerY + 35f, scoreNumberPaint)

        val scoreLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xCCFFFFFF.toInt()
            textSize = 28f
            letterSpacing = 0.12f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("OVERALL LIFESCORE INDEX", centerX, centerY + 85f, scoreLabelPaint)

        // Tier / Percentile Pill
        val tier = getScoreTier(data.score)
        val tierPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = theme.accentColor
            textSize = 30f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(tier, centerX, 720f, tierPaint)

        // 4. 8-Dimension Breakdown Grid
        val dimCardRect = RectF(70f, 790f, (WIDTH - 70).toFloat(), 1540f)
        canvas.drawRoundRect(dimCardRect, 48f, 48f, cardPaint)
        canvas.drawRoundRect(dimCardRect, 48f, 48f, cardStrokePaint)

        val dimSectionTitle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 34f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.LEFT
        }
        canvas.drawText("8-DIMENSION EQUILIBRIUM", 110f, 855f, dimSectionTitle)

        // Draw 8 Dimension Progress Bars
        val dimensions = DimensionType.values()
        var startY = 920f
        val colWidth = 410f

        dimensions.forEachIndexed { index, dim ->
            val col = index % 2
            val row = index / 2
            val itemX = if (col == 0) 110f else 560f
            val itemY = startY + (row * 145f)
            val score = data.dimensionScores[dim] ?: 50

            // Dimension Name
            val dimNamePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textSize = 28f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            canvas.drawText(dim.displayName, itemX, itemY, dimNamePaint)

            // Dimension Score
            val dimScorePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = dim.baseColorHex.toInt()
                textSize = 28f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.RIGHT
            }
            canvas.drawText("$score%", itemX + colWidth, itemY, dimScorePaint)

            // Progress Bar Track
            val barY = itemY + 16f
            val barRect = RectF(itemX, barY, itemX + colWidth, barY + 16f)
            val barBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = 0x22FFFFFF
            }
            canvas.drawRoundRect(barRect, 8f, 8f, barBgPaint)

            // Progress Bar Fill
            val fillWidth = (score / 100f) * colWidth
            val fillRect = RectF(itemX, barY, itemX + fillWidth, barY + 16f)
            val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = dim.baseColorHex.toInt()
            }
            canvas.drawRoundRect(fillRect, 8f, 8f, fillPaint)
        }

        // 5. Bottom Callouts: Streak Badge & QR Watermark
        // Streak Pill
        val streakPillRect = RectF(70f, 1570f, (WIDTH - 70).toFloat(), 1660f)
        val streakBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0x33FF9800.toInt()
            style = Paint.Style.FILL
        }
        val streakStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFFF9800.toInt()
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        canvas.drawRoundRect(streakPillRect, 30f, 30f, streakBgPaint)
        canvas.drawRoundRect(streakPillRect, 30f, 30f, streakStroke)

        val streakTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 34f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("🔥  ${data.streak}-DAY ACTIVE STREAK  •  HABIT MASTER", centerX, 1628f, streakTextPaint)

        // Footer QR / Download Code Watermark
        val qrBoxSize = 130f
        val qrLeft = 100f
        val qrTop = 1710f
        drawStylizedQrBox(canvas, qrLeft, qrTop, qrBoxSize, theme.accentColor)

        val footerTitle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 32f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.LEFT
        }
        canvas.drawText("Scan to calculate your LifeScore", qrLeft + qrBoxSize + 30f, qrTop + 50f, footerTitle)

        val footerSub = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xAAFFFFFF.toInt()
            textSize = 24f
            textAlign = Paint.Align.LEFT
        }
        canvas.drawText("Available on Android & iOS • lifescore.app/dl", qrLeft + qrBoxSize + 30f, qrTop + 95f, footerSub)

        return bitmap
    }

    private fun drawStylizedQrBox(canvas: Canvas, left: Float, top: Float, size: Float, accentColor: Int) {
        val rect = RectF(left, top, left + size, top + size)
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
        }
        canvas.drawRoundRect(rect, 16f, 16f, bgPaint)

        val darkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
        }

        // Draw outer QR corner markers
        val cornerSize = size * 0.28f
        // Top-left
        canvas.drawRect(left + 8f, top + 8f, left + 8f + cornerSize, top + 8f + cornerSize, darkPaint)
        // Top-right
        canvas.drawRect(left + size - 8f - cornerSize, top + 8f, left + size - 8f, top + 8f + cornerSize, darkPaint)
        // Bottom-left
        canvas.drawRect(left + 8f, top + size - 8f - cornerSize, left + 8f + cornerSize, top + size - 8f, darkPaint)

        // Inner decorative matrix dots
        val step = size / 6f
        for (i in 1..4) {
            for (j in 1..4) {
                if ((i + j) % 2 == 0) {
                    canvas.drawRect(
                        left + (i * step),
                        top + (j * step),
                        left + (i * step) + (step * 0.7f),
                        top + (j * step) + (step * 0.7f),
                        darkPaint
                    )
                }
            }
        }

        // Central Sparkle Logo
        val sparklePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = accentColor
            textSize = 28f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("✨", left + (size / 2f), top + (size / 2f) + 10f, sparklePaint)
    }

    fun generateArchetypeCardBitmap(data: ArchetypeShareCardData, theme: CardTheme = CardTheme.COSMIC_NIGHT): Bitmap {
        val bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Background Gradient
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, 0f, 0f, HEIGHT.toFloat(),
                intArrayOf(theme.topColor, theme.bottomColor),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, WIDTH.toFloat(), HEIGHT.toFloat(), bgPaint)

        // Glowing Orbs
        val orbPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                WIDTH * 0.5f, HEIGHT * 0.35f, 500f,
                intArrayOf(theme.accentColor and 0x55FFFFFF.toInt(), Color.TRANSPARENT),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(WIDTH * 0.5f, HEIGHT * 0.35f, 500f, orbPaint)

        // 2. Header
        val brandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 42f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("L I F E S C O R E   P E R S O N A", WIDTH / 2f, 150f, brandPaint)

        val subBrandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE and 0x88FFFFFF.toInt()
            textSize = 24f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("OFFICIAL HERO ARCHETYPE CALIBRATION", WIDTH / 2f, 195f, subBrandPaint)

        // 3. Central Card Glass Container
        val cardRect = RectF(80f, 260f, WIDTH - 80f, HEIGHT - 320f)
        val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = theme.cardBgColor
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(cardRect, 48f, 48f, cardPaint)

        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = theme.accentColor and 0x66FFFFFF.toInt()
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }
        canvas.drawRoundRect(cardRect, 48f, 48f, borderPaint)

        // Archetype Icon
        val iconPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 130f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(data.archetype.icon, WIDTH / 2f, 420f, iconPaint)

        // "I AM" Label
        val iAmPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = theme.accentColor
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("I  A M", WIDTH / 2f, 490f, iAmPaint)

        // Archetype Name
        val namePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 64f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(data.archetype.name.uppercase(), WIDTH / 2f, 570f, namePaint)

        // Archetype Subtitle
        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFFFD700.toInt()
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(data.archetype.title, WIDTH / 2f, 620f, titlePaint)

        // Overview Paragraph (split into lines)
        val overviewPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE and 0xCCFFFFFF.toInt()
            textSize = 26f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(data.archetype.overview.take(75) + "...", WIDTH / 2f, 700f, overviewPaint)

        // Divider
        canvas.drawLine(140f, 760f, WIDTH - 140f, 760f, borderPaint)

        // Superpower Box
        val superLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = theme.accentColor
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.LEFT
        }
        canvas.drawText("⚡ CORE SUPERPOWER", 140f, 820f, superLabelPaint)

        val superTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 30f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.LEFT
        }
        canvas.drawText(data.archetype.superpower, 140f, 870f, superTextPaint)

        // Top Tendencies Section
        val tendLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFFFD700.toInt()
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.LEFT
        }
        canvas.drawText("🌟 KEY TENDENCIES", 140f, 960f, tendLabelPaint)

        val tendTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE and 0xEEFFFFFF.toInt()
            textSize = 26f
            textAlign = Paint.Align.LEFT
        }
        var yPos = 1010f
        data.archetype.tendencies.take(3).forEach { tend ->
            canvas.drawText("• $tend", 140f, yPos, tendTextPaint)
            yPos += 55f
        }

        // Stats Box (Score & Level)
        val statsRect = RectF(140f, yPos + 30f, WIDTH - 140f, yPos + 160f)
        val statsPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE and 0x15FFFFFF.toInt()
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(statsRect, 24f, 24f, statsPaint)

        val statValuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 36f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        val statLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE and 0x88FFFFFF.toInt()
            textSize = 20f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("${data.score}/1000", WIDTH * 0.35f, yPos + 95f, statValuePaint)
        canvas.drawText("LIFESCORE", WIDTH * 0.35f, yPos + 135f, statLabelPaint)

        canvas.drawText("Level ${data.level}", WIDTH * 0.65f, yPos + 95f, statValuePaint)
        canvas.drawText("ACHIEVER", WIDTH * 0.65f, yPos + 135f, statLabelPaint)

        // 4. Footer Branding & QR Code Watermark
        val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 30f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("DISCOVER YOUR ARCHETYPE", WIDTH / 2f, HEIGHT - 180f, footerPaint)

        val linkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = theme.accentColor
            textSize = 24f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("lifescore.app/archetype", WIDTH / 2f, HEIGHT - 140f, linkPaint)

        drawStylizedQrBox(canvas, (WIDTH - 200).toFloat(), (HEIGHT - 220).toFloat(), 90f, theme.accentColor)

        return bitmap
    }

    fun generateGraduationCertificateBitmap(
        certificate: com.lifescore.app.domain.model.MasterclassCertificate,
        theme: CardTheme = CardTheme.ROYAL_GOLD
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Background
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, 0f, 0f, HEIGHT.toFloat(),
                intArrayOf(0xFF18181B.toInt(), 0xFF09090B.toInt()),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, WIDTH.toFloat(), HEIGHT.toFloat(), bgPaint)

        // 2. Double Gold Filigree Border
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFFFD700.toInt()
            style = Paint.Style.STROKE
            strokeWidth = 6f
        }
        val innerBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0x88FFD700.toInt()
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        canvas.drawRoundRect(RectF(60f, 60f, WIDTH - 60f, HEIGHT - 60f), 32f, 32f, borderPaint)
        canvas.drawRoundRect(RectF(80f, 80f, WIDTH - 80f, HEIGHT - 80f), 24f, 24f, innerBorderPaint)

        // 3. Header Branding
        val brandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFFFD700.toInt()
            textSize = 36f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("L I F E S C O R E   A C A D E M Y", WIDTH / 2f, 200f, brandPaint)

        val certTitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 52f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("CERTIFICATE OF GRADUATION", WIDTH / 2f, 280f, certTitlePaint)

        val subPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE and 0x99FFFFFF.toInt()
            textSize = 26f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("This official credential certifies that", WIDTH / 2f, 380f, subPaint)

        // 4. Student Name
        val namePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 68f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(certificate.userName, WIDTH / 2f, 480f, namePaint)

        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFFFD700.toInt()
            strokeWidth = 3f
        }
        canvas.drawLine(WIDTH * 0.2f, 520f, WIDTH * 0.8f, 520f, linePaint)

        // 5. Course Completion Text
        canvas.drawText("has successfully completed the 14-day masterclass curriculum:", WIDTH / 2f, 600f, subPaint)

        val coursePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFFFD700.toInt()
            textSize = 40f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(certificate.masterclassTitle, WIDTH / 2f, 680f, coursePaint)

        // 6. Coach Section
        canvas.drawText("under the curriculum design and instruction of", WIDTH / 2f, 780f, subPaint)

        val coachNamePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 38f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(certificate.coachName, WIDTH / 2f, 840f, coachNamePaint)

        val coachTitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE and 0x88FFFFFF.toInt()
            textSize = 24f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(certificate.coachTitle, WIDTH / 2f, 890f, coachTitlePaint)

        // 7. Gold Seal & Ribbon
        val sealPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFFFD700.toInt()
            style = Paint.Style.FILL
        }
        canvas.drawCircle(WIDTH / 2f, 1140f, 90f, sealPaint)

        val sealInnerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF18181B.toInt()
            style = Paint.Style.FILL
        }
        canvas.drawCircle(WIDTH / 2f, 1140f, 75f, sealInnerPaint)

        val sealTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFFFD700.toInt()
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("VERIFIED", WIDTH / 2f, 1135f, sealTextPaint)
        canvas.drawText("14 / 14", WIDTH / 2f, 1165f, sealTextPaint)

        // 8. Credentials & Verification Info
        val dateTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE and 0xCCFFFFFF.toInt()
            textSize = 26f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Issued on ${certificate.completionDate} • ${certificate.dimension.displayName}", WIDTH / 2f, 1340f, dateTextPaint)

        val certIdPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFFFD700.toInt()
            textSize = 24f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Credential ID: ${certificate.certificateId}", WIDTH / 2f, 1390f, certIdPaint)
        canvas.drawText("SHA-256 Hash: ${certificate.verificationHash}", WIDTH / 2f, 1430f, certIdPaint)

        // 9. QR Code Verification Box
        drawStylizedQrBox(canvas, (WIDTH / 2f) - 60f, 1550f, 120f, 0xFFFFD700.toInt())

        val verifyLinkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE and 0x88FFFFFF.toInt()
            textSize = 22f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Scan to verify credential authenticity on LifeScore Cloud", WIDTH / 2f, 1720f, verifyLinkPaint)

        return bitmap
    }

    private fun getScoreTier(score: Int): String {
        return when {
            score >= 900 -> "👑  LEGENDARY BALANCE • TOP 1%"
            score >= 750 -> "⚡  ELITE ACHIEVER • TOP 5%"
            score >= 600 -> "🌟  HIGH PERFORMER • TOP 15%"
            score >= 450 -> "🚀  RISING HERO • COMMITTED"
            else -> "🌱  FOUNDATION SEEKER • LEVELING UP"
        }
    }

    fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri {
        val shareDir = File(context.cacheDir, "share_cards").apply { mkdirs() }
        val shareFile = File(shareDir, "lifescore_share_${System.currentTimeMillis()}.png")
        FileOutputStream(shareFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            shareFile
        )
    }

    fun generateCaption(data: ShareCardData): String {
        return "My LifeScore is ${data.score}/1000 across 8 life dimensions! 🔥 ${data.streak}-day streak active. Download LifeScore and level up your life: https://lifescore.app/dl #LifeScore #LifeBalance #GamifyYourLife"
    }

    fun createShareIntent(context: Context, imageUri: Uri, caption: String, targetPackage: String? = null): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, imageUri)
            putExtra(Intent.EXTRA_TEXT, caption)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (!targetPackage.isNullOrBlank()) {
                setPackage(targetPackage)
            }
        }
    }
}

data class ArchetypeShareCardData(
    val userName: String,
    val score: Int,
    val level: Int,
    val archetype: DetailedArchetype
)
