package com.nagpal.shivam.vtucslab.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nagpal.shivam.vtucslab.R
import com.nagpal.shivam.vtucslab.adapters.NavigationAdapter.NavigationViewHolder
import com.nagpal.shivam.vtucslab.models.Laboratory
import java.util.*

class NavigationAdapter(
    private val context: Context,
    private val laboratoryArrayList: ArrayList<Laboratory>
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavigationViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.layout_card_repository, parent, false)
        return NavigationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NavigationViewHolder, position: Int) {
        val laboratory = laboratoryArrayList[position]
        val fileName = laboratory.fileName
        val parts = fileName.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        laboratory.title = parts[0].replace('_', ' ')
        holder.textView.text = laboratory.title
    }

    override fun getItemCount(): Int {
        return laboratoryArrayList.size
    }

    interface NavigationAdapterItemClickHandler {
        fun onNavigationAdapterItemClick(laboratory: Laboratory, i: Int)
    }

    inner class NavigationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var textView: TextView

        init {
            textView = itemView.findViewById(R.id.text_view_layout)
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            navigationAdapterItemClickHandler?.let {
                val i = adapterPosition
                it.onNavigationAdapterItemClick(laboratoryArrayList[i], i)
            }
        }
    }
}
