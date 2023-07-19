package com.mariam.registeration;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mariam.registeration.screens.profile.ProfileSettings;

import java.util.ArrayList;
import java.util.List;

class Services {
    private String title;
    private String description;
    private int icon;

    public Services(String title, String description, int icon) {
        this.title = title;
        this.description = description;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getIcon() {
        return icon;
    }
}

public class PostService extends AppCompatActivity {
    private ImageButton backButton;
    private ListView listView;
    private List<Services> itemList;
    private ArrayAdapter<Services> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_service);

        listView = findViewById(R.id.listView);

        // Initialize the itemList
        itemList = new ArrayList<>();

        itemList.add(new Services("Pet Care", "Seeking help with pet sitting, dog walking, or pet grooming.", R.drawable.pets));
        itemList.add(new Services("Installation", "Pertaining any furniture installation or basic handyman tasks around the house.", R.drawable.installation));
        itemList.add(new Services("Gardening", "Help with your home garden: mowing the lawn, trimming bushes, washing pavement, etc...", R.drawable.garden));
        itemList.add(new Services("Transportation", "Whether you want to carpool, deliver an item, or pick up something.", R.drawable.transportation));
        itemList.add(new Services("Car Care", "Need someone to wash your car, fill up the gas tank, or anything car related.", R.drawable.car));

        adapter = new ArrayAdapter<Services>(this, R.layout.service_list_item, R.id.titleText, itemList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ViewHolder viewHolder;

                if (convertView == null) {
                    viewHolder = new ViewHolder();
                    viewHolder.iconImageView = view.findViewById(R.id.icon);
                    viewHolder.titleTextView = view.findViewById(R.id.titleText);
                    viewHolder.descriptionTextView = view.findViewById(R.id.descriptionText);
                    view.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) view.getTag();
                }

                Services service = getItem(position);

                if (service != null) {
                    viewHolder.iconImageView.setImageResource(service.getIcon());
                    viewHolder.titleTextView.setText(service.getTitle());
                    viewHolder.descriptionTextView.setText(service.getDescription());
                }

                return view;
            }
        };

        listView.setAdapter(adapter);

        // Handle item click event
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Services service = itemList.get(position);
                if (service != null) {
                    String selectedTitle = service.getTitle();
                    moveToNextActivity(selectedTitle);
                }
            }
        });

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle back button press
                goback();
            }
        });

        ImageView icon1 = findViewById(R.id.icon1);
        ImageView icon3 = findViewById(R.id.icon3);


        icon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle icon1 click here
                Intent intent = new Intent(PostService.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        icon3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle icon1 click here
                Intent intent = new Intent(PostService.this, ProfileSettings.class);
                startActivity(intent);
            }
        });
    }

    private void moveToNextActivity(String selectedTitle) {
        // Start a new activity or perform any other action you desire
        Intent intent = new Intent(PostService.this, CreatePost.class);
        intent.putExtra("title", selectedTitle);
        startActivity(intent);
    }

    private void goback() {
        // Start a new activity or perform any other action you desire
        Intent intent = new Intent(PostService.this, MyRequests.class);
        startActivity(intent);
    }

    private class ViewHolder {
        ImageView iconImageView;
        TextView titleTextView;
        TextView descriptionTextView;
    }
}
