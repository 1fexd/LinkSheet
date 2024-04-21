package fe.linksheet.experiment.ui.overhaul.composable.component.page.twoline

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import fe.android.preference.helper.compose.StatePreference
import fe.linksheet.experiment.ui.overhaul.composable.component.page.RememberGroupDslMarker
import fe.linksheet.experiment.ui.overhaul.composable.component.page.RememberGroupScope
import fe.linksheet.experiment.ui.overhaul.composable.component.page.TwoLineGroupValueProvider

@Stable
class SwitchPreferenceItem(
    val preference: StatePreference<Boolean>,
    headlineId: Int,
    subtitleId: Int,
) : TwoLineGroupValueProvider(headlineId, subtitleId)

@RememberGroupDslMarker
class TwoLinePreferenceScope : RememberGroupScope<Int, SwitchPreferenceItem>() {
    fun add(preference: StatePreference<Boolean>, @StringRes headlineId: Int, @StringRes subtitleId: Int) {
        add(SwitchPreferenceItem(preference, headlineId, subtitleId))
    }
}

@Composable
fun <T : Any?> rememberTwoLinePreferenceGroup(
    key1: T,
    fn: (T) -> List<SwitchPreferenceItem>,
): List<SwitchPreferenceItem> {
    return remember(key1 = key1) { fn(key1) }
}
