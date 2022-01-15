package com.notes.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.notes.data.NoteDbo

class DetailsViewModel : ViewModel() {

    sealed class State {
        object New: State()
        data class Update(val note: NoteDbo): State()
    }

    private var _state = MutableLiveData<State>()
    val state: LiveData<State> = _state
    fun setState(state: State) {
        _state.value = state
    }

}