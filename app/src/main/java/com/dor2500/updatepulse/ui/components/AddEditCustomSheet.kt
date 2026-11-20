package com.dor2500.updatepulse.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dor2500.updatepulse.R
import com.dor2500.updatepulse.data.local.TrackedServiceEntity
import com.dor2500.updatepulse.data.model.ServiceCategory
import com.dor2500.updatepulse.data.model.SourceType
import com.dor2500.updatepulse.data.network.FetchedRelease
import kotlinx.coroutines.launch
import java.util.UUID

private val AccentPalette = listOf(
    0xFF60A5FAL,
    0xFF34D399L,
    0xFFA78BFAL,
    0xFFF472B6L,
    0xFFFBBF24L,
    0xFF38BDF8L
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCustomSheet(
    existing: TrackedServiceEntity?,
    onTestSource: suspend (TrackedServiceEntity) -> Result<FetchedRelease>,
    onSave: (TrackedServiceEntity) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf(existing?.name ?: "") }
    var category by remember { mutableStateOf(existing?.category ?: ServiceCategory.CUSTOM) }
    var sourceType by remember { mutableStateOf(existing?.sourceType ?: SourceType.GITHUB_RELEASES) }
    var sourceUrlOrRepo by remember { mutableStateOf(existing?.sourceUrlOrRepo ?: "") }
    var includePreReleases by remember { mutableStateOf(existing?.includePreReleases ?: false) }
    var jsonKey by remember { mutableStateOf(existing?.jsonKey ?: "") }
    var regexPattern by remember { mutableStateOf(existing?.regexPattern ?: "") }
    var accentColorHex by remember { mutableLongStateOf(existing?.accentColorHex ?: AccentPalette.first()) }

    var isTesting by remember { mutableStateOf(false) }
    var testResultText by remember { mutableStateOf<String?>(null) }
    var testIsSuccess by remember { mutableStateOf(false) }

    fun buildCandidate(): TrackedServiceEntity = TrackedServiceEntity(
        id = existing?.id ?: "custom_${UUID.randomUUID()}",
        name = name.trim().ifBlank { "Custom Tracker" },
        category = category,
        sourceType = sourceType,
        sourceUrlOrRepo = sourceUrlOrRepo.trim(),
        includePreReleases = includePreReleases,
        jsonKey = jsonKey.trim().ifBlank { null },
        regexPattern = regexPattern.trim().ifBlank { null },
        accentColorHex = accentColorHex,
        isCustom = true,
        isSubscribed = existing?.isSubscribed ?: true,
        lastKnownVersion = existing?.lastKnownVersion,
        lastCheckedAt = existing?.lastCheckedAt
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(
                    if (existing == null) R.string.add_custom_title else R.string.edit_custom_title
                ),
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.field_service_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            Text(
                text = stringResource(R.string.field_source_type),
                style = MaterialTheme.typography.labelLarge
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SourceType.entries.forEach { type ->
                    FilterChip(
                        selected = sourceType == type,
                        onClick = { sourceType = type },
                        label = { Text(stringResource(type.labelRes)) }
                    )
                }
            }

            OutlinedTextField(
                value = sourceUrlOrRepo,
                onValueChange = { sourceUrlOrRepo = it },
                label = {
                    Text(
                        stringResource(
                            if (sourceType == SourceType.GITHUB_RELEASES) {
                                R.string.field_github_repo
                            } else {
                                R.string.field_url
                            }
                        )
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            if (sourceType == SourceType.GITHUB_RELEASES) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.field_include_prereleases))
                    Switch(
                        checked = includePreReleases,
                        onCheckedChange = { includePreReleases = it }
                    )
                }
            }

            if (sourceType == SourceType.JSON_ENDPOINT) {
                OutlinedTextField(
                    value = jsonKey,
                    onValueChange = { jsonKey = it },
                    label = { Text(stringResource(R.string.field_json_key)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )
            }

            if (sourceType == SourceType.WEBPAGE_REGEX) {
                OutlinedTextField(
                    value = regexPattern,
                    onValueChange = { regexPattern = it },
                    label = { Text(stringResource(R.string.field_regex_pattern)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )
            }

            Text(
                text = stringResource(R.string.field_category),
                style = MaterialTheme.typography.labelLarge
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ServiceCategory.entries.forEach { cat ->
                    FilterChip(
                        selected = category == cat,
                        onClick = { category = cat },
                        label = { Text(stringResource(cat.labelRes)) }
                    )
                }
            }

            Text(
                text = stringResource(R.string.field_accent_color),
                style = MaterialTheme.typography.labelLarge
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AccentPalette.forEach { hex ->
                    val selected = accentColorHex == hex
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(hex))
                            .then(
                                if (selected) {
                                    Modifier.border(2.5.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                } else Modifier
                            )
                            .clickable { accentColorHex = hex }
                    )
                }
            }

            testResultText?.let { msg ->
                Surface(
                    color = if (testIsSuccess) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.errorContainer
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = msg,
                        color = if (testIsSuccess) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onErrorContainer
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        if (sourceUrlOrRepo.isBlank() || isTesting) return@OutlinedButton
                        scope.launch {
                            isTesting = true
                            testResultText = null
                            val result = onTestSource(buildCandidate())
                            isTesting = false
                            result.fold(
                                onSuccess = { rel ->
                                    testIsSuccess = true
                                    testResultText = "Detected version: ${rel.version}"
                                },
                                onFailure = { err ->
                                    testIsSuccess = false
                                    testResultText = "Failed: ${err.message}"
                                }
                            )
                        }
                    },
                    enabled = sourceUrlOrRepo.isNotBlank() && !isTesting
                ) {
                    if (isTesting) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(stringResource(R.string.btn_test_source))
                }

                Spacer(modifier = Modifier.weight(1f))

                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.btn_cancel))
                }
                Button(
                    onClick = {
                        onSave(buildCandidate())
                        onDismiss()
                    },
                    enabled = name.isNotBlank() && sourceUrlOrRepo.isNotBlank()
                ) {
                    Text(stringResource(R.string.btn_save))
                }
            }
        }
    }
}
