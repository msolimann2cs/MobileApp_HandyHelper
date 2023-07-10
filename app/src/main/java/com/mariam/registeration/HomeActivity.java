package com.mariam.registeration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.BoringLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.mariam.registeration.R.drawable;
import com.mariam.registeration.R.id;
import com.mariam.registeration.R.layout;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    TextView filterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        int[] images = new int[]{R.drawable.a, R.drawable.a, R.drawable.a, R.drawable.a, R.drawable.a};
        String[] descs = new String[]{"Walk my dog for 20 minutes hjklh hjkl hlk; hl;kjkl; kl; hjkl;hhiohiopph upu huioguiogo guio guiog uio ij kl hjlh oih uj lhuiohhuiohuioghuio hio huioh uiohuioh uiohu iohuiohui", "Change 4 light bulbs", "Change 4 light bulbs", "Mow my loan", "Deliver a 4kg package"};
        String[] titles = new String[]{"Pet Care", "Installation", "Installation", "Gardening", "Transportation"};
        String[] dates = new String[]{"Tom", "Today", "In two Days", "In 4 days", "Today"};
        String[] locations = new String[]{"20 meters", "200 meters", "1 km", "500 meters", "5 meters"};
        float[] prices = new float[]{100.0F, 200.0F, 300.0F, 100.0F, 200.0F};
        final ArrayList<Request> reqs = new ArrayList();

        for (int i = 0; i < 5; ++i) {
            Request req = new Request(titles[i], descs[i], dates[i], locations[i], 2.0F, prices[i], R.drawable.a);
            reqs.add(req);
        }

        Bundle bundle = this.getIntent().getExtras();
        boolean cats[] = new boolean[5];
        float minprice, maxprice, mindate, maxdate, mindis, maxdis;

        if (bundle != null) {
            cats = bundle.getBooleanArray("catagories");
            for (int i = 0; i < reqs.size(); i++) {
                if (cats[0] == false && reqs.get(i).title == "Gardening") {
                    reqs.remove(reqs.get(i));
                    i--;
                }

            }
            for (int i = 0; i < reqs.size(); i++) {

                if (cats[1] == false && reqs.get(i).title == "Car Care") {
                    reqs.remove(reqs.get(i));
                    i--;
                }

            }
            for (int i = 0; i < reqs.size(); i++) {


                if (cats[2] == false && reqs.get(i).title == "Installation") {
                    reqs.remove(reqs.get(i));
                    i--;

                }


            }
            for (int i = 0; i < reqs.size(); i++) {


                if (cats[3] == false && reqs.get(i).title == "Transportation") {
                    reqs.remove(reqs.get(i));
                    i--;
                }


            }
            for (int i = 0; i < reqs.size(); i++) {

                if (cats[4] == false && reqs.get(i).title == "Pet Care") {
                    reqs.remove(reqs.get(i));
                    i--;
                }
            }
            minprice = bundle.getFloat("priceMin");
            maxprice = bundle.getFloat("priceMax");

            mindate = bundle.getFloat("dateMin");
            maxdate = bundle.getFloat("dateMax");
            mindis = bundle.getFloat("disMin");
            maxdis = bundle.getFloat("disMax");


        }

        RequestAdaptor adaptor = new RequestAdaptor(this, reqs);
        ListView lv = (ListView) this.findViewById(id.listView);
        this.filterBtn = (TextView) this.findViewById(id.filterButton);
        lv.setAdapter(adaptor);
        LinearLayout ll2 = (LinearLayout) this.findViewById(id.linearLayout2);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Request clickedReq = (Request) reqs.get(i);
                Intent intent = new Intent(HomeActivity.this, RequestDetailsActivity.class);
                intent.putExtra("reqName", clickedReq.title);
                intent.putExtra("reqDesc", clickedReq.description);
                intent.putExtra("reqDate", clickedReq.date);
                intent.putExtra("reqLocation", clickedReq.location);
                intent.putExtra("reqTime", clickedReq.time);
                intent.putExtra("reqPrice", clickedReq.price);
                intent.putExtra("userImage", clickedReq.image);
                HomeActivity.this.startActivity(intent);
            }
        });
        this.filterBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, FilterActivity.class);
                HomeActivity.this.startActivity(intent);
            }
        });
    }
}