package com.lifescore.app.core.designsystem.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lifescore.app.R
import com.lifescore.app.domain.model.DimensionType

enum class LifeIcons(
    @DrawableRes val resId: Int,
    val contentDescription: String
) {
    Check(R.drawable.ic_check_custom, "Complete"),
    Streak(R.drawable.ic_flame_streak, "Streak"),
    Hydration(R.drawable.ic_hydration, "Hydration"),
    Sleep(R.drawable.ic_sleep, "Sleep"),
    Meditation(R.drawable.ic_meditation, "Meditation"),
    MoodHappy(R.drawable.ic_mood_happy, "Happy Mood"),
    MoodSad(R.drawable.ic_mood_sad, "Thoughtful Mood"),
    Energy(R.drawable.ic_energy, "Energy"),
    Reading(R.drawable.ic_reading, "Reading"),
    Goal(R.drawable.ic_goal, "Goal"),
    Trophy(R.drawable.ic_trophy, "Achievements"),
    Run(R.drawable.ic_run, "Fitness"),
    Wealth(R.drawable.ic_wealth, "Wealth"),
    Career(R.drawable.ic_career, "Career"),
    Relationships(R.drawable.ic_relationships, "Relationships"),
    Social(R.drawable.ic_social, "Social"),
    Mental(R.drawable.ic_mental, "Mental Health"),
    Health(R.drawable.ic_health, "Health"),
    Star(R.drawable.ic_star, "Star"),
    Rocket(R.drawable.ic_rocket, "Launch"),
    Profile(R.drawable.ic_profile, "Profile"),
    Settings(R.drawable.ic_settings, "Settings"),
    Notification(R.drawable.ic_notification, "Notifications"),
    Analytics(R.drawable.ic_analytics, "Analytics"),
    Theme(R.drawable.ic_theme, "Theme"),
    Calendar(R.drawable.ic_calendar, "Calendar"),
    Search(R.drawable.ic_search, "Search"),
    Add(R.drawable.ic_add, "Add"),
    Remove(R.drawable.ic_remove, "Remove"),
    Back(R.drawable.ic_back, "Back"),
    Forward(R.drawable.ic_forward, "Forward"),

    // Dimension specific
    DimHealth(R.drawable.ic_dimension_health, "Health Dimension"),
    DimWealth(R.drawable.ic_dimension_wealth, "Wealth Dimension"),
    DimRelationships(R.drawable.ic_dimension_relationships, "Relationships Dimension"),
    DimCareer(R.drawable.ic_dimension_career, "Career Dimension"),
    DimLearning(R.drawable.ic_dimension_learning, "Learning Dimension"),
    DimFitness(R.drawable.ic_dimension_fitness, "Fitness Dimension"),
    DimMental(R.drawable.ic_dimension_mental, "Mental Health Dimension"),
    DimSocial(R.drawable.ic_dimension_social, "Social Life Dimension");

    companion object {
        fun forDimension(dim: DimensionType): LifeIcons = when (dim) {
            DimensionType.HEALTH -> DimHealth
            DimensionType.WEALTH -> DimWealth
            DimensionType.RELATIONSHIPS -> DimRelationships
            DimensionType.CAREER -> DimCareer
            DimensionType.LEARNING -> DimLearning
            DimensionType.FITNESS -> DimFitness
            DimensionType.MENTAL_HEALTH -> DimMental
            DimensionType.SOCIAL_LIFE -> DimSocial
        }
    }
}

enum class LifeIllustrations(
    @DrawableRes val resId: Int,
    val contentDescription: String
) {
    EmptyHabits(R.drawable.ill_empty_habits, "Empty Habits Illustration"),
    EmptyTrackers(R.drawable.ill_empty_trackers, "Empty Trackers Illustration"),
    EmptyJournal(R.drawable.ill_empty_journal, "Empty Journal Illustration"),
    EmptySocial(R.drawable.ill_empty_social, "Empty Social Feed Illustration"),
    EmptyProgress(R.drawable.ill_empty_progress, "Empty Progress Illustration"),
    EmptyMeditation(R.drawable.ill_empty_meditation, "Empty Meditation Illustration"),
    EmptySleep(R.drawable.ill_empty_sleep, "Empty Sleep Illustration")
}

@Composable
fun LifeIcon(
    icon: LifeIcons,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color? = null
) {
    if (tint != null) {
        Icon(
            painter = painterResource(icon.resId),
            contentDescription = icon.contentDescription,
            tint = tint,
            modifier = modifier.size(size)
        )
    } else {
        // Render with original vector asset stroke/colors
        Icon(
            painter = painterResource(icon.resId),
            contentDescription = icon.contentDescription,
            tint = Color.Unspecified,
            modifier = modifier.size(size)
        )
    }
}

@Composable
fun LifeIllustration(
    illustration: LifeIllustrations,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp
) {
    Image(
        painter = painterResource(illustration.resId),
        contentDescription = illustration.contentDescription,
        modifier = modifier.size(size)
    )
}
