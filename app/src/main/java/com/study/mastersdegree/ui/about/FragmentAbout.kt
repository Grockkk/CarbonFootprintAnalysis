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