package com.smartintern.student

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.smartintern.student.ui.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { AppTheme { Root() } }
    }
}

private val authRoutes = setOf("login", "register")

@Composable
private fun Root() {
    val nav = rememberNavController()
    val token by Graph.store.token.collectAsState(initial = Graph.token)
    val route = nav.currentBackStackEntryAsState().value?.destination?.route
    val loggedIn = token != null

    // sign in / sign out: the token in DataStore decides which part of the app is shown
    LaunchedEffect(loggedIn) {
        if (loggedIn) nav.navigate("home") { popUpTo(0) { inclusive = true } }
        else nav.navigate("login") { popUpTo(0) { inclusive = true } }
    }

    Scaffold(contentWindowInsets = WindowInsets(0),
        bottomBar = { if (loggedIn && route != null && route !in authRoutes) BottomBar(route) { dest ->
            nav.navigate(dest) { popUpTo(nav.graph.findStartDestination().id) { saveState = true }; launchSingleTop = true; restoreState = true }
        } }) { pad ->
        NavHost(nav, startDestination = if (loggedIn) "home" else "login", modifier = Modifier.padding(pad)) {
            val bell = { nav.navigate("notifications") }
            composable("login") { LoginScreen(onRegister = { nav.navigate("register") }) }
            composable("register") { RegisterScreen(onDone = { nav.popBackStack() }) }
            composable("home") { HomeScreen(bell) }
            composable("profile") { ProfileScreen(bell) }
            composable("search") { SearchScreen(bell) }
            composable("reports") { ReportsScreen(bell, onEvaluation = { nav.navigate("evaluation") }) }
            composable("evaluation") { EvaluationScreen(bell) }
            composable("more") { MoreScreen(bell, { nav.navigate("applications") }, { nav.navigate("evaluation") }, { nav.navigate("notifications") }) }
            composable("applications") { ApplicationsScreen(bell) }
            composable("notifications") { NotificationsScreen() }
        }
    }
}
