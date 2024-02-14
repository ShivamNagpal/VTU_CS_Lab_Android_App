package com.nagpal.shivam.vtucslab.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.RecyclerView
import com.nagpal.shivam.vtucslab.adapters.NavigationAdapter.NavigationViewHolder
import com.nagpal.shivam.vtucslab.models.Laboratory
import com.nagpal.shivam.vtucslab.screens.repository.RepositoryCard

class NavigationAdapter(
    private val context: Context,
    private val laboratoryArrayList: ArrayList<Laboratory>,
) : RecyclerView.Adapter<NavigationViewHolder>() {
    private var navigationAdapterItemClickHandler: NavigationAdapterItemClickHandler? = null

    fun addAll(laboratories: List<Laboratory>) {
        val i = laboratoryArrayList.size
        laboratoryArrayList.addAll(laboratories)
        notifyItemRangeInserted(i, laboratories.size)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        laboratoryArrayList.clear()
        notifyDataSetChanged()
    }

    fun setNavigationAdapterItemClickHandler(navigationAdapterItemClickHandler: NavigationAdapterItemClickHandler?) {
        this.navigationAdapterItemClickHandler = navigationAdapterItemClickHandler
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): NavigationViewHolder {
        val composeView = ComposeView(context)
        return NavigationViewHolder(composeView)
    }

    override fun onBindViewHolder(
        holder: NavigationViewHolder,
        position: Int,
    ) {
        val laboratory = laboratoryArrayList[position]
        holder.bind(laboratory)
    }

    override fun getItemCount(): Int {
        return laboratoryArrayList.size
    }

    interface NavigationAdapterItemClickHandler {
        fun onNavigationAdapterItemClick(
            laboratory: Laboratory,
            i: Int,
        )
    }

    inner class NavigationViewHolder(private val composeView: ComposeView) :
        RecyclerView.ViewHolder(composeView) {
        fun bind(laboratory: Laboratory) {
            val fileName = laboratory.fileName
            val parts = fileName.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val title = parts[0].replace('_', ' ')
            laboratory.title = title
            composeView.setContent {
                RepositoryCard(text = title) {
                    navigationAdapterItemClickHandler?.let {
                        val i = adapterPosition
                        it.onNavigationAdapterItemClick(laboratoryArrayList[i], i)
                    }
                }
            }
        }
    }
}
