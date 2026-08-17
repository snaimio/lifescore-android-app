package com.lifescore.app.presentation.ui.auth

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.domain.model.UserProfile
import kotlinx.coroutines.delay

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

    var logoVisible by remember { mutableStateOf(false) }
    var titleVisible by remember { mutableStateOf(false) }
    var tabsVisible by remember { mutableStateOf(false) }
    var formsVisible by remember { mutableStateOf(false) }
    var buttonsVisible by remember { mutableStateOf(false) }

    var logoScale by remember { mutableFloatStateOf(0.95f) }
    val animatedScale by animateFloatAsState(
        targetValue = logoScale,
        animationSpec = tween(800, easing = EaseOutBack),
        label = "LogoScale"
    )

    LaunchedEffect(Unit) {
        logoVisible = true
        logoScale = 1.05f
        delay(200)
        titleVisible = true
        delay(200)
        tabsVisible = true
        delay(200)
        formsVisible = true
        delay(200)
        buttonsVisible = true
    }

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.08f)
                        )
                    )
                )
                .padding(padding)
                .verticalScroll(scrollState),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = com.lifescore.app.core.designsystem.Spacing.responsiveHorizontalPadding(), vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))

                // Hero Logo Badge
                AnimatedVisibility(
                    visible = logoVisible,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { 20 })
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier
                            .size(96.dp)
                            .graphicsLayer {
                                scaleX = animatedScale
                                scaleY = animatedScale
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("✨", fontSize = 48.sp)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // App Title & Tagline
                AnimatedVisibility(
                    visible = titleVisible,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { 20 })
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "LifeScore",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onBackground,
                            letterSpacing = 0.5.sp
                        )

                        Text(
                            text = "Gamify your life across 8 dimensions",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                        )
                    }
                }

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
                AnimatedVisibility(
                    visible = tabsVisible,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { 20 })
                ) {
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
                }

                Spacer(Modifier.height(20.dp))

                // Form Fields
                AnimatedVisibility(
                    visible = formsVisible,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { 20 })
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                                    shape = MaterialTheme.shapes.medium,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                    ),
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
                            shape = MaterialTheme.shapes.medium,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            ),
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
                            shape = MaterialTheme.shapes.medium,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                focusManager.clearFocus()
                                viewModel.submitEmailAuth { onNavigateToHome() }
                            }),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Error Message Display
                AnimatedVisibility(
                    visible = uiState.errorMessage != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
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

                // Buttons Group
                AnimatedVisibility(
                    visible = buttonsVisible,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { 20 })
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Primary Submit Button (Email / Password)
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.submitEmailAuth { onNavigateToHome() }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = MaterialTheme.shapes.medium,
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 0.dp),
                            enabled = !uiState.isLoading
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
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
                                .height(56.dp),
                            shape = MaterialTheme.shapes.medium,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outlineVariant
                            ),
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
                                .height(56.dp),
                            shape = MaterialTheme.shapes.medium,
                            enabled = !uiState.isLoading
                        ) {
                            Icon(Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(10.dp))
                            Text("1-Tap Guest Onboarding", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }

                        Spacer(Modifier.height(18.dp))

                        // Legal Footer with proper wrapping
                        val annotatedLegalText = androidx.compose.ui.text.buildAnnotatedString {
                            append("By continuing, you agree to our ")
                            pushStringAnnotation(tag = "TERMS", annotation = "https://lifescore-app.web.app/terms")
                            withStyle(
                                style = androidx.compose.ui.text.SpanStyle(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("Terms")
                            }
                            pop()
                            append(" & ")
                            pushStringAnnotation(tag = "PRIVACY", annotation = "https://lifescore-app.web.app/privacy")
                            withStyle(
                                style = androidx.compose.ui.text.SpanStyle(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("Privacy Policy")
                            }
                            pop()
                        }

                        androidx.compose.foundation.text.ClickableText(
                            text = annotatedLegalText,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                            onClick = { offset ->
                                annotatedLegalText.getStringAnnotations(tag = "TERMS", start = offset, end = offset)
                                    .firstOrNull()?.let { annotation ->
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item))
                                        try { context.startActivity(intent) } catch (e: Exception) {}
                                    }
                                annotatedLegalText.getStringAnnotations(tag = "PRIVACY", start = offset, end = offset)
                                    .firstOrNull()?.let { annotation ->
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item))
                                        try { context.startActivity(intent) } catch (e: Exception) {}
                                    }
                            }
                        )

                        Spacer(Modifier.height(24.dp))
                    }
                }
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
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
        label = "TabBackground"
    )
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        modifier = modifier.height(42.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontWeight = fontWeight,
                color = contentColor,
                fontSize = 13.sp
            )
        }
    }
}
