package com.josecorral.trabajofingradojosecorral.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.josecorral.trabajofingradojosecorral.R
import com.josecorral.trabajofingradojosecorral.databinding.ActivityMainBinding
import com.josecorral.trabajofingradojosecorral.databinding.FragmentLoginBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

       val navHostFragment=supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController=navHostFragment.navController

        binding.NavBar.setupWithNavController(navController)


        binding.NavBar.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.nav_home -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.homeFragmen)
                    true
                }
                R.id.nav_qr -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.qrFragment)
                    true
                }
                R.id.nav_fastfood -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.pedidoFragment)
                    true
                }
                R.id.nav_person -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.loginFragment)
                    true
                }
                else -> false
            }
        }
    }
}
