package com.venus.meditrace.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

val BgDark       = Color(0xFF0D1F0D)
val GreenPrimary = Color(0xFF1D9E75)
val GreenDark    = Color(0xFF0F6E56)
val FieldBg      = Color(0xFF1A2E1A)
val FieldStroke  = Color(0xFF2E4D2E)
val TextWhite    = Color(0xFFFFFFFF)
val TextMuted    = Color(0xFF8FAF8F)

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onLoginSuccess()
            viewModel.resetState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            Text(
                text = "MediTrace",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = GreenPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Verify your medicine. Stay safe.",
                fontSize = 14.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(56.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; emailError = null },
                label = { Text("Email address", color = TextMuted) },
                singleLine = true,
                isError = emailError != null,
                supportingText = emailError?.let { { Text(it, color = Color(0xFFFF6B6B)) } },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                colors = authFieldColors(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it; passwordError = null },
                label = { Text("Password", color = TextMuted) },
                singleLine = true,
                isError = passwordError != null,
                supportingText = passwordError?.let { { Text(it, color = Color(0xFFFF6B6B)) } },
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = TextMuted
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                colors = authFieldColors(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            if (uiState is AuthUiState.Error) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = (uiState as AuthUiState.Error).message,
                    color = Color(0xFFFF6B6B),
                    fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    focusManager.clearFocus()
                    if (validate(email, password,
                            onEmailError    = { emailError = it },
                            onPasswordError = { passwordError = it })) {
                        viewModel.login(email.trim(), password)
                    }
                },
                enabled = uiState !is AuthUiState.Loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenPrimary,
                    contentColor   = TextWhite
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                if (uiState is AuthUiState.Loading) {
                    CircularProgressIndicator(
                        color = TextWhite,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Log in", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Don't have an account? ",
                    color = TextMuted,
                    fontSize = 14.sp
                )
                TextButton(
                    onClick = onNavigateToRegister,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Register",
                        color = GreenPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf("patient") }
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onRegisterSuccess()
            viewModel.resetState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp)
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = "Create account",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Join MediTrace to verify your medicines",
                fontSize = 14.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(36.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it; nameError = null },
                label = { Text("Full name", color = TextMuted) },
                singleLine = true,
                isError = nameError != null,
                supportingText = nameError?.let { { Text(it, color = Color(0xFFFF6B6B)) } },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                colors = authFieldColors(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; emailError = null },
                label = { Text("Email address", color = TextMuted) },
                singleLine = true,
                isError = emailError != null,
                supportingText = emailError?.let { { Text(it, color = Color(0xFFFF6B6B)) } },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                colors = authFieldColors(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it; passwordError = null },
                label = { Text("Password (min. 8 characters)", color = TextMuted) },
                singleLine = true,
                isError = passwordError != null,
                supportingText = passwordError?.let { { Text(it, color = Color(0xFFFF6B6B)) } },
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = TextMuted
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                colors = authFieldColors(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text("I am a...", fontSize = 14.sp, color = TextMuted)
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf("patient", "pharmacist").forEach { role ->
                    val selected = selectedRole == role
                    FilterChip(
                        selected = selected,
                        onClick  = { selectedRole = role },
                        label    = {
                            Text(
                                text = role.replaceFirstChar { it.uppercase() },
                                color = if (selected) TextWhite else TextMuted,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GreenPrimary,
                            containerColor         = FieldBg,
                            labelColor             = TextMuted,
                            selectedLabelColor     = TextWhite,
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (selected) GreenPrimary else FieldStroke
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (uiState is AuthUiState.Error) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = (uiState as AuthUiState.Error).message,
                    color = Color(0xFFFF6B6B),
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    focusManager.clearFocus()
                    if (validateRegister(name, email, password,
                            onNameError     = { nameError = it },
                            onEmailError    = { emailError = it },
                            onPasswordError = { passwordError = it })) {
                        viewModel.register(name.trim(), email.trim(), password, selectedRole)
                    }
                },
                enabled = uiState !is AuthUiState.Loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenPrimary,
                    contentColor   = TextWhite
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                if (uiState is AuthUiState.Loading) {
                    CircularProgressIndicator(
                        color = TextWhite,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Create account", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Already have an account? ", color = TextMuted, fontSize = 14.sp)
                TextButton(
                    onClick = onNavigateToLogin,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Log in",
                        color = GreenPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor      = GreenPrimary,
    unfocusedBorderColor    = FieldStroke,
    focusedLabelColor       = GreenPrimary,
    unfocusedLabelColor     = TextMuted,
    focusedTextColor        = TextWhite,
    unfocusedTextColor      = TextWhite,
    cursorColor             = GreenPrimary,
    focusedContainerColor   = FieldBg,
    unfocusedContainerColor = FieldBg,
)

private fun validate(
    email: String,
    password: String,
    onEmailError: (String) -> Unit,
    onPasswordError: (String) -> Unit
): Boolean {
    var valid = true
    if (email.isBlank()) { onEmailError("Email is required"); valid = false }
    else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        onEmailError("Enter a valid email"); valid = false
    }
    if (password.isBlank()) { onPasswordError("Password is required"); valid = false }
    return valid
}

private fun validateRegister(
    name: String,
    email: String,
    password: String,
    onNameError: (String) -> Unit,
    onEmailError: (String) -> Unit,
    onPasswordError: (String) -> Unit
): Boolean {
    var valid = true
    if (name.isBlank()) { onNameError("Name is required"); valid = false }
    if (email.isBlank()) { onEmailError("Email is required"); valid = false }
    else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        onEmailError("Enter a valid email"); valid = false
    }
    if (password.length < 8) { onPasswordError("Min. 8 characters"); valid = false }
    else if (!password.any { it.isUpperCase() }) { onPasswordError("Needs an uppercase letter"); valid = false }
    else if (!password.any { it.isDigit() }) { onPasswordError("Needs a number"); valid = false }
    return valid
}