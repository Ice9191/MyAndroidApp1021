package com.hxb.myandroidapp_1021.ui.scrapout

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ScrapOutFlowViewModel : ViewModel() {
    private val _selectedOrder = MutableStateFlow<ScrapOrder?>(null)
    val selectedOrder = _selectedOrder.asStateFlow()

    fun selectOrder(order: ScrapOrder) {
        _selectedOrder.value = order
    }
}
