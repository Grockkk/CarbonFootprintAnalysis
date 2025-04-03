package com.study.mastersdegree.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.study.mastersdegree.databinding.FragmentAboutBinding

class FragmentAbout : Fragment() {

    private var binding: FragmentAboutBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentAboutBinding.inflate(inflater, container, false)
        val root: View = binding!!.root
        setupExpandableSection(binding!!.question1, binding!!.answer1)
        setupExpandableSection(binding!!.question2, binding!!.answer2)
        setupExpandableSection(binding!!.question3, binding!!.answer3)
        setupExpandableSection(binding!!.question4, binding!!.answer4)
        setupExpandableSection(binding!!.question5, binding!!.answer5)

        setupExpandableSection(binding!!.answer1, binding!!.answer1)
        setupExpandableSection(binding!!.answer2, binding!!.answer2)
        setupExpandableSection(binding!!.answer3, binding!!.answer3)
        setupExpandableSection(binding!!.answer4, binding!!.answer4)
        setupExpandableSection(binding!!.answer5, binding!!.answer5)

        return root
    }

    private fun setupExpandableSection(question: TextView, answer: TextView) {
        question.setOnClickListener {
            answer.visibility = if (answer.visibility == View.GONE) View.VISIBLE else View.GONE

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}