package com.dor2500.uptrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.NotificationsOff
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dor2500.uptrack.R
import com.dor2500.uptrack.data.local.TrackedServiceEntity
import com.dor2500.uptrack.data.local.isBetaChannel
import com.dor2500.uptrack.data.model.ReleaseChannel
import com.dor2500.uptrack.data.model.ServiceCategory
import com.dor2500.uptrack.data.model.ServiceRegion
import com.dor2500.uptrack.data.model.SourceType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    services: List<TrackedServiceEntity>,
    isScanningApps: Boolean,
    onScanInstalledApps: () -> Unit,
    onToggleSubscription: (TrackedServiceEntity) -> Unit,
    onToggleNotifications: (TrackedServiceEntity) -> Unit,
    onCheckService: (TrackedServiceEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedChannel by remember { mutableStateOf(ReleaseChannel.ALL) }
    var selectedCategory by remember { mutableStateOf<ServiceCategory?>(null) }
    var selectedRegion by remember { mutableStateOf<ServiceRegion?>(null) }
    var selectedSourceType by remember { mutableStateOf<SourceType?>(null) }
    var subscribedOnly by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }

    val activeSheetFilterCount = remember(
        selectedChannel,
        selectedCategory,
        selectedRegion,
        selectedSourceType
    ) {
        var count = 0
        if (selectedChannel != ReleaseChannel.ALL) count++
        if (selectedCategory != null) count++
        if (selectedRegion != null) count++
        if (selectedSourceType != null) count++
        count
    }

    val catalogServices = remember(
        services,
        searchQuery,
        selectedChannel,
        selectedCategory,
        selectedRegion,
        selectedSourceType,
        subscribedOnly
    ) {
        services
            .filter { !subscribedOnly || it.isSubscribed }
            .filter {
                when (selectedChannel) {
                    ReleaseChannel.ALL -> true
                    ReleaseChannel.REGULAR -> !it.isBetaChannel()
                    ReleaseChannel.BETA -> it.isBetaChannel()
                }
            }
            .filter { selectedCategory == null || it.category == selectedCategory }
            .filter { selectedRegion == null || it.region == selectedRegion }
            .filter { selectedSourceType == null || it.sourceType == selectedSourceType }
            .filter {
                searchQuery.isBlank() ||
                    it.name.contains(searchQuery, ignoreCase = true) ||
                    it.sourceUrlOrRepo.contains(searchQuery, ignoreCase = true)
            }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(stringResource(R.string.catalog_title)) },
            actions = {
                IconButton(
                    onClick = onScanInstalledApps,
                    enabled = !isScanningApps
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PhoneAndroid,
                        contentDescription = stringResource(R.string.scan_apps_action)
                    )
                }
                Text(
                    text = stringResource(R.string.catalog_count, catalogServices.size),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = { Text(stringResource(R.string.catalog_search_hint)) },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Rounded.Clear, contentDescription = null)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledTonalButton(
                onClick = { showFilterSheet = true },
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.FilterList,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (activeSheetFilterCount > 0) {
                        stringResource(R.string.catalog_filter_button_count, activeSheetFilterCount)
                    } else {
                        stringResource(R.string.catalog_filter_button)
                    }
                )
            }

            FilterChip(
                selected = subscribedOnly,
                onClick = { subscribedOnly = !subscribedOnly },
                label = { Text(stringResource(R.string.catalog_subscribed_only)) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = 12.dp,
                end = 16.dp,
                bottom = 96.dp
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(catalogServices, key = { it.id }) { service ->
                CatalogServiceCard(
                    service = service,
                    onToggle = { onToggleSubscription(service) },
                    onToggleNotifications = { onToggleNotifications(service) },
                    onCheckNow = { onCheckService(service) }
                )
            }
        }
    }

    if (showFilterSheet) {
        CatalogFilterBottomSheet(
            initialChannel = selectedChannel,
            initialCategory = selectedCategory,
            initialRegion = selectedRegion,
            initialSourceType = selectedSourceType,
            onApply = { channel, category, region, sourceType ->
                selectedChannel = channel
                selectedCategory = category
                selectedRegion = region
                selectedSourceType = sourceType
                showFilterSheet = false
            },
            onDismiss = { showFilterSheet = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun CatalogFilterBottomSheet(
    initialChannel: ReleaseChannel,
    initialCategory: ServiceCategory?,
    initialRegion: ServiceRegion?,
    initialSourceType: SourceType?,
    onApply: (ReleaseChannel, ServiceCategory?, ServiceRegion?, SourceType?) -> Unit,
    onDismiss: () -> Unit
) {
    var draftChannel by remember { mutableStateOf(initialChannel) }
    var draftCategory by remember { mutableStateOf(initialCategory) }
    var draftRegion by remember { mutableStateOf(initialRegion) }
    var draftSourceType by remember { mutableStateOf(initialSourceType) }

    val categories = remember {
        ServiceCategory.entries.filter { it != ServiceCategory.BETA }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            Text(
                text = stringResource(R.string.catalog_filter_sheet_title),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FilterSection(title = stringResource(R.string.filter_section_channel)) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        ReleaseChannel.entries.forEach { channel ->
                            FilterChip(
                                selected = draftChannel == channel,
                                onClick = { draftChannel = channel },
                                label = { Text(stringResource(channel.labelRes)) }
                            )
                        }
                    }
                }

                FilterSection(title = stringResource(R.string.filter_section_category)) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FilterChip(
                            selected = draftCategory == null,
                            onClick = { draftCategory = null },
                            label = { Text(stringResource(R.string.catalog_all_categories)) }
                        )
                        categories.forEach { category ->
                            FilterChip(
                                selected = draftCategory == category,
                                onClick = {
                                    draftCategory = if (draftCategory == category) null else category
                                },
                                label = { Text(stringResource(category.labelRes)) }
                            )
                        }
                    }
                }

                FilterSection(title = stringResource(R.string.filter_section_region)) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FilterChip(
                            selected = draftRegion == null,
                            onClick = { draftRegion = null },
                            label = { Text(stringResource(R.string.catalog_all_regions)) }
                        )
                        ServiceRegion.entries.forEach { region ->
                            FilterChip(
                                selected = draftRegion == region,
                                onClick = {
                                    draftRegion = if (draftRegion == region) null else region
                                },
                                label = { Text(stringResource(region.labelRes)) }
                            )
                        }
                    }
                }

                FilterSection(title = stringResource(R.string.filter_section_source)) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FilterChip(
                            selected = draftSourceType == null,
                            onClick = { draftSourceType = null },
                            label = { Text(stringResource(R.string.catalog_all_sources)) }
                        )
                        SourceType.entries.forEach { source ->
                            FilterChip(
                                selected = draftSourceType == source,
                                onClick = {
                                    draftSourceType = if (draftSourceType == source) null else source
                                },
                                label = { Text(stringResource(source.labelRes)) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        draftChannel = ReleaseChannel.ALL
                        draftCategory = null
                        draftRegion = null
                        draftSourceType = null
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.catalog_reset_filters))
                }
                Button(
                    onClick = {
                        onApply(draftChannel, draftCategory, draftRegion, draftSourceType)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.btn_apply))
                }
            }
        }
    }
}

@Composable
private fun FilterSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        content()
    }
}

@Composable
private fun CatalogServiceCard(
    service: TrackedServiceEntity,
    onToggle: () -> Unit,
    onToggleNotifications: () -> Unit,
    onCheckNow: () -> Unit
) {
    val accent = Color(service.accentColorHex)
    val sourceLabel = stringResource(service.sourceType.labelRes)
    val regionLabel = if (service.region != ServiceRegion.GLOBAL) {
        stringResource(service.region.labelRes)
    } else {
        null
    }
    val versionLabel = service.lastKnownVersion?.let {
        stringResource(R.string.catalog_latest_version, it)
    } ?: stringResource(R.string.catalog_not_checked)
    val metadataLine = listOfNotNull(sourceLabel, regionLabel, versionLabel)
        .joinToString(" • ")

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (service.isSubscribed) {
                MaterialTheme.colorScheme.surfaceContainerHigh
            } else {
                MaterialTheme.colorScheme.surfaceContainerLow
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = service.name.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = accent
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = service.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = metadataLine,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                service.lastError?.takeIf { it.isNotBlank() }?.let { err ->
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.catalog_error, err),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
            if (service.isSubscribed) {
                IconButton(onClick = onCheckNow, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = stringResource(R.string.feed_check_now),
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(onClick = onToggleNotifications, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = if (service.notificationsEnabled) {
                            Icons.Rounded.Notifications
                        } else {
                            Icons.Rounded.NotificationsOff
                        },
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = if (service.notificationsEnabled) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
            Switch(
                checked = service.isSubscribed,
                onCheckedChange = { onToggle() }
            )
        }
    }
}
