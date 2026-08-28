package com.example.firestorestudentdirectory

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.example.firestorestudentdirectory.databinding.ActivityMainBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.content.Intent
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StudentAdapter
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = StudentAdapter(emptyList())
        binding.studentRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.studentRecyclerView.adapter = adapter

        val departments = listOf("All", "MIT", "PS", "CS", "bio science")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, departments)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.departmentSpinner.adapter = spinnerAdapter

        binding.departmentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val selected = departments[position]
                loadStudents(if (selected == "All") null else selected)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.addStudentButton.setOnClickListener {
            addStudent()
        }

        binding.signOutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
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
                //loadStudents() snapshot linstner handles this
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadStudents(department: String? = null) {
        binding.loadingProgressBar.visibility = android.view.View.VISIBLE
        binding.statusTextView.visibility = android.view.View.GONE

        val query = if (department != null) {
            db.collection("students").whereEqualTo("department", department)
        } else {
            db.collection("students")
        }

        query.addSnapshotListener { result, exception ->
            binding.loadingProgressBar.visibility = android.view.View.GONE

            if (exception != null) {
                showStatus("Something went wrong: ${exception.message}")
                return@addSnapshotListener
            }

            if (result != null) {
                val isFromCache = result.metadata.isFromCache
                val students = result.map { document ->
                    Student(
                        name = document.getString("name") ?: "",
                        registrationNo = document.getString("registrationNo") ?: "",
                        department = document.getString("department") ?: ""
                    )
                }
                adapter.updateList(students)

                when {
                    students.isEmpty() -> showStatus("No students found.")
                    isFromCache -> showStatus("Offline — showing last saved data.")
                    else -> binding.statusTextView.visibility = android.view.View.GONE
                }
            }
        }
    }

    private fun showStatus(message: String) {
        binding.statusTextView.text = message
        binding.statusTextView.visibility = android.view.View.VISIBLE
    }
}