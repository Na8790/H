package com.example.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val emailInput = findViewById<EditText>(R.id.email)
        val passInput = findViewById<EditText>(R.id.password)
        val btnSignUp = findViewById<Button>(R.id.btnSignUp)
        val btnSignIn = findViewById<Button>(R.id.btnSignIn)

        btnSignUp.setOnClickListener {
            val email = emailInput.text.toString()
            val pass = passInput.text.toString()
            if (email.isBlank() || pass.isBlank()) return@setOnClickListener
            auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser!!.uid
                        // create user doc with default role = "user"
                        val userDoc = hashMapOf(
                            "email" to email,
                            "displayName" to "",
                            "role" to "user",
                            "isActive" to true,
                            "createdAt" to Date()
                        )
                        db.collection("users").document(uid).set(userDoc)
                            .addOnSuccessListener {
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Firestore error: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    } else {
                        Toast.makeText(this, "Signup failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        btnSignIn.setOnClickListener {
            val email = emailInput.text.toString()
            val pass = passInput.text.toString()
            if (email.isBlank() || pass.isBlank()) return@setOnClickListener
            auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Signin failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}
