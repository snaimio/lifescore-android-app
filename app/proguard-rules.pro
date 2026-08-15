# LifeScore ProGuard & R8 Enterprise Optimization Rules

# 1. Room Database
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
-keep class com.lifescore.app.data.local.entity.** { *; }
-keep class com.lifescore.app.core.database.** { *; }

# 2. Google Play Billing
-keep class com.android.billingclient.api.** { *; }
-keep class com.lifescore.app.core.billing.** { *; }

# 3. Google Generative AI (Gemini)
-keep class com.google.ai.client.generativeai.** { *; }
-dontwarn com.google.ai.client.generativeai.**

# 4. Firebase Firestore & Auth
-keep class com.google.firebase.firestore.** { *; }
-keep class com.google.firebase.auth.** { *; }
-keep class com.lifescore.app.data.remote.model.** { *; }

# 5. WorkManager & Jetpack Glance Widgets
-keep class * extends androidx.work.Worker { *; }
-keep class * extends androidx.work.ListenableWorker { *; }
-keep class * extends androidx.glance.appwidget.GlanceAppWidget { *; }
-keep class * extends androidx.glance.appwidget.GlanceAppWidgetReceiver { *; }

# 6. CameraX
-keep class androidx.camera.core.** { *; }
-keep class androidx.camera.camera2.** { *; }
-keep class androidx.camera.view.** { *; }

# 7. Kotlin Coroutines & Flow
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# 8. Jetpack Compose
-keepclassmembers class * extends androidx.compose.runtime.** { *; }
-keep class androidx.compose.material.icons.** { *; }

# 9. Domain Models
-keep class com.lifescore.app.domain.model.** { *; }

# 10. Native JNI & 16 KB Page Size Compatibility
-keep class * {
    native <methods>;
}
-keepclasseswithmembernames class * {
    native <methods>;
}

# 11. Ktor / Gemini AI R8 Rules
-dontwarn java.lang.management.**
-dontwarn io.ktor.**
