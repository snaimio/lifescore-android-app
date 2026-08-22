package com.lifescore.app.presentation.widget

import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.lifescore.app.MainActivity

class LifeScoreWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .padding(14.dp)
                        .background(GlanceTheme.colors.surface)
                        .cornerRadius(18.dp),
                    verticalAlignment = Alignment.Vertical.CenterVertically,
                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                ) {
                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Vertical.CenterVertically,
                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                    ) {
                        Text(
                            text = "⚡ LifeScore OS",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = GlanceTheme.colors.primary
                            )
                        )
                        Spacer(modifier = GlanceModifier.width(8.dp))
                        Text(
                            text = "🔥 7d Streak",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = GlanceTheme.colors.onSurfaceVariant
                            )
                        )
                    }

                    Spacer(modifier = GlanceModifier.height(6.dp))

                    Text(
                        text = "Score: 780 / 1000",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = GlanceTheme.colors.onSurface
                        )
                    )

                    Spacer(modifier = GlanceModifier.height(6.dp))

                    Text(
                        text = "Next Quest: Morning Sunlight (15m)",
                        style = TextStyle(
                            fontWeight = FontWeight.Normal,
                            fontSize = 11.sp,
                            color = GlanceTheme.colors.onSurfaceVariant
                        )
                    )

                    Spacer(modifier = GlanceModifier.height(8.dp))

                    Button(
                        text = "🚀 Open LifeScore",
                        onClick = actionStartActivity<MainActivity>(),
                        modifier = GlanceModifier.fillMaxWidth().height(36.dp)
                    )
                }
            }
        }
    }
}

class LifeScoreWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = LifeScoreWidget()
}
