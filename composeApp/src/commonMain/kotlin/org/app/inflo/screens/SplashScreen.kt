package org.app.inflo.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.app.inflo.navigation.InfloNavigationManager
import org.app.inflo.navigation.InfloNavigationRequest
import org.app.inflo.navigation.args.LoginArgs

@Composable
fun SplashScreen(navigationManager: InfloNavigationManager) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primary
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Inflo",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Welcome to the app",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(top = 16.dp)
            )
            
            Button(
                onClick = { 
                    navigationManager.postNavigationRequest(
                        InfloNavigationRequest.Navigate(LoginArgs)
                    )
                },
                modifier = Modifier.padding(top = 32.dp)
            ) {
                Text("Go to Login")
            }
        }
    }
    
    // Auto-navigate to login after 3 seconds
    LaunchedEffect(Unit) {
        delay(3000)
        navigationManager.postNavigationRequest(
            InfloNavigationRequest.Navigate(LoginArgs)
        )
    }
} 