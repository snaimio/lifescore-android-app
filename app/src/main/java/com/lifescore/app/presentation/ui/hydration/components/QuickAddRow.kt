package com.lifescore.app.presentation.ui.hydration.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.core.designsystem.Spacing

@Composable
fun QuickAddRow(
    onAddSmall: () -> Unit,
    onAddMedium: () -> Unit,
    onAddLarge: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        OutlinedButton(
            onClick = onAddSmall,
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
            modifier = Modifier.weight(1f)
        ) {
            Text("☕ 150ml", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        OutlinedButton(
            onClick = onAddMedium,
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
            modifier = Modifier.weight(1f)
        ) {
            Text("🥤 250ml", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        OutlinedButton(
            onClick = onAddLarge,
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
            modifier = Modifier.weight(1f)
        ) {
            Text("🫙 500ml", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}
