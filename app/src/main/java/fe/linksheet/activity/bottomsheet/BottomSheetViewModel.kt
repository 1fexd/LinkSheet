package fe.linksheet.activity.bottomsheet

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.tasomaniac.openwith.data.LinkSheetDatabase
import com.tasomaniac.openwith.data.PreferredApp
import com.tasomaniac.openwith.resolver.DisplayActivityInfo
import com.tasomaniac.openwith.resolver.IntentResolverResult
import com.tasomaniac.openwith.resolver.ResolveIntents
import fe.fastforwardkt.isTracker
import fe.gson.extensions.array
import fe.gson.extensions.bool
import fe.gson.extensions.int
import fe.gson.extensions.string
import fe.httpkt.Request
import fe.httpkt.json.readToJson
import fe.linksheet.activity.MainActivity
import fe.linksheet.data.entity.AppSelectionHistory
import fe.linksheet.data.entity.WhitelistedBrowser
import fe.linksheet.extension.startActivityWithConfirmation
import fe.linksheet.module.preference.PreferenceRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class BottomSheetViewModel : ViewModel(),
    KoinComponent {
    private val database by inject<LinkSheetDatabase>()
    private val preferenceRepository by inject<PreferenceRepository>()

    var result by mutableStateOf<IntentResolverResult?>(null)
    var enableCopyButton by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.enableCopyButton) ?: false
    )

    var hideAfterCopying by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.hideAfterCopying) ?: false
    )

    var singleTap by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.singleTap) ?: false
    )

    var enableSendButton by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.enableSendButton) ?: false
    )

    var alwaysShowPackageName by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.alwaysShowPackageName) ?: false
    )

    var disableToasts by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.disableToasts) ?: false
    )

    var gridLayout by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.gridLayout) ?: false
    )

    var useClearUrls by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.useClearUrls) ?: false
    )

    var followRedirects by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.followRedirects) ?: false
    )

    var followRedirectsExternalService by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.followRedirectsExternalService)
            ?: false
    )

    var followOnlyKnownTrackers by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.followOnlyKnownTrackers) ?: false
    )

    fun resolveAsync(context: Context, intent: Intent): Deferred<IntentResolverResult?> {
        return viewModelScope.async(Dispatchers.IO) {
            result = ResolveIntents.resolve(context, intent, this@BottomSheetViewModel)

            result
        }
    }

    fun startMainActivity(context: Context): Boolean {
        return context.startActivityWithConfirmation(Intent(context, MainActivity::class.java))
    }

    fun startPackageInfoActivity(context: Context, info: DisplayActivityInfo): Boolean {
        return context.startActivityWithConfirmation(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            this.data = Uri.parse("package:${info.packageName}")
        })
    }

    suspend fun persistSelectedIntentAsync(intent: Intent, always: Boolean) {
        Log.d("PersistingSelectedIntent", "Component: ${intent.component}")
        return withContext(Dispatchers.IO) {
            intent.component?.let { component ->
                val host = intent.data!!.host!!.lowercase(Locale.getDefault())
                val app = PreferredApp(
                    host = host,
                    packageName = component.packageName,
                    component = component.flattenToString(),
                    alwaysPreferred = always
                )

                Log.d("PersistingSelectedIntent", "Inserting $app")
                database.preferredAppDao().insert(app)

                val historyEntry = AppSelectionHistory(
                    host = host,
                    packageName = component.packageName,
                    lastUsed = System.currentTimeMillis()
                )

                database.appSelectionHistoryDao().insert(historyEntry)
                Log.d("PersistingSelectedIntent", "Inserting $historyEntry")
            }
        }
    }

    suspend fun getWhiteListedBrowsers(): List<WhitelistedBrowser> {
        return withContext(Dispatchers.IO) {
            database.whitelistedBrowsersDao().getWhitelistedBrowsers()
        }
    }

    fun followRedirects(
        uri: Uri,
        request: Request,
        fastForwardRulesObject: JsonObject
    ): Result<String> {
        Log.d("FollowRedirects", "Following redirects for $uri")

        if (!followOnlyKnownTrackers || isTracker(uri.toString(), fastForwardRulesObject)) {
            if (followRedirectsExternalService) {
                Log.d("FollowRedirects", "Using external service for $uri")

                val response = followRedirectsExternal(request, uri)
                if (response.isSuccess) {
                    return response
                }
            }

            return Result.success(followRedirectsLocal(request, uri))
        }

        return Result.success(uri.toString())
    }

    private fun followRedirectsLocal(request: Request, uri: Uri): String {
        return request.head(uri.toString(), followRedirects = true).url.toString()
    }

    private fun followRedirectsExternal(request: Request, uri: Uri): Result<String> {
        val con = request.get("https://unshorten.me/json/$uri")
        if (con.responseCode != 200) {
            return Result.failure(Exception("Failed to resolve API!"))
        }

        val obj = con.readToJson().asJsonObject
        Log.d("FollowRedirects", "Returned json $obj")

        return if (obj.bool("success") == true) {
            Result.success(obj.string("resolved_url")!!)
        } else Result.failure(Exception("Ratelimited by API!"))
    }
}