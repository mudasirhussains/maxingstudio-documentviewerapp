package com.mudassir.documentviewer.bottomnavigation.feedback

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.ads.AdRequest
import com.mudassir.documentviewer.R
import com.mudassir.documentviewer.databinding.ActivityFeedbackBinding

class FeedbackActivity : AppCompatActivity() {
    lateinit var binding: ActivityFeedbackBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        callBacks()
        val adRequest = AdRequest.Builder().build()
        binding.adViewDialog.loadAd(adRequest)

    }

    private fun callBacks() {
        binding.imgGoFeedback.setOnClickListener {
            finish()
        }

        binding.sendFeedback.setOnClickListener {
            binding.textFeedback.setText("")
            Toast.makeText(applicationContext, "Feedback Sent", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setBinding() {
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}