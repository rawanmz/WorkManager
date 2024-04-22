package com.example.workmanager

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.workmanager.NotificationWorkManager.Companion.NOTIFICATION_ID
import com.example.workmanager.NotificationWorkManager.Companion.NOTIFICATION_WORK
import com.example.workmanager.ui.theme.WorkManagerTheme
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkNotificationPermission()
        setContent {
            WorkManagerTheme {
                // A surface container using the 'background' color from the theme
                Column {
                    Button(onClick = {
                        val customCalendar = Calendar.getInstance()
                        customCalendar.set(
                            2024,
                            3,//index from zero
                            22,
                            19,
                            7,
                            0
                        )
                        val customTime = customCalendar.timeInMillis
                        val currentTime = System.currentTimeMillis()
                        if (customTime > currentTime) {
                            val data = Data.Builder().putInt(NOTIFICATION_ID, 0).build()
                            val delay = customTime - currentTime
                            scheduleNotification(delay, data)
                        }
                    }) {
                        Text(text = "Schedule Notification")
                    }
                }
            }
        }
    }

    private fun scheduleNotification(delay: Long, data: Data) {
        val notificationWork = OneTimeWorkRequest.Builder(NotificationWorkManager::class.java)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS).setInputData(data).build()

        val instanceWorkManager = WorkManager.getInstance(this)
        instanceWorkManager.beginUniqueWork(
            NOTIFICATION_WORK,
            ExistingWorkPolicy.REPLACE, notificationWork
        ).enqueue()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun checkNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                0
            )
            Toast.makeText(this, "Not granted show permission", Toast.LENGTH_SHORT).show()

        }
    }
}





