package com.hxb.myandroidapp_1021.ui.otherpicking

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OtherPickingConfirmViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OtherPickingConfirmUiState())
    val uiState = _uiState.asStateFlow()

    fun onDialogChange(value: Boolean) { _uiState.update { it.copy(showDialog = value) } }
}
