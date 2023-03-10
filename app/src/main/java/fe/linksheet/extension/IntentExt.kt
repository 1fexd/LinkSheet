package fe.linksheet.extension

import android.content.Intent
import android.net.Uri
import android.util.Log
import com.tasomaniac.openwith.resolver.ResolveIntents
import fe.clearurlskt.ClearURLLoader
import fe.clearurlskt.clearUrl
import fe.fastforwardkt.getRuleRedirect

fun Intent.sourceIntent(uri: Uri?) = Intent(this).apply {
    component = null
    action = Intent.ACTION_VIEW
    data = uri
    flags = flags and Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS.inv()
}

//{ act=android.intent.action.SEND typ=text/plain flg=0x10800001 cmp=fe.linksheet/.activity.bottomsheet.BottomSheetActivity clip={text/plain {T(59)}} (has extras) }
//{ act=android.intent.action.VIEW dat=https://twitter.com/... flg=0x10800000 cmp=fe.linksheet/.activity.bottomsheet.BottomSheetActivity (has extras) }

private val clearUrlProviders by lazy {
    ClearURLLoader.loadBuiltInClearURLProviders()
}

fun Intent.getUri(clearUrl: Boolean = false, fastForward: Boolean = false): Uri? {
    var uriData = dataString
    if (uriData == null) {
        uriData = getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()
    }

    if (uriData == null) {
        uriData = getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString()
    }

    if (uriData != null) {
        val uri = Uri.parse(uriData)
        if (uri.host != null && uri.scheme != null) {
            val host = uri.host!!
            val scheme = "${uri.scheme}://".lowercase()

            var url = buildString {
                append(scheme)
                if (uri.userInfo != null) {
                    append(uri.userInfo).append("@")
                }

                append(host.lowercase())
                append(uriData.substring(uriData.indexOf(host) + host.length))
            }

            Log.d("Url Pre modification", url)

            if (fastForward) {
                getRuleRedirect(url, ResolveIntents.fastForwardRulesObject)?.let { url = it }
            }

            Log.d("Url Post FastForward", url)

            if (clearUrl) {
                url = clearUrl(url,clearUrlProviders)
            }

            Log.d("Url Post ClearURL", url)

            return Uri.parse(url)
        }
    }

    return null
}

fun Intent.buildSendTo(uri: Uri?): Intent {
    return Intent.createChooser(this.apply {
        this.action = Intent.ACTION_SEND
        this.type = "text/plain"
        this.putExtra(Intent.EXTRA_TEXT, uri?.toString())
    }, null)
}