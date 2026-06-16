package com.example.burguerlab.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.burguerlab.MainActivity
import com.example.burguerlab.R
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Servicio FCM que maneja notificaciones push entrantes.
 *
 * Tipos de notificación soportados (campo "tipo" en el payload data):
 *   pedido_recibido    → "Tu pedido ha sido recibido 🎉"
 *   pedido_preparando  → "Estamos preparando tu pedido 👨‍🍳"
 *   pedido_listo       → "¡Tu pedido está listo! 🍔"
 *   promocion          → Título y cuerpo personalizados
 *
 * Registro en AndroidManifest.xml — agregar dentro de <application>:
 *
 *   <service
 *       android:name=".notifications.BurgerLabMessagingService"
 *       android:exported="false">
 *       <intent-filter>
 *           <action android:name="com.google.firebase.MESSAGING_EVENT" />
 *       </intent-filter>
 *   </service>
 */
class BurgerLabMessagingService : FirebaseMessagingService() {

    companion object {
        const val CHANNEL_PEDIDOS    = "channel_pedidos"
        const val CHANNEL_PROMOCIONES = "channel_promociones"

        /** Crea los canales de notificación (llamar desde MainActivity.onCreate) */
        fun crearCanales(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val manager = context.getSystemService(NotificationManager::class.java)

                manager.createNotificationChannel(
                    NotificationChannel(
                        CHANNEL_PEDIDOS,
                        "Estado de pedidos",
                        NotificationManager.IMPORTANCE_HIGH
                    ).apply { description = "Actualizaciones sobre tu pedido" }
                )

                manager.createNotificationChannel(
                    NotificationChannel(
                        CHANNEL_PROMOCIONES,
                        "Promociones y ofertas",
                        NotificationManager.IMPORTANCE_DEFAULT
                    ).apply { description = "Descuentos y promociones especiales" }
                )
            }
        }

        /** Suscribe al tema de promociones para recibir notificaciones masivas */
        fun suscribirPromocionesGlobal() {
            FirebaseMessaging.getInstance().subscribeToTopic("promociones")
        }

        /** Desuscribirse (ej: cuando el usuario lo desactiva en ajustes) */
        fun desuscribirPromociones() {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("promociones")
        }
    }

    /** Se llama cuando llega un mensaje FCM con la app en primer plano */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val data    = message.data
        val notif   = message.notification
        val tipo    = data["tipo"] ?: ""
        val titulo  = notif?.title ?: data["titulo"] ?: tituloPorTipo(tipo)
        val cuerpo  = notif?.body  ?: data["cuerpo"] ?: cuerpoPorTipo(tipo)
        val channel = if (tipo == "promocion") CHANNEL_PROMOCIONES else CHANNEL_PEDIDOS

        mostrarNotificacion(titulo, cuerpo, channel)
    }

    /** Token nuevo → aquí podrías enviarlo a tu backend/Firestore */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO: guardar token en Firestore del usuario autenticado
        //   FirebaseFirestore.getInstance()
        //       .collection("usuarios").document(uid)
        //       .update("fcmToken", token)
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private fun tituloPorTipo(tipo: String): String = when (tipo) {
        "pedido_recibido"   -> "Pedido recibido 🎉"
        "pedido_preparando" -> "En preparación 👨‍🍳"
        "pedido_listo"      -> "¡Tu pedido está listo! 🍔"
        "promocion"         -> "¡Oferta especial para ti! 🏷️"
        else                -> "Burger Lab"
    }

    private fun cuerpoPorTipo(tipo: String): String = when (tipo) {
        "pedido_recibido"   -> "Hemos recibido tu pedido. ¡Pronto comenzamos a prepararlo!"
        "pedido_preparando" -> "Nuestros chefs están trabajando en tu hamburguesa. 🔥"
        "pedido_listo"      -> "Tu pedido está listo para ser recogido. ¡Buen provecho!"
        "promocion"         -> "Entra a la app y no te pierdas las ofertas de hoy."
        else                -> ""
    }

    private fun mostrarNotificacion(titulo: String, cuerpo: String, channelId: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Indicar que se abrió desde notificación (opcional para deep-link)
            if (channelId == CHANNEL_PROMOCIONES) putExtra("open_screen", "promociones")
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notif = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(titulo)
            .setContentText(cuerpo)
            .setStyle(NotificationCompat.BigTextStyle().bigText(cuerpo))
            .setPriority(
                if (channelId == CHANNEL_PEDIDOS)
                    NotificationCompat.PRIORITY_HIGH
                else
                    NotificationCompat.PRIORITY_DEFAULT
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notif)
    }
}
