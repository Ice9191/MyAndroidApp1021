package com.hxb.myandroidapp_1021.ui.inventory

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class InventoryFlowViewModel : ViewModel() {
    private val _selectedOrder = MutableStateFlow<InventoryRecord?>(null)
    val selectedOrder = _selectedOrder.asStateFlow()

    fun selectOrder(order: InventoryRecord) {
        _selectedOrder.value = order
    }
}


fun loadDashboard(){
    if (_uiState.value.isLoading || _uiState.value.isLoggingOut) return
    _uiState.update { it.copy(isLoading = true, errorMessage = null) }
    loadJob = viewModelS\.la\
    try {
        val res = dashbo\.\
        val menuSet = res.me\,toMenuS\()
        _uiSt\.update\ {
            it.copy
        }
    }
}