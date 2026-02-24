package com.spark.userbase.feature.list_users.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.spark.userbase.R
import com.spark.userbase.common.ui.theme.extraColors
import com.spark.userbase.feature.list_users.presentation.ui.component.UserItem
import com.spark.userbase.feature.list_users.presentation.ui.component.UserListItem
import com.spark.userbase.feature.list_users.presentation.viewmodel.ListUsersViewModel
import com.spark.userbase.feature.list_users.presentation.viewmodel.event.ListUsersEvent
import com.spark.userbase.feature.list_users.presentation.viewmodel.state.ListUsersUiState

@Composable
fun UserListScreen(
    viewModel: ListUsersViewModel = hiltViewModel(),
    onNavigateBackToAdd: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect {
            when (it) {
                is ListUsersEvent.NavigateToAdd -> onNavigateBackToAdd()
            }
        }
    }

    when (val s = state) {
        is ListUsersUiState.Success -> SuccessContent(s)
        is ListUsersUiState.Error -> ErrorContent(s)
        is ListUsersUiState.Loading -> LoadingContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SuccessContent(state: ListUsersUiState.Success) {

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.title_list_users)) }) },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.users) { user ->
                    UserListItem(
                        item = UserItem(
                            user = user,
                            genderColor = {
                                if (user.gender == "Male") MaterialTheme.extraColors.maleLabel
                                else MaterialTheme.extraColors.femaleLabel
                            },
                        ),
                    )
                }
            }

            FloatingActionButton(
                modifier = Modifier.align(Alignment.BottomEnd).padding(end = 16.dp, bottom = 16.dp),
                onClick = state.onAddClicked
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(error: ListUsersUiState.Error) {
    Column(verticalArrangement = Arrangement.Center) {
        Text(error.value, color = Color.Red)

        Button(onClick = error.onRetry) {
            Text(stringResource(R.string.retry))
        }
    }
}