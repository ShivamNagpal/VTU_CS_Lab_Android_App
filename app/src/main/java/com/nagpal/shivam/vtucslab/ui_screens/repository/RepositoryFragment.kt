package com.nagpal.shivam.vtucslab.ui_screens.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nagpal.shivam.vtucslab.R
import com.nagpal.shivam.vtucslab.databinding.FragmentRepositoryBinding
import com.nagpal.shivam.vtucslab.ui_screens.isInternetAvailable

class RepositoryFragment : Fragment() {

    private lateinit var mBinding: FragmentRepositoryBinding
    private lateinit var mViewModel: RepositoryViewModel
    private lateinit var mContext: Context

    private fun showErrorMessage(error: String) {
        mBinding.emptyTextView.visibility = View.VISIBLE
        mBinding.emptyTextView.text = error
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main_activity, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_menu_item_privacy -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://github.com/ShivamNagpal/Privacy_Policies/blob/master/VTU_CS_LAB_MANUAL.md")
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mContext = container!!.context
        mBinding = FragmentRepositoryBinding.inflate(layoutInflater, container, false)
        mViewModel = ViewModelProvider(this).get(RepositoryViewModel::class.java)
        setHasOptionsMenu(true)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.repositoryRecyclerView.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        mBinding.repositoryRecyclerView.setHasFixedSize(true)

        if (isInternetAvailable(mContext)) {
            mViewModel.loadRepositories()
        } else {
            mBinding.progressBar.visibility = View.GONE
            showErrorMessage(getString(R.string.no_internet_connection))
        }
    }
}