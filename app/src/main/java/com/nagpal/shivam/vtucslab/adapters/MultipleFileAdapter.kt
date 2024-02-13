package com.nagpal.shivam.vtucslab.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nagpal.shivam.vtucslab.adapters.ContentAdapter.ItemClickHandler
import com.nagpal.shivam.vtucslab.adapters.MultipleFileAdapter.ContentFileViewHolder
import com.nagpal.shivam.vtucslab.databinding.LayoutCardSingleFilesWithoutSubPartsBinding
import com.nagpal.shivam.vtucslab.models.ContentFile
import com.nagpal.shivam.vtucslab.utilities.StaticMethods.formatProgramName

class MultipleFileAdapter(
    private val context: Context,
    private val contentFiles: List<ContentFile>,
    private val itemClickHandler: ItemClickHandler?,
) : RecyclerView.Adapter<ContentFileViewHolder>() {
    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int,
    ): ContentFileViewHolder {
        val binding =
            LayoutCardSingleFilesWithoutSubPartsBinding.inflate(
                LayoutInflater.from(context),
                viewGroup,
                false,
            )
        return ContentFileViewHolder(binding)
    }

    override fun onBindViewHolder(
        contentFileViewHolder: ContentFileViewHolder,
        i: Int,
    ) {
        val parts =
            contentFiles[i].fileName.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
        if (parts.size >= 2) {
            contentFileViewHolder.binding.programTitle.text =
                formatProgramName(parts[parts.size - 2])
        }
    }

    override fun getItemCount(): Int {
        return contentFiles.size
    }

    inner class ContentFileViewHolder(var binding: LayoutCardSingleFilesWithoutSubPartsBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            binding.root.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            itemClickHandler?.let {
                val position = adapterPosition
                val file = contentFiles[position]
                it.onContentFileClick(file)
            }
        }
    }
}
