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

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {

    private Context mContext;

    private ArrayList<Info> mInfoArrayList;
    private ItemClickHandler mItemClickHandler;

    public ContentAdapter(Context context, ArrayList<Info> infoArrayList) {
        this.mContext = context;
        this.mInfoArrayList = infoArrayList;
    }

    public void setItemClickHandler(ItemClickHandler itemClickHandler) {
        mItemClickHandler = itemClickHandler;
    }

    public void addAll(ArrayList<Info> infoArrayList) {
        mInfoArrayList.addAll(infoArrayList);
        notifyDataSetChanged();
    }


    public void clear() {
        mInfoArrayList.clear();
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_content_tile, parent, false);
        return new ContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentViewHolder holder, int position) {
        Info info = mInfoArrayList.get(position);
        holder.mTitle.setText(info.getTitle());
    }

    @Override
    public int getItemCount() {
        return mInfoArrayList.size();
    }

    public interface ItemClickHandler {
        void onClick(Info info, int position);
    }

    class ContentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mNumber;
        TextView mTitle;

        ContentViewHolder(View itemView) {
            super(itemView);
            mNumber = itemView.findViewById(R.id.layout_content_text_view_number);
            mTitle = itemView.findViewById(R.id.layout_content_text_view_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mItemClickHandler != null) {
                int position = getAdapterPosition();
                mItemClickHandler.onClick(mInfoArrayList.get(position), position);
            }
        }
    }
}
