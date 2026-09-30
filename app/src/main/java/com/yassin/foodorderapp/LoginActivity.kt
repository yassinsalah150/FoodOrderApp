package com.yassin.foodorderapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.yassin.foodorderapp.databinding.ActivityLoginBinding

/**
 * Launcher screen. Lets an existing user log in.
 *
 * Flow: sign in with Firebase Auth -> read the user's role from
 * Realtime Database -> open HomeActivity.
 * If a user is already signed in, the login form is skipped.
 */
class LoginActivity : AppCompatActivity() {

    // View binding gives type-safe access to the views (no findViewById)
    private lateinit var binding: ActivityLoginBinding

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener { loginUser() }

        // "Create account" opens the sign-up screen (explicit intent)
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()

        // If someone is already signed in, skip the form and go straight home
        val currentUser = auth.currentUser
        if (currentUser != null) {
            binding.btnLogin.isEnabled = false
            loadRoleAndOpenHome(currentUser.uid)
        }
    }

    /** Checks the input, then signs in with Firebase Auth. */
    private fun loginUser() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        // Both fields are required
        if (email.isEmpty() || password.isEmpty()) {
            toast("Please enter your email and password")
            return
        }

        // Disable the button so the user cannot tap Login twice
        binding.btnLogin.isEnabled = false

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                loadRoleAndOpenHome(result.user?.uid ?: "")
            }
            .addOnFailureListener { e ->
                showError(e.message)
            }
    }

    /**
     * Reads users/{uid}/role and opens HomeActivity with it.
     * If the role is missing, the user is signed out and told why.
     */
    private fun loadRoleAndOpenHome(uid: String) {
        database.getReference("users").child(uid).child("role").get()
            .addOnSuccessListener { snapshot ->
                val role = snapshot.getValue(String::class.java)
                if (role.isNullOrEmpty()) {
                    // No profile data for this account: sign out so it does not stay logged in
                    auth.signOut()
                    showError("No role found for this account")
                } else {
                    openHome(role)
                }
            }
            .addOnFailureListener { e ->
                showError(e.message)
            }
    }

    /** Opens HomeActivity and clears the back stack so Back cannot return here. */
    private fun openHome(role: String) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra(HomeActivity.EXTRA_ROLE, role)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    /** Shows a failure message and lets the user try again. */
    private fun showError(message: String?) {
        toast(message ?: "Something went wrong")
        binding.btnLogin.isEnabled = true
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
