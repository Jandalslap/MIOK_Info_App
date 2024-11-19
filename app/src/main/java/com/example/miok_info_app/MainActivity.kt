package com.example.miok_info_app

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import com.example.miok_info_app.ui.DisclaimerFragment
import com.example.miok_info_app.ui.SplashFragment
import com.example.miok_info_app.viewmodel.SharedViewModel
import com.google.firebase.FirebaseApp

// MainActivity serves as the entry point to the app and handles navigation and Firebase initialization
class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout // Drawer layout component for the navigation drawer
    private lateinit var navView: NavigationView // Navigation view for handling drawer menu items

    // Obtain SharedViewModel instance
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Set the main layout for this activity

        // Observe currentLanguage LiveData for updates
        sharedViewModel.currentLanguage.observe(this) { language ->
            // Call the function to update the navigation menu language
            updateNavigationMenuLanguage(language)
        }

        // Initialize Firebase Services
        FirebaseApp.initializeApp(this)

        // Setup DrawerLayout and NavigationView references
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        drawerLayout.setScrimColor(Color.TRANSPARENT) // Set scrim color to transparent for drawer background

        // Setup NavHostFragment to manage fragment navigation
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        NavigationUI.setupWithNavController(navView, navHostFragment.navController)

        // Set up navigation item selection listener for the drawer menu
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_home -> {
                    // Navigate to HomeFragment
                    navHostFragment.navController.navigate(R.id.homeFragment) // Make sure the ID matches your nav graph
                }
                R.id.action_call_111 -> {
                    // Handle CALL 111 action here
                    val phoneNumber = "0273408401" // The number to dial
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:$phoneNumber") // Use the "tel:" URI scheme
                    }
                    startActivity(intent) // Start the dialer activity
                }
                R.id.action_emergency_hotlines -> {
                    // Open Oranga Tamariki emergency hotline webpage
                    val url = "https://www.orangatamariki.govt.nz/about-us/contact-us/"
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(url)
                    startActivity(intent)
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

                R.id.about_page -> {
                    // Navigate to AboutFragment using NavController
                    val navController = findNavController(R.id.nav_host_fragment)
                    navController.navigate(R.id.aboutFragment)
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
            setDisplayHomeAsUpEnabled(false)

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

    // Shows or hides the navigation drawer based on whether the current fragment is SplashFragment or DisclaimerFragment.
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

    // Closes the navigation drawer if it is open when navigating "up." (back)
    override fun onSupportNavigateUp(): Boolean {
        return drawerLayout.isDrawerOpen(GravityCompat.END).also {
            if (it) {
                drawerLayout.closeDrawer(GravityCompat.END)
            }
        }
    }
    // Handle the back arrow click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> { // This is the home icon
                // Navigate to the home fragment or activity
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                val navController = navHostFragment.navController
                navController.navigate(R.id.homeFragment)
                true
            }
            else -> super.onOptionsItemSelected(item) // Handle other menu item clicks
        }
    }

    // Function to update navigation menu language based on the current language
    private fun updateNavigationMenuLanguage(language: String) {
        val navView = findViewById<NavigationView>(R.id.nav_view)
        when (language) {
            "English" -> {
                navView.menu.findItem(R.id.action_home).title = getString(R.string.nav_home)
                navView.menu.findItem(R.id.action_call_111).title = getString(R.string.nav_call_111)
                navView.menu.findItem(R.id.action_emergency_hotlines).title = getString(R.string.nav_emergency_hotlines)
                navView.menu.findItem(R.id.about_page).title = getString(R.string.about_page)
            }
            "Māori" -> {
                navView.menu.findItem(R.id.action_home).title = getString(R.string.nav_home_mr)
                navView.menu.findItem(R.id.action_call_111).title = getString(R.string.nav_call_111_mr)
                navView.menu.findItem(R.id.action_emergency_hotlines).title = getString(R.string.nav_emergency_hotlines_mr)
                navView.menu.findItem(R.id.about_page).title = getString(R.string.about_page_mr)
            }
        }
    }

}
