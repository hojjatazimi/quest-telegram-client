package com.hojjatazimi.questtelegram.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.hojjatazimi.questtelegram.telegram.AuthState
import com.hojjatazimi.questtelegram.ui.components.QuestTextField
import com.hojjatazimi.questtelegram.ui.components.TeleQuestLogo
import com.hojjatazimi.questtelegram.ui.theme.TeleQuestLiquidGlass

@Composable
fun LoginScreen(
    authState: AuthState,
    lastLoginStep: AuthState,
    onSubmitPhone: (String) -> Unit,
    onSubmitCode: (String) -> Unit,
    onSubmitPassword: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var phone by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val visibleStep = if (authState is AuthState.Error) lastLoginStep else authState

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(TeleQuestLiquidGlass.appBackgroundBrush())
                .padding(horizontal = 56.dp, vertical = 42.dp),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.76f),
                shape = MaterialTheme.shapes.extraLarge,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.74f)),
                tonalElevation = 3.dp,
                shadowElevation = 10.dp,
            ) {
                Column(
                    modifier = Modifier.padding(34.dp),
                    verticalArrangement = Arrangement.spacedBy(22.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(18.dp),
                    ) {
                        TeleQuestLogo(size = 74.dp)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "TeleQuest",
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "Unofficial client",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    Text(
                        text = "A calm spatial messaging panel for Quest.",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    if (authState is AuthState.Error) {
                        Text(
                            text = authState.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }

                    when (authState) {
                        AuthState.Uninitialized -> Text(
                            text = "Preparing sign-in...",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        AuthState.SubmittingPhoneNumber -> Text(
                            text = "Sending login code...",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        AuthState.SubmittingCode -> Text(
                            text = "Checking login code...",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        AuthState.SubmittingPassword -> Text(
                            text = "Checking password...",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        else -> AuthForm(
                            visibleStep = visibleStep,
                            phone = phone,
                            onPhoneChange = { phone = it },
                            code = code,
                            onCodeChange = { code = it },
                            password = password,
                            onPasswordChange = { password = it },
                            onSubmitPhone = onSubmitPhone,
                            onSubmitCode = onSubmitCode,
                            onSubmitPassword = onSubmitPassword,
                        )
                    }

                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "Not affiliated with Telegram. Real mode connects directly through TDLib.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun AuthForm(
    visibleStep: AuthState,
    phone: String,
    onPhoneChange: (String) -> Unit,
    code: String,
    onCodeChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    onSubmitPhone: (String) -> Unit,
    onSubmitCode: (String) -> Unit,
    onSubmitPassword: (String) -> Unit,
) {
    when (visibleStep) {
        AuthState.WaitingForCode,
        AuthState.SubmittingCode,
        -> {
            QuestTextField(
                value = code,
                onValueChange = onCodeChange,
                label = "Telegram login code",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.fillMaxWidth(),
            )
            LargeActionButton(text = "Continue", onClick = { onSubmitCode(code) })
        }
        AuthState.WaitingForPassword,
        AuthState.SubmittingPassword,
        -> {
            QuestTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = "Two-step password",
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
            )
            LargeActionButton(text = "Unlock", onClick = { onSubmitPassword(password) })
        }
        else -> {
            QuestTextField(
                value = phone,
                onValueChange = onPhoneChange,
                label = "Phone number (+ country code)",
                keyboardType = KeyboardType.Phone,
                modifier = Modifier.fillMaxWidth(),
            )
            LargeActionButton(text = "Continue", onClick = { onSubmitPhone(phone) })
        }
    }
}

@Composable
private fun LargeActionButton(
    text: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}
