package com.nagpal.shivam.vtucslab.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nagpal.shivam.vtucslab.R;
import com.nagpal.shivam.vtucslab.Utility.Info;

import java.util.ArrayList;

public class InfoAdapter extends ArrayAdapter<Info> {

    public InfoAdapter(@NonNull Context context, ArrayList<Info> infoArrayList) {
        super(context, 0, infoArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (convertView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.text_view_layout, parent, false);
        }
        Info currentInfo = getItem(position);
        TextView textView = listItemView.findViewById(R.id.text_view_layout);
        if (currentInfo != null) {
            textView.setText(currentInfo.getTitle());
        }
        return listItemView;
    }
}
