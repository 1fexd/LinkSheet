package com.tasomaniac.openwith.preferred

import android.content.Context
import android.util.Log
import com.tasomaniac.openwith.data.LinkSheetDatabase
import com.tasomaniac.openwith.data.PreferredApp
import com.tasomaniac.openwith.resolver.DisplayActivityInfo
import fe.linksheet.data.entity.AppSelectionHistory
import fe.linksheet.extension.queryFirstIntentActivityByPackageNameOrNull
import fe.linksheet.extension.toDisplayActivityInfo
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object PreferredResolver : KoinComponent {
    private val database by inject<LinkSheetDatabase>()

    fun resolve(context: Context, host: String): PreferredDisplayActivityInfo? {
        Log.d("PreferredResolver", "Host: $host")

        return database.preferredAppDao().preferredAppByHost(host)?.let { app ->
            Log.d("PreferredResolver", "App: $app")
            app.resolve(context)?.let {
                Log.d("PreferredResolver", "DisplayActivityInfo: $it")
                PreferredDisplayActivityInfo(app, it)
            }
        }
    }

    fun PreferredApp.resolve(context: Context): DisplayActivityInfo? {
        return context.packageManager
            .queryFirstIntentActivityByPackageNameOrNull(componentName.packageName)
            ?.toDisplayActivityInfo(context)
    }

    fun resolveHostHistory(context: Context, host: String): Map<String, AppSelectionHistory> {
        val map = mutableMapOf<String, AppSelectionHistory>()
        database.appSelectionHistoryDao().historyForHost(host).forEach { app ->
            if(app.lastUsed > map.getOrPut(app.packageName) { app }.lastUsed){
                map[app.packageName] = app
            }
        }

        return map
    }
}
