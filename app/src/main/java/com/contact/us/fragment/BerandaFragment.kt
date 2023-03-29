package com.contact.us.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.contact.us.LoginActivity
import com.contact.us.TaskActivity
import com.contact.us.adapter.AdapterTask
import com.contact.us.adapter.RecyclerViewTaskClickListener
import com.contact.us.model.Task
import com.contact.us.utils.Extensions.toast
import com.contact.us.utils.FirebaseUtils
import com.contant.us.R
import com.contant.us.databinding.DialogDetailTaskBinding
import com.contant.us.databinding.FragmentAkunBinding
import com.contant.us.databinding.FragmentBerandaBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*


class BerandaFragment : Fragment() , RecyclerViewTaskClickListener {

    private lateinit var binding: FragmentBerandaBinding
    private lateinit var bindingDialog: DialogDetailTaskBinding

    val inventoryAdapter = AdapterTask()
    var linearLayoutManager: LinearLayoutManager? = null
    private var database: DatabaseReference? = null

    private var listDataTask: MutableList<Task> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBerandaBinding.inflate(inflater, container, false)
        val root: View = binding!!.getRoot()

        loadrecylerviewBarang()
        loadfirebase()
        onChange()

        return root
    }

    private fun onChange() {
        binding.mbAddTask.setOnClickListener {
            startActivity(Intent(requireActivity(), TaskActivity::class.java))
        }
    }

    private fun loadfirebase() {
        FirebaseApp.initializeApp(requireActivity())
        database = FirebaseDatabase.getInstance().getReference("Task")
        database!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    listDataTask.clear()
                    for (dataSnapshot in snapshot.children){
                        val task = dataSnapshot.getValue(Task::class.java)
                        listDataTask.add(task!!)
                    }
                    inventoryAdapter.setView(listDataTask)
                    inventoryAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadrecylerviewBarang() {
        val recyclerView = binding!!.rvView

        // set click listener
        inventoryAdapter.listener = this

        linearLayoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        recyclerView.setLayoutManager(linearLayoutManager)
        recyclerView.setItemAnimator(DefaultItemAnimator())
        recyclerView.adapter = inventoryAdapter
    }

    override fun onItemClicked(view: View, pos: Int, task: Task) {
        bindingDialog = DialogDetailTaskBinding.inflate(LayoutInflater.from(activity))
        val messageBoxBuilder = AlertDialog.Builder(requireActivity()).setView(bindingDialog.getRoot())
        val messageBoxInstance = messageBoxBuilder.show()
        messageBoxInstance.window?.setBackgroundDrawableResource(android.R.color.transparent)

        bindingDialog.tvDeskripsi.setText(task.deskripsi)
    }
}