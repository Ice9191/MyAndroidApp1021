package com.hxb.myandroidapp_1021.ui.otherputaway

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OtherPutawayConfirmViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OtherPutawayConfirmUiState())
    val uiState = _uiState.asStateFlow()

    fun onDialogChange(value: Boolean) {
        _uiState.update { it.copy(showDialog = value) }
    }
    fun onConfirm() {
        _uiState.update { it.copy(showDialog = false, completionPending = true) }
    }

    fun onCompletionHandled() {
        _uiState.update { it.copy(completionPending = false) }
    }
}
