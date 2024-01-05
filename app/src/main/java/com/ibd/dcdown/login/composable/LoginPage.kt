package com.ibd.dcdown.login.composable

import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ibd.dcdown.R
import com.ibd.dcdown.login.viewmodel.LoginViewModel
import com.ibd.dcdown.main.viewmodel.HomeViewModel
import com.ibd.dcdown.tools.Extensions.activity
import com.ibd.dcdown.ui.theme.DCDownTheme

@Composable
fun LoginPage(vm: LoginViewModel = hiltViewModel()) {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !isSystemInDarkTheme()
    val statusBarColor = MaterialTheme.colorScheme.surface
    val navBarColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
    var id by remember { mutableStateOf("") }
    var pw by remember { mutableStateOf("") }
    val fr = remember { FocusRequester() }

    val event by vm.eventChannel.collectAsState(initial = null)
    val context = LocalContext.current
    DisposableEffect(systemUiController, useDarkIcons) {
        systemUiController.setStatusBarColor(statusBarColor, useDarkIcons)
        systemUiController.setNavigationBarColor(navBarColor, useDarkIcons)
        onDispose { }
    }
    LaunchedEffect(event) {
        event?.let {
            when (it) {
                is LoginViewModel.E.Toast -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }

                is LoginViewModel.E.LoginEnd -> {
                    Toast.makeText(
                        context,
                        context.getString(
                            R.string.login_welcome_message,
                            it.user.session?.nickname,
                            it.user.id
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
                    context.activity?.finish()
                }
            }
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(top = 24.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                stringResource(R.string.dc_login),
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            OutlinedTextField(
                value = id,
                modifier = Modifier.fillMaxWidth(),
                onValueChange = { id = it },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = {
                        fr.requestFocus()
                    }
                ),
                label = { Text(stringResource(R.string.id)) })
            OutlinedTextField(
                value = pw,
                modifier = Modifier.fillMaxWidth().focusRequester(fr),
                onValueChange = { pw = it },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        vm.login(id, pw)
                    }
                ),
                label = { Text(stringResource(R.string.password)) })
            Box(Modifier.height(8.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { vm.login(id, pw) },
                enabled = !vm.isProcessing
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (vm.isProcessing)
                        with(LocalDensity.current) {
                            val size = 12.sp.toDp()
                            CircularProgressIndicator(
                                Modifier.size(size),
                                strokeWidth = size / 6,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    Text(stringResource(R.string.login), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }

}

@Preview
@Composable
fun LoginPagePreview() {
    DCDownTheme {
        Surface(Modifier.fillMaxSize()) {
            LoginPage()
        }
    }
}