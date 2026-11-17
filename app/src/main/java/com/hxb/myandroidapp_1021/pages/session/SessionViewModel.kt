package com.hxb.myandroidapp_1021.pages.session

import androidx.lifecycle.ViewModel
import com.hxb.myandroidapp_1021.domain.session.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(repository: SessionRepository) : ViewModel() {
    val state = repository.state
}
