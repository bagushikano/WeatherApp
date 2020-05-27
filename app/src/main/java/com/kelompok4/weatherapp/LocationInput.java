package com.kelompok4.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LocationInput extends AppCompatActivity {
    private TextInputEditText qSearch; // untuk buat form input text
    private Button btnSearch;
    String API;
    String id, cityName, countryCode, countryName;
    String search;

    private ArrayList<LocationModel.Location> locationArray; //deklarasi arraylist bertipe model Location pada class LocationModel

    private SearchLocationAdapter adapter; //implementasi adapter untuk recycleview dari kelas LocationList
    private RecyclerView recyclerView; //implementasi recycleview
    private LinearLayoutManager linearLayoutManager; //implementasi linear layout, biar listnya menurun kebawah secara linear

    private TextView listKosong;
    private TextView lokasiNotFound;
    OkHttpClient client = new OkHttpClient();
    Locale loc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_input);

        API = getResources().getString(R.string.api_key);

        qSearch = (TextInputEditText) findViewById(R.id.search_lokasi);
        btnSearch = (Button) findViewById(R.id.button_search);

        lokasiNotFound = findViewById(R.id.lokasi_notfound);
        lokasiNotFound.setVisibility(View.GONE);

        listKosong = findViewById(R.id.search_kosong);

        locationArray = new ArrayList<>(); //di dalem locationArray di buatin arraylist nantinya arraylist ini di tampilin

        recyclerView = findViewById(R.id.recyclerviewsearch); //untuk nge set layout recycleview nya sesuai xml recycleview

        adapter = new SearchLocationAdapter(this, locationArray, this); //ngehubungin adapter sama data di array locationArray nya
        linearLayoutManager = new LinearLayoutManager(this); // menampilkan item berupa list

        recyclerView.setLayoutManager(linearLayoutManager); // menset layoutmanager yg telah kita buat
        recyclerView.setAdapter(adapter); // menset adapter-nya ke recycleview nya sendiri

        if (locationArray.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            listKosong.setVisibility(View.VISIBLE);
        }

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search = qSearch.getText().toString();
                locationArray.clear();
                adapter.notifyDataSetChanged();
                new SearchLokasi().execute();
            }
        });

        qSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search = qSearch.getText().toString();
                    locationArray.clear();
                    adapter.notifyDataSetChanged();
                    new SearchLokasi().execute();
                    return true;
                }
                return false;
            }
        });
    }

    class SearchLokasi extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog = new ProgressDialog(LocationInput.this, R.style.ProgressBarStyle);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage(getResources().getString(R.string.wait));
            this.dialog.show();
        }

        protected String doInBackground(String... args) {

            String url = "http://api.openweathermap.org/data/2.5/find?mode=json&type=like&q=" + search + "&cnt=10&appid=" + API;
            Request.Builder builder = new Request.Builder();
            builder.url(url);
            Request request = builder.build();
            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            try {
                JSONObject jsonObj = new JSONObject(result);
                JSONArray list = jsonObj.getJSONArray("list");

                if (jsonObj.getInt("count") == 0){
                    lokasiNotFound.setVisibility(View.VISIBLE);
                }

                else {
                    lokasiNotFound.setVisibility(View.GONE);
                }

                for (int i = 0; i < list.length(); i++) {
                    JSONObject row = list.getJSONObject(i);
                    JSONObject sys = row.getJSONObject("sys");
                    id = row.getString("id");
                    cityName = row.getString("name");
                    countryCode = sys.getString("country");

                    loc = new Locale("",countryCode);
                    countryName = loc.getDisplayCountry();

                    locationArray.add(new LocationModel.Location(countryName, cityName, id));
                }

                recyclerView.setVisibility(View.VISIBLE);
                listKosong.setVisibility(View.GONE);

                adapter.notifyDataSetChanged();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
