package com.example.burguerlab.navigation   // ← corregido

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.burguerlab.ui.BurguerDetailScreen   // ← corregido
import com.example.burguerlab.ui.HomeScreen
import com.example.burguerlab.ui.SplashScreen
import com.example.burguerlab.ui.LoginScreen
import com.example.burguerlab.ui.RegisterScreen
import com.example.burguerlab.ui.ProfileScreen
import com.example.burguerlab.ui.CartScreen
import com.example.burguerlab.ui.ConfirmOrderScreen
// Rutas de navegación como constantes
object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val DETAIL = "detail/{hamburguesaId}"
    const val PROFILE = "profile"

    const val CART = "cart"

    const val CONFIRM_ORDER = "confirm_order"

    // Construye la ruta de detalle con el ID real
    fun detail(id: String) = "detail/$id"
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.SPLASH) {

        // Pantalla de Splash
        composable(Routes.SPLASH) {
            SplashScreen(navController = navController)
        }

        // Pantalla de Login
        composable(Routes.LOGIN) {
            LoginScreen(navController = navController)
        }

        // Pantalla de Registro
        composable(Routes.REGISTER) {
            RegisterScreen(navController = navController)
        }

        // Pantalla principal
        composable(Routes.HOME) {
            HomeScreen(navController = navController)
        }

        // Pantalla de perfil de usuario
        composable(Routes.PROFILE) {
            ProfileScreen(navController = navController)
        }

        // Pantalla de detalle — recibe el ID de la hamburguesa seleccionada
        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("hamburguesaId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("hamburguesaId") ?: ""
            BurguerDetailScreen(hamburguesaId = id, navController = navController)
        }
        composable(Routes.CART) {
            CartScreen(navController)
        }

        composable(Routes.CONFIRM_ORDER) {
            ConfirmOrderScreen(navController)
        }
    }
}