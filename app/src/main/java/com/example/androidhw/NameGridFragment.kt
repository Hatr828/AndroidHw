package com.example.androidhw

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.GridView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit

class NameGridFragment : Fragment(R.layout.fragment_name_grid) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val gridView = view.findViewById<GridView>(R.id.grid_names)
        val names = (activity as? MainActivity)?.getAllNames().orEmpty()

        val adapter = ArrayAdapter(requireContext(), R.layout.item_name, R.id.text_name_item, names)
        gridView.adapter = adapter
        gridView.setOnItemClickListener { _, _, position, _ ->
            val selectedName = names[position]
            parentFragmentManager.commit {
                replace(R.id.fragment_container, NameDetailFragment.newInstance(selectedName))
                addToBackStack(null)
            }
        }
    }
}
