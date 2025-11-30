package com.coinsocket.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coinsocket.domain.model.Coin

@Composable
fun DashboardRoute(
    viewModel: CoinViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    DashboardScreen(
        state = state,
        onSortChange = viewModel::onSortChange
    )
}

@Composable
fun DashboardScreen(
    state: CoinUiState,
    onSortChange: (SortOption) -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SortChip(text = "Name") { onSortChange(SortOption.Name) }
                SortChip(text = "Price") { onSortChange(SortOption.Price) }
                SortChip(text = "Change %") { onSortChange(SortOption.Change) }
            }
            when (state) {
                is CoinUiState.Loading -> {
                    LoadingView()
                }
                is CoinUiState.Error -> {
                    ErrorView(message = state.message)
                }
                is CoinUiState.Success -> {
                    CoinList(coins = state.coins)
                }
            }
        }
    }
}

@Composable
fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(48.dp)
                .semantics { contentDescription = "Loading coin prices" },
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeWidth = 4.dp,
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
fun ErrorView(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
fun CoinList(coins: List<Coin>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(
            items = coins,
            key = { it.symbol }
        ) { coin ->
            CoinItem(coin = coin)
        }
    }
}

@Composable
fun SortChip(text: String, onClick: () -> Unit) {
    SuggestionChip(
        onClick = onClick,
        label = { Text(text) }
    )
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    MaterialTheme {
        DashboardScreen(
            state = CoinUiState.Success(
                listOf(
                    Coin("BTC", 50000.0, 51000.0, 49000.0, 49500.0),
                    Coin("ETH", 3000.0, 3100.0, 2900.0, 2950.0)
                )
            ),
            onSortChange = {  }
        )
    }
}