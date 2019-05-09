package com.nagpal.shivam.vtucslab.Adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nagpal.shivam.vtucslab.Model.ContentFile;
import com.nagpal.shivam.vtucslab.Model.LabExperimentSubPart;
import com.nagpal.shivam.vtucslab.R;
import com.nagpal.shivam.vtucslab.Utility.StaticMethods;
import com.nagpal.shivam.vtucslab.databinding.LayoutCardSingleSubPartsWithFilesBinding;
import com.nagpal.shivam.vtucslab.databinding.LayoutCardSingleSubPartsWithoutFilesBinding;

public class MultipleSubPartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private LabExperimentSubPart[] mSubParts;
    private boolean mContainsMultipleFiles;
    private ContentAdapter.ItemClickHandler mItemClickHandler;

    public MultipleSubPartAdapter(Context context, LabExperimentSubPart[] subParts, boolean containsMultipleFiles, ContentAdapter.ItemClickHandler itemClickHandler) {
        mContext = context;
        mSubParts = subParts;
        mContainsMultipleFiles = containsMultipleFiles;
        mItemClickHandler = itemClickHandler;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (!mContainsMultipleFiles) {
            LayoutCardSingleSubPartsWithoutFilesBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_card_single_sub_parts_without_files, viewGroup, false);
            return new SubPartWithoutFilesViewHolder(binding);
        } else {
            LayoutCardSingleSubPartsWithFilesBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_card_single_sub_parts_with_files, viewGroup, false);
            return new SubPartWithFilesViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        if (holder instanceof SubPartWithoutFilesViewHolder) {
            SubPartWithoutFilesViewHolder subPartWithoutFilesViewHolder = (SubPartWithoutFilesViewHolder) holder;
            subPartWithoutFilesViewHolder.mBinding.serialOrder.setText(mSubParts[i].getSubSerialOrder());
            String[] parts = mSubParts[i].getContentFiles()[0].getFileName().split("\\.");
            if (parts.length >= 2) {
                subPartWithoutFilesViewHolder.mBinding.programTitle.setText(StaticMethods.formatProgramName(parts[parts.length - 2]));
            }
        } else if (holder instanceof SubPartWithFilesViewHolder) {
            SubPartWithFilesViewHolder subPartWithFilesViewHolder = (SubPartWithFilesViewHolder) holder;
            subPartWithFilesViewHolder.mBinding.serialOrder.setText(mSubParts[i].getSubSerialOrder());
            MultipleFileAdapter adapter = new MultipleFileAdapter(mContext, R.layout.layout_card_single_files_without_sub_parts, mSubParts[i].getContentFiles(), mItemClickHandler);
            subPartWithFilesViewHolder.mBinding.filesContainer.setAdapter(adapter);
        }
    }

    @Override
    public int getItemCount() {
        if (mSubParts != null) {
            return mSubParts.length;
        }
        return 0;
    }

    class SubPartWithoutFilesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        LayoutCardSingleSubPartsWithoutFilesBinding mBinding;

        SubPartWithoutFilesViewHolder(LayoutCardSingleSubPartsWithoutFilesBinding binding) {
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

    class SubPartWithFilesViewHolder extends RecyclerView.ViewHolder {

        LayoutCardSingleSubPartsWithFilesBinding mBinding;

        SubPartWithFilesViewHolder(LayoutCardSingleSubPartsWithFilesBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            mBinding.filesContainer.setLayoutManager(new LinearLayoutManager(mContext));
            mBinding.filesContainer.setHasFixedSize(true);
        }

    }
}
