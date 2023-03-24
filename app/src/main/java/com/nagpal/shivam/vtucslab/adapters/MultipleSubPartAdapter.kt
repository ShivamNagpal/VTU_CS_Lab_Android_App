package com.nagpal.shivam.vtucslab.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nagpal.shivam.vtucslab.adapters.ContentAdapter.ItemClickHandler
import com.nagpal.shivam.vtucslab.databinding.LayoutCardSingleSubPartsWithFilesBinding
import com.nagpal.shivam.vtucslab.databinding.LayoutCardSingleSubPartsWithoutFilesBinding
import com.nagpal.shivam.vtucslab.models.LabExperimentSubPart
import com.nagpal.shivam.vtucslab.utilities.StaticMethods.formatProgramName

class MultipleSubPartAdapter(
    private val context: Context,
    private val subParts: List<LabExperimentSubPart>,
    private val containsMultipleFiles: Boolean,
    private val itemClickHandler: ItemClickHandler?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        return if (!containsMultipleFiles) {
            val binding =
                LayoutCardSingleSubPartsWithoutFilesBinding.inflate(
                    LayoutInflater.from(context),
                    viewGroup,
                    false
                )
            SubPartWithoutFilesViewHolder(binding)
        } else {
            val binding =
                LayoutCardSingleSubPartsWithFilesBinding.inflate(
                    LayoutInflater.from(context),
                    viewGroup,
                    false
                )
            SubPartWithFilesViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        when (holder) {
            is SubPartWithoutFilesViewHolder -> {
                holder.binding.serialOrder.text = subParts[i].subSerialOrder
                val parts = subParts[i].contentFiles[0].fileName.split("\\.".toRegex())
                    .dropLastWhile { it.isEmpty() }.toTypedArray()
                if (parts.size >= 2) {
                    holder.binding.programTitle.text = formatProgramName(
                        parts[parts.size - 2]
                    )
                }
            }
            is SubPartWithFilesViewHolder -> {
                holder.binding.serialOrder.text = subParts[i].subSerialOrder
                val adapter = MultipleFileAdapter(
                    context,
                    subParts[i].contentFiles,
                    itemClickHandler
                )
                holder.binding.filesContainer.adapter = adapter
            }
        }
    }

    override fun getItemCount(): Int {
        return subParts.size
    }

    internal inner class SubPartWithoutFilesViewHolder(var binding: LayoutCardSingleSubPartsWithoutFilesBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ), View.OnClickListener {
        init {
            binding.root.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            itemClickHandler?.let {
                val position = adapterPosition
                val contentFile = subParts[position].contentFiles[0]
                it.onContentFileClick(contentFile)
            }
        }
    }

    internal inner class SubPartWithFilesViewHolder(var binding: LayoutCardSingleSubPartsWithFilesBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        init {
            binding.filesContainer.layoutManager = LinearLayoutManager(context)
            binding.filesContainer.setHasFixedSize(true)
        }
    }
}
