package com.josecorral.trabajofingradojosecorral.viewmodels

import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.josecorral.trabajofingradojosecorral.model.FirebaseAuthManager

class RegistroViewModel(private val authRepository: FirebaseAuthManager) : ViewModel() {

   
    fun signUp(email: String, password: String): Task<AuthResult> {
        return authRepository.signUp(email, password)
    }
}
