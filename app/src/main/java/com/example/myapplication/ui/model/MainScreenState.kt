package com.example.myapplication.ui.model

import com.example.myapplication.network.TestResponse

data class MainScreenState(
    val selectedGender: GenderTypes?,
    val selectedAge: Int?,
    val result: TestResponse?,
    val isSubmitEnabled: Boolean
) {

    fun isFormValid() = selectedGender != null && selectedAge != null

    companion object {
        val EMPTY = MainScreenState(
            selectedGender = null,
            selectedAge = null,
            result = null,
            isSubmitEnabled = false
        )
    }
}