package com.dor2500.updatepulse.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.NotificationsOff
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import com.dor2500.updatepulse.R
import com.dor2500.updatepulse.data.local.TrackedServiceEntity
import com.dor2500.updatepulse.data.model.ServiceCategory
import com.dor2500.updatepulse.data.model.ServiceRegion
import com.dor2500.updatepulse.data.model.SourceType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    services: List<TrackedServiceEntity>,
    onToggleSubscription: (TrackedServiceEntity) -> Unit,
    onToggleNotifications: (TrackedServiceEntity) -> Unit,
    onCheckService: (TrackedServiceEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ServiceCategory?>(null) }
    var selectedRegion by remember { mutableStateOf<ServiceRegion?>(null) }
    var selectedSourceType by remember { mutableStateOf<SourceType?>(null) }
    var subscribedOnly by remember { mutableStateOf(false) }

    val hasActiveFilters = searchQuery.isNotBlank() ||
        selectedCategory != null ||
        selectedRegion != null ||
        selectedSourceType != null ||
        subscribedOnly

    val catalogServices = remember(
        services,
        searchQuery,
        selectedCategory,
        selectedRegion,
        selectedSourceType,
        subscribedOnly
    ) {
        services
            .filter { !it.isCustom }
            .filter { !subscribedOnly || it.isSubscribed }
            .filter { selectedCategory == null || it.category == selectedCategory }
            .filter { selectedRegion == null || it.region == selectedRegion }
            .filter { selectedSourceType == null || it.sourceType == selectedSourceType }
            .filter {
                searchQuery.isBlank() ||
                    it.name.contains(searchQuery, ignoreCase = true) ||
                    it.sourceUrlOrRepo.contains(searchQuery, ignoreCase = true)
            }
    }

    val categories = remember {
        ServiceCategory.entries.filter { it != ServiceCategory.CUSTOM }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(stringResource(R.string.catalog_title)) },
            actions = {
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

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { selectedCategory = null },
                label = { Text(stringResource(R.string.catalog_all_categories)) }
            )
            categories.forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = {
                        selectedCategory = if (selectedCategory == category) null else category
                    },
                    label = { Text(stringResource(category.labelRes)) }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = subscribedOnly,
                onClick = { subscribedOnly = !subscribedOnly },
                label = { Text(stringResource(R.string.catalog_subscribed_only)) }
            )
            FilterChip(
                selected = selectedRegion == null,
                onClick = { selectedRegion = null },
                label = { Text(stringResource(R.string.catalog_all_regions)) }
            )
            ServiceRegion.entries.forEach { region ->
                FilterChip(
                    selected = selectedRegion == region,
                    onClick = {
                        selectedRegion = if (selectedRegion == region) null else region
                    },
                    label = { Text(stringResource(region.labelRes)) }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (hasActiveFilters) {
                FilterChip(
                    selected = false,
                    onClick = {
                        searchQuery = ""
                        selectedCategory = null
                        selectedRegion = null
                        selectedSourceType = null
                        subscribedOnly = false
                    },
                    label = { Text(stringResource(R.string.catalog_reset_filters)) }
                )
            }
            FilterChip(
                selected = selectedSourceType == null,
                onClick = { selectedSourceType = null },
                label = { Text(stringResource(R.string.catalog_all_sources)) }
            )
            SourceType.entries.forEach { source ->
                FilterChip(
                    selected = selectedSourceType == source,
                    onClick = {
                        selectedSourceType = if (selectedSourceType == source) null else source
                    },
                    label = { Text(stringResource(source.labelRes)) }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
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
}

@Composable
private fun CatalogServiceCard(
    service: TrackedServiceEntity,
    onToggle: () -> Unit,
    onToggleNotifications: () -> Unit,
    onCheckNow: () -> Unit
) {
    val accent = Color(service.accentColorHex)

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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = stringResource(service.sourceType.labelRes),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    if (service.region != ServiceRegion.GLOBAL) {
                        Surface(
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = stringResource(service.region.labelRes),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = service.lastKnownVersion?.let {
                            stringResource(R.string.catalog_latest_version, it)
                        } ?: stringResource(R.string.catalog_not_checked),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
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
