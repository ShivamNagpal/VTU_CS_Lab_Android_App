package com.nagpal.shivam.vtucslab.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navArgs
import com.nagpal.shivam.vtucslab.R
import com.nagpal.shivam.vtucslab.databinding.ActivityContentBinding
import com.nagpal.shivam.vtucslab.screens.display.DisplayFragmentArgs

class ContentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContentBinding
    private val contentActivityArgs by navArgs<ContentActivityArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navigation_host_fragment) as NavHostFragment

        val displayFragmentArgs = DisplayFragmentArgs.Builder(
            contentActivityArgs.baseUrl,
            contentActivityArgs.fileName,
            contentActivityArgs.title
        ).build()

        navHostFragment.navController.setGraph(
            R.navigation.content_navigation_graph,
            displayFragmentArgs.toBundle()
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            this.onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
