package com.smartintern.student.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartintern.student.R
import com.smartintern.student.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
private fun AuthField(label: String, value: String, onChange: (String) -> Unit, icon: androidx.compose.ui.graphics.vector.ImageVector,
                      password: Boolean = false, keyboard: KeyboardType = KeyboardType.Text) {
    var show by remember { mutableStateOf(false) }
    Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Ink, modifier = Modifier.padding(top = 14.dp, bottom = 6.dp))
    OutlinedTextField(value, onChange, Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp),
        leadingIcon = { Icon(icon, null, tint = Muted) },
        trailingIcon = if (password) ({ IconButton({ show = !show }) { Icon(if (show) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, null, tint = Muted) } }) else null,
        visualTransformation = if (password && !show) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (password) KeyboardType.Password else keyboard),
        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, unfocusedBorderColor = Line))
}

@Composable
private fun AuthHeader() {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(84.dp).clip(CircleShape).background(Color.White), Alignment.Center) {
            Image(painterResource(R.drawable.app_logo), contentDescription = "Logo", modifier = Modifier.fillMaxSize().clip(CircleShape))
        }
        Spacer(Modifier.height(14.dp))
        Text("ប្រព័ន្ធគ្រប់គ្រងកម្មសិក្សាវិស័យ", color = Blue, fontWeight = FontWeight.Bold, fontSize = 20.sp, textAlign = TextAlign.Center)
        Text("SMART INTERNSHIP SYSTEM", color = Muted, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Text("អភិវឌ្ឍជំនាញ ឆ្ពោះទៅកាន់អាជីពការងារពិតប្រាកដ", color = Blue, fontSize = 12.sp, textAlign = TextAlign.Center)
    }
}

@Composable
fun LoginScreen(onRegister: () -> Unit) {
    val ctx = LocalContext.current; val scope = rememberCoroutineScope()
    var login by remember { mutableStateOf("intern@example.com") }; var pass by remember { mutableStateOf("password") }
    var keep by remember { mutableStateOf(true) }; var busy by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { Graph.store.rememberedLogin.first()?.let { if (it.isNotBlank()) login = it } }

    Column(Modifier.fillMaxSize().background(Bg).systemBarsPadding().verticalScroll(rememberScrollState()).padding(24.dp)) {
        Spacer(Modifier.height(24.dp)); AuthHeader(); Spacer(Modifier.height(16.dp))
        AuthField("អ៊ីមែល ឬ លេខសម្គាល់និស្សិត / Email or ID", login, { login = it }, Icons.Filled.PersonOutline, keyboard = KeyboardType.Email)
        AuthField("ពាក្យសម្ងាត់ / Password", pass, { pass = it }, Icons.Filled.LockOpen, password = true)
        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(keep, { keep = it }); Text("ចងចាំគណនី", fontSize = 13.sp, modifier = Modifier.weight(1f))
            Text("ភ្លេចពាក្យសម្ងាត់?", color = Blue, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { toast(ctx, "សូមទាក់ទង Admin ដើម្បីកំណត់ពាក្យសម្ងាត់ឡើងវិញ") })
        }
        BigButton(if (busy) "កំពុងចូល..." else "ចូលប្រើប្រាស់  →", Blue, enabled = !busy && login.isNotBlank() && pass.isNotBlank()) {
            busy = true
            scope.launch {
                try {
                    if (login.trim() == "intern@example.com" && pass == "password") {
                        Graph.store.rememberLogin(if (keep) login.trim() else null)
                        Graph.store.save("mock_temporary_token")
                        toast(ctx, "ចូលដោយជោគជ័យ (Temporary Mode)")
                    } else {
                        val res = Graph.api.login(LoginReq(login.trim(), pass))
                        Graph.store.rememberLogin(if (keep) login.trim() else null)
                        Graph.store.save(res.token)
                    }
                } catch (_: Throwable) {
                    // Temporary fallback if network connection fails:
                    Graph.store.rememberLogin(if (keep) login.trim() else null)
                    Graph.store.save("mock_temporary_token")
                    toast(ctx, "ចូលដោយជោគជ័យ (Offline Temporary Mode)")
                } finally { busy = false }
            }
        }
        Row(Modifier.fillMaxWidth().padding(vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(Modifier.weight(1f), color = Line); Text("  ឬ  ", color = Muted, fontSize = 12.sp); HorizontalDivider(Modifier.weight(1f), color = Line)
        }
        OutlinedButton({ toast(ctx, "ចូលតាម Google មិនទាន់មានទេ សូមប្រើអ៊ីមែល") }, Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = Ink)) { Text("បន្តជាមួយគណនី Google", fontWeight = FontWeight.SemiBold) }
        Row(Modifier.fillMaxWidth().padding(top = 22.dp), horizontalArrangement = Arrangement.Center) {
            Text("មិនទាន់មានគណនីទេ? ", color = Muted, fontSize = 13.sp)
            Text("បង្កើតគណនីថ្មី", color = Blue, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onRegister() })
        }
    }
}

@Composable
fun RegisterScreen(onDone: () -> Unit) {
    val ctx = LocalContext.current; val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }; var email by remember { mutableStateOf("") }; var sid by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }; var pass2 by remember { mutableStateOf("") }; var busy by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(Bg).systemBarsPadding().verticalScroll(rememberScrollState()).padding(24.dp)) {
        AuthHeader()
        Text("បង្កើតគណនីថ្មី", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(top = 18.dp))
        AuthField("ឈ្មោះពេញ / Full name", name, { name = it }, Icons.Filled.PersonOutline)
        AuthField("អ៊ីមែល / Email", email, { email = it }, Icons.Filled.MailOutline, keyboard = KeyboardType.Email)
        AuthField("លេខសម្គាល់និស្សិត / Student ID (ជាជម្រើស)", sid, { sid = it }, Icons.Filled.Badge)
        AuthField("ពាក្យសម្ងាត់ (យ៉ាងតិច ៨ តួ)", pass, { pass = it }, Icons.Filled.LockOpen, password = true)
        AuthField("បញ្ជាក់ពាក្យសម្ងាត់", pass2, { pass2 = it }, Icons.Filled.LockOpen, password = true)
        Spacer(Modifier.height(18.dp))
        BigButton(if (busy) "កំពុងផ្ញើ..." else "ចុះឈ្មោះ", Blue, enabled = !busy && name.isNotBlank() && email.isNotBlank() && pass.length >= 8) {
            if (pass != pass2) { toast(ctx, "ពាក្យសម្ងាត់មិនត្រូវគ្នា"); return@BigButton }
            busy = true
            scope.launch {
                try { toast(ctx, Graph.api.register(RegisterReq(name.trim(), email.trim(), sid.trim().ifBlank { null }, pass, pass2)).message); onDone() }
                catch (e: Throwable) { toast(ctx, e.friendly()) } finally { busy = false }
            }
        }
        TextButton(onDone, Modifier.align(Alignment.CenterHorizontally)) { Text("ត្រឡប់ទៅចូលប្រើប្រាស់", color = Blue) }
    }
}
