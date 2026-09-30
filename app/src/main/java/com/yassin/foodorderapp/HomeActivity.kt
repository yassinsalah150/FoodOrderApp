package com.yassin.foodorderapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yassin.foodorderapp.databinding.ActivityHomeBinding

/**
 * Placeholder home screen. Later it will show and edit the user's profile.
 * For now it only displays the role passed in by the previous screen.
 */
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Read the role sent by RegisterActivity ("buyer" or "seller")
        val role = intent.getStringExtra(EXTRA_ROLE) ?: ""
        binding.tvRole.text = "Role: $role"
    }

    companion object {
        // Key used to pass the role between activities
        const val EXTRA_ROLE = "extra_role"
    }
}
