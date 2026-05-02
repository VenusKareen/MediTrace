package com.venus.meditrace.ui.screens.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venus.meditrace.ui.components.MediTraceTopBar
import com.venus.meditrace.ui.theme.*
import com.venus.meditrace.viewmodel.ReportViewModel

@Composable
fun ReportProductScreen(
    viewModel: ReportViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var pharmacyName   by remember { mutableStateOf("") }
    var location       by remember { mutableStateOf("") }
    var medicationName by remember { mutableStateOf("") }

    // Navigate to home on success
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Top Bar ───────────────────────────────────────────────────────
        MediTraceTopBar(
            title    = "Report Suspicious Product",
            showBack = true,
            onBack   = onBack
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            // ── Intro text ────────────────────────────────────────────────
            Text(
                text       = "Your input is vital to public safety. Reporting " +
                        "counterfeit or unauthorized medication helps us secure " +
                        "the pharmaceutical supply chain.",
                color      = TextGray,
                fontSize   = 14.sp,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text       = "Please provide the following details:",
                color      = MediDarkGreen,
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Input fields ──────────────────────────────────────────────
            ReportField(
                value         = pharmacyName,
                onValueChange = { pharmacyName = it },
                placeholder   = "Pharmacy Name",
                imeAction     = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(12.dp))

            ReportField(
                value         = location,
                onValueChange = { location = it },
                placeholder   = "Location",
                imeAction     = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(12.dp))

            ReportField(
                value         = medicationName,
                onValueChange = { medicationName = it },
                placeholder   = "Medication Name",
                imeAction     = ImeAction.Done
            )

            // Error message
            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text     = uiState.errorMessage!!,
                    color    = ErrorRed,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Submit button ─────────────────────────────────────────────
            Button(
                onClick  = {
                    viewModel.submitReport(
                        pharmacyName   = pharmacyName,
                        location       = location,
                        medicationName = medicationName
                    )
                },
                enabled  = !uiState.isLoading,
                colors   = ButtonDefaults.buttonColors(containerColor = MediDarkGreen),
                shape    = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .height(50.dp)
                    .widthIn(min = 160.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color       = White,
                        modifier    = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text       = "Submit",
                        color      = White,
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ReportField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    imeAction: ImeAction
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        placeholder   = { Text(placeholder, color = TextGray, fontSize = 14.sp) },
        singleLine    = true,
        shape         = RoundedCornerShape(10.dp),
        colors        = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = MediDarkGreen,
            unfocusedBorderColor = BorderGray,
            cursorColor          = MediDarkGreen
        ),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction      = imeAction
        ),
        modifier = Modifier.fillMaxWidth()
    )
}