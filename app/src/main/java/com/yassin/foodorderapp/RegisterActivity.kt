package com.yassin.foodorderapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.yassin.foodorderapp.databinding.ActivityRegisterBinding

/**
 * Lets a new user create an account (buyer or seller).
 *
 * Flow: validate input -> create the Firebase Auth account ->
 * save the profile in Realtime Database -> open HomeActivity.
 */
class RegisterActivity : AppCompatActivity() {

    // View binding gives type-safe access to the views (no findViewById)
    private lateinit var binding: ActivityRegisterBinding

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener { registerUser() }

        // "Already have an account?" goes back to LoginActivity. CLEAR_TOP reuses
        // the existing login screen instead of stacking a second copy on top.
        binding.tvLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }

    /** Validates the form, then creates the account in Firebase Auth. */
    private fun registerUser() {
        val name = binding.etName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val role = if (binding.rbSeller.isChecked) "seller" else "buyer"

        // Stop here if any field is invalid (validateInput shows the toast)
        if (!validateInput(name, phone, email, password)) return

        // Disable the button so the user cannot tap Register twice
        binding.btnRegister.isEnabled = false

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                // The new account's unique id becomes the key of the profile
                val uid = result.user?.uid ?: ""
                saveUser(User(uid, name, phone, email, role))
            }
            .addOnFailureListener { e ->
                showError(e.message)
            }
    }

    /**
     * Saves the profile at /users/{uid}, then moves on to the home screen.
     * If the save fails, the new account is rolled back (see rollbackAccount).
     */
    private fun saveUser(user: User) {
        database.getReference("users").child(user.uid).setValue(user)
            .addOnSuccessListener { openHome(user.role) }
            .addOnFailureListener { rollbackAccount() }
    }

    /**
     * Undoes a half-finished registration: the Auth account exists but its
     * profile could not be saved. Deleting the account (and signing out) means
     * no account is left without a role, so the user can simply register again.
     */
    private fun rollbackAccount() {
        // Even if the delete fails, we still sign out so the app does not
        // treat this role-less account as logged in.
        auth.currentUser?.delete()?.addOnCompleteListener { auth.signOut() }
            ?: auth.signOut()

        showError("Could not save your profile. Please try again.")
    }

    /** Opens HomeActivity and clears the back stack so Back cannot return here. */
    private fun openHome(role: String) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra(HomeActivity.EXTRA_ROLE, role)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    /**
     * Checks the input and shows a toast for the first problem found.
     * Returns true only if everything is valid.
     */
    private fun validateInput(
        name: String,
        phone: String,
        email: String,
        password: String
    ): Boolean {
        return when {
            name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty() -> {
                toast("Please fill in all fields")
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                toast("Please enter a valid email")
                false
            }
            password.length < 6 -> {
                toast("Password must be at least 6 characters")
                false
            }
            else -> true
        }
    }

    /** Shows a failure message and lets the user try again. */
    private fun showError(message: String?) {
        toast(message ?: "Something went wrong")
        binding.btnRegister.isEnabled = true
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
