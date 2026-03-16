package com.xi.app.ui.today

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.xi.app.core.PromptEngine
import com.xi.app.data.JournalRepository
import com.xi.app.databinding.FragmentTodayPromptBinding
import com.xi.app.ui.writing.ZenWritingActivity
import kotlinx.coroutines.launch

class TodayPromptFragment : Fragment() {
    private var _binding: FragmentTodayPromptBinding? = null
    private val binding get() = _binding!!
    private lateinit var repository: JournalRepository

    private val promptEngine = PromptEngine()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodayPromptBinding.inflate(inflater, container, false)
        repository = JournalRepository.getInstance(requireContext())
        return binding.root
    }

    /**
     * 在 onHiddenChanged 中监听 Fragment 的可见性变化
     * 确保每次点击底部导航回到主页时，都会随机刷新提示词
     */
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            refreshPrompt()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshPrompt()
    }

    private fun refreshPrompt() {
        viewLifecycleOwner.lifecycleScope.launch {
            val prompt = promptEngine.getRandomPrompt()
            binding.promptText.text = prompt
            binding.dateText.text = promptEngine.todayDate()

            // 点击整个页面进入写作模式
            binding.root.setOnClickListener {
                startActivity(Intent(requireContext(), ZenWritingActivity::class.java).apply {
                    putExtra(ZenWritingActivity.EXTRA_PROMPT, prompt)
                })
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
