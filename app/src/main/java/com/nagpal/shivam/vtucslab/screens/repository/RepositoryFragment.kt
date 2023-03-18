package com.nagpal.shivam.vtucslab.screens.repository

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nagpal.shivam.vtucslab.R
import com.nagpal.shivam.vtucslab.adapters.NavigationAdapter
import com.nagpal.shivam.vtucslab.databinding.FragmentRepositoryBinding
import com.nagpal.shivam.vtucslab.utilities.Constants
import com.nagpal.shivam.vtucslab.utilities.Stages
import kotlinx.coroutines.launch

class RepositoryFragment : Fragment() {
    private var _binding: FragmentRepositoryBinding? = null
    private lateinit var navigationAdapter: NavigationAdapter

    private val binding get() = _binding!!
    private lateinit var viewModel: RepositoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRepositoryBinding.inflate(inflater, container, false)
        setupMenuProvider()
        setupViews()
        setupRepositoryAdapter()

        viewModel = ViewModelProvider(this)[RepositoryViewModel::class.java]


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
                                navigationAdapter.clear()
                                navigationAdapter.addAll(it.labResponse.laboratories)
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

    private fun setupViews() {
        binding.repositoryRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.repositoryRecyclerView.setHasFixedSize(true)
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadContent(Constants.INDEX_REPOSITORY_URL)
    }

    private fun setupRepositoryAdapter() {
        navigationAdapter = NavigationAdapter(requireContext(), ArrayList())
        navigationAdapter.setNavigationAdapterItemClickHandler { laboratory, _ ->
            val actionRepositoryFragmentToDetailsActivity =
                RepositoryFragmentDirections.actionRepositoryFragmentToDetailsActivity(
                    viewModel.uiState.value.baseUrl!!,
                    laboratory.fileName,
                    laboratory.title
                )
            findNavController().navigate(actionRepositoryFragmentToDetailsActivity)
        }
        binding.repositoryRecyclerView.adapter = navigationAdapter
    }

    private fun setupMenuProvider() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main_activity, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.main_menu_item_privacy -> {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(Constants.PRIVACY_POLICY_LINK)
                        startActivity(intent)
                        true
                    }
                    else -> false
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
