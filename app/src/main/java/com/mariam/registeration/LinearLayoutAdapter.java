package com.mariam.registeration;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import java.util.List;

public class LinearLayoutAdapter extends ArrayAdapter<LinearLayout> {
    private LayoutInflater inflater;

    public LinearLayoutAdapter(Context context, List<LinearLayout> linearLayouts) {
        super(context, 0, linearLayouts);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout linearLayout = getItem(position);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_transaction_item, parent, false);
        }

        // Bind data or modify views in the LinearLayout here if needed

        return convertView;
    }
}
