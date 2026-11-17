package com.hxb.myandroidapp_1021.ui.pages.otherputaway

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.domain.otherputaway.OtherPutawayRepository
import com.hxb.myandroidapp_1021.utils.operationResult
import com.hxb.myandroidapp_1021.utils.userMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@HiltViewModel
class OtherPutawayListViewModel @Inject constructor(private val repository: OtherPutawayRepository) :
    ViewModel() {
    private val _uiState = MutableStateFlow(OtherPutawayListUiState())
    val uiState = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        viewModelScope.launch { repository.revision.collect { refresh() } }
    }

    fun refresh() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            operationResult { repository.tasks() }
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(
                            tasks = result.data,
                            isLoading = false,
                            fromCache = result.fromCache
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.userMessage()
                        )
                    }
                }
        }
    }

    fun onQueryChange(value: String) {
        _uiState.update { it.copy(query = value) }
    }

    fun onSourceChange(value: Int) {
        _uiState.update { it.copy(selectedSource = SourceType.entries.getOrNull(value - 1)) }
    }

    fun onStatusChange(value: Int) {
        _uiState.update { it.copy(selectedStatus = PutawayStatus.entries.getOrNull(value - 1)) }
    }

    // 将当前任务进度同步到列表。
    fun updateTask(task: OtherPutawayTaskUi?) {
        if (task == null) return
        _uiState.update { state ->
            state.copy(tasks = state.tasks.map { if (it.sourceNo == task.sourceNo && it.sourceType == task.sourceType) task else it })
        }
    }

}
