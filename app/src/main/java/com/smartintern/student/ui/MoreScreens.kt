package com.smartintern.student.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartintern.student.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/* ======================= REPORTS ======================= */
@Composable
fun ReportsScreen(onBell: () -> Unit, onEvaluation: () -> Unit) {
    val ctx = LocalContext.current; val scope = rememberCoroutineScope()
    var type by remember { mutableStateOf("weekly") }
    val l = rememberLoader(type) { Graph.api.reports(type) }
    var dialog by remember { mutableStateOf<ReportDto?>(null) }   // edit existing
    var creating by remember { mutableStateOf(false) }

    ScreenBody {
        AppHeader("របាយការណ៍", "INTERNSHIP REPORTS", onBell)
        Column(Modifier.weight(1f).padding(16.dp)) {
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White).border(1.dp, Line, RoundedCornerShape(12.dp)).padding(4.dp)) {
                listOf("weekly" to ("របាយការណ៍ប្រចាំសប្តាហ៍" to "Weekly Report"), "final" to ("របាយការណ៍ចុងក្រោយ" to "Final Report")).forEach { (k, v) ->
                    val on = type == k
                    Column(Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(if (on) Blue else Color.Transparent).clickable { type = k }.padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(v.first, color = if (on) Color.White else Ink, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(v.second, color = if (on) Color.White.copy(alpha = .9f) else Muted, fontSize = 10.sp)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Box(Modifier.weight(1f)) { LoadView(l) { d ->
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (d.items.isEmpty()) item { Text("មិនទាន់មានរបាយការណ៍", color = Muted, modifier = Modifier.padding(12.dp)) }
                    items(d.items) { r ->
                        WhiteCard { Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFE3F2FD)), Alignment.Center) { Icon(Icons.Filled.Description, null, tint = Blue) }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(r.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                if (r.period_label.isNotBlank()) Text("កាលបរិច្ឆេទ: ${r.period_label}", color = Muted, fontSize = 11.sp)
                            }
                            StatusPill(r.status); Spacer(Modifier.width(6.dp))
                            IconButton({ dialog = r }) { Icon(Icons.Filled.EditNote, null, tint = Ink) }
                        } }
                    }
                    item { Spacer(Modifier.height(4.dp)); WhiteCardLink(onEvaluation) }
                }
            } }
            Spacer(Modifier.height(8.dp))
            BigButton(if (type == "weekly") "+ ផ្ញើរបាយការណ៍ /WEEKLY REPORT" else "+ ផ្ញើរបាយការណ៍ចុងក្រោយ /FINAL REPORT", Green) { creating = true }
        }
    }

    if (creating || dialog != null) {
        val editing = dialog
        var notes by remember(editing) { mutableStateOf(editing?.notes ?: "") }
        fun send(submit: Boolean) = scope.launch {
            try {
                if (editing == null) Graph.api.createReport(ReportReq(type, notes.ifBlank { null }, submit))
                else Graph.api.updateReport(editing.id, ReportReq(editing.type, notes.ifBlank { null }, submit))
                creating = false; dialog = null; l.reload(true)
                toast(ctx, if (submit) "ផ្ញើរបាយការណ៍បានជោគជ័យ" else "រក្សាទុកជាព្រាងរួចរាល់")
            } catch (e: Throwable) { toast(ctx, e.friendly()) }
        }
        AlertDialog(onDismissRequest = { creating = false; dialog = null },
            title = { Text(editing?.title ?: if (type == "weekly") "របាយការណ៍ប្រចាំសប្តាហ៍" else "របាយការណ៍ចុងក្រោយ", fontWeight = FontWeight.Bold) },
            text = { OutlinedTextField(notes, { notes = it }, Modifier.fillMaxWidth().heightIn(min = 140.dp), label = { Text("អ្វីដែលបានធ្វើ / What you did") }) },
            confirmButton = { Button({ send(true) }, colors = ButtonDefaults.buttonColors(containerColor = Green)) { Text("ផ្ញើ") } },
            dismissButton = { TextButton({ send(false) }) { Text("រក្សាទុកជាព្រាង") } })
    }
}

@Composable
private fun WhiteCardLink(onClick: () -> Unit) {
    WhiteCard(Modifier.clickable(onClick = onClick)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Star, null, tint = Blue); Spacer(Modifier.width(12.dp))
            Text("វាយតម្លៃ/កាលវិភាគ · Evaluation & Calendar", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, modifier = Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, null, tint = Muted, modifier = Modifier.size(14.dp))
        }
    }
}

/* ======================= SEARCH ======================= */
@Composable
fun SearchScreen(onBell: () -> Unit) {
    val ctx = LocalContext.current; val scope = rememberCoroutineScope()
    var query by remember { mutableStateOf("") }; var debounced by remember { mutableStateOf("") }
    var type by remember { mutableStateOf<String?>(null) }; var menu by remember { mutableStateOf(false) }
    LaunchedEffect(query) { delay(350); debounced = query }
    val l = rememberLoader(debounced, type) { Graph.api.programs(debounced.ifBlank { null }, type) }

    ScreenBody {
        AppHeader("ស្វែងរកកម្មសិក្សា", "FIND INTERNSHIPS", onBell)
        Column(Modifier.weight(1f).padding(16.dp)) {
            OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp),
                placeholder = { Text("ស្វែងរកការងារកម្មសិក្សា / Search positions...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Filled.Search, null, tint = Muted) },
                trailingIcon = { Box { IconButton({ menu = true }) { Icon(Icons.Filled.Tune, null, tint = Blue) }
                    DropdownMenu(menu, { menu = false }) {
                        listOf(null to "ទាំងអស់", "full_time" to "Full-Time", "part_time" to "Part-Time").forEach { (k, v) -> DropdownMenuItem({ Text(v) }, { type = k; menu = false }) }
                    } } },
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, unfocusedBorderColor = Line))
            Box(Modifier.weight(1f)) { LoadView(l) { d ->
                Column {
                    Row(Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("លទ្ធផលស្វែងរក (${d.items.size}) / RESULTS (${d.items.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f))
                        Text("លុបតម្រង", color = Blue, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable { query = ""; type = null })
                    }
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(d.items) { p ->
                            WhiteCard {
                                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                                    CompanyLogo(p.company); Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) { Text(p.title, fontWeight = FontWeight.Bold, fontSize = 15.sp); Text(p.company, color = Muted, fontSize = 13.sp) }
                                    Pill(p.type_label, Color(0xFFE3F2FD), Blue)
                                }
                                HorizontalDivider(Modifier.padding(horizontal = 14.dp), color = Line)
                                Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.LocationOn, null, tint = Muted, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(4.dp))
                                    Text(p.location, color = Muted, fontSize = 12.sp, modifier = Modifier.weight(1f), maxLines = 1)
                                    Button({ scope.launch { try { toast(ctx, Graph.api.apply(p.id).message); l.reload(true) } catch (e: Throwable) { toast(ctx, e.friendly()) } } },
                                        enabled = !p.has_applied, shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = Blue)) {
                                        Text(if (p.has_applied) "បានដាក់ពាក្យ" else "ដាក់ពាក្យ / Apply", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            } }
        }
    }
}

/* ======================= EVALUATION & CALENDAR ======================= */
@Composable
fun EvaluationScreen(onBell: () -> Unit) {
    val ctx = LocalContext.current; val scope = rememberCoroutineScope()
    val l = rememberLoader { Graph.api.evaluation() }
    var composing by remember { mutableStateOf(false) }; var body by remember { mutableStateOf("") }
    ScreenBody {
        AppHeader("វាយតម្លៃ/កាលវិភាគ", "EVALUATION & CALENDAR", onBell)
        Box(Modifier.weight(1f)) { LoadView(l) { d ->
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
                Section("ការវាយតម្លៃពី SUPERVISOR / PERFORMANCE EVALUATION")
                WhiteCard { Column(Modifier.padding(16.dp)) {
                    val ev = d.evaluation
                    if (ev == null) Text("មិនទាន់មានការវាយតម្លៃ", color = Muted) else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("លទ្ធផលរួម / Overall Performance", fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                            Pill(d.overall_label ?: "", Color(0xFFE8F5E9), Green)
                        }
                        HorizontalDivider(Modifier.padding(vertical = 10.dp), color = Line)
                        listOf("ការអនុវត្តជំនាញ: / Professionalism" to ev.professionalism, "ទំនាក់ទំនង / Communication" to ev.communication).forEach { (k, v) ->
                            Row(Modifier.padding(vertical = 5.dp)) { Text(k, color = Muted, fontSize = 12.sp, modifier = Modifier.weight(1f)); Text("$v / 5", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                        }
                    }
                } }
                Section("ប្រតិទិន និងកាលវិភាគ / CALENDAR & SCHEDULE")
                if (d.meetings.isEmpty()) WhiteCard { Text("មិនទាន់មានកាលវិភាគ", color = Muted, modifier = Modifier.padding(16.dp)) }
                d.meetings.forEach { m ->
                    WhiteCard(Modifier.padding(bottom = 8.dp)) { Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        DateBlock(m.day, "${m.month} ${m.year}"); Spacer(Modifier.width(14.dp))
                        Column { Text("${m.title} • ${m.start_time}", fontWeight = FontWeight.Bold, fontSize = 13.sp); if (!m.topic.isNullOrBlank()) Text("ប្រធានបទ: ${m.topic}", color = Muted, fontSize = 12.sp) }
                    } }
                }
                Section("សារ និងការទំនាក់ទំនង / COMMUNICATIONS")
                BigButton("ផ្ញើសារទៅកាន់ Supervisor / Admin", Teal, icon = Icons.Filled.MailOutline) { composing = true }
            }
        } }
    }
    if (composing) AlertDialog(onDismissRequest = { composing = false },
        title = { Text("ផ្ញើសារ", fontWeight = FontWeight.Bold) },
        text = { OutlinedTextField(body, { body = it }, Modifier.fillMaxWidth().heightIn(min = 120.dp), label = { Text("សាររបស់អ្នក") }) },
        confirmButton = { Button({ scope.launch { try { toast(ctx, Graph.api.sendMessage(SendMessageReq(body.trim())).message); body = ""; composing = false } catch (e: Throwable) { toast(ctx, e.friendly()) } } },
            enabled = body.isNotBlank(), colors = ButtonDefaults.buttonColors(containerColor = Teal)) { Text("ផ្ញើ") } },
        dismissButton = { TextButton({ composing = false }) { Text("បោះបង់") } })
}

/* ======================= MORE + NOTIFICATIONS ======================= */
@Composable
private fun MenuRow(icon: ImageVector, kh: String, en: String, tint: Color = Blue, onClick: () -> Unit) {
    WhiteCard(Modifier.padding(bottom = 10.dp).clickable(onClick = onClick)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = tint); Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) { Text(kh, fontWeight = FontWeight.Bold, fontSize = 14.sp); Text(en, color = Muted, fontSize = 11.sp) }
            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, null, tint = Muted, modifier = Modifier.size(14.dp))
        }
    }
}

@Composable
fun MoreScreen(onBell: () -> Unit, onApplications: () -> Unit, onEvaluation: () -> Unit, onNotifications: () -> Unit) {
    val scope = rememberCoroutineScope()
    ScreenBody {
        AppHeader("ផ្សេងៗ", "MORE", onBell)
        Column(Modifier.padding(16.dp)) {
            MenuRow(Icons.Filled.Assignment, "ពាក្យស្នើរបស់ខ្ញុំ", "My Applications", onClick = onApplications)
            MenuRow(Icons.Filled.Star, "វាយតម្លៃ/កាលវិភាគ", "Evaluation & Calendar", onClick = onEvaluation)
            MenuRow(Icons.Filled.NotificationsNone, "ការជូនដំណឹង", "Notifications", onClick = onNotifications)
            MenuRow(Icons.AutoMirrored.Filled.Logout, "ចាកចេញ", "Log out", tint = Orange) {
                scope.launch { try { Graph.api.logout() } catch (_: Throwable) {}; Graph.store.clear() }
            }
        }
    }
}

@Composable
fun NotificationsScreen() {
    val l = rememberLoader { Graph.api.notifications() }
    ScreenBody {
        AppHeader("ការជូនដំណឹង", "NOTIFICATIONS")
        Box(Modifier.weight(1f)) { LoadView(l) { d ->
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (d.items.isEmpty()) item { Text("មិនទាន់មានការជូនដំណឹង", color = Muted) }
                items(d.items) { n ->
                    WhiteCard { Column(Modifier.padding(14.dp)) {
                        Text(n.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        if (!n.body.isNullOrBlank()) Text(n.body, color = Muted, fontSize = 12.sp)
                        Text(n.ago, color = Muted, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                    } }
                }
            }
        } }
    }
}
