package com.nagpal.shivam.vtucslab.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import androidx.activity.addCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import com.google.android.material.navigation.NavigationView
import com.nagpal.shivam.vtucslab.R
import com.nagpal.shivam.vtucslab.databinding.ActivityMainBinding
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.coordinator.toolbar)
        setupDrawerToggle()
        setupNavigationView()
    }

    private fun setupNavigationView() {
        val navigationView = binding.navigationView
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.menu.getItem(0).isChecked = true

        val headerView = navigationView.getHeaderView(0)
        val navigationDrawerBackButton =
            headerView.findViewById<ImageButton>(R.id.close_drawer)
        navigationDrawerBackButton.setOnClickListener { closeNavigationDrawer() }
    }

    private fun setupDrawerToggle() {
        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.coordinator.toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        )
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        val drawerBackPressedCallback = onBackPressedDispatcher.addCallback(this, false) {
            closeNavigationDrawer()
        }
        binding.drawerLayout.addDrawerListener(object : DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerOpened(drawerView: View) {
                drawerBackPressedCallback.isEnabled = true
            }

            override fun onDrawerClosed(drawerView: View) {
                drawerBackPressedCallback.isEnabled = false
            }

            override fun onDrawerStateChanged(newState: Int) {
            }
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var flag = false
        val itemId: Int = item.itemId
        if (itemId == R.id.menu_item_repository) {
            flag = true
        } else if (itemId == R.id.menu_item_exit) {
            exitApplication()
            flag = true
        }
        if (flag) {
            closeNavigationDrawer()
        }
        return flag
    }

    private fun exitApplication() {
        finishAffinity()
        Handler(Looper.getMainLooper()).postDelayed({
            exitProcess(
                0
            )
        }, 1000)
    }

    private fun closeNavigationDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.START, true)
    }
}
