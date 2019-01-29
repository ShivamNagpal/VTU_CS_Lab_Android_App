package com.nagpal.shivam.vtucslab.Adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nagpal.shivam.vtucslab.Model.ContentFile;
import com.nagpal.shivam.vtucslab.R;
import com.nagpal.shivam.vtucslab.databinding.LayoutCardSingleFilesWithoutSubPartsBinding;

public class MultipleFileAdapter extends RecyclerView.Adapter<MultipleFileAdapter.ContentFileViewHolder> {
    private Context mContext;
    private ContentFile[] mContentFiles;
    private ContentAdapter.ItemClickHandler mItemClickHandler;

    public MultipleFileAdapter(Context context, ContentFile[] contentFiles, ContentAdapter.ItemClickHandler itemClickHandler) {
        mContext = context;
        mContentFiles = contentFiles;
        mItemClickHandler = itemClickHandler;
    }

    @NonNull
    @Override
    public ContentFileViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutCardSingleFilesWithoutSubPartsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_card_single_files_without_sub_parts, viewGroup, false);
        return new ContentFileViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentFileViewHolder contentFileViewHolder, int i) {
        String[] parts = mContentFiles[i].getFileName().split("\\.");
        if (parts.length >= 2) {
            contentFileViewHolder.mBinding.programTitle.setText(parts[parts.length - 2]);
        }
    }

    @Override
    public int getItemCount() {
        if (mContentFiles != null) {
            return mContentFiles.length;
        } else {
            return 0;
        }
    }

    class ContentFileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        LayoutCardSingleFilesWithoutSubPartsBinding mBinding;

        ContentFileViewHolder(LayoutCardSingleFilesWithoutSubPartsBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickHandler != null) {
                int position = getAdapterPosition();
                ContentFile file = mContentFiles[position];
                mItemClickHandler.onContentFileClick(file);
            }
        }
    }
}
