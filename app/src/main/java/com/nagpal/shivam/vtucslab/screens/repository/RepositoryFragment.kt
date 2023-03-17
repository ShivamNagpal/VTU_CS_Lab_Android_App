package com.nagpal.shivam.vtucslab.screens.repository

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
    private val viewModel: RepositoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRepositoryBinding.inflate(inflater, container, false)
        setupMenuProvider()
        setupViews()
        setupRepositoryAdapter()


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
                            showErrorMessage(getString(R.string.error_occurred))
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
        viewModel.loadRepositories()
    }

    private fun setupRepositoryAdapter() {
        navigationAdapter = NavigationAdapter(requireContext(), ArrayList())
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
