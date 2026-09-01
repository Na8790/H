package com.example.app

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class AdminPanelActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_panel)
        val container = findViewById<LinearLayout>(R.id.container)

        // list users (limited)
        db.collection("users").limit(50).get().addOnSuccessListener { snapshot ->
            for (doc in snapshot.documents) {
                val uid = doc.id
                val email = doc.getString("email") ?: ""
                val role = doc.getString("role") ?: "user"
                val isActive = doc.getBoolean("isActive") ?: true

                val tv = TextView(this)
                tv.text = "$email — $role — active:$isActive"
                container.addView(tv)

                val btnMakeAdmin = Button(this)
                btnMakeAdmin.text = "Make Admin"
                btnMakeAdmin.setOnClickListener {
                    db.collection("users").document(uid).update("role", "admin")
                }
                container.addView(btnMakeAdmin)

                val btnDisable = Button(this)
                btnDisable.text = if (isActive) "Disable" else "Enable"
                btnDisable.setOnClickListener {
                    db.collection("users").document(uid).update("isActive", !isActive)
                }
                container.addView(btnDisable)
            }
        }
    }
}
