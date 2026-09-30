package com.yassin.foodorderapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.yassin.foodorderapp.databinding.ActivityHomeBinding

/**
 * Profile screen shown after login.
 *
 * Shows the user's role and email (read-only) and lets them edit their
 * name and phone. Also has a Logout button.
 */
class HomeActivity : AppCompatActivity() {

    // View binding gives type-safe access to the views (no findViewById)
    private lateinit var binding: ActivityHomeBinding

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    // Where this user's profile lives: /users/{uid} (set in onCreate)
    private lateinit var userRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Nobody signed in (should not normally happen): go back to login
        val uid = auth.currentUser?.uid
        if (uid == null) {
            openLogin()
            return
        }
        userRef = database.getReference("users").child(uid)

        // Show the role we were given right away; it is refreshed after loading
        binding.tvRole.text = "Role: ${intent.getStringExtra(EXTRA_ROLE) ?: ""}"

        // Save stays disabled until the profile has loaded, so we never
        // save empty fields over the real data
        binding.btnSave.isEnabled = false

        binding.btnSave.setOnClickListener { saveProfile() }
        binding.btnLogout.setOnClickListener { logout() }

        loadProfile()
    }

    /** Reads the profile once from /users/{uid} and fills in the screen. */
    private fun loadProfile() {
        userRef.get()
            .addOnSuccessListener { snapshot ->
                // Turn the database data into a User object
                val user = snapshot.getValue(User::class.java)
                if (user == null) {
                    toast("Profile not found")
                    return@addOnSuccessListener
                }
                binding.etName.setText(user.name)
                binding.etPhone.setText(user.phone)
                binding.tvEmail.text = "Email: ${user.email}"
                binding.tvRole.text = "Role: ${user.role}"
                binding.btnSave.isEnabled = true
            }
            .addOnFailureListener { e ->
                toast(e.message ?: "Could not load your profile")
            }
    }

    /** Validates the name, then updates only name and phone in the database. */
    private fun saveProfile() {
        val name = binding.etName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()

        // Name is required
        if (name.isEmpty()) {
            toast("Name cannot be empty")
            return
        }

        // Disable the button while saving so the user cannot tap it twice
        binding.btnSave.isEnabled = false

        // updateChildren changes only these two fields and leaves
        // uid, email and role untouched
        val updates = mapOf<String, Any>(
            "name" to name,
            "phone" to phone
        )
        userRef.updateChildren(updates)
            .addOnSuccessListener {
                toast("Profile saved")
                binding.btnSave.isEnabled = true
            }
            .addOnFailureListener { e ->
                toast(e.message ?: "Could not save your profile")
                binding.btnSave.isEnabled = true
            }
    }

    /** Signs the user out and returns to the login screen. */
    private fun logout() {
        auth.signOut()
        openLogin()
    }

    /** Opens LoginActivity and clears the back stack so Back cannot return here. */
    private fun openLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    companion object {
        // Key used to pass the role between activities
        const val EXTRA_ROLE = "extra_role"
    }
}
