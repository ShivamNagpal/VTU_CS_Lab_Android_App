package com.nagpal.shivam.vtucslab.screens.programs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.nagpal.shivam.vtucslab.databinding.FragmentProgramBinding


class ProgramFragment : Fragment() {
    private var _binding: FragmentProgramBinding? = null
    private val binding get() = _binding!!

    private val programFragmentArgs by navArgs<ProgramFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProgramBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Toast.makeText(
            requireContext(),
            "${programFragmentArgs.title}\n${programFragmentArgs.fileName}\n${programFragmentArgs.baseUrl}",
            Toast.LENGTH_LONG
        ).show()
    }
}
