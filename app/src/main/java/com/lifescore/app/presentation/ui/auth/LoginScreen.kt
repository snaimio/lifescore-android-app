package com.lifescore.app.presentation.ui.auth

import androidx.compose.animation.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.R
import com.lifescore.app.core.designsystem.Space

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToHome: () -> Unit,
    onBack: (() -> Unit)? = null,
    initialIsSignUp: Boolean = false
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    LaunchedEffect(initialIsSignUp) {
        if (initialIsSignUp && !uiState.isSignUp) {
            viewModel.toggleAuthMode()
        }
    }

    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val backgroundColor = if (isDark) Color(0xFF0C0B12) else MaterialTheme.colorScheme.background
    val textPrimary = if (isDark) Color(0xFFFBF8F3) else Color(0xFF19181F)
    val textSecondary = if (isDark) Color(0xFF9E958B) else Color(0xFF6B6357)
    val cardBackground = if (isDark) Color(0xFF14131E) else Color(0xFFFFFFFF)
    val cardBorder = if (isDark) Color(0x1FD4A24C) else Color(0x33D4A24C)
    val switcherBg = if (isDark) Color(0xFF1A1926) else Color(0xFFF2EFE9)
    val inputBg = if (isDark) Color(0xFF0F0E16) else Color(0xFFFAFAF7)
    val inputBorder = if (isDark) Color(0x22FFFFFF) else Color(0x1A000000)

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            if (onBack != null) {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = textPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (isDark) {
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0C0B12),
                                Color(0xFF14131E)
                            )
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFF9F8F5),
                                Color(0xFFF0EDE4)
                            )
                        )
                    }
                )
                .padding(padding)
                .verticalScroll(scrollState),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Space.screenH)
                    .padding(bottom = Space.xxxl),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(Space.md))

                // Brand Emblem
                Surface(
                    shape = CircleShape,
                    color = Color(0x22D4A24C),
                    border = BorderStroke(1.dp, Color(0x44D4A24C)),
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(R.drawable.lifescore_logo),
                            contentDescription = "LifeScore Brand Mark",
                            modifier = Modifier.size(54.dp)
                        )
                    }
                }

                Spacer(Modifier.height(Space.md))

                // Title & Subtitle
                Text(
                    text = "LifeScore",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = textPrimary
                )

                Text(
                    text = if (uiState.isSignUp) "Create your account to sync and secure your life metrics" else "Sign in to access your continuous life metrics",
                    style = MaterialTheme.typography.bodySmall,
                    color = textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .padding(top = 4.dp, bottom = Space.lg)
                )

                // Sign In / Create Account Tab Switcher
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = switcherBg,
                    border = BorderStroke(1.dp, cardBorder),
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

                Spacer(Modifier.height(Space.lg))

                // Form Container
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = cardBackground,
                    border = BorderStroke(1.dp, cardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Space.lg)
                    ) {
                        // Display Name (for Sign Up only)
                        AnimatedVisibility(
                            visible = uiState.isSignUp,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column {
                                Text(
                                    text = "Full Name",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFD4A24C)
                                )
                                Spacer(Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = uiState.displayName,
                                    onValueChange = { viewModel.onDisplayNameChange(it) },
                                    placeholder = { Text("Enter your full name", color = Color(0xFF8A8275), fontSize = 14.sp) },
                                    leadingIcon = {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFFD4A24C))
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFFD4A24C),
                                        unfocusedBorderColor = inputBorder,
                                        focusedTextColor = textPrimary,
                                        unfocusedTextColor = textPrimary,
                                        focusedContainerColor = inputBg,
                                        unfocusedContainerColor = inputBg
                                    ),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(Modifier.height(Space.md))
                            }
                        }

                        // Email Field
                        Text(
                            text = "Email Address",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFD4A24C)
                        )
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = uiState.email,
                            onValueChange = { viewModel.onEmailChange(it) },
                            placeholder = { Text("name@domain.com", color = Color(0xFF8A8275), fontSize = 14.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFFD4A24C))
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFD4A24C),
                                unfocusedBorderColor = inputBorder,
                                focusedTextColor = textPrimary,
                                unfocusedTextColor = textPrimary,
                                focusedContainerColor = inputBg,
                                unfocusedContainerColor = inputBg
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(Space.md))

                        // Password Field
                        Text(
                            text = "Password",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFD4A24C)
                        )
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = uiState.password,
                            onValueChange = { viewModel.onPasswordChange(it) },
                            placeholder = { Text("••••••••", color = Color(0xFF8A8275), fontSize = 14.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFD4A24C))
                            },
                            trailingIcon = {
                                IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                                    Icon(
                                        if (uiState.isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle password",
                                        tint = textSecondary
                                    )
                                }
                            },
                            visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFD4A24C),
                                unfocusedBorderColor = inputBorder,
                                focusedTextColor = textPrimary,
                                unfocusedTextColor = textPrimary,
                                focusedContainerColor = inputBg,
                                unfocusedContainerColor = inputBg
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                focusManager.clearFocus()
                                viewModel.submitEmailAuth { onNavigateToHome() }
                            }),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Error Banner
                        AnimatedVisibility(
                            visible = uiState.errorMessage != null,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = Space.sm)
                            ) {
                                Text(
                                    text = uiState.errorMessage ?: "",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(Space.lg))

                        // Submit Button
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.submitEmailAuth { onNavigateToHome() }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFD4A24C),
                                contentColor = Color(0xFF0C0B12)
                            ),
                            enabled = !uiState.isLoading
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color(0xFF0C0B12), strokeWidth = 2.dp)
                            } else {
                                Text(
                                    text = if (uiState.isSignUp) "Create Account" else "Sign In",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(Space.md))

                // Guest Mode Card / Button
                Surface(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.continueAsGuest { onNavigateToHome() }
                    },
                    shape = RoundedCornerShape(16.dp),
                    color = cardBackground,
                    border = BorderStroke(1.dp, cardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Space.md, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Space.sm)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0x22D4A24C),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.AccountCircle,
                                        contentDescription = null,
                                        tint = Color(0xFFD4A24C),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = "Continue as Guest",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = textPrimary
                                )
                                Text(
                                    text = "Explore features without signing in",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = textSecondary
                                )
                            }
                        }

                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color(0xFFD4A24C),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(Modifier.height(Space.lg))

                // Encrypted notice footer
                Text(
                    text = "LifeScore Executive Life Operating System • Local & Cloud Encrypted",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = textSecondary,
                        textAlign = TextAlign.Center,
                        fontSize = 11.sp
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
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
        targetValue = if (isSelected) Color(0xFFD4A24C) else Color.Transparent,
        label = "TabBackground"
    )
    val contentColor = if (isSelected) Color(0xFF0C0B12) else Color(0xFF9E958B)
    val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = backgroundColor,
        modifier = modifier.height(40.dp)
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
