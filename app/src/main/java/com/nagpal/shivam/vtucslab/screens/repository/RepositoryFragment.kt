package com.nagpal.shivam.vtucslab.screens.repository

import android.content.Intent
import android.net.Uri
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.nagpal.shivam.vtucslab.R
import com.nagpal.shivam.vtucslab.adapters.NavigationAdapter
import com.nagpal.shivam.vtucslab.databinding.FragmentRepositoryBinding
import com.nagpal.shivam.vtucslab.models.Laboratory
import com.nagpal.shivam.vtucslab.screens.UiEvent
import com.nagpal.shivam.vtucslab.screens.Utils
import com.nagpal.shivam.vtucslab.screens.Utils.asString
import com.nagpal.shivam.vtucslab.screens.Utils.safeNavigate
import com.nagpal.shivam.vtucslab.utilities.Constants
import com.nagpal.shivam.vtucslab.utilities.Stages
import kotlinx.coroutines.launch

class RepositoryFragment : Fragment() {
  private var _binding: FragmentRepositoryBinding? = null
  private lateinit var navigationAdapter: NavigationAdapter

  private val binding
    get() = _binding!!

  private val viewModel: RepositoryViewModel by viewModels { RepositoryViewModel.Factory }
  private val url = Constants.INDEX_REPOSITORY_URL

  private var toast: Toast? = null

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View {
    _binding = FragmentRepositoryBinding.inflate(inflater, container, false)
    setupMenuProvider()
    setupViews()
    setupRepositoryAdapter()

    viewLifecycleOwner.lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.uiState.collect {
          binding.emptyTextView.visibility = View.GONE
          toast = Utils.showToast(requireContext(), toast, it.toast, viewModel, UiEvent.ResetToast)

          if (it.stage != Stages.LOADING) {
            binding.swipeRefresh.isRefreshing = false
          }

          when (it.stage) {
            Stages.LOADING -> {
              binding.swipeRefresh.isRefreshing = true
            }
            Stages.SUCCEEDED -> {
              if (it.data!!.isValid) {
                navigationAdapter.clear()
                navigationAdapter.addAll(it.data.laboratories)
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

  private fun setupViews() {
    binding.repositoryRecyclerView.layoutManager =
        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    binding.repositoryRecyclerView.setHasFixedSize(true)
    binding.swipeRefresh.setOnRefreshListener { viewModel.onEvent(UiEvent.RefreshContent(url)) }
  }

  override fun onResume() {
    super.onResume()
    viewModel.onEvent(UiEvent.LoadContent(url))
  }

  private fun setupRepositoryAdapter() {
    navigationAdapter = NavigationAdapter(requireContext(), ArrayList())
    navigationAdapter.setNavigationAdapterItemClickHandler(
        object : NavigationAdapter.NavigationAdapterItemClickHandler {
          override fun onNavigationAdapterItemClick(laboratory: Laboratory, i: Int) {
            val actionRepositoryFragmentToProgramFragment =
                RepositoryFragmentDirections.actionRepositoryFragmentToProgramFragment(
                    viewModel.uiState.value.baseUrl!!,
                    laboratory.fileName,
                    laboratory.title.orEmpty(),
                )
            findNavController().safeNavigate(actionRepositoryFragmentToProgramFragment)
          }
        })
    binding.repositoryRecyclerView.adapter = navigationAdapter
  }

  private fun setupMenuProvider() {
    requireActivity()
        .addMenuProvider(
            object : MenuProvider {
              override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main_fragment, menu)
              }

              override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                  R.id.menu_item_refresh -> {
                    viewModel.onEvent(UiEvent.RefreshContent(url))
                    true
                  }
                  R.id.main_menu_item_privacy -> {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(Constants.PRIVACY_POLICY_LINK)
                    startActivity(intent)
                    true
                  }
                  else -> false
                }
              }
            },
            viewLifecycleOwner)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}
