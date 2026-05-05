package com.hojjatazimi.questtelegram.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
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

@Composable
fun LoginScreen(
    authState: AuthState,
    onSubmitPhone: (String) -> Unit,
    onSubmitCode: (String) -> Unit,
    onSubmitPassword: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var phone by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp, vertical = 36.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Text(
                    text = "Quest Chat MVP",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Unofficial Telegram client prototype for Meta Quest 3. Fake mode is active for UI development.",
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
                    AuthState.WaitingForCode -> {
                        QuestTextField(
                            value = code,
                            onValueChange = { code = it },
                            label = "Fake login code",
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        LargeActionButton(text = "Continue", onClick = { onSubmitCode(code) })
                    }
                    AuthState.WaitingForPassword -> {
                        QuestTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Two-step password",
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                        )
                        LargeActionButton(text = "Unlock", onClick = { onSubmitPassword(password) })
                    }
                    else -> {
                        QuestTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = "Phone number",
                            keyboardType = KeyboardType.Phone,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        LargeActionButton(text = "Continue", onClick = { onSubmitPhone(phone) })
                    }
                }

                Spacer(Modifier.height(4.dp))
                Text(
                    text = "This app is not affiliated with Telegram. Do not enter real codes in fake mode.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
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
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}
