package com.contact.us.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.contact.us.model.Task
import com.contant.us.databinding.ListTaskBinding

class AdapterTask(
    private val resTransaksi: MutableList<Task> = mutableListOf()
) : RecyclerView.Adapter<AdapterTask.ViewHolder>() {

    var listener: RecyclerViewTaskClickListener? = null
    private var viewBinding: ListTaskBinding? = null
    lateinit var mContext: Context

    private var isLoadingAdded: Boolean = false

    inner class ViewHolder(
        val itemCategoriBinding: ListTaskBinding
    ) : RecyclerView.ViewHolder(itemCategoriBinding.root)

    // untuk mendapatkan jumlah data yang dimasukkan ke dalam adapter
    override fun getItemCount() = resTransaksi.size

    // untuk membuat setiap item recyclerview berdasarkan jumlah data yang dimasukkan ke dalam adapter
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        viewBinding =
            ListTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(viewBinding!!)
    }

    fun setView(resProductNew: List<Task>) {
        resTransaksi.clear() // clear list
        resTransaksi.addAll(resProductNew)
        notifyDataSetChanged() // let your adapter know about the changes and reload view.
    }

    // untuk memasukkan atau set data ke dalam view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemCategoriBinding.tvJudulTask.setText(resTransaksi[position].nama_judul)
        holder.itemCategoriBinding.tvBayaran.setDoubleValue(resTransaksi[position].harga.toDouble())
        holder.itemCategoriBinding.tvDeadLine.setText(resTransaksi[position].deadline)

        // event onclick pada setiap item
        holder.itemCategoriBinding.layout.setOnClickListener {
            listener?.onItemClicked(it,position, resTransaksi[position])
        }

        holder.itemCategoriBinding.tvJudulTask.setOnClickListener {
            listener?.onItemClicked(it,position, resTransaksi[position])
        }
        holder.itemCategoriBinding.tvBayaran.setOnClickListener {
            listener?.onItemClicked(it,position, resTransaksi[position])
        }
        holder.itemCategoriBinding.tvDeadLine.setOnClickListener {
            listener?.onItemClicked(it,position, resTransaksi[position])
        }

    }
}