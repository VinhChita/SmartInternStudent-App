package com.smartintern.student.ui

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartintern.student.friendly
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

fun toast(ctx: Context, msg: String?) { if (!msg.isNullOrBlank()) Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show() }

/* ---------- loading helper ---------- */
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Ok<T>(val data: T) : UiState<T>
    data class Err(val msg: String) : UiState<Nothing>
}

class Loader<T>(private val scope: CoroutineScope, private val block: suspend () -> T) {
    var state by mutableStateOf<UiState<T>>(UiState.Loading)
        private set
    fun reload(silent: Boolean = false) {
        scope.launch {
            if (!silent) state = UiState.Loading
            state = try { UiState.Ok(block()) } catch (e: CancellationException) { throw e } catch (e: Throwable) { UiState.Err(e.friendly()) }
        }
    }
}

@Composable
fun <T> rememberLoader(vararg keys: Any?, block: suspend () -> T): Loader<T> {
    val scope = rememberCoroutineScope()
    val loader = remember(*keys) { Loader(scope, block) }
    LaunchedEffect(loader) { loader.reload() }
    return loader
}

@Composable
fun <T> LoadView(loader: Loader<T>, content: @Composable (T) -> Unit) {
    when (val s = loader.state) {
        UiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = Blue) }
        is UiState.Err -> Column(Modifier.fillMaxSize().padding(24.dp), Arrangement.Center, Alignment.CenterHorizontally) {
            Text(s.msg, color = Muted, textAlign = TextAlign.Center)
            Spacer(Modifier.height(12.dp))
            Button(onClick = { loader.reload() }, colors = ButtonDefaults.buttonColors(containerColor = Blue)) { Text("ព្យាយាមម្តងទៀត") }
        }
        is UiState.Ok -> content(s.data)
    }
}

/* ---------- building blocks ---------- */
@Composable
fun AppHeader(title: String, sub: String, onBell: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth().background(Blue).statusBarsPadding().padding(horizontal = 20.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(sub, color = Color.White.copy(alpha = .85f), fontSize = 12.sp)
        }
        if (onBell != null) Box(Modifier.size(40.dp).clip(CircleShape).background(Color.White.copy(alpha = .18f)).clickable(onClick = onBell), Alignment.Center) {
            Icon(Icons.Filled.NotificationsNone, null, tint = Color.White)
        }
    }
}


@Composable
fun Section(text: String) = Text(text, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Ink, modifier = Modifier.padding(top = 18.dp, bottom = 8.dp))

@Composable
fun WhiteCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Line)) { Column(content = content) }
}

@Composable
fun Pill(text: String, bg: Color, fg: Color) {
    Text(text, color = fg, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
        modifier = Modifier.clip(RoundedCornerShape(50)).background(bg).padding(horizontal = 12.dp, vertical = 5.dp))
}

/** status: pending | approved | rejected | draft | submitted */
@Composable
fun StatusPill(status: String) = when (status) {
    "approved" -> Pill("អនុម័ត", Color(0xFFE8F5E9), Green)
    "rejected" -> Pill("បដិសេធ", Color(0xFFFFF3E0), Orange)
    "draft" -> Pill("ព្រាង", Color(0xFFFFF3E0), Orange)
    "submitted" -> Pill("ផ្ញើរួច", Color(0xFFE8F5E9), Green)
    else -> Pill("កំពុងពិនិត្យ", Color(0xFFE3F2FD), Blue)
}

@Composable
fun CompanyLogo(name: String) {
    val palette = listOf(Color(0xFF0B3A6F), Color(0xFFF57C20), Color(0xFF00897B), Color(0xFF6A1B9A), Color(0xFFC62828))
    val color = palette[(name.hashCode().and(0x7fffffff)) % palette.size]
    Box(Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)).background(color), Alignment.Center) {
        Text(name.trim().split(" ").firstOrNull().orEmpty().take(4), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

@Composable
fun BigButton(text: String, color: Color, enabled: Boolean = true, icon: ImageVector? = null, onClick: () -> Unit) {
    Button(onClick, Modifier.fillMaxWidth().height(50.dp), enabled = enabled, shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color)) {
        if (icon != null) { Icon(icon, null, Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)) }
        Text(text, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
fun ScreenBody(content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize().background(Bg)) { content() }
}

/* ---------- bottom navigation ---------- */
private data class Tab(val route: String, val kh: String, val en: String, val icon: ImageVector, val group: Set<String>)

private val tabs = listOf(
    Tab("home", "ទំព័រដើម", "Home", Icons.Filled.Home, setOf("home")),
    Tab("profile", "កម្រងរូប", "Profile", Icons.Filled.Person, setOf("profile")),
    Tab("search", "ស្វែងរក", "Search", Icons.Filled.Search, setOf("search")),
    Tab("reports", "របាយការណ៍", "Reports", Icons.Filled.Description, setOf("reports", "evaluation")),
    Tab("more", "ផ្សេងៗ", "More", Icons.Filled.Menu, setOf("more", "applications", "notifications")),
)

@Composable
fun BottomBar(current: String?, onNavigate: (String) -> Unit) {
    NavigationBar(containerColor = Color.White) {
        tabs.forEach { t ->
            NavigationBarItem(
                selected = current in t.group, onClick = { onNavigate(t.route) },
                icon = { Icon(t.icon, null) },
                label = { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(t.kh, fontSize = 10.sp, fontWeight = FontWeight.Bold); Text(t.en, fontSize = 9.sp) } },
                colors = NavigationBarItemDefaults.colors(selectedIconColor = Blue, selectedTextColor = Blue, indicatorColor = Color.Transparent,
                    unselectedIconColor = Muted, unselectedTextColor = Muted))
        }
    }
}

/* ---------- file helper ---------- */
fun Context.toPart(uri: Uri, field: String = "file"): MultipartBody.Part {
    val name = contentResolver.query(uri, null, null, null, null)?.use { c ->
        val i = c.getColumnIndex(OpenableColumns.DISPLAY_NAME); if (c.moveToFirst() && i >= 0) c.getString(i) else null } ?: "cv.pdf"
    val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: ByteArray(0)
    return MultipartBody.Part.createFormData(field, name, bytes.toRequestBody((contentResolver.getType(uri) ?: "application/pdf").toMediaTypeOrNull()))
}
