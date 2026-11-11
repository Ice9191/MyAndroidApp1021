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
