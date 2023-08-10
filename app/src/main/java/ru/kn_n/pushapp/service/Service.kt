package ru.kn_n.pushapp.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.kn_n.pushapp.Api
import ru.kn_n.pushapp.MainActivity
import ru.kn_n.pushapp.MessageModel
import ru.kn_n.pushapp.R
import java.util.*


class Service : Service() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api::class.java)

        val timer = Timer()
        val timerTask = object : TimerTask(){
            override fun run() {
                scope.launch {
                    try {
                        val isChange = api.haveChanges("haveChange")
                        if (isChange.have_ch) {
                            try {
                                val push = api.getMessage("getMsg")
                                showPush(push)
                            } catch (e: Exception) {
                                Log.d("PUSHH", "$e push error")
                            }
                        }
                    } catch (e: Exception) {
                        Log.d("PUSHH", "$e ask error")
                    }
                }
            }
        }

        timer.schedule(timerTask, 0, 10000)
        return START_STICKY
    }

    private fun showPush(message: MessageModel) {

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

        val builder = NotificationCompat.Builder(this, MainActivity.CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(message.title)
            .setContentText(message.body)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()
        startForeground(1, builder)
    }

    companion object{
        private const val BASE_URL = "http://192.168.0.106/"
    }
}