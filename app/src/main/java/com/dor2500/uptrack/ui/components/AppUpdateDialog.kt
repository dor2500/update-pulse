package com.dor2500.uptrack.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dor2500.uptrack.R
import com.dor2500.uptrack.updater.AppReleaseInfo
import com.dor2500.uptrack.updater.DownloadState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppUpdateDialog(
    release: AppReleaseInfo,
    downloadState: DownloadState?,
    legacyPackages: List<String>,
    onRemoveLegacyPackage: (String) -> Unit,
    onUninstallCurrentApp: () -> Unit,
    onDownloadAndInstall: (uninstallFirst: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var uninstallFirst by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            Text(
                text = stringResource(R.string.updater_available_title, release.version),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = release.releaseNotes,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .verticalScroll(rememberScrollState())
            )

            if (legacyPackages.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.updater_legacy_detected),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedButton(
                    onClick = { onRemoveLegacyPackage(legacyPackages.first()) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.updater_remove_legacy))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { uninstallFirst = !uninstallFirst },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = uninstallFirst,
                    onCheckedChange = { uninstallFirst = it }
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.updater_uninstall_first),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = stringResource(R.string.updater_uninstall_first_sub),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (downloadState) {
                is DownloadState.Progress -> {
                    Text(
                        text = stringResource(R.string.updater_downloading, downloadState.percent),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { downloadState.percent / 100f },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                is DownloadState.NeedsUninstall -> {
                    Text(
                        text = stringResource(R.string.updater_needs_uninstall, downloadState.savedFileName),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = onUninstallCurrentApp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.updater_uninstall_now))
                    }
                }
                is DownloadState.Error -> {
                    Text(
                        text = downloadState.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                else -> Unit
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.btn_cancel))
                }
                Button(
                    onClick = { onDownloadAndInstall(uninstallFirst) },
                    enabled = downloadState !is DownloadState.Progress
                ) {
                    Text(stringResource(R.string.updater_download_install))
                }
            }
        }
    }
}
