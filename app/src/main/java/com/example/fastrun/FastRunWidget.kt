package com.example.fastrun

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 */
class FastRunWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        val mainHandler = Handler(Looper.getMainLooper())

        mainHandler.post(object : Runnable {
            override fun run() {
                for (appWidgetId in appWidgetIds) {
                    updateAppWidget(context, appWidgetManager, appWidgetId)
                }
                mainHandler.postDelayed(this, 1000)
            }
        })

    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val widgetText = context.getString(R.string.appwidget_text)
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.fast_run_widget)
    val prefs: SharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    var kek = prefs.getInt("spinnerSelection", selectedPosition)
    var cel:String = ""
    if (kek == 0){
        cel = "4000"
    }else if (kek == 1){
        cel = "5000"
    }else if (kek == 2){
        cel = "6000"
    }else if (kek == 3){
        cel = "7000"
    }else if (kek == 4){
        cel = "8000"
    }else if (kek == 5){
        cel = "9000"
    }else if (kek == 6){
        cel = "10000"
    }
    val sharedPreferences = context.getSharedPreferences("FastPrefs", Context.MODE_PRIVATE)
    var steps:Int = sharedPreferences.getInt("shagi_1", currentSteps)

    views.setTextViewText(R.id.appwidget_text, "$steps/$cel")

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}