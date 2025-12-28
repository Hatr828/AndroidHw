package com.example.androidhw

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment

class NameDetailFragment : Fragment(R.layout.fragment_name_detail) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val nameArg = arguments?.getString(ARG_NAME)
        val model = nameArg?.let { (activity as? MainActivity)?.findNameModel(it) }

        val unknown = getString(R.string.value_unknown)
        val displayName = model?.name ?: (nameArg ?: unknown)
        val nameDay = model?.nameDay ?: unknown
        val meaning = model?.meaning ?: unknown

        view.findViewById<TextView>(R.id.text_name).text =
            "${getString(R.string.detail_name_label)} $displayName"
        view.findViewById<TextView>(R.id.text_nameday).text =
            "${getString(R.string.detail_nameday_label)} $nameDay"
        view.findViewById<TextView>(R.id.text_meaning).text =
            "${getString(R.string.detail_meaning_label)} $meaning"

        view.findViewById<android.widget.Button>(R.id.button_back).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    companion object {
        private const val ARG_NAME = "arg_name"

        fun newInstance(name: String): NameDetailFragment {
            return NameDetailFragment().apply {
                arguments = Bundle().apply { putString(ARG_NAME, name) }
            }
        }
    }
}
