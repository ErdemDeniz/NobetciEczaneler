package com.example.deniz.maptest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.deniz.maptest.Model.Venue;
import com.example.deniz.maptest.Model.VenueResponse;
import com.google.gson.Gson;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<Venue> venueList;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mRequestQueue = Volley.newRequestQueue(MainActivity.this);
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });




        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://api.foursquare.com/v2/venues/search?oauth_token=EJU4B0U1G1PT10U2AOBFWFXCXIMECRZFU1IXYCS1F5EDY0IK&v=20131016&ll=39.381241%2C%2033.021666&intent=checkin&categoryId=4bf58dd8d48988d10f951735 ";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.

                        processResponse(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);



    }

    public void processResponse(String response)
    {

        Gson gson = new Gson();
        VenueResponse vr = gson.fromJson(response,VenueResponse.class);

        venueList = vr.getResponse().getVenues();


        ListView lv = (ListView) findViewById(R.id.lv_venues);

        VenuesArrayAdapter adapter = new VenuesArrayAdapter(MainActivity.this);

        lv.setAdapter(adapter);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                try {
                    Toast.makeText(MainActivity.this, venueList.get(position).getContact().getPhone(), Toast.LENGTH_LONG).show();
                    Toast.makeText(MainActivity.this, venueList.get(position).getLocation().getAddress(), Toast.LENGTH_LONG).show();

                    Intent goTO = new Intent(MainActivity.this,MapsActivity.class);
                    startActivity(goTO);

                }catch (Exception e)
                {
                    Toast.makeText(MainActivity.this,"Telefon Yok",Toast.LENGTH_SHORT).show();

                }


            }
        });

    }

    /*public double Lat(double i){
        double lat = 0;
        for (int j = 0; j < venueList.size(); j++) {
            lat = venueList.get(j).getLocation().getLat();

        }
        return lat;
    }
    public double Lng(double a){
        double log = 0;
        for (int j = 0; j < venueList.size(); j++) {
            log = venueList.get(j).getLocation().getLng();

        }
        return log;
    }*/



    public class VenuesArrayAdapter extends ArrayAdapter<String> {
        private final Context context;

        public VenuesArrayAdapter(Context context) {
            super(context, -1);
            this.context = context;
        }


        @Override
        public int getCount() {
            return venueList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.rowlayout, parent, false);

            //Mekan Adi
            TextView txtVenue = (TextView) rowView.findViewById(R.id.txt_venue);

            txtVenue.setText( venueList.get(position).getName());


            //Mekan Kategori Ikon

            try {
                String imgUrl = venueList.get(position).getCategories().get(0).getIcon().getPrefix() + "88.png";


                NetworkImageView avatar = (NetworkImageView) rowView.findViewById(R.id.img_venue);
                avatar.setImageUrl(imgUrl, mImageLoader);
            }catch (Exception e)
            {

                e.printStackTrace();

            }


            return rowView;
        }
    }
}
