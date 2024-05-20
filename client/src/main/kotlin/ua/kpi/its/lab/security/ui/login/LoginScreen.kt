// LoginScreen.kt
package ua.kpi.its.lab.security.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import ua.kpi.its.lab.security.LocalHttpClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    snackbarHostState: SnackbarHostState,
    updateToken: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val client = LocalHttpClient.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(horizontal = 120.dp)
    ) {
        Text("Welcome Back!")
        TextField(
            value = username,
            onValueChange = {
                username = it
            },
            label = {
                Text("Username")
            },
            isError = loginError,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp), // Increase the height of the text field
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.White,
                focusedIndicatorColor = Color.Black,
                unfocusedIndicatorColor = Color.Black,
                cursorColor = Color.Black,
                errorIndicatorColor = Color.Red,
                errorLabelColor = Color.Red
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = password,
            onValueChange = {
                password = it
            },
            visualTransformation = PasswordVisualTransformation(),
            label = {
                Text("Password")
            },
            isError = loginError,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp), // Increase the height of the text field
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.White,
                focusedIndicatorColor = Color.Black,
                unfocusedIndicatorColor = Color.Black,
                cursorColor = Color.Black,
                errorIndicatorColor = Color.Red,
                errorLabelColor = Color.Red
            )
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                scope.launch {
                    val token = withContext(Dispatchers.IO) {
                        try {
                            val response = client.post("http://localhost:8080/auth/token") {
                                expectSuccess = true
                                basicAuth(username, password)
                            }
                            val token: String = Json.decodeFromString(response.body())
                            token
                        } catch (e: ClientRequestException) {
                            ""
                        }
                    }

                    if (token.isNotBlank()) {
                        loginError = false
                        scope.launch {
                            updateToken(token)
                        }
                    } else {
                        loginError = true
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Wrong username or password"
                            )
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp), // Increase the height of the button
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            )
        ) {
            Text("Login")
        }
    }
}
