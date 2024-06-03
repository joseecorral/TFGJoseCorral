package com.josecorral.trabajofingradojosecorral.view.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.josecorral.trabajofingradojosecorral.R
import com.josecorral.trabajofingradojosecorral.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

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
