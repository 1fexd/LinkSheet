package fe.linksheet.experiment.ui.overhaul.composable.page.settings.link.redirect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import fe.linksheet.LinkSheetAppConfig
import fe.linksheet.R
import fe.linksheet.experiment.ui.overhaul.composable.ContentTypeDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.PreferenceSwitchListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.SliderListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.experiment.ui.overhaul.composable.util.AnnotatedStringResource.Companion.annotated
import fe.linksheet.experiment.ui.overhaul.composable.util.Resource.Companion.textContent
import fe.linksheet.module.viewmodel.FollowRedirectsSettingsViewModel
import fe.linksheet.util.Darknet
import org.koin.androidx.compose.koinViewModel


@Composable
fun NewFollowRedirectsSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: FollowRedirectsSettingsViewModel = koinViewModel()
) {
//    val followRedirectsKnownTrackers = dialogHelper<Unit, List<String>, Unit>(
//        fetch = { FastForwardRules.rules["tracker"]?.map { it.toString() } ?: emptyList() },
//        awaitFetchBeforeOpen = true,
//        dynamicHeight = true
//    ) { trackers, close ->
//        FollowRedirectsKnownTrackersDialog(trackers!!, close)
//    }

    val darknets = remember {
        Darknet.entries.joinToString(separator = ", ") { it.displayName }
    }

    SaneScaffoldSettingsPage(headline = stringResource(id = R.string.follow_redirects), onBackPressed = onBackPressed) {
        item(key = R.string.follow_redirects, contentType = ContentTypeDefaults.SingleGroupItem) {
            PreferenceSwitchListItem(
                preference = viewModel.followRedirects,
                headlineContent = textContent(R.string.follow_redirects),
                supportingContent = annotated(R.string.follow_redirects_explainer),
            )
        }

        divider(stringRes = R.string.options)

        group(size = 4 + if(LinkSheetAppConfig.isPro()) 1 else 0) {
            item(key = R.string.follow_redirects_local_cache) { padding, shape ->
                PreferenceSwitchListItem(
                    enabled = viewModel.followRedirects(),
                    shape = shape,
                    padding = padding,
                    preference = viewModel.followRedirectsLocalCache,
                    headlineContent = textContent(R.string.follow_redirects_local_cache),
                    supportingContent = textContent(R.string.follow_redirects_local_cache_explainer),
                )
            }

            item(key = R.string.follow_only_known_trackers) { padding, shape ->
                // TODO: This settings should allow the user to add their own rules in the future, or at least display a _understandable_ list of known tracker domains
                PreferenceSwitchListItem(
                    enabled = viewModel.followRedirects() && !viewModel.followRedirectsExternalService(),
                    shape = shape,
                    padding = padding,
                    preference = viewModel.followOnlyKnownTrackers,
//                    onContentClick = { followRedirectsKnownTrackers.open(Unit) },
                    headlineContent = textContent(R.string.follow_only_known_trackers),
                    supportingContent = annotated(R.string.follow_only_known_trackers_explainer),
                )
            }

            item(key = R.string.allow_darknets) { padding, shape ->
                PreferenceSwitchListItem(
                    enabled = viewModel.followRedirects(),
                    shape = shape,
                    padding = padding,
                    preference = viewModel.followRedirectsAllowsDarknets,
                    headlineContent = textContent(R.string.allow_darknets),
                    supportingContent = textContent(id = R.string.follow_redirects_allow_darknets_explainer, darknets),
                )
            }

            if(LinkSheetAppConfig.isPro()){
                item(key = R.string.follow_redirects_external_service) { padding, shape ->
                    PreferenceSwitchListItem(
                        enabled = viewModel.followRedirects() && LinkSheetAppConfig.isPro(),
                        shape = shape,
                        padding = padding,
                        preference = viewModel.followRedirectsExternalService,
                        headlineContent = textContent(R.string.follow_redirects_external_service),
                        supportingContent = annotated(R.string.follow_redirects_external_service_explainer),
                    )
                }
            }

            item(key = R.string.request_timeout) { padding, shape ->
                SliderListItem(
                    enabled = viewModel.followRedirects(),
                    shape = shape,
                    padding = padding,
                    valueRange = 0f..30f,
                    value = viewModel.followRedirectsTimeout().toFloat(),
                    onValueChange = { viewModel.followRedirectsTimeout(it.toInt()) },
                    valueFormatter = { it.toInt().toString() },
                    headlineContent = textContent(R.string.request_timeout),
                    supportingContent = annotated(R.string.request_timeout_explainer),
                )
            }
        }
    }
}