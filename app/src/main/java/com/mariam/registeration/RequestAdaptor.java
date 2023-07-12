package com.mariam.registeration;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mariam.registeration.R.id;
import com.mariam.registeration.R.layout;
import java.util.ArrayList;

public class RequestAdaptor extends ArrayAdapter<Request> {
    public RequestAdaptor(Context context, ArrayList<Request> ReqArrayList) {
        super(context, layout.list_item, ReqArrayList);
    }

    @NonNull
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Request request = (Request)this.getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext()).inflate(layout.list_item, parent, false);
        }

        TextView descText = (TextView)convertView.findViewById(id.desc);
        TextView titleText = (TextView)convertView.findViewById(id.title);
        TextView priceText = (TextView)convertView.findViewById(id.price);
        TextView locatText = (TextView)convertView.findViewById(id.location);
        TextView dateText = (TextView)convertView.findViewById(id.date);
        descText.setText(request.description);
        titleText.setText(request.title);
        priceText.setText(Float.toString(request.price));
        locatText.setText(String.format("%.0f", request.distance()) + 'm');
        dateText.setText(request.date);
        return convertView;
    }
}
