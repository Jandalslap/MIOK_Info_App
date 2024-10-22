package com.example.miok_info_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup the NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        NavigationUI.setupActionBarWithNavController(this, navHostFragment.navController)

        // Hide the action bar for specific fragments
        navHostFragment.navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashFragment, R.id.disclaimerFragment -> {
                    supportActionBar?.hide() // Hide the action bar
                }
                else -> {
                    supportActionBar?.show() // Show the action bar for other fragments
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navController.navController.navigateUp() || super.onSupportNavigateUp()
    }
}
