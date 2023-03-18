package com.nagpal.shivam.vtucslab.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navArgs
import com.nagpal.shivam.vtucslab.R
import com.nagpal.shivam.vtucslab.databinding.ActivityDetailsBinding
import com.nagpal.shivam.vtucslab.screens.programs.ProgramFragmentArgs

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private val detailsActivityArgs by navArgs<DetailsActivityArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navigation_host_fragment) as NavHostFragment

        val programFragmentArgs = ProgramFragmentArgs.Builder(
            detailsActivityArgs.baseUrl,
            detailsActivityArgs.fileName,
            detailsActivityArgs.title
        ).build()
        navHostFragment.navController.setGraph(
            R.navigation.details_navigation_graph,
            programFragmentArgs.toBundle()
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
