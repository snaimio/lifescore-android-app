package com.lifescore.app.core.engine

class FourLawsSystem {

    // Law 1: Make It Obvious (Habit Stacking & Implementation Intentions)
    fun createHabitStack(existingHabit: String, newHabit: String): String {
        return "After I $existingHabit, I will $newHabit."
    }

    fun getHabitStackExamples(): List<String> {
        return listOf(
            "After I pour my morning coffee, I will meditate for 1 minute.",
            "After I take off my work shoes, I will change into workout clothes.",
            "After I sit down for dinner, I will state one thing I am grateful for.",
            "After I get into bed, I will read 1 page of non-fiction."
        )
    }

    // Law 2: Make It Attractive (Temptation Bundling)
    fun createTemptationBundling(desiredHabit: String, guiltyPleasure: String): String {
        return "Only while I $guiltyPleasure, will I $desiredHabit."
    }

    fun getTemptationBundlingExamples(): List<String> {
        return listOf(
            "Only listen to my favorite podcast while doing cardio.",
            "Only watch Netflix while folding the laundry.",
            "Only get a premium iced latte while reviewing my weekly budget."
        )
    }

    // Law 3: Make It Easy (The 2-Minute Rule)
    fun createTwoMinuteRule(bigHabit: String): String {
        return "Start by doing just 2 minutes of $bigHabit."
    }

    fun getTwoMinuteExamples(): List<Pair<String, String>> {
        return listOf(
            "Become a reader" to "Read 1 page every night",
            "Exercise 5x a week" to "Tie my running shoes and do 1 pushup",
            "Study for 3 hours" to "Open my textbook and read one sentence",
            "Write a novel" to "Write one paragraph each morning",
            "Practice deep stillness" to "Close my eyes for 1 minute"
        )
    }

    // Law 4: Make It Satisfying (Immediate Rewards & Habit Tracking)
    fun getImmediateRewards(): List<String> {
        return listOf(
            "🎯 Check off your habit on the visual tracker",
            "✨ Earn +25 XP and level up your character",
            "🔥 Extend your active streak with fire badges",
            "🏆 Unlock behavior transformation milestones",
            "📊 Watch your habit scorecard balance turn positive"
        )
    }
}
