package com.nagpal.shivam.vtucslab.Adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nagpal.shivam.vtucslab.Model.ContentFile;
import com.nagpal.shivam.vtucslab.Model.LabExperimentSubPart;
import com.nagpal.shivam.vtucslab.R;
import com.nagpal.shivam.vtucslab.databinding.LayoutCardSingleSubPartsWithoutFilesBinding;

public class MultipleSubPartAdapter extends RecyclerView.Adapter<MultipleSubPartAdapter.SubPartViewHolder> {
    private Context mContext;
    private LabExperimentSubPart[] mSubParts;
    private ContentAdapter.ItemClickHandler mItemClickHandler;

    public MultipleSubPartAdapter(Context context, LabExperimentSubPart[] subParts, ContentAdapter.ItemClickHandler itemClickHandler) {
        mContext = context;
        mSubParts = subParts;
        mItemClickHandler = itemClickHandler;
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

    class SubPartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        LayoutCardSingleSubPartsWithoutFilesBinding mBinding;

        SubPartViewHolder(LayoutCardSingleSubPartsWithoutFilesBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickHandler != null) {
                int position = getAdapterPosition();
                ContentFile contentFile = mSubParts[position].getContentFiles()[0];
                mItemClickHandler.onContentFileClick(contentFile);
            }
        }
    }
}
