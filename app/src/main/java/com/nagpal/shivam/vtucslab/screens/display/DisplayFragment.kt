package com.nagpal.shivam.vtucslab.screens.display

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.nagpal.shivam.vtucslab.R
import com.nagpal.shivam.vtucslab.databinding.FragmentDisplayBinding
import com.nagpal.shivam.vtucslab.utilities.Constants
import com.nagpal.shivam.vtucslab.utilities.Stages
import kotlinx.coroutines.launch


class DisplayFragment : Fragment() {

    private var _binding: FragmentDisplayBinding? = null
    private val binding get() = _binding!!
    private val displayFragmentArgs by navArgs<DisplayFragmentArgs>()
    private lateinit var viewModel: DisplayViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDisplayBinding.inflate(inflater, container, false)
        setupMenuProvider()

        viewModel = ViewModelProvider(this)[DisplayViewModel::class.java]

        requireActivity().title = displayFragmentArgs.title

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
                            binding.displayTextView.text = it.response
                            requireActivity().invalidateOptionsMenu()
                            Handler(Looper.getMainLooper()).postDelayed({
                                binding.horizontalScroll.scrollX = viewModel.scrollX
                                binding.verticalScroll.scrollY = viewModel.scrollY
                            }, 500)
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

    private fun setupMenuProvider() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_display_activity, menu)
                if (viewModel.uiState.value.stage == Stages.SUCCEEDED) {
                    menu.findItem(R.id.menu_item_copy_display_activity).isEnabled = true
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.display_menu_item_refresh -> {
                        viewModel.resetState()
                        loadContent()
                        return true
                    }
                    R.id.menu_item_copy_display_activity -> {

                        val clipboard =
                            requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clipData = ClipData(
                            ClipData.newPlainText(
                                Constants.LABEL_CODE,
                                viewModel.uiState.value.response
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
        })
    }

    override fun onResume() {
        super.onResume()
        loadContent()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        viewModel.scrollX = binding.horizontalScroll.scrollX
        viewModel.scrollY = binding.verticalScroll.scrollY
        super.onSaveInstanceState(outState)
    }

    private fun loadContent() {
        viewModel.loadContent("${displayFragmentArgs.baseUrl}/${displayFragmentArgs.fileName}")
    }
}
