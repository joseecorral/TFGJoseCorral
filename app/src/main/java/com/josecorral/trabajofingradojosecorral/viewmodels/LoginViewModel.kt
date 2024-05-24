package com.josecorral.trabajofingradojosecorral.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _currentUser: MutableLiveData<FirebaseUser?> = MutableLiveData()
    val currentUser: LiveData<FirebaseUser?> = _currentUser

    private val _userRole: MutableLiveData<String?> = MutableLiveData()
    val userRole: LiveData<String?> = _userRole

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        _currentUser.value = user
        if (user != null) {
            loadUserRole(user.uid)
        }
    }

    init {
        // Agregar el Listener del estado de autenticación
        auth.addAuthStateListener(authStateListener)
        // Inicializar el valor de currentUser con el usuario actual
        _currentUser.value = auth.currentUser
        auth.currentUser?.uid?.let { loadUserRole(it) }
    }

    fun signIn(email: String, password: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    _currentUser.value = user
                    user?.uid?.let { loadUserRole(it) }
                }
            }
    }

    private fun loadUserRole(userId: String) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    _userRole.value = document.getString("role")
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error al obtener el rol del usuario", e)
            }
    }

    fun signOut() {
        auth.signOut()
        _currentUser.value = null
        _userRole.value = null
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
    }
}