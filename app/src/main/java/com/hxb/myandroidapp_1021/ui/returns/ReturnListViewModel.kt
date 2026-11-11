package com.hxb.myandroidapp_1021.ui.returns

import androidx.lifecycle.ViewModel
import com.hxb.myandroidapp_1021.data.models.FilterType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ReturnListViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(
        ReturnListUiState(
            orders = listOf(
                ReturnOrder(
                    "JC20261027006",
                    ReturnStatus.PARTIALLY_RETURNED,
                    "检修一班",
                    "张伟",
                    3,
                    "",
                    totalCount = 5,
                    returnedCount = 2
                ),
                ReturnOrder(
                    "JC20261027007",
                    ReturnStatus.UNRETURNED,
                    "维修二班",
                    "李明",
                    3,
                    "",
                    totalCount = 3,
                    returnedCount = 0
                ),
                ReturnOrder(
                    "JC20261027008",
                    ReturnStatus.PARTIALLY_RETURNED,
                    "电气班组",
                    "王健",
                    2,
                    "",
                    totalCount = 6,
                    returnedCount = 4
                ),
                ReturnOrder(
                    "JC20261027009",
                    ReturnStatus.PARTIALLY_RETURNED,
                    "运行一班",
                    "刘洋",
                    3,
                    "",
                    totalCount = 4,
                    returnedCount = 1
                ),
                ReturnOrder(
                    "JC20261027010",
                    ReturnStatus.UNRETURNED,
                    "热控班组",
                    "陈强",
                    2,
                    "",
                    totalCount = 2,
                    returnedCount = 0
                ),
                ReturnOrder(
                    "JC20261027011",
                    ReturnStatus.PARTIALLY_RETURNED,
                    "继保班",
                    "赵磊",
                    2,
                    "",
                    totalCount = 8,
                    returnedCount = 6
                ),
                ReturnOrder(
                    "JC20261027012",
                    ReturnStatus.PARTIALLY_RETURNED,
                    "综合班",
                    "孙浩",
                    0,
                    "",
                    totalCount = 3,
                    returnedCount = 3
                ),
                ReturnOrder(
                    "JC20261027013",
                    ReturnStatus.PARTIALLY_RETURNED,
                    "变电检修班",
                    "周杰",
                    4,
                    "",
                    totalCount = 6,
                    returnedCount = 2
                ),
                ReturnOrder(
                    "JC20261027014",
                    ReturnStatus.UNRETURNED,
                    "汽机本体班",
                    "吴刚",
                    5,
                    "",
                    totalCount = 5,
                    returnedCount = 0
                ),
                ReturnOrder(
                    "JC20261027015",
                    ReturnStatus.PARTIALLY_RETURNED,
                    "锅炉辅机班",
                    "郑爽",
                    2,
                    "",
                    totalCount = 7,
                    returnedCount = 5
                )
            )
        )
    )
    val uiState = _uiState.asStateFlow()

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onFilterChange(filter: FilterType) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }
}
