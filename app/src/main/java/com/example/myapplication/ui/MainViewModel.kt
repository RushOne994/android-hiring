package com.example.myapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.network.SocketManager
import com.example.myapplication.network.TestRequest
import com.example.myapplication.ui.model.GenderTypes
import com.example.myapplication.ui.model.MainScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val socketManager: SocketManager
) : ViewModel() {

    private val _screenState: MutableStateFlow<MainScreenState> =
        MutableStateFlow(MainScreenState.EMPTY)
    val screenState: StateFlow<MainScreenState> = _screenState.asStateFlow()

    fun handleAction(actions: MainActions) {
        when (actions) {
            is MainActions.SelectGender -> onGenderSelect(actions.type)
            is MainActions.SelectAge -> onAgeSelect(actions.age)
            MainActions.OnSubmit -> onSendClick()
        }
    }

    private fun onGenderSelect(type: GenderTypes?) {
        _screenState.update {
            it.copy(
                selectedGender = type
            )
        }
        validate()
    }

    private fun onAgeSelect(age: Int) {
        _screenState.update {
            it.copy(
                selectedAge = age
            )
        }
        validate()
    }

    private fun validate() {
        _screenState.update {
            it.copy(
                isSubmitEnabled = it.isFormValid()
            )
        }
    }

    private fun onSendClick() {
        viewModelScope.launch {
            socketManager.connect()
            socketManager.send(
                TestRequest(
                    gender = _screenState.value.selectedGender?.value.orEmpty(),
                    age = _screenState.value.selectedAge ?: 0
                )
            )
            val result = socketManager.receive()
            socketManager.close()
            _screenState.update {
                it.copy(
                    result = result
                )
            }
        }
    }
}