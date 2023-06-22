package com.nagpal.shivam.vtucslab.screens.display

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.navigation.fragment.navArgs
import com.nagpal.shivam.vtucslab.R
import com.nagpal.shivam.vtucslab.databinding.FragmentDisplayBinding
import com.nagpal.shivam.vtucslab.screens.UiEvent
import com.nagpal.shivam.vtucslab.screens.Utils
import com.nagpal.shivam.vtucslab.screens.Utils.asString
import com.nagpal.shivam.vtucslab.utilities.Constants
import com.nagpal.shivam.vtucslab.utilities.Stages
import kotlinx.coroutines.launch


class DisplayFragment : Fragment() {

    private var _binding: FragmentDisplayBinding? = null
    private val binding get() = _binding!!
    private val displayFragmentArgs by navArgs<DisplayFragmentArgs>()
    private val viewModel: DisplayViewModel by viewModels { DisplayViewModel.Factory }
    private var toast: Toast? = null

    private val url: String by lazy {
        return@lazy "${displayFragmentArgs.baseUrl}/${displayFragmentArgs.fileName}"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDisplayBinding.inflate(inflater, container, false)
        setupMenuProvider()
        setupViews()
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    binding.emptyTextView.visibility = View.GONE
                    toast = Utils.showToast(
                        requireContext(),
                        toast,
                        it.toast,
                        viewModel,
                        UiEvent.ResetToast
                    )

                    if (it.stage != Stages.LOADING) {
                        binding.swipeRefresh.isRefreshing = false
                    }

                    when (it.stage) {
                        Stages.LOADING -> {
                            binding.swipeRefresh.isRefreshing = true
                        }

                        Stages.SUCCEEDED -> {
                            binding.displayTextView.text = it.data
                            requireActivity().invalidateOptionsMenu()
                            Handler(Looper.getMainLooper()).postDelayed({
                                binding.horizontalScroll.scrollX = viewModel.scrollX
                                binding.verticalScroll.scrollY = viewModel.scrollY
                            }, 500)
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

    private fun setupViews() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.onEvent(UiEvent.RefreshContent(url))
        }
    }

    private fun showErrorMessage(message: String) {
        binding.emptyTextView.visibility = View.VISIBLE
        binding.emptyTextView.text = message
    }

    private fun setupMenuProvider() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_display_fragment, menu)
                if (viewModel.uiState.value.stage == Stages.SUCCEEDED) {
                    menu.findItem(R.id.menu_item_copy_display_activity).isEnabled = true
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_item_refresh -> {
                        viewModel.onEvent(UiEvent.RefreshContent(url))
                        return true
                    }

                    R.id.menu_item_copy_display_activity -> {

                        val clipboard =
                            requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clipData = ClipData(
                            ClipData.newPlainText(
                                Constants.LABEL_CODE,
                                viewModel.uiState.value.data
                            )
                        )
                        clipboard.setPrimaryClip(clipData)
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.code_copied_to_clipboard),
                            Toast.LENGTH_SHORT
                        ).show()
                        return true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner)
    }

    override fun onResume() {
        super.onResume()
        viewModel.onEvent(UiEvent.LoadContent(url))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        viewModel.scrollX = binding.horizontalScroll.scrollX
        viewModel.scrollY = binding.verticalScroll.scrollY
        super.onSaveInstanceState(outState)
    }
}
