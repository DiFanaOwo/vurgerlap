package com.example.burguerlab

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.burguerlab.navigation.NavGraph
import com.example.burguerlab.notifications.BurgerLabMessagingService

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* no-op */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Crear canales de notificación (requiere API 26+)
        BurgerLabMessagingService.crearCanales(this)

        // Pedir permiso POST_NOTIFICATIONS en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Suscribir al tema global de promociones
        BurgerLabMessagingService.suscribirPromocionesGlobal()

        setContent {
            NavGraph()
        }
    }
}
