package com.smartintern.student.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartintern.student.R
import com.smartintern.student.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/* ======================= HOME (dashboard) ======================= */
@Composable
private fun StatCard(kh: String, en: String, value: Int, icon: ImageVector, tint: Color, bg: Color, modifier: Modifier) {
    WhiteCard(modifier) {
        Column(Modifier.padding(horizontal = 10.dp, vertical = 12.dp)) {
            Text(kh, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
            Text(en, color = Muted, fontSize = 9.sp, maxLines = 1)
            Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("$value", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = tint)
                Spacer(Modifier.width(6.dp))
                Box(Modifier.size(26.dp).clip(CircleShape).background(bg), Alignment.Center) { Icon(icon, null, tint = tint, modifier = Modifier.size(15.dp)) }
            }
        }
    }
}

@Composable
fun DateBlock(day: String, month: String) {
    Column(Modifier.clip(RoundedCornerShape(10.dp)).background(Color(0xFFE3F2FD)).padding(horizontal = 14.dp, vertical = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(day, color = Blue, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(month, color = Blue, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun HomeScreen(onBell: () -> Unit) {
    val l = rememberLoader { Graph.api.dashboard() }
    ScreenBody {
        AppHeader("ទំព័រដើម", "STUDENT DASHBOARD", onBell)
        Box(Modifier.weight(1f)) { LoadView(l) { d ->
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
                Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Blue).padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("សួស្តី, ${d.name.substringBefore(" /").ifBlank { "និស្សិត" }}!", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("សូមស្វាគមន៍មកកាន់ Smart Internship", color = Color.White.copy(alpha = .9f), fontSize = 12.sp)
                    }
                    Box(Modifier.size(48.dp).clip(CircleShape).background(Color.White), Alignment.Center) {
                        Image(painterResource(R.drawable.app_logo), contentDescription = null, modifier = Modifier.fillMaxSize().clip(CircleShape))
                    }
                }
                Section("ស្ថានភាពពាក្យស្នើ / APPLICATION STATUS")
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard("ពាក្យស្នើ", "Applications", d.stats.applications, Icons.Filled.Timeline, Blue, Color(0xFFE3F2FD), Modifier.weight(1f))
                    StatCard("អនុម័ត", "Approved", d.stats.approved, Icons.Filled.Timeline, Green, Color(0xFFE8F5E9), Modifier.weight(1f))
                    StatCard("បដិសេធ", "Rejected", d.stats.rejected, Icons.Filled.Timeline, Orange, Color(0xFFFFF3E0), Modifier.weight(1f))
                    StatCard("សរុប", "Activities", d.stats.activities, Icons.Filled.Timeline, Muted, Color(0xFFF1F5F9), Modifier.weight(1f))
                }
                Section("កាលវិភាគខាងមុខ / UPCOMING SCHEDULE")
                val m = d.next_meeting
                WhiteCard {
                    if (m == null) Text("មិនទាន់មានកាលវិភាគ", color = Muted, modifier = Modifier.padding(16.dp))
                    else Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        DateBlock(m.day, m.month); Spacer(Modifier.width(14.dp))
                        Column { Text(m.title, fontWeight = FontWeight.Bold, fontSize = 14.sp); Text("ពេលវេលា: ${m.time}", color = Muted, fontSize = 12.sp) }
                    }
                }
            }
        } }
    }
}

/* ======================= MY APPLICATIONS ======================= */
@Composable
fun ApplicationsScreen(onBell: () -> Unit) {
    val l = rememberLoader { Graph.api.applications() }
    var filter by remember { mutableStateOf("all") }
    ScreenBody {
        AppHeader("ពាក្យស្នើរបស់ខ្ញុំ", "MY APPLICATIONS", onBell)
        Box(Modifier.weight(1f)) { LoadView(l) { d ->
            Column(Modifier.fillMaxSize()) {
                val chips = listOf("all" to ("ទាំងអស់" to d.counts.all), "pending" to ("កំពុងពិនិត្យ" to d.counts.pending),
                    "approved" to ("អនុម័ត" to d.counts.approved), "rejected" to ("បដិសេធ" to d.counts.rejected))
                LazyRow(Modifier.padding(vertical = 14.dp), contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(chips) { (key, v) ->
                        val on = filter == key
                        Row(Modifier.clip(RoundedCornerShape(50)).background(if (on) Blue else Color.White).clickable { filter = key }
                            .padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(v.first, color = if (on) Color.White else Ink, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.width(6.dp))
                            Text("${v.second}", color = if (on) Color.White else Blue, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                                modifier = Modifier.clip(CircleShape).background(if (on) Color.White.copy(alpha = .25f) else Color(0xFFE3F2FD)).padding(horizontal = 7.dp, vertical = 1.dp))
                        }
                    }
                }
                val rows = d.items.filter { filter == "all" || it.status == filter }
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (rows.isEmpty()) item { Text("គ្មានពាក្យស្នើ", color = Muted, modifier = Modifier.padding(16.dp)) }
                    items(rows) { a ->
                        WhiteCard {
                            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                CompanyLogo(a.company); Spacer(Modifier.width(12.dp))
                                Column { Text(a.title, fontWeight = FontWeight.Bold, fontSize = 15.sp); Text(a.company, color = Muted, fontSize = 13.sp) }
                            }
                            HorizontalDivider(Modifier.padding(horizontal = 14.dp), color = Line)
                            Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("កាលបរិច្ឆេទ: ${a.applied_label}", color = Muted, fontSize = 12.sp, modifier = Modifier.weight(1f)); StatusPill(a.status)
                            }
                        }
                    }
                }
            }
        } }
    }
}

/* ======================= PROFILE / CV ======================= */
@Composable
private fun DetailRow(k: String, v: String?) {
    Row(Modifier.fillMaxWidth().padding(vertical = 7.dp)) {
        Text(k, color = Muted, fontSize = 12.sp, modifier = Modifier.weight(1f))
        Text(v ?: "-", fontWeight = FontWeight.Bold, fontSize = 13.sp, textAlign = TextAlign.End)
    }
}

@Composable
fun ProfileScreen(onBell: () -> Unit) {
    val ctx = LocalContext.current; val scope = rememberCoroutineScope()
    val l = rememberLoader { Graph.api.profile() }
    val picker = androidx.activity.compose.rememberLauncherForActivityResult(androidx.activity.result.contract.ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) scope.launch {
            try { toast(ctx, Graph.api.uploadCv(ctx.toPart(uri)).message); l.reload(true) } catch (e: Throwable) { toast(ctx, e.friendly()) }
        }
    }
    ScreenBody {
        AppHeader("ប្រវត្តិរូប / CV", "MY PROFILE & RESUME", onBell)
        Box(Modifier.weight(1f)) { LoadView(l) { p ->
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
                WhiteCard { Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(64.dp).clip(CircleShape).background(Color(0xFFE3F2FD)), Alignment.Center) { Text(p.initials, color = Blue, fontWeight = FontWeight.Bold, fontSize = 20.sp) }
                    Spacer(Modifier.width(14.dp))
                    Column { Text(p.name, fontWeight = FontWeight.Bold, fontSize = 16.sp); Text("ជំនាញ: ${p.major ?: "-"}", color = Muted, fontSize = 12.sp) }
                } }
                Section("ព័ត៌មានផ្ទាល់ខ្លួន / PERSONAL DETAILS")
                WhiteCard { Column(Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    DetailRow("ថ្ងៃកំណើត / Date of Birth", p.birth_date_label); DetailRow("អ៊ីមែល / Email", p.email)
                    DetailRow("ទូរស័ព្ទ / Phone", p.phone); DetailRow("ទីតាំង / Location", p.location)
                } }
                Section("ឯកសាររបស់អ្នក / ATTACHED DOCUMENTS")
                WhiteCard {
                    val cv = p.cv
                    if (cv == null) Text("មិនទាន់មានឯកសារ CV", color = Muted, modifier = Modifier.padding(16.dp))
                    else Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(42.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFFDECEA)), Alignment.Center) { Icon(Icons.Filled.Description, null, tint = Color(0xFFD32F2F)) }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) { Text(cv.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1); Text("បានផ្ទុកឡើង: ${cv.uploaded} • ${cv.size}", color = Muted, fontSize = 11.sp) }
                        IconButton({ scope.launch { try { Graph.api.deleteCv(); l.reload(true) } catch (e: Throwable) { toast(ctx, e.friendly()) } } }) { Icon(Icons.Filled.DeleteOutline, null, tint = Muted) }
                    }
                }
                Spacer(Modifier.height(16.dp))
                BigButton(if (p.cv == null) "Upload" else "Change", Green) { picker.launch("*/*") }
            }
        } }
    }
}
