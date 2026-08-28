package com.example.firestorestudentdirectory

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.example.firestorestudentdirectory.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addStudentButton.setOnClickListener {
            addStudent()
        }
    }

    private fun addStudent() {
        val name = binding.nameEditText.text.toString().trim()
        val regNo = binding.regNoEditText.text.toString().trim()
        val dept = binding.deptEditText.text.toString().trim()

        if (name.isEmpty() || regNo.isEmpty() || dept.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val student = hashMapOf(
            "name" to name,
            "registrationNo" to regNo,
            "department" to dept
        )

        db.collection("students")
            .add(student)
            .addOnSuccessListener {
                Toast.makeText(this, "Student added", Toast.LENGTH_SHORT).show()
                binding.nameEditText.text.clear()
                binding.regNoEditText.text.clear()
                binding.deptEditText.text.clear()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}