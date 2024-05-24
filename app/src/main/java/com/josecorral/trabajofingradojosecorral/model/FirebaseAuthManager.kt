package com.josecorral.trabajofingradojosecorral.model

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class FirebaseAuthManager {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signIn(email: String, password: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, password)
    }

    fun signUp(email: String, password: String): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email, password)
    }


    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUserName(): String? {
        val currentUser: FirebaseUser? = auth.currentUser
        return currentUser?.displayName
    }


}
