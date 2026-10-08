package dev.xavier.adbtoggle

import android.Manifest
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.widget.RemoteViews
import android.widget.Toast

class AdbToggleWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        render(context, manager, ids)
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_TOGGLE) {
            toggle(context)
            refreshAll(context)
            return
        }
        super.onReceive(context, intent)
    }

    private fun toggle(context: Context) {
        val granted = context.checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) ==
            PackageManager.PERMISSION_GRANTED
        if (!granted) {
            Toast.makeText(context, R.string.permission_missing, Toast.LENGTH_LONG).show()
            return
        }
        val next = if (isAdbEnabled(context)) 0 else 1
        Settings.Global.putInt(context.contentResolver, Settings.Global.ADB_ENABLED, next)
    }

    companion object {
        private const val ACTION_TOGGLE = "dev.xavier.adbtoggle.TOGGLE"

        private fun isAdbEnabled(context: Context): Boolean =
            Settings.Global.getInt(context.contentResolver, Settings.Global.ADB_ENABLED, 0) == 1

        private fun refreshAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, AdbToggleWidget::class.java))
            render(context, manager, ids)
        }

        private fun render(context: Context, manager: AppWidgetManager, ids: IntArray) {
            val on = isAdbEnabled(context)
            val toggleIntent = Intent(context, AdbToggleWidget::class.java).setAction(ACTION_TOGGLE)
            val pending = PendingIntent.getBroadcast(
                context, 0, toggleIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

            val views = RemoteViews(context.packageName, R.layout.widget).apply {
                setTextViewText(R.id.state, context.getString(if (on) R.string.state_on else R.string.state_off))
                setInt(R.id.root, "setBackgroundResource", if (on) R.drawable.bg_on else R.drawable.bg_off)
                setOnClickPendingIntent(R.id.root, pending)
            }
            manager.updateAppWidget(ids, views)
        }
    }
}
