package com.xi.app.ui.today

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.xi.app.core.PromptEngine
import com.xi.app.data.JournalRepository
import com.xi.app.databinding.FragmentTodayPromptBinding
import com.xi.app.ui.writing.ZenWritingActivity

class TodayPromptFragment : Fragment() {
    private var _binding: FragmentTodayPromptBinding? = null
    private val binding get() = _binding!!

    private val promptEngine = PromptEngine()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodayPromptBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prompt = promptEngine.promptFor(JournalRepository.streakDays())
        binding.promptText.text = prompt
        binding.dateText.text = promptEngine.todayDate()

        binding.root.setOnClickListener {
            startActivity(Intent(requireContext(), ZenWritingActivity::class.java).apply {
                putExtra(ZenWritingActivity.EXTRA_PROMPT, prompt)
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
