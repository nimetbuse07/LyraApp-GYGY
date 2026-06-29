package com.nimetatila.lyraapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nimetatila.lyraapp.ui.icons.LyraIcons
import com.nimetatila.lyraapp.ui.theme.LyraAppTheme

/**
 * Home akışının durumlu (stateful) giriş noktası.
 */
@Composable
fun HomeRoute(
    onNavigateToDetails: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeEffect.NavigateToDetails -> onNavigateToDetails(effect.itemId)
                HomeEffect.NavigateToProfile -> onNavigateToProfile()
                is HomeEffect.ShowNotification -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    HomeScreen(
        state = uiState,
        onIntent = viewModel::onIntent,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

/**
 * Ana Sayfa ekranı. Tamamen durumsuzdur (stateless).
 */
@Composable
fun HomeScreen(
    state: HomeUiState,
    onIntent: (HomeIntent) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            state.currentPlayingTrack?.let { track ->
                MiniPlayer(
                    track = track,
                    isPlaying = state.isPlaying,
                    isFavorite = state.isFavorite,
                    onTogglePlay = { onIntent(HomeIntent.TogglePlayPause) },
                    onToggleFavorite = { onIntent(HomeIntent.ToggleFavorite) },
                )
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            item {
                HomeHeader(
                    userName = state.userName,
                    onProfileClick = { onIntent(HomeIntent.ProfileClicked) },
                )
            }

            item {
                Text(
                    text = "Ne dinlemek istersin?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
                QuickPicksGrid(
                    items = state.quickPicks,
                    onIntent = onIntent,
                )
            }

            item {
                SectionHeader(
                    title = "Son çalınanlar",
                    onSeeAllClick = { onIntent(HomeIntent.SeeAllRecentlyPlayedClicked) },
                )
                HorizontalTrackList(
                    items = state.recentlyPlayed,
                    onIntent = onIntent,
                )
            }

            item {
                SectionHeader(
                    title = "Senin için çalma listeleri",
                    onSeeAllClick = {},
                )
                HorizontalTrackList(
                    items = state.customPlaylists,
                    onIntent = onIntent,
                )
            }
        }
    }
}

@Composable
private fun HomeHeader(
    userName: String,
    onProfileClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = "İyi akşamlar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = userName.ifBlank { "Misafir" },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable(onClick = onProfileClick),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = userName.take(2).uppercase().ifBlank { "LY" },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
private fun QuickPicksGrid(
    items: List<PlayableItem>,
    onIntent: (HomeIntent) -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        items.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                rowItems.forEach { item ->
                    QuickPickCard(
                        item = item,
                        modifier = Modifier.weight(1f),
                        onClick = { onIntent(HomeIntent.QuickPickClicked(item.id)) },
                    )
                }
                if (rowItems.size == 1) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun QuickPickCard(
    item: PlayableItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val gradients = remember {
        listOf(
            listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0)),
            listOf(Color(0xFFED213A), Color(0xFF93291E)),
            listOf(Color(0xFF11998e), Color(0xFF38ef7d)),
        )
    }
    val currentGradient = gradients[item.gradientIndex % gradients.size]

    Row(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Brush.horizontalGradient(currentGradient))
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(Modifier.width(16.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    onSeeAllClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Tümü",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable(onClick = onSeeAllClick),
        )
    }
}

@Composable
private fun HorizontalTrackList(
    items: List<PlayableItem>,
    onIntent: (HomeIntent) -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(items, key = { it.id }) { item ->
            Column(
                modifier = Modifier
                    .width(140.dp)
                    .clickable { onIntent(HomeIntent.TrackClicked(item.id)) },
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.linearGradient(listOf(Color(0xFF434343), Color(0xFF000000)))),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = LyraIcons.Waveform,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp),
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                item.subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun MiniPlayer(
    track: PlayableItem,
    isPlaying: Boolean,
    isFavorite: Boolean,
    onTogglePlay: () -> Unit,
    onToggleFavorite: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
            .height(64.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 4.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = LyraIcons.Waveform,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                track.subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (isFavorite) LyraIcons.Favorite else LyraIcons.FavoriteBorder,
                    contentDescription = "Favori",
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                )
            }
            IconButton(onClick = onTogglePlay) {
                Icon(
                    imageVector = if (isPlaying) LyraIcons.Pause else LyraIcons.PlayArrow,
                    contentDescription = "Oynat/Durdur",
                )
            }
        }
    }
}

@Preview(name = "Home - Light", showBackground = true)
@Composable
private fun HomeScreenLightPreview() {
    LyraAppTheme(darkTheme = false) {
        HomeScreen(
            state = HomeUiState(
                userName = "Nimet Atila",
                quickPicks = listOf(PlayableItem("1", "Gece Sürüşü")),
            ),
            onIntent = {},
        )
    }
}