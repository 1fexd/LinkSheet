package fe.linksheet.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fe.android.compose.dialog.helper.dialogHelper
import fe.linksheet.R
import fe.linksheet.composable.util.BottomRow
import fe.linksheet.composable.util.ExportLogDialog
import fe.linksheet.composable.util.PreferenceSubtitle
import fe.linksheet.extension.compose.setContentWithKoin
import fe.linksheet.extension.koin.injectLogger
import fe.linksheet.module.log.file.FileAppLogger
import fe.linksheet.module.log.file.entry.LogEntry
import fe.linksheet.module.viewmodel.CrashHandlerViewerViewModel
import fe.linksheet.ui.AppHost
import fe.linksheet.ui.HkGroteskFontFamily
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CrashHandlerActivity : ComponentActivity(), KoinComponent {
    companion object {
        const val EXTRA_CRASH_EXCEPTION = "EXTRA_CRASH_EXCEPTION_TEXT"
    }

    private val viewModel by viewModel<CrashHandlerViewerViewModel>()
    private val logger by injectLogger<CrashHandlerActivity>()
    private val fileAppLogger by inject<FileAppLogger>()

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val throwableString = intent.getStringExtra(EXTRA_CRASH_EXCEPTION) ?: return
        logger.fatal(throwableString)

        setContentWithKoin {
            AppHost {
                val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
                    rememberTopAppBarState(),
                    canScroll = { true }
                )

                val exportDialog = dialogHelper<Unit, List<LogEntry>, Unit>(
                    fetch = { fileAppLogger.logEntries },
                    awaitFetchBeforeOpen = true,
                    dynamicHeight = true
                ) { state, close ->
                    ExportLogDialog(
                        logViewCommon = viewModel.logViewCommon,
                        clipboardManager = viewModel.clipboardManager,
                        close = close,
                        logEntries = state!!
                    )
                }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        LargeTopAppBar(
                            colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = Color.Transparent),
                            title = {
                                Text(
                                    text = stringResource(id = R.string.app_name),
                                    fontFamily = HkGroteskFontFamily,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }, scrollBehavior = scrollBehavior
                        )
                    },
                    content = { padding ->
                        Column(modifier = Modifier.fillMaxSize()) {
                            LazyColumn(
                                modifier = Modifier
                                    .padding(padding)
                                    .weight(1f),
                                contentPadding = PaddingValues(5.dp)
                            ) {
                                stickyHeader(key = "header") {
                                    Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                                        PreferenceSubtitle(
                                            text = stringResource(id = R.string.app_crashed),
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))
                                    }
                                }


                                item("exception") {
                                    SelectionContainer {
                                        Text(
                                            text = throwableString,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }

                            BottomRow {
                                TextButton(
                                    onClick = {
                                        exportDialog.open(Unit)
                                    }
                                ) {
                                    Text(text = stringResource(id = R.string.export))
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}
