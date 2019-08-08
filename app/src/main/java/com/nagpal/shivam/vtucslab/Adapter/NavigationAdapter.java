package com.nagpal.shivam.vtucslab.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nagpal.shivam.vtucslab.Model.Laboratory;
import com.nagpal.shivam.vtucslab.R;

import java.util.ArrayList;
import java.util.Arrays;

public class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.NavigationViewHolder> {

    private Context mContext;
    private ArrayList<Laboratory> mLaboratoryArrayList;
    private NavigationAdapterItemClickHandler mNavigationAdapterItemClickHandler;

    public NavigationAdapter(@NonNull Context context, @NonNull ArrayList<Laboratory> laboratoryArrayList) {
        mContext = context;
        mLaboratoryArrayList = laboratoryArrayList;
    }

    public void addAll(ArrayList<Laboratory> laboratoryArrayList) {
        int i = mLaboratoryArrayList.size();
        mLaboratoryArrayList.addAll(laboratoryArrayList);
        notifyItemRangeInserted(i, laboratoryArrayList.size());
    }

    public void addAll(Laboratory[] laboratories) {
        int i = mLaboratoryArrayList.size();
        mLaboratoryArrayList.addAll(Arrays.asList(laboratories));
        notifyItemRangeInserted(i, laboratories.length);
    }

    public void clear() {
        mLaboratoryArrayList.clear();
        notifyDataSetChanged();
    }

    public void setNavigationAdapterItemClickHandler(NavigationAdapterItemClickHandler navigationAdapterItemClickHandler) {
        mNavigationAdapterItemClickHandler = navigationAdapterItemClickHandler;
    }

    @NonNull
    @Override
    public NavigationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_card_repository, parent, false);
        return new NavigationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NavigationViewHolder holder, int position) {
        Laboratory laboratory = mLaboratoryArrayList.get(position);
        String fileName = laboratory.getFileName();
        String[] parts = fileName.split("\\.");
        laboratory.setTitle(parts[0].replace('_', ' '));
        holder.mTextView.setText(laboratory.getTitle());
    }

    @Override
    public int getItemCount() {
        return mLaboratoryArrayList.size();
    }

    public interface NavigationAdapterItemClickHandler {
        void onNavigationAdapterItemClick(Laboratory laboratory, int i);
    }

    class NavigationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTextView;

        NavigationViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.text_view_layout);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mNavigationAdapterItemClickHandler != null) {
                int i = getAdapterPosition();
                mNavigationAdapterItemClickHandler.onNavigationAdapterItemClick(mLaboratoryArrayList.get(i), i);
            }
        }
    }
}
