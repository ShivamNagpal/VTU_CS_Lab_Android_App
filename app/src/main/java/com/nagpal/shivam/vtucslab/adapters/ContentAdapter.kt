package com.nagpal.shivam.vtucslab.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nagpal.shivam.vtucslab.R
import com.nagpal.shivam.vtucslab.databinding.LayoutCardSeMspBinding
import com.nagpal.shivam.vtucslab.databinding.LayoutCardSeSspMfBinding
import com.nagpal.shivam.vtucslab.databinding.LayoutCardSeSspSfBinding
import com.nagpal.shivam.vtucslab.models.ContentFile
import com.nagpal.shivam.vtucslab.models.LabExperiment
import com.nagpal.shivam.vtucslab.utilities.StaticMethods.formatProgramName
import java.util.*

class ContentAdapter(
    private val context: Context,
    private val labExperimentArrayList: ArrayList<LabExperiment>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var itemClickHandler: ItemClickHandler? = null
    fun setItemClickHandler(itemClickHandler: ItemClickHandler) {
        this.itemClickHandler = itemClickHandler
    }

    fun addAll(labExperiments: List<LabExperiment>) {
        val i = labExperimentArrayList.size
        labExperimentArrayList.addAll(labExperiments)
        notifyItemRangeInserted(i, labExperiments.size)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        labExperimentArrayList.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SSP_SF -> {
                val sspSfBinding =
                    DataBindingUtil.inflate<LayoutCardSeSspSfBinding>(
                        LayoutInflater.from(context),
                        R.layout.layout_card_se_ssp_sf,
                        parent,
                        false
                    )
                SspSfViewHolder(sspSfBinding)
            }
            VIEW_TYPE_MSP_SF -> {
                val mspSfBinding =
                    DataBindingUtil.inflate<LayoutCardSeMspBinding>(
                        LayoutInflater.from(context),
                        R.layout.layout_card_se_msp,
                        parent,
                        false
                    )
                MspSfViewHolder(mspSfBinding)
            }
            VIEW_TYPE_SSP_MF -> {
                val sspMfBinding =
                    DataBindingUtil.inflate<LayoutCardSeSspMfBinding>(
                        LayoutInflater.from(context),
                        R.layout.layout_card_se_ssp_mf,
                        parent,
                        false
                    )
                SspMfViewHolder(sspMfBinding)
            }
            VIEW_TYPE_MSP_MF -> {
                val seMspBinding =
                    DataBindingUtil.inflate<LayoutCardSeMspBinding>(
                        LayoutInflater.from(context),
                        R.layout.layout_card_se_msp,
                        parent,
                        false
                    )
                MspMfViewHolder(seMspBinding)
            }
            else -> InvalidViewHolder(View(context))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val labExperiment = labExperimentArrayList[position]
        val serialOrder = processSerialOrder(labExperiment.serialOrder)
        when (holder) {
            is SspSfViewHolder -> {
                holder.binding.serialOrder.text = serialOrder
                val parts =
                    labExperiment.labExperimentSubParts[0].contentFiles[0].fileName.split("\\.".toRegex())
                        .dropLastWhile { it.isEmpty() }.toTypedArray()
                if (parts.size >= 2) {
                    holder.binding.programTitle.text =
                        formatProgramName(parts[parts.size - 2])
                }
            }
            is MspSfViewHolder -> {
                holder.binding.serialOrder.text = serialOrder
                val adapter = MultipleSubPartAdapter(
                    context,
                    labExperiment.labExperimentSubParts,
                    false,
                    itemClickHandler
                )
                holder.binding.subPartContainer.adapter = adapter
            }
            is SspMfViewHolder -> {
                holder.binding.serialOrder.text = serialOrder
                val adapter = MultipleFileAdapter(
                    context,
                    R.layout.layout_card_single_files_without_sub_parts,
                    labExperiment.labExperimentSubParts[0].contentFiles,
                    itemClickHandler
                )
                holder.binding.filesContainer.adapter = adapter

            }
            is MspMfViewHolder -> {
                holder.binding.serialOrder.text = serialOrder
                val adapter = MultipleSubPartAdapter(
                    context,
                    labExperiment.labExperimentSubParts,
                    true,
                    itemClickHandler
                )
                holder.binding.subPartContainer.adapter = adapter
            }
        }
    }

    override fun getItemCount(): Int {
        return labExperimentArrayList.size
    }

    override fun getItemViewType(position: Int): Int {
        val experiment = labExperimentArrayList[position]
        var contentFileLength = 1
        val labExperimentSubParts = experiment.labExperimentSubParts
        for (subPart in labExperimentSubParts) {
            contentFileLength = contentFileLength.coerceAtLeast(subPart.contentFiles.size)
        }
        val labExperimentSubPartsSize = labExperimentSubParts.size
        return if (labExperimentSubPartsSize == 1 && contentFileLength == 1)
            VIEW_TYPE_SSP_SF
        else if (labExperimentSubPartsSize > 1 && contentFileLength == 1)
            VIEW_TYPE_MSP_SF
        else if (labExperimentSubPartsSize == 1)
            VIEW_TYPE_SSP_MF
        else if (labExperimentSubPartsSize > 1)
            VIEW_TYPE_MSP_MF
        else VIEW_TYPE_INVALID
    }

    private fun processSerialOrder(order: String?): String {
        if (order == null) {
            return "#"
        }
        return try {
            val i = order.toInt()
            i.toString()
        } catch (e: NumberFormatException) {
            order
        }
    }

    interface ItemClickHandler {
        fun onContentFileClick(file: ContentFile)
    }

    internal class InvalidViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    internal inner class SspSfViewHolder(var binding: LayoutCardSeSspSfBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ), View.OnClickListener {
        init {
            binding.root.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            itemClickHandler?.let {
                val position = adapterPosition
                val contentFile =
                    labExperimentArrayList[position].labExperimentSubParts[0].contentFiles[0]
                it.onContentFileClick(contentFile)
            }
        }
    }

    internal inner class MspSfViewHolder(var binding: LayoutCardSeMspBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        init {
            binding.subPartContainer.layoutManager = LinearLayoutManager(context)
            binding.subPartContainer.setHasFixedSize(true)
        }
    }

    internal inner class SspMfViewHolder(var binding: LayoutCardSeSspMfBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        init {
            binding.filesContainer.layoutManager = LinearLayoutManager(context)
            binding.filesContainer.setHasFixedSize(true)
        }
    }

    internal inner class MspMfViewHolder(var binding: LayoutCardSeMspBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        init {
            binding.subPartContainer.layoutManager = LinearLayoutManager(context)
            binding.subPartContainer.setHasFixedSize(true)
        }
    }

    companion object {
        private const val VIEW_TYPE_SSP_SF = 0
        private const val VIEW_TYPE_MSP_SF = 1
        private const val VIEW_TYPE_SSP_MF = 2
        private const val VIEW_TYPE_MSP_MF = 3
        private const val VIEW_TYPE_INVALID = -1
    }
}
