package com.example.myapplication.ui

import com.example.myapplication.ui.model.GenderTypes

sealed interface MainActions {
    class SelectGender(val type: GenderTypes?): MainActions
    class SelectAge(val age: Int): MainActions
    object OnSubmit: MainActions
}