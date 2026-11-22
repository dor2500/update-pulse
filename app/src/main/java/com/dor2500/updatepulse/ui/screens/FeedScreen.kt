package com.dor2500.updatepulse.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.format.DateUtils
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dor2500.updatepulse.R
import com.dor2500.updatepulse.data.local.UpdateEventEntity
import com.dor2500.updatepulse.data.local.isBetaChannel
import com.dor2500.updatepulse.data.model.ReleaseChannel
import com.dor2500.updatepulse.data.model.ServiceCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    events: List<UpdateEventEntity>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onMarkRead: (Long) -> Unit,
    onMarkAllRead: () -> Unit,
    onClearFeed: () -> Unit
) {
    var showUnreadOnly by remember { mutableStateOf(false) }
    var selectedChannel by remember { mutableStateOf(ReleaseChannel.ALL) }
    var selectedCategory by remember { mutableStateOf<ServiceCategory?>(null) }
    var showSearch by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val availableCategories = remember(events) {
        events.map { it.category }.distinct()
    }

    val filtered = remember(events, showUnreadOnly, selectedChannel, selectedCategory, searchQuery) {
        events
            .filter { !showUnreadOnly || !it.isRead }
            .filter {
                when (selectedChannel) {
                    ReleaseChannel.ALL -> true
                    ReleaseChannel.REGULAR -> !it.isBetaChannel()
                    ReleaseChannel.BETA -> it.isBetaChannel()
                }
            }
            .filter { selectedCategory == null || it.category == selectedCategory }
            .filter {
                searchQuery.isBlank() ||
                    it.serviceName.contains(searchQuery, ignoreCase = true) ||
                    it.version.contains(searchQuery, ignoreCase = true) ||
                    it.changelog.contains(searchQuery, ignoreCase = true)
            }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(stringResource(R.string.feed_title)) },
            actions = {
                IconButton(onClick = {
                    showSearch = !showSearch
                    if (!showSearch) searchQuery = ""
                }) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = stringResource(R.string.feed_search_hint)
                    )
                }
                if (events.any { !it.isRead }) {
                    IconButton(onClick = onMarkAllRead) {
                        Icon(
                            imageVector = Icons.Rounded.DoneAll,
                            contentDescription = stringResource(R.string.feed_mark_all_read)
                        )
                    }
                }
                if (events.isNotEmpty()) {
                    IconButton(onClick = onClearFeed) {
                        Icon(
                            imageVector = Icons.Rounded.DeleteSweep,
                            contentDescription = stringResource(R.string.feed_clear_history)
                        )
                    }
                }
                IconButton(onClick = onRefresh) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = stringResource(R.string.feed_check_now)
                    )
                }
            }
        )

        if (showSearch) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                placeholder = { Text(stringResource(R.string.feed_search_hint)) },
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
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = !showUnreadOnly && selectedChannel == ReleaseChannel.ALL && selectedCategory == null,
                onClick = {
                    showUnreadOnly = false
                    selectedChannel = ReleaseChannel.ALL
                    selectedCategory = null
                },
                label = { Text(stringResource(R.string.feed_filter_all)) }
            )
            FilterChip(
                selected = showUnreadOnly,
                onClick = { showUnreadOnly = !showUnreadOnly },
                label = { Text(stringResource(R.string.feed_filter_unread)) }
            )
            FilterChip(
                selected = selectedChannel == ReleaseChannel.REGULAR,
                onClick = {
                    selectedChannel = if (selectedChannel == ReleaseChannel.REGULAR) {
                        ReleaseChannel.ALL
                    } else {
                        ReleaseChannel.REGULAR
                    }
                },
                label = { Text(stringResource(R.string.channel_regular)) }
            )
            FilterChip(
                selected = selectedChannel == ReleaseChannel.BETA,
                onClick = {
                    selectedChannel = if (selectedChannel == ReleaseChannel.BETA) {
                        ReleaseChannel.ALL
                    } else {
                        ReleaseChannel.BETA
                    }
                },
                label = { Text(stringResource(R.string.channel_beta)) }
            )
            if (availableCategories.size > 1) {
                availableCategories.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = {
                            selectedCategory = if (selectedCategory == category) null else category
                        },
                        label = { Text(stringResource(category.labelRes)) }
                    )
                }
            }
        }

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            if (filtered.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(R.drawable.ic_empty_feed),
                            contentDescription = null,
                            modifier = Modifier.size(140.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.feed_empty_title),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.feed_empty_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtered, key = { it.id }) { event ->
                        UpdateEventCard(
                            event = event,
                            onMarkRead = { onMarkRead(event.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UpdateEventCard(
    event: UpdateEventEntity,
    onMarkRead: () -> Unit
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val accent = Color(event.accentColorHex)
    val relativeTime = remember(event.detectedAt) {
        DateUtils.getRelativeTimeSpanString(
            event.detectedAt,
            System.currentTimeMillis(),
            DateUtils.MINUTE_IN_MILLIS
        ).toString()
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (!event.isRead) {
                MaterialTheme.colorScheme.surfaceContainerHigh
            } else {
                MaterialTheme.colorScheme.surfaceContainerLow
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(accent)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = event.serviceName,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${stringResource(event.category.labelRes)} • $relativeTime",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (!event.isRead) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.feed_new_badge),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Surface(
                    color = accent.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = event.version,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = accent,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            if (event.releaseTitle.isNotBlank() && event.releaseTitle != event.version) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = event.releaseTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = if (expanded) 4 else 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (event.changelog.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = event.changelog,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = if (expanded) 12 else 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!event.isRead) {
                    TextButton(onClick = onMarkRead) {
                        Text(stringResource(R.string.feed_mark_read))
                    }
                }
                val copiedFormat = stringResource(R.string.feed_copied_version, "${event.serviceName} ${event.version}")
                IconButton(onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(
                        ClipData.newPlainText("UpdatePulse Version", "${event.serviceName} ${event.version}")
                    )
                    Toast.makeText(context, copiedFormat, Toast.LENGTH_SHORT).show()
                }) {
                    Icon(
                        imageVector = Icons.Rounded.ContentCopy,
                        contentDescription = stringResource(R.string.feed_copy_version),
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "${event.serviceName} ${event.version}\n${event.releaseUrl}"
                        )
                    }
                    context.startActivity(Intent.createChooser(shareIntent, null))
                }) {
                    Icon(
                        imageVector = Icons.Rounded.Share,
                        contentDescription = stringResource(R.string.feed_share),
                        modifier = Modifier.size(18.dp)
                    )
                }
                AssistChip(
                    onClick = {
                        onMarkRead()
                        runCatching {
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse(event.releaseUrl))
                            )
                        }
                    },
                    label = { Text(stringResource(R.string.feed_open_link)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }
        }
    }
}
