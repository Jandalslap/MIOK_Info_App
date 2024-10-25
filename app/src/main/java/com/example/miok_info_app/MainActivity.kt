package com.example.miok_info_app

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import com.example.miok_info_app.ui.DisclaimerFragment
import com.example.miok_info_app.ui.SplashFragment
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Setup DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        drawerLayout.setScrimColor(Color.TRANSPARENT) // Set transparent scrim color

        // Setup the NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        NavigationUI.setupWithNavController(navView, navHostFragment.navController)

        // Set up Navigation Item Selection Listener
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_home -> {
                    // Navigate to HomeFragment
                    navHostFragment.navController.navigate(R.id.homeFragment) // Make sure the ID matches your nav graph
                }
                R.id.action_call_111 -> {
                    // Handle CALL 111 action here
                }
                R.id.action_emergency_hotlines -> {
                    // Handle EMERGENCY HOTLINES action here
                }
                R.id.action_oranga_tamariki -> {
                    // Open Oranga Tamariki website
                    val url = "https://www.orangatamariki.govt.nz/"
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(url)
                    startActivity(intent)

                }
                R.id.action_massey_university -> {
                    // Open Massey University website
                    val url = "https://www.massey.ac.nz/"
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(url)
                    startActivity(intent)
                }
            }
            drawerLayout.closeDrawer(GravityCompat.END) // Close the drawer after selection
            true
        }

        // Inflate the custom ActionBar layout
        val customActionBarView: View = LayoutInflater.from(this)
            .inflate(R.layout.custom_action_bar, null)

        // Set the custom ActionBar
        supportActionBar?.apply {
            displayOptions = androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM
            customView = customActionBarView
            setDisplayHomeAsUpEnabled(false) // Disable default back button
        }

        // Find the hamburger icon and set the click listener to open the drawer
        val hamburgerIcon: ImageView = customActionBarView.findViewById(R.id.hamburger_icon)
        hamburgerIcon.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END)
            } else {
                drawerLayout.openDrawer(GravityCompat.END) // Ensure the drawer opens from the right
            }
        }

        // Add a destination change listener
        navHostFragment.navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashFragment, R.id.disclaimerFragment -> {
                    supportActionBar?.hide() // Hide the action bar
                }
                else -> {
                    supportActionBar?.show() // Show the action bar
                }
            }
        }

        // Listener for drawer open and close events
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                // Handle drawer slide events if needed
            }

            override fun onDrawerOpened(drawerView: View) {
                hamburgerIcon.setImageResource(R.drawable.ic_close_white) // Change to 'X' icon
            }

            override fun onDrawerClosed(drawerView: View) {
                hamburgerIcon.setImageResource(R.drawable.ic_hamburger_menu) // Change back to hamburger icon
            }

            override fun onDrawerStateChanged(newState: Int) {
                // Handle state changes if needed
            }
        })

    }

    override fun onResume() {
        super.onResume()
        // Show or hide the navigation drawer based on the current fragment
        val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        navView.visibility = if (currentFragment is SplashFragment || currentFragment is DisclaimerFragment) {
            View.GONE // Hide the navigation drawer
        } else {
            View.VISIBLE // Show the navigation drawer
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return drawerLayout.isDrawerOpen(GravityCompat.END).also {
            if (it) {
                drawerLayout.closeDrawer(GravityCompat.END)
            }
        }
    }


}
