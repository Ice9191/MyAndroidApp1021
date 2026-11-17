package com.hxb.myandroidapp_1021.domain.common

import kotlinx.coroutines.flow.StateFlow

interface Repository {
    val revision: StateFlow<Long>
}
