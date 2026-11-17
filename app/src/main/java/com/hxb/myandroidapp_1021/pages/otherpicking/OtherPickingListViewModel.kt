package com.hxb.myandroidapp_1021.pages.otherpicking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.domain.otherpicking.OtherPickingRepository
import com.hxb.myandroidapp_1021.utils.operationResult
import com.hxb.myandroidapp_1021.utils.userMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class OtherPickingListViewModel @Inject constructor(private val repository: OtherPickingRepository) :
    ViewModel() {
    private val _uiState = MutableStateFlow(OtherPickingListUiState())
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
        _uiState.update { it.copy(selectedStatus = PickingStatus.entries.getOrNull(value - 1)) }
    }

    fun updateTask(task: OtherPickingTaskUi?) {
        if (task == null) return
        _uiState.update { state ->
            state.copy(tasks = state.tasks.map { if (it.sourceNo == task.sourceNo && it.sourceType == task.sourceType) task else it })
        }
    }

    /**
     * 接收临时借出单的拣选任务；重复进入时保留已经完成的器具，不重复生成任务。
     */
    fun selectSource(sourceNo: String) {
        _uiState.update {
            it.copy(
                query = sourceNo,
                selectedSource = SourceType.TEMPORARY,
                selectedStatus = null
            )
        }
        refresh()
    }

    fun openTemporaryTask(task: OtherPickingTaskUi) {
        selectSource(task.sourceNo)
    }
}
