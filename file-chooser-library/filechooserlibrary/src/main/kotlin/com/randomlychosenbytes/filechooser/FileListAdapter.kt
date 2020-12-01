package com.randomlychosenbytes.filechooser

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class FileListAdapter(
        private val context: Context,
        var files: List<File>,
        private val onFileSelected: (File) -> Unit
) : RecyclerView.Adapter<FileListAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ) = ItemViewHolder(LayoutInflater
            .from(parent.context)
            .inflate(R.layout.recyclerview_item_file, parent, false))

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val file = files[position]
        holder.fileNameTextView.text = file.name

        if (file.isFile) {
            holder.fileNameTextView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            holder.iconImageView.visibility = View.GONE
        } else {
            holder.fileNameTextView.setTextColor(ContextCompat.getColor(context, android.R.color.tertiary_text_dark))
            holder.iconImageView.visibility = View.VISIBLE
        }

        holder.v.setOnClickListener { onFileSelected(file) }
    }

    override fun getItemCount() = files.size

    inner class ItemViewHolder(var v: View) : RecyclerView.ViewHolder(v) {
        var iconImageView = v.findViewById<ImageView>(R.id.iconImageView)!!
        var fileNameTextView = v.findViewById<TextView>(R.id.fileNameTextView)!!
    }
}