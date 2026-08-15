package com.lifescore.app.presentation.ui.auth

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.domain.model.UserProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToHome: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))

                // Hero Logo Badge
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(84.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("✨", fontSize = 42.sp)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // App Title & Tagline
                Text(
                    text = "LifeScore",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )

                Text(
                    text = "Gamify your life across 8 dimensions",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                )

                if (uiState.isOffline) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = "⚡ Offline-First Mode",
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    Spacer(Modifier.height(16.dp))
                }

                // Sign In / Sign Up Mode Switcher
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        TabButton(
                            text = "Sign In",
                            isSelected = !uiState.isSignUp,
                            onClick = { if (uiState.isSignUp) viewModel.toggleAuthMode() },
                            modifier = Modifier.weight(1f)
                        )
                        TabButton(
                            text = "Create Account",
                            isSelected = uiState.isSignUp,
                            onClick = { if (!uiState.isSignUp) viewModel.toggleAuthMode() },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Display Name Field (Sign Up only)
                AnimatedVisibility(
                    visible = uiState.isSignUp,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column {
                        OutlinedTextField(
                            value = uiState.displayName,
                            onValueChange = { viewModel.onDisplayNameChange(it) },
                            label = { Text("Display Name / Hero Alias") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                }

                // Email Field
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                // Password Field with Visibility Toggle
                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                            Icon(
                                if (uiState.isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle password"
                            )
                        }
                    },
                    visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                        viewModel.submitEmailAuth { onNavigateToHome() }
                    }),
                    modifier = Modifier.fillMaxWidth()
                )

                // Error Message Display
                AnimatedVisibility(visible = uiState.errorMessage != null) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    ) {
                        Text(
                            text = uiState.errorMessage ?: "",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Primary Submit Button (Email / Password)
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.submitEmailAuth { onNavigateToHome() }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text(
                            text = if (uiState.isSignUp) "Create My Account" else "Sign In with Email",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Divider OR
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f))
                    Text(
                        text = "  OR CONNECT WITH  ",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.outline
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f))
                }

                Spacer(Modifier.height(10.dp))

                // Option 2: Google Sign-In Button
                OutlinedButton(
                    onClick = {
                        focusManager.clearFocus()
                        // Demonstrates 1-tap Google Authentication via token or web fallback
                        viewModel.signInWithGoogleToken("sample_google_auth_token_mock") { onNavigateToHome() }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !uiState.isLoading
                ) {
                    Text("🌐", fontSize = 18.sp)
                    Spacer(Modifier.width(10.dp))
                    Text("Continue with Google", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }

                Spacer(Modifier.height(10.dp))

                // Option 3: 1-Tap Guest Mode Button (Anonymous Login)
                FilledTonalButton(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.continueAsGuest { onNavigateToHome() }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !uiState.isLoading
                ) {
                    Icon(Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("1-Tap Guest Onboarding", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }

                Spacer(Modifier.height(18.dp))

                // Legal Footer
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "By continuing, you agree to our ",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "Terms",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://lifescore-app.web.app/terms"))
                            try { context.startActivity(intent) } catch (e: Exception) {}
                        }
                    )
                    Text(
                        text = " & ",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "Privacy Policy",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://lifescore-app.web.app/privacy"))
                            try { context.startActivity(intent) } catch (e: Exception) {}
                        }
                    )
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
        modifier = modifier.height(42.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
        }
    }
}
