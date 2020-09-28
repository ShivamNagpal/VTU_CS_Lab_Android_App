package com.nagpal.shivam.vtucslab.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nagpal.shivam.vtucslab.models.ContentFile;
import com.nagpal.shivam.vtucslab.models.LabExperiment;
import com.nagpal.shivam.vtucslab.models.LabExperimentSubPart;
import com.nagpal.shivam.vtucslab.R;
import com.nagpal.shivam.vtucslab.utils.StaticMethods;
import com.nagpal.shivam.vtucslab.databinding.LayoutCardSeMspBinding;
import com.nagpal.shivam.vtucslab.databinding.LayoutCardSeSspMfBinding;
import com.nagpal.shivam.vtucslab.databinding.LayoutCardSeSspSfBinding;

import java.util.ArrayList;
import java.util.Arrays;

public class ContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SSP_SF = 0;
    private static final int VIEW_TYPE_MSP_SF = 1;
    private static final int VIEW_TYPE_SSP_MF = 2;
    private static final int VIEW_TYPE_MSP_MF = 3;
    private static final int VIEW_TYPE_INVALID = -1;
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
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_SSP_SF:
                LayoutCardSeSspSfBinding sspSfBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_card_se_ssp_sf, parent, false);
                return new SspSfViewHolder(sspSfBinding);
            case VIEW_TYPE_MSP_SF:
                LayoutCardSeMspBinding mspSfBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_card_se_msp, parent, false);
                return new MspSfViewHolder(mspSfBinding);
            case VIEW_TYPE_SSP_MF:
                LayoutCardSeSspMfBinding sspMfBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_card_se_ssp_mf, parent, false);
                return new SspMfViewHolder(sspMfBinding);
            case VIEW_TYPE_MSP_MF:
                LayoutCardSeMspBinding seMspBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_card_se_msp, parent, false);
                return new MspMfViewHolder(seMspBinding);
            default:
                return new InvalidViewHolder(new View(mContext));

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LabExperiment labExperiment = mLabExperimentArrayList.get(position);
        if (holder instanceof SspSfViewHolder) {
            SspSfViewHolder sspSfViewHolder = (SspSfViewHolder) holder;
            sspSfViewHolder.mBinding.serialOrder.setText(processSerialOrder(labExperiment.getSerialOrder()));
            String[] parts = labExperiment.getLabExperimentSubParts()[0].getContentFiles()[0].getFileName().split("\\.");
            if (parts.length >= 2) {
                sspSfViewHolder.mBinding.programTitle.setText(StaticMethods.formatProgramName(parts[parts.length - 2]));
            }
        } else if (holder instanceof MspSfViewHolder) {
            MspSfViewHolder mspSfViewHolder = (MspSfViewHolder) holder;
            mspSfViewHolder.mBinding.serialOrder.setText(processSerialOrder(labExperiment.getSerialOrder()));
            MultipleSubPartAdapter adapter = new MultipleSubPartAdapter(mContext, labExperiment.getLabExperimentSubParts(), false, mItemClickHandler);
            mspSfViewHolder.mBinding.subPartContainer.setAdapter(adapter);
        } else if (holder instanceof SspMfViewHolder) {
            SspMfViewHolder sspMfViewHolder = (SspMfViewHolder) holder;
            sspMfViewHolder.mBinding.serialOrder.setText(processSerialOrder(labExperiment.getSerialOrder()));
            MultipleFileAdapter adapter = new MultipleFileAdapter(mContext, R.layout.layout_card_single_files_without_sub_parts, labExperiment.getLabExperimentSubParts()[0].getContentFiles(), mItemClickHandler);
            sspMfViewHolder.mBinding.filesContainer.setAdapter(adapter);
        } else if (holder instanceof MspMfViewHolder) {
            MspMfViewHolder mspMfViewHolder = (MspMfViewHolder) holder;
            mspMfViewHolder.mBinding.serialOrder.setText(processSerialOrder(labExperiment.getSerialOrder()));
            MultipleSubPartAdapter adapter = new MultipleSubPartAdapter(mContext, labExperiment.getLabExperimentSubParts(), true, mItemClickHandler);
            mspMfViewHolder.mBinding.subPartContainer.setAdapter(adapter);
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
        else if (labExperimentSubParts.length > 1 && contentFileLength > 1)
            return VIEW_TYPE_MSP_MF;
        else
            return VIEW_TYPE_INVALID;
    }

    private String processSerialOrder(String order) {
        try {
            int i = Integer.parseInt(order);
            return String.valueOf(i);
        } catch (NumberFormatException e) {
            return order;
        }
    }

    public interface ItemClickHandler {
        void onContentFileClick(ContentFile file);
    }

    class SspSfViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LayoutCardSeSspSfBinding mBinding;

        SspSfViewHolder(LayoutCardSeSspSfBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mItemClickHandler != null) {
                int position = getAdapterPosition();
                ContentFile contentFile = mLabExperimentArrayList.get(position).getLabExperimentSubParts()[0].getContentFiles()[0];
                mItemClickHandler.onContentFileClick(contentFile);
            }
        }
    }

    class MspSfViewHolder extends RecyclerView.ViewHolder {
        LayoutCardSeMspBinding mBinding;

        MspSfViewHolder(LayoutCardSeMspBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            mBinding.subPartContainer.setLayoutManager(new LinearLayoutManager(mContext));
            mBinding.subPartContainer.setHasFixedSize(true);
        }
    }

    class SspMfViewHolder extends RecyclerView.ViewHolder {
        LayoutCardSeSspMfBinding mBinding;

        SspMfViewHolder(LayoutCardSeSspMfBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            mBinding.filesContainer.setLayoutManager(new LinearLayoutManager(mContext));
            mBinding.filesContainer.setHasFixedSize(true);
        }
    }

    class MspMfViewHolder extends RecyclerView.ViewHolder {
        LayoutCardSeMspBinding mBinding;

        MspMfViewHolder(LayoutCardSeMspBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            mBinding.subPartContainer.setLayoutManager(new LinearLayoutManager(mContext));
            mBinding.subPartContainer.setHasFixedSize(true);
        }
    }

    class InvalidViewHolder extends RecyclerView.ViewHolder {
        InvalidViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
