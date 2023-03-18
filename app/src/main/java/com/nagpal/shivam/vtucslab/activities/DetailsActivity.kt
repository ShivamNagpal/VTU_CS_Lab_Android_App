package com.nagpal.shivam.vtucslab.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import com.nagpal.shivam.vtucslab.R

class DetailsActivity : AppCompatActivity() {
    private val detailsActivityArgs by navArgs<DetailsActivityArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        Toast.makeText(
            this,
            "${detailsActivityArgs.title}\n${detailsActivityArgs.fileName}\n${detailsActivityArgs.baseUrl}",
            Toast.LENGTH_LONG
        ).show()

    }
}