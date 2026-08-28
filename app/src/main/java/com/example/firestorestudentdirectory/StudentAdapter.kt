package com.example.firestorestudentdirectory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StudentAdapter(
    private var students: List<Student>
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val regNoTextView: TextView = itemView.findViewById(R.id.regNoTextView)
        val deptTextView: TextView = itemView.findViewById(R.id.deptTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]
        holder.nameTextView.text = student.name
        holder.regNoTextView.text = "Reg No: ${student.registrationNo}"
        holder.deptTextView.text = "Dept: ${student.department}"
    }

    override fun getItemCount(): Int = students.size

    fun updateList(newStudents: List<Student>) {
        students = newStudents
        notifyDataSetChanged()
    }
}