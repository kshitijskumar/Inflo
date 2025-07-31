package org.app.inflo.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.app.inflo.navigation.InfloNavOptions
import org.app.inflo.navigation.InfloNavigationManager
import org.app.inflo.navigation.InfloNavigationRequest
import org.app.inflo.navigation.PopUpToConfig
import org.app.inflo.navigation.args.SplashArgs

@Composable
fun LoginScreen(navigationManager: InfloNavigationManager) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Login",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )
            
            Button(
                onClick = {
                    // Simulate login success
                    println("Login attempted with email: $email")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text("Login")
            }
            
            TextButton(
                onClick = { 
                    navigationManager.postNavigationRequest(
                        InfloNavigationRequest.GoBack()
                    )
                }
            ) {
                Text("Back to Splash")
            }
            
            TextButton(
                onClick = { 
                    navigationManager.postNavigationRequest(
                        InfloNavigationRequest.Navigate(
                            SplashArgs,
                            InfloNavOptions(
                                launchSingleTop = true,
                                popUpToConfig = PopUpToConfig.ClearAll()
                            )
                        )
                    )
                }
            ) {
                Text("Restart App")
            }
        }
    }
} 