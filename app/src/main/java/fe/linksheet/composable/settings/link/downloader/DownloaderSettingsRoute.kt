package fe.linksheet.composable.settings.link.downloader

import android.Manifest
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import fe.android.preference.helper.Preference
import fe.android.preference.helper.compose.StatePreference
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.SettingEnabledCardColumn
import fe.linksheet.composable.util.SliderRow
import fe.linksheet.composable.util.SwitchRow
import fe.linksheet.composable.util.linkableSubtitleBuilder
import fe.linksheet.module.viewmodel.DownloaderSettingsViewModel
import fe.linksheet.util.AndroidVersion
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalPermissionsApi::class, ExperimentalFoundationApi::class)
@Composable
fun DownloaderSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: DownloaderSettingsViewModel = koinViewModel()
) {
    val writeExternalStoragePermissionState = downloaderPermissionState()

    SettingsScaffold(
        headline = stringResource(id = R.string.enable_downloader),
        onBackPressed = onBackPressed
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            stickyHeader(key = "enable_downloader") {
                SettingEnabledCardColumn(
                    checked = viewModel.enableDownloader(),
                    onChange = {
                      requestDownloadPermission(
                            writeExternalStoragePermissionState,
                            viewModel.enableDownloader,
                            it
                        )
                    },
                    headline = stringResource(id = R.string.enable_downloader),
                    subtitleBuilder = linkableSubtitleBuilder(id = R.string.enable_downloader_explainer),
                    contentTitle = stringResource(id = R.string.options)
                )
            }

            item(key = "downloader_url_mime_type") {
                SwitchRow(
                    state = viewModel.downloaderCheckUrlMimeType,
                    enabled = viewModel.enableDownloader(),
                    headlineId = R.string.downloader_url_mime_type,
                    subtitleId = R.string.downloader_url_mime_type_explainer
                )
            }

            item(key = "downloader_timeout") {
                SliderRow(
                    value = viewModel.requestTimeout().toFloat(),
                    onValueChange = { viewModel.requestTimeout(it.toInt()) },
                    enabled = viewModel.enableDownloader(),
                    valueRange = 0f..30f,
                    valueFormatter = { it.toInt().toString() },
                    headlineId = R.string.request_timeout,
                    subtitleId = R.string.request_timeout_explainer
                )
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun downloaderPermissionState() = rememberPermissionState(
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)

@OptIn(ExperimentalPermissionsApi::class)
fun requestDownloadPermission(
    permissionState: PermissionState,
    state: StatePreference<Boolean>,
    newState: Boolean
) {
    if (!AndroidVersion.AT_LEAST_API_29_Q && !permissionState.status.isGranted) {
        permissionState.launchPermissionRequest()
    } else state(newState)
}
