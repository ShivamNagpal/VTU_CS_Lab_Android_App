package com.nagpal.shivam.vtucslab.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nagpal.shivam.vtucslab.R;
import com.nagpal.shivam.vtucslab.Utility.Info;

import java.util.ArrayList;

public class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.NavigationViewHolder> {

    private Context mContext;
    private ArrayList<Info> mInfoArrayList;
    private NavigationAdapterItemClickHandler mNavigationAdapterItemClickHandler;

    public NavigationAdapter(@NonNull Context context, @NonNull ArrayList<Info> infoArrayList) {
        mContext = context;
        mInfoArrayList = infoArrayList;
    }

    public void addAll(ArrayList<Info> infoArrayList) {
        int i = mInfoArrayList.size();
        mInfoArrayList.addAll(infoArrayList);
        notifyItemRangeInserted(i, infoArrayList.size());
    }

    public void clear() {
        mInfoArrayList.clear();
        notifyDataSetChanged();
    }

    public void setNavigationAdapterItemClickHandler(NavigationAdapterItemClickHandler navigationAdapterItemClickHandler) {
        mNavigationAdapterItemClickHandler = navigationAdapterItemClickHandler;
    }

    @NonNull
    @Override
    public NavigationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.text_view_layout, parent, false);
        return new NavigationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NavigationViewHolder holder, int position) {
        Info info = mInfoArrayList.get(position);
        holder.mTextView.setText(info.getTitle());
    }

    @Override
    public int getItemCount() {
        return mInfoArrayList.size();
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
                mNavigationAdapterItemClickHandler.onNavigationAdapterItemClick(mInfoArrayList.get(i), i);
            }
        }
    }

    public interface NavigationAdapterItemClickHandler {
        void onNavigationAdapterItemClick(Info info, int i);
    }
}
