package com.nagpal.shivam.vtudslab.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nagpal.shivam.vtudslab.Utility.ProgramInfo;
import com.nagpal.shivam.vtudslab.R;

import java.util.ArrayList;

public class ProgramInfoAdapter extends ArrayAdapter<ProgramInfo> {

    public ProgramInfoAdapter(@NonNull Context context, ArrayList<ProgramInfo> programInfoArrayList) {
        super(context, 0, programInfoArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (convertView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.text_view_layout, parent, false);
        }
        ProgramInfo currentInfo = getItem(position);
        TextView textView = listItemView.findViewById(R.id.text_view_layout);
        if (currentInfo != null) {
            textView.setText(currentInfo.getTitle());
        }
        return listItemView;
    }
}
