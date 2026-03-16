package com.xi.app.ui.gallery

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.xi.app.data.JournalRepository
import com.xi.app.databinding.FragmentGalleryBinding
import com.xi.app.ui.writing.ZenWritingActivity
import kotlinx.coroutines.launch

class GalleryFragment : Fragment() {
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private lateinit var repository: JournalRepository
    private val adapter = GalleryAdapter { entry ->
        val intent = Intent(requireContext(), ZenWritingActivity::class.java).apply {
            putExtra(ZenWritingActivity.EXTRA_PROMPT, entry.prompt)
            putExtra(ZenWritingActivity.EXTRA_CONTENT, entry.content)
        }
        startActivity(intent)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        repository = JournalRepository.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.galleryRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.galleryRecycler.adapter = adapter
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                repository.entries.collect { entries ->
                    adapter.submitList(entries)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
