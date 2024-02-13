package com.nagpal.shivam.vtucslab.screens.programs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.nagpal.shivam.vtucslab.R
import com.nagpal.shivam.vtucslab.adapters.ContentAdapter
import com.nagpal.shivam.vtucslab.databinding.FragmentProgramBinding
import com.nagpal.shivam.vtucslab.models.ContentFile
import com.nagpal.shivam.vtucslab.screens.UiEvent
import com.nagpal.shivam.vtucslab.screens.Utils
import com.nagpal.shivam.vtucslab.screens.Utils.asString
import com.nagpal.shivam.vtucslab.screens.Utils.safeNavigate
import com.nagpal.shivam.vtucslab.utilities.Stages
import kotlinx.coroutines.launch

class ProgramFragment : Fragment() {
    private var _binding: FragmentProgramBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: ProgramViewModel by viewModels { ProgramViewModel.Factory }
    private lateinit var contentAdapter: ContentAdapter

    private val programFragmentArgs by navArgs<ProgramFragmentArgs>()
    private var toast: Toast? = null

    private val url: String by lazy {
        return@lazy "${programFragmentArgs.baseUrl}/${programFragmentArgs.fileName}"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProgramBinding.inflate(inflater, container, false)
        setupMenuProvider()
        setupViews()
        setupRepositoryAdapter()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    binding.emptyTextView.visibility = View.GONE
                    toast =
                        Utils.showToast(
                            requireContext(),
                            toast,
                            it.toast,
                            viewModel,
                            UiEvent.ResetToast,
                        )

                    if (it.stage != Stages.LOADING) {
                        binding.swipeRefresh.isRefreshing = false
                    }

                    when (it.stage) {
                        Stages.LOADING -> {
                            binding.swipeRefresh.isRefreshing = true
                        }

                        Stages.SUCCEEDED -> {
                            if (it.data!!.isValid) {
                                contentAdapter.clear()
                                contentAdapter.addAll(it.data.labExperiments)
                            } else {
                                // TODO: Handle this logic in Data Layer
                                showErrorMessage(it.data.invalidationMessage)
                            }
                        }

                        Stages.FAILED -> {
                            it.errorMessage?.let { uiMessage ->
                                showErrorMessage(uiMessage.asString(requireContext()))
                            }
                        }
                    }
                }
            }
        }

        return binding.root
    }

    // TODO: Duplicate: Move to a static method
    private fun showErrorMessage(message: String?) {
        binding.emptyTextView.visibility = View.VISIBLE
        binding.emptyTextView.text = message
    }

    private fun setupMenuProvider() {
        requireActivity()
            .addMenuProvider(
                object : MenuProvider {
                    override fun onCreateMenu(
                        menu: Menu,
                        menuInflater: MenuInflater,
                    ) {
                        menuInflater.inflate(R.menu.menu_program_fragment, menu)
                    }

                    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                        return when (menuItem.itemId) {
                            R.id.menu_item_refresh -> {
                                viewModel.onEvent(UiEvent.RefreshContent(url))
                                true
                            }

                            else -> false
                        }
                    }
                },
                viewLifecycleOwner,
            )
    }

    private fun setupRepositoryAdapter() {
        contentAdapter = ContentAdapter(requireContext(), ArrayList())
        contentAdapter.setItemClickHandler(
            object : ContentAdapter.ItemClickHandler {
                override fun onContentFileClick(file: ContentFile) {
                    val actionProgramFragmentToDisplayFragment =
                        ProgramFragmentDirections.actionProgramFragmentToDisplayFragment(
                            viewModel.uiState.value.baseUrl!!,
                            file.fileName,
                            file.fileName,
                        )
                    findNavController().safeNavigate(actionProgramFragmentToDisplayFragment)
                }
            },
        )
        binding.recyclerView.adapter = contentAdapter
    }

    private fun setupViews() {
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.setHasFixedSize(true)
        binding.swipeRefresh.setOnRefreshListener { viewModel.onEvent(UiEvent.RefreshContent(url)) }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onEvent(UiEvent.LoadContent(url))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
