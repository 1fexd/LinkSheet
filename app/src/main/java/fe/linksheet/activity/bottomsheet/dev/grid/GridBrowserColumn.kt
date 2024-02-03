package fe.linksheet.activity.bottomsheet.dev.grid

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.linksheet.R
import fe.linksheet.composable.util.defaultRoundedCornerShape
import fe.linksheet.resolver.DisplayActivityInfo
import fe.linksheet.util.PrivateBrowsingBrowser

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GridBrowserButton(
    appInfo: DisplayActivityInfo,
    privateBrowser: PrivateBrowsingBrowser?,
    showPackage: Boolean,
    launchApp: (DisplayActivityInfo, Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .heightIn(min = 85.dp)
            .padding(start = 7.dp, top = 7.dp, end = 7.dp, bottom = 0.dp)
            .clip(defaultRoundedCornerShape)
            .clickable { launchApp(appInfo, false) }
            .padding(all = 3.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(modifier = Modifier.size(40.dp)) {
            Image(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(32.dp),
                bitmap = appInfo.iconBitmap,
                contentDescription = appInfo.label
            )

            if (privateBrowser != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(20.dp)
                        .clip(IconButtonDefaults.filledShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                ) {
                    Icon(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(16.dp),
                        imageVector = Icons.Outlined.Shield,
                        contentDescription = stringResource(id = R.string.request_private_browsing)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = appInfo.label,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        if (showPackage) {
            Text(
                modifier = Modifier.basicMarquee(velocity = 20.dp),
                text = appInfo.packageName,
                fontSize = 12.sp,
                lineHeight = 12.sp
            )
        }
    }
}
