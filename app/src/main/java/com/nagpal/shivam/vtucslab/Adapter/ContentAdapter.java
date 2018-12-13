package com.nagpal.shivam.vtucslab.Adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nagpal.shivam.vtucslab.Model.LabExperiment;
import com.nagpal.shivam.vtucslab.Model.LabExperimentSubPart;
import com.nagpal.shivam.vtucslab.R;
import com.nagpal.shivam.vtucslab.databinding.LayoutCardSeMspSfBinding;
import com.nagpal.shivam.vtucslab.databinding.LayoutCardSeSspSfBinding;

import java.util.ArrayList;
import java.util.Arrays;

public class ContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SSP_SF = 0;
    private static final int VIEW_TYPE_MSP_SF = 1;
    private static final int VIEW_TYPE_SSP_MF = 2;
    private static final int VIEW_TYPE_MSP_MF = 3;
    private Context mContext;
    private ArrayList<LabExperiment> mLabExperimentArrayList;
    private ItemClickHandler mItemClickHandler;

    public ContentAdapter(Context context, ArrayList<LabExperiment> labExperimentArrayList) {
        this.mContext = context;
        this.mLabExperimentArrayList = labExperimentArrayList;
    }

    public void setItemClickHandler(ItemClickHandler itemClickHandler) {
        mItemClickHandler = itemClickHandler;
    }

    public void addAll(ArrayList<LabExperiment> labExperimentArrayList) {
        int i = mLabExperimentArrayList.size();
        mLabExperimentArrayList.addAll(labExperimentArrayList);
        notifyItemRangeInserted(i, labExperimentArrayList.size());
    }

    public void addAll(@NonNull LabExperiment[] labExperiments) {
        int i = mLabExperimentArrayList.size();
        mLabExperimentArrayList.addAll(Arrays.asList(labExperiments));
        notifyItemRangeInserted(i, labExperiments.length);
    }


    public void clear() {
        mLabExperimentArrayList.clear();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_SSP_SF:
                LayoutCardSeSspSfBinding sspSfBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_card_se_ssp_sf, parent, false);
                return new SspSfViewHolder(sspSfBinding);
            case VIEW_TYPE_MSP_SF:
                LayoutCardSeMspSfBinding mspSfBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_card_se_msp_sf, parent, false);
                return new MspSfViewHolder(mspSfBinding);
            default:
                return null;

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LabExperiment labExperiment = mLabExperimentArrayList.get(position);
        if (holder instanceof SspSfViewHolder) {
            SspSfViewHolder sspSfViewHolder = (SspSfViewHolder) holder;
            sspSfViewHolder.mBinding.serialOrder.setText(labExperiment.getSerialOrder());
            String[] parts = labExperiment.getLabExperimentSubParts()[0].getContentFiles()[0].getFileName().split("\\.");
            if (parts.length >= 2) {
                sspSfViewHolder.mBinding.programTitle.setText(parts[parts.length-2]);
            }
        } else if (holder instanceof MspSfViewHolder) {
            MspSfViewHolder mspSfViewHolder = (MspSfViewHolder) holder;
            mspSfViewHolder.mBinding.serialOrder.setText(labExperiment.getSerialOrder());
            MultipleSubPartAdapter adapter = new MultipleSubPartAdapter(mContext, labExperiment.getLabExperimentSubParts());
            mspSfViewHolder.mBinding.subPartContainer.setAdapter(adapter);
        }
    }

    @Override
    public int getItemCount() {
        return mLabExperimentArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        LabExperiment experiment = mLabExperimentArrayList.get(position);
        int contentFileLength = 1;
        LabExperimentSubPart[] labExperimentSubParts = experiment.getLabExperimentSubParts();
        for (int i = 0, labExperimentSubPartsLength = labExperimentSubParts.length; i < labExperimentSubPartsLength; i++) {
            LabExperimentSubPart subPart = labExperimentSubParts[i];
            contentFileLength = Math.max(contentFileLength, subPart.getContentFiles().length);
        }
        if (labExperimentSubParts.length == 1 && contentFileLength == 1)
            return VIEW_TYPE_SSP_SF;
        else if (labExperimentSubParts.length > 1 && contentFileLength == 1)
            return VIEW_TYPE_MSP_SF;
        else if (labExperimentSubParts.length == 1 && contentFileLength > 1)
            return VIEW_TYPE_SSP_MF;
        else
            return VIEW_TYPE_MSP_MF;
    }

    public interface ItemClickHandler {
        void onContentFileClick(LabExperiment labExperiment, int position);
    }

    class SspSfViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //        TextView mNumber;
//        TextView mTitle;
        LayoutCardSeSspSfBinding mBinding;

        SspSfViewHolder(LayoutCardSeSspSfBinding binding) {
            super(binding.getRoot());
//            mNumber = itemView.findViewById(R.id.serial_order);
//            mTitle = itemView.findViewById(R.id.program_title);
            mBinding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mItemClickHandler != null) {
                int position = getAdapterPosition();
                mItemClickHandler.onContentFileClick(mLabExperimentArrayList.get(position), position);
            }
        }
    }

    class MspSfViewHolder extends RecyclerView.ViewHolder {
        LayoutCardSeMspSfBinding mBinding;

        public MspSfViewHolder(LayoutCardSeMspSfBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            mBinding.subPartContainer.setLayoutManager(new LinearLayoutManager(mContext));
            mBinding.subPartContainer.setHasFixedSize(true);
        }
    }
}
