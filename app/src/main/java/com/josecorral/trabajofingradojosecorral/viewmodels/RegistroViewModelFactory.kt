package com.josecorral.trabajofingradojosecorral.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.josecorral.trabajofingradojosecorral.model.FirebaseAuthManager


class RegistroViewModelFactory(
    private val authManager: FirebaseAuthManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistroViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegistroViewModel(authManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}