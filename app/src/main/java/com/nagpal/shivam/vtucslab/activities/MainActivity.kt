package com.nagpal.shivam.vtucslab.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.nagpal.shivam.vtucslab.R
import com.nagpal.shivam.vtucslab.databinding.ActivityMainBinding
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navigation_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupActionBar()
        setupNavigationView()
        setupDrawerToggle()
    }

    private fun setupActionBar() {
        appBarConfiguration =
            AppBarConfiguration(setOf(R.id.repositoryFragment), binding.drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun setupNavigationView() {
        binding.navigationView.setupWithNavController(navController)
        binding.navigationView.setNavigationItemSelectedListener {
            var flag = false
            val itemId: Int = it.itemId
            if (itemId == R.id.repositoryFragment) {
                flag = true
            } else if (itemId == R.id.menu_item_exit) {
                exitApplication()
                flag = true
            }
            if (flag) {
                closeNavigationDrawer()
            }
            return@setNavigationItemSelectedListener flag
        }
    }

    private fun setupDrawerToggle() {
        actionBarDrawerToggle =
            ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                R.string.drawer_open,
                R.string.drawer_close,
            )
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        val drawerBackPressedCallback =
            onBackPressedDispatcher.addCallback(this, false) {
                closeNavigationDrawer()
            }
        binding.drawerLayout.addDrawerListener(
            object : DrawerLayout.DrawerListener {
                override fun onDrawerSlide(
                    drawerView: View,
                    slideOffset: Float,
                ) {
                }

                override fun onDrawerOpened(drawerView: View) {
                    drawerBackPressedCallback.isEnabled = true
                }

                override fun onDrawerClosed(drawerView: View) {
                    drawerBackPressedCallback.isEnabled = false
                }

                override fun onDrawerStateChanged(newState: Int) {
                }
            },
        )
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (appBarConfiguration.topLevelDestinations.contains(destination.id)) {
                binding.drawerLayout.setDrawerLockMode(LOCK_MODE_UNLOCKED, GravityCompat.START)
            } else {
                binding.drawerLayout.setDrawerLockMode(
                    LOCK_MODE_LOCKED_CLOSED,
                    GravityCompat.START,
                )
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home &&
            appBarConfiguration.topLevelDestinations.contains(
                navController.currentDestination?.id,
            )
        ) {
            actionBarDrawerToggle.syncState()
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
        }
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    private fun exitApplication() {
        finishAffinity()
        Handler(Looper.getMainLooper()).postDelayed({
            exitProcess(0)
        }, 1000)
    }

    private fun closeNavigationDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.START, true)
    }
}
