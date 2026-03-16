package com.xi.app.ui.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xi.app.data.JournalEntry
import com.xi.app.databinding.ItemGalleryBinding

class GalleryAdapter : RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {
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

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) = holder.bind(items[position])

    class GalleryViewHolder(private val binding: ItemGalleryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(entry: JournalEntry) {
            binding.dateText.text = entry.date
            binding.quoteText.text = entry.quote
        }
    }
}
