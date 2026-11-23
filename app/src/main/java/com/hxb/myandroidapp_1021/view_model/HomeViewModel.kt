package com.hxb.myandroidapp_1021.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.models.PageQuestionItem
import com.hxb.myandroidapp_1021.utils.HttpClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    // 状态定义...
    private val _questionList = MutableStateFlow<List<PageQuestionItem>>(emptyList())
    val questionList = _questionList.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private var page = 1 // 当前页码

    fun loadQuestionList() {
        viewModelScope.launch {
            _loading.value = true
            try {
                // request 自动剥离 → 直接拿到 PageData<QuestionItem>
                val result = HttpClient.request {
                    HttpClient.apiService.getQuestionList(
                        type = 0, // 分类ID，0=全部，实际项目从 activeIndex 获取
                        page = page,
                        pageSize = 10,
                        questionBankType = "1"
                    )
                }
                // 第一页替换，后续页追加
                if (page == 1) {
                    _questionList.value = result.rows
                } else {
                    _questionList.value += result.rows
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            _loading.value = false
        }
    }
}