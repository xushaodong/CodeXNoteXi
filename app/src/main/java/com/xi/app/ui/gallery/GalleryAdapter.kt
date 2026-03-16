package com.xi.app.ui.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xi.app.data.JournalEntry
import com.xi.app.databinding.ItemGalleryBinding

class GalleryAdapter(
    private val onItemClick: (JournalEntry) -> Unit
) : RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {
    private val items = mutableListOf<JournalEntry>()

    fun submitList(data: List<JournalEntry>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val binding = ItemGalleryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GalleryViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) = 
        holder.bind(items[position], onItemClick)

    class GalleryViewHolder(private val binding: ItemGalleryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(entry: JournalEntry, onClick: (JournalEntry) -> Unit) {
            binding.dateText.text = entry.date
            binding.quoteText.text = "“${entry.quote}”"
            binding.contentText.text = entry.content
            
            binding.root.setOnClickListener { onClick(entry) }
        }
    }
}
