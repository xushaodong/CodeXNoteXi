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
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
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
        
        // 核心方案：依靠 RecyclerView 的 padding + clipToPadding=false
        // 1. 布局管理器设为水平
        binding.galleryRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.galleryRecycler.adapter = adapter
        
        // 2. 使用 LinearSnapHelper 强制居中对齐
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.galleryRecycler)

        // 3. 动态缩放效果，增加画廊纵深感
        binding.galleryRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val centerX = recyclerView.width / 2f
                if (centerX <= 0) return
                
                for (i in 0 until recyclerView.childCount) {
                    val child = recyclerView.getChildAt(i)
                    val childCenterX = (child.left + child.right) / 2f
                    val distanceFromCenter = Math.abs(centerX - childCenterX)
                    
                    // 计算缩放：距离中心越远越小。最大缩小到 0.85，最小透明度 0.6
                    val scale = 1f - (distanceFromCenter / centerX) * 0.15f
                    val finalScale = Math.max(0.85f, scale)
                    child.scaleX = finalScale
                    child.scaleY = finalScale
                    child.alpha = Math.max(0.6f, finalScale)
                }
            }
        })
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                repository.entries.collect { entries ->
                    adapter.submitList(entries)
                    // 确保数据加载后触发一次居中对齐
                    binding.galleryRecycler.post {
                        val view = binding.galleryRecycler.getChildAt(0)
                        if (view != null) {
                            val snapView = snapHelper.findSnapView(binding.galleryRecycler.layoutManager)
                            if (snapView == null) {
                                binding.galleryRecycler.smoothScrollBy(1, 0)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
