package com.example.androidhw

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    private lateinit var editText: EditText
    private var pendingNotificationText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setActionBar(toolbar)
        title = getString(R.string.app_name)

        editText = findViewById(R.id.edit_text)
        createNotificationChannel()
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_edit_text -> {
                showEditDialog()
                true
            }
            R.id.menu_notify -> {
                handleNotificationRequest()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showEditDialog() {
        val input = EditText(this).apply {
            hint = getString(R.string.dialog_hint)
            setText(editText.text)
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_title_edit)
            .setView(input)
            .setPositiveButton(R.string.dialog_ok) { _, _ ->
                editText.setText(input.text.toString())
            }
            .setNegativeButton(R.string.dialog_cancel, null)
            .show()
    }

    private fun handleNotificationRequest() {
        val text = editText.text.toString().trim()
        if (text.isEmpty()) {
            Toast.makeText(this, R.string.toast_empty_text, Toast.LENGTH_SHORT).show()
            return
        }

        if (Build.VERSION.SDK_INT >= 33) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                pendingNotificationText = text
                ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_CODE_NOTIFICATIONS)
                return
            }
        }

        showNotification(text)
    }

    private fun showNotification(text: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(text)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = getString(R.string.notification_channel_description)
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_NOTIFICATIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pendingNotificationText?.let { showNotification(it) }
            } else {
                Toast.makeText(this, R.string.toast_permission_denied, Toast.LENGTH_SHORT).show()
            }
            pendingNotificationText = null
        }
    }

    companion object {
        private const val CHANNEL_ID = "user_text_channel"
        private const val NOTIFICATION_ID = 1001
        private const val REQUEST_CODE_NOTIFICATIONS = 2001
    }
}
