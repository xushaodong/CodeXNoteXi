package com.xi.app.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.xi.app.data.JournalRepository
import com.xi.app.databinding.FragmentGalleryBinding

class GalleryFragment : Fragment() {
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private val adapter = GalleryAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.galleryRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.galleryRecycler.adapter = adapter
        adapter.submitList(JournalRepository.observeEntries().value)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
