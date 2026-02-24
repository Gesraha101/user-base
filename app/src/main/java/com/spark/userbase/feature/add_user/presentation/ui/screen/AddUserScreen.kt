package com.spark.userbase.feature.add_user.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.spark.userbase.R
import com.spark.userbase.common.ui.theme.spacing
import com.spark.userbase.feature.add_user.presentation.ui.component.GenderDropdown
import com.spark.userbase.feature.add_user.presentation.ui.component.ValidatedTextField
import com.spark.userbase.feature.add_user.presentation.viewmodel.AddUserViewModel
import com.spark.userbase.feature.add_user.presentation.viewmodel.event.AddUserEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserScreen(
    onNavigateToUserList: () -> Unit,
    viewModel: AddUserViewModel = hiltViewModel(),
) {
    val form by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                AddUserEffect.NavigateToUserList -> onNavigateToUserList()
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.title_add_user)) }) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.sm)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smMd),
        ) {
            ValidatedTextField(
                value = form.name.value,
                onValueChange = form.name.onChanged,
                label = stringResource(R.string.label_name),
                errorMessage = form.name.displayError,
                onFocusLost = form.name.onFocusLost,
            )
            ValidatedTextField(
                value = form.age.value,
                onValueChange = form.age.onChanged,
                label = stringResource(R.string.label_age),
                errorMessage = form.age.displayError,
                keyboardType = KeyboardType.Number,
                onFocusLost = form.age.onFocusLost,
            )
            ValidatedTextField(
                value = form.jobTitle.value,
                onValueChange = form.jobTitle.onChanged,
                label = stringResource(R.string.label_job_title),
                errorMessage = form.jobTitle.displayError,
                onFocusLost = form.jobTitle.onFocusLost,
            )
            GenderDropdown(
                selectedGender = form.gender.value,
                onGenderSelected = form.gender.onChanged,
                label = stringResource(R.string.label_gender),
                options = form.genderOptions,
                errorMessage = form.gender.displayError,
                onFocusLost = form.gender.onFocusLost,
            )
            Button(
                onClick = form.onSubmit,
                enabled = form.isFormValid && !form.isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = MaterialTheme.spacing.xs),
            ) {
                Text(stringResource(R.string.button_add))
            }
        }
    }
}
