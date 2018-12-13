package com.nagpal.shivam.vtucslab.Adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.nagpal.shivam.vtucslab.Model.LabExperimentSubPart;
import com.nagpal.shivam.vtucslab.R;
import com.nagpal.shivam.vtucslab.databinding.LayoutCardSingleSubPartsWithoutFilesBinding;

public class MultipleSubPartAdapter extends RecyclerView.Adapter<MultipleSubPartAdapter.SubPartViewHolder> {
    private Context mContext;
    private LabExperimentSubPart[] mSubParts;

    public MultipleSubPartAdapter(Context context, LabExperimentSubPart[] subParts) {
        mContext = context;
        mSubParts = subParts;
    }

    @NonNull
    @Override
    public SubPartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutCardSingleSubPartsWithoutFilesBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_card_single_sub_parts_without_files, null, false);
        return new SubPartViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SubPartViewHolder subPartViewHolder, int i) {
        subPartViewHolder.mBinding.serialOrder.setText(mSubParts[i].getSubSerialOrder());
        String[] parts = mSubParts[i].getContentFiles()[0].getFileName().split("\\.");
        if (parts.length >= 2) {
            subPartViewHolder.mBinding.programTitle.setText(parts[parts.length - 2]);
        }
    }

    @Override
    public int getItemCount() {
        if (mSubParts != null) {
            return mSubParts.length;
        }
        return 0;
    }

    class SubPartViewHolder extends RecyclerView.ViewHolder {
        LayoutCardSingleSubPartsWithoutFilesBinding mBinding;

        SubPartViewHolder(LayoutCardSingleSubPartsWithoutFilesBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}
