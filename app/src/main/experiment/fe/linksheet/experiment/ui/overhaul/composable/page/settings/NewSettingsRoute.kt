package fe.linksheet.experiment.ui.overhaul.composable.page.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fe.linksheet.*
import fe.linksheet.experiment.ui.overhaul.composable.ContentTypeDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.RouteNavItem
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.RouteNavigateListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.experiment.ui.overhaul.composable.component.page.layout.group
import fe.linksheet.experiment.ui.overhaul.composable.component.util.Resource.Companion.textContent
import fe.linksheet.module.viewmodel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

internal object NewSettingsRouteData {
    val verifiedApps = RouteNavItem(
        appsWhichCanOpenLinksSettingsRoute,
        Icons.Outlined.DomainVerification,
        textContent(R.string.verified_link_handlers),
        textContent(R.string.verified_link_handlers_subtitle)
    )

    val customization = arrayOf(
        RouteNavItem(
            browserSettingsRoute,
            Icons.Outlined.Apps,
            textContent(R.string.app_browsers),
            textContent(R.string.app_browsers_subtitle),
        ),
        RouteNavItem(
            bottomSheetSettingsRoute,
            Icons.Outlined.SwipeUp,
            textContent(R.string.bottom_sheet),
            textContent(R.string.bottom_sheet_explainer),
        ),
        RouteNavItem(
            linksSettingsRoute,
            Icons.Outlined.Link,
            textContent(R.string.links),
            textContent(R.string.links_explainer),
        )
    )

    val miscellaneous = arrayOf(
        RouteNavItem(
            generalSettingsRoute,
            Icons.Outlined.Settings,
            textContent(R.string.general),
            textContent(R.string.general_settings_explainer),
        ),
        RouteNavItem(
            notificationSettingsRoute,
            Icons.Outlined.Notifications,
            textContent(R.string.notifications),
            textContent(R.string.notifications_explainer),
        ),
        RouteNavItem(
            themeSettingsRoute,
            Icons.Outlined.Palette,
            textContent(R.string.theme),
            textContent(R.string.theme_explainer),
        ),
        RouteNavItem(
            privacySettingsRoute,
            Icons.Outlined.PrivacyTip,
            textContent(R.string.privacy),
            textContent(R.string.privacy_settings_explainer),
        )
    )

    val advanced = arrayOf(
        RouteNavItem(
            advancedSettingsRoute,
            Icons.Outlined.Terminal,
            textContent(R.string.advanced),
            textContent(R.string.advanced_explainer),
        ),
        RouteNavItem(
            debugSettingsRoute,
            Icons.Outlined.BugReport,
            textContent(R.string.debug),
            textContent(R.string.debug_explainer),
        )
    )

    val dev = RouteNavItem(
        devModeRoute,
        Icons.Outlined.DeveloperMode,
        textContent(R.string.dev),
        textContent(R.string.dev_explainer)
    )

    val about = arrayOf(
        RouteNavItem(
            Routes.Help,
            Icons.AutoMirrored.Outlined.HelpOutline,
            textContent(R.string.help),
            textContent(R.string.help_subtitle),
        ),
        RouteNavItem(
            Routes.Shortcuts,
            Icons.Outlined.SwitchAccessShortcut,
            textContent(R.string.settings__title_shortcuts),
            textContent(R.string.settings__subtitle_shortcuts),
        ),
        RouteNavItem(
            Routes.Updates,
            Icons.Outlined.Update,
            textContent(R.string.settings__title_updates),
            textContent(R.string.settings__subtitle_updates),
        ),
        RouteNavItem(
            aboutSettingsRoute,
            Icons.Outlined.Info,
            textContent(R.string.about),
            textContent(R.string.about_explainer),
        )
    )
}

@Composable
fun NewSettingsRoute(
    onBackPressed: () -> Unit,
    navigate: (String) -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val devMode = viewModel.devModeEnabled()

    SaneScaffoldSettingsPage(
        headline = stringResource(id = R.string.settings),
        onBackPressed = onBackPressed
    ) {
        item(key = R.string.enable_libredirect, contentType = ContentTypeDefaults.SingleGroupItem) {
            RouteNavigateListItem(data = NewSettingsRouteData.verifiedApps, navigate = navigate)
        }

        divider(stringRes = R.string.customization)

        group(items = NewSettingsRouteData.customization) { data, padding, shape ->
            RouteNavigateListItem(data = data, padding = padding, shape = shape, navigate = navigate)
        }

        divider(stringRes = R.string.misc_settings)

        group(items = NewSettingsRouteData.miscellaneous) { data, padding, shape ->
            RouteNavigateListItem(data = data, padding = padding, shape = shape, navigate = navigate)
        }

        divider(stringRes = R.string.advanced)

        group(size = NewSettingsRouteData.advanced.size + if (devMode) 1 else 0) {
            items(values = NewSettingsRouteData.advanced) { data, padding, shape ->
                RouteNavigateListItem(data = data, padding = padding, shape = shape, navigate = navigate)
            }

            if (devMode) {
                item(key = NewSettingsRouteData.dev.route) { padding, shape ->
                    RouteNavigateListItem(
                        data = NewSettingsRouteData.dev,
                        padding = padding,
                        shape = shape,
                        navigate = navigate
                    )
                }
            }
        }

        divider(stringRes = R.string.about)

        group(items = NewSettingsRouteData.about) { data, padding, shape ->
            RouteNavigateListItem(data = data, padding = padding, shape = shape, navigate = navigate)
        }
    }
}
