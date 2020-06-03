package com.kelompok4.weatherapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LocationInput extends AppCompatActivity {
    private TextInputEditText qSearch; // untuk buat form input text
    private Button btnSearch;
    private String API;
    private String id, cityName, countryCode, countryName;
    private String search;

    private ArrayList<LocationModel.Location> locationArray; //deklarasi arraylist bertipe model Location pada class LocationModel
    private SearchLocationAdapter adapter; //implementasi adapter untuk recycleview dari kelas LocationList
    private RecyclerView recyclerView; //implementasi recycleview
    private LinearLayoutManager linearLayoutManager; //implementasi linear layout, biar listnya menurun kebawah secara linear

    private TextView listKosong;
    private TextView lokasiNotFound;

    private Locale loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_input);

        API = BuildConfig.API_KEY;

        qSearch = (TextInputEditText) findViewById(R.id.search_lokasi);
        btnSearch = (Button) findViewById(R.id.button_search);

        lokasiNotFound = findViewById(R.id.lokasi_notfound);
        lokasiNotFound.setVisibility(View.GONE);
        listKosong = findViewById(R.id.search_kosong);

        locationArray = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerviewsearch);

        adapter = new SearchLocationAdapter(locationArray, this);
        linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

    @Override
    public boolean onSupportNavigateUp() {
        this.finish();
        return super.onSupportNavigateUp();
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

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            }
            catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if (result == null ){
                Toast.makeText(getApplicationContext(), R.string.timeout_message, Toast.LENGTH_SHORT).show();
            }
            else{
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
                        locationArray.add(new LocationModel.Location(cityName, countryName, id));
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
}
