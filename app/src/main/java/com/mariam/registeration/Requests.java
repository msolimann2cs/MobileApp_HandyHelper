package com.mariam.registeration;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class Requests extends AppCompatActivity {
    private ListView listView;
    private List<ClipData.Item> itemList;
    private ArrayAdapter<ClipData.Item> adapter;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        listView = findViewById(R.id.listView);
        tabLayout = findViewById(R.id.tabLayout);

        // Generate random data
        generateRandomData();

        // Create adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemList);

        // Set adapter to ListView
        listView.setAdapter(adapter);

        // Set tab layout onTabSelected listener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                if (position == 0) {
                    // My Requests tab selected
                    Intent intent = new Intent(Requests.this, Requests.class);
                    startActivity(intent);
                } else if (position == 1) {
                    // My Applications tab selected
                    Intent intent = new Intent(Requests.this, MyApplications.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Do nothing
            }
        });
    }

    private void generateRandomData() {
        itemList = new ArrayList<>();

        // Generate 10 random items
        for (int i = 0; i < 10; i++) {
            String name = "Item " + (i + 1);
            String description = "Description " + (i + 1);
            ClipData.Item item = new ClipData.Item(name, description);
            itemList.add(item);
        }
    }
}
