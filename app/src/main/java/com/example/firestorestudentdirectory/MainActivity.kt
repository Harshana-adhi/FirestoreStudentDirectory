package com.example.firestorestudentdirectory

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.example.firestorestudentdirectory.databinding.ActivityMainBinding
import androidx.recyclerview.widget.LinearLayoutManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StudentAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = StudentAdapter(emptyList())
        binding.studentRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.studentRecyclerView.adapter = adapter

        loadStudents()

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
                loadStudents()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadStudents() {
        db.collection("students")
            .get()
            .addOnSuccessListener { result ->
                val students = result.map { document ->
                    Student(
                        name = document.getString("name") ?: "",
                        registrationNo = document.getString("registrationNo") ?: "",
                        department = document.getString("department") ?: ""
                    )
                }
                adapter.updateList(students)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to load students: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}