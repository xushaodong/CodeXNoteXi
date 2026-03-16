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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewLifecycleOwner.lifecycleScope.launch {
            val streak = repository.streakDays()
            val prompt = promptEngine.promptFor(streak)
            binding.promptText.text = prompt
            binding.dateText.text = promptEngine.todayDate()

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
