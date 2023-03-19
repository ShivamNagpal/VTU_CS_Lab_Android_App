package com.nagpal.shivam.vtucslab.screens.programs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.nagpal.shivam.vtucslab.R
import com.nagpal.shivam.vtucslab.adapters.ContentAdapter
import com.nagpal.shivam.vtucslab.databinding.FragmentProgramBinding
import com.nagpal.shivam.vtucslab.utilities.Constants
import com.nagpal.shivam.vtucslab.utilities.Stages
import kotlinx.coroutines.launch


class ProgramFragment : Fragment() {
    private var _binding: FragmentProgramBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProgramViewModel
    private lateinit var contentAdapter: ContentAdapter

    private val programFragmentArgs by navArgs<ProgramFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProgramBinding.inflate(inflater, container, false)
        setupViews()
        setupRepositoryAdapter()

        viewModel = ViewModelProvider(this)[ProgramViewModel::class.java]

        requireActivity().title = programFragmentArgs.title


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    binding.progressBar.visibility = View.GONE
                    binding.emptyTextView.visibility = View.GONE
                    when (it.stage) {
                        Stages.LOADING -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        Stages.SUCCEEDED -> {
                            if (it.labResponse!!.isValid) {
                                contentAdapter.clear()
                                contentAdapter.addAll(it.labResponse.labExperiments)
                            } else {
                                showErrorMessage(it.labResponse.invalidationMessage)
                            }
                        }
                        Stages.FAILED -> {
                            if (it.message == Constants.NO_ACTIVE_NETWORK) {
                                showErrorMessage(getString(R.string.no_internet_connection))
                            } else {
                                showErrorMessage(getString(R.string.error_occurred))
                            }
                        }
                    }
                }
            }
        }

        return binding.root
    }

    private fun showErrorMessage(message: String) {
        binding.emptyTextView.visibility = View.VISIBLE
        binding.emptyTextView.text = message
    }

    private fun setupRepositoryAdapter() {
        contentAdapter = ContentAdapter(requireContext(), ArrayList())
        contentAdapter.setItemClickHandler {
            val actionProgramFragmentToContentActivity =
                ProgramFragmentDirections.actionProgramFragmentToContentActivity(
                    viewModel.uiState.value.baseUrl!!,
                    it.fileName,
                    it.fileName
                )
            findNavController().navigate(actionProgramFragmentToContentActivity)
        }
        binding.recyclerView.adapter = contentAdapter
    }

    private fun setupViews() {
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.setHasFixedSize(true)
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadContent("${programFragmentArgs.baseUrl}/${programFragmentArgs.fileName}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
