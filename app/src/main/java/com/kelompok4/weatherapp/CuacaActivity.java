package com.kelompok4.weatherapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CuacaActivity extends AppCompatActivity {
    String id;
    String API;
    private String KEY_ID = "LOCATIONID";

    Long updatedAtUnix;

    String weatherDescription;
    String countryCode;
    String countryName;
    String address;
    Locale loc;

    String temp;
    String tempMin;
    String tempMax;
    String feelsLike;
    String windSpeed;

    Long sunriseUnix;
    Long sunsetUnix;
    String timeZoneName;
    Date sunriseFormatted;
    Date sunsetFormatted;
    SimpleDateFormat sunTime;
    SimpleDateFormat updatedAtTime;
    Long timeZone;
    Date updatedAtFormated;

    OkHttpClient client = new OkHttpClient();

    ImageView drawableBackground;
    private int weatherBackground;

    TextView addressTxt, updated_atTxt, statusTxt, tempTxt, temp_minTxt, temp_maxTxt, sunriseTxt,
            sunsetTxt, windTxt, feelsLiketxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuaca2);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        drawableBackground = findViewById(R.id.cuacabackground);

        API = getResources().getString(R.string.api_key);

        Bundle extras = getIntent().getExtras(); //ini di pake buat nangkep data yang di dapet dari activity yang manggil activity ini
        id = extras.getString(KEY_ID);

        addressTxt = findViewById(R.id.address);
        updated_atTxt = findViewById(R.id.updated_at);
        statusTxt = findViewById(R.id.status);
        tempTxt = findViewById(R.id.temp);
        temp_minTxt = findViewById(R.id.temp_min);
        temp_maxTxt = findViewById(R.id.temp_max);
        sunriseTxt = findViewById(R.id.sunrise_time);
        sunsetTxt = findViewById(R.id.sunset_time);
        windTxt = findViewById(R.id.wind);
        feelsLiketxt = findViewById(R.id.feels_like_temp);

        new weatherTask().execute();
    }

    class weatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            findViewById(R.id.spinner).setVisibility(View.VISIBLE);
            findViewById(R.id.mainContainer).setVisibility(View.GONE);
            findViewById(R.id.errorText).setVisibility(View.GONE);
        }

        protected String doInBackground(String... args) { // ini method di jalanin setelah onPreExecute di jalanin, biasanya disini di taruh reqquest ke data API, dll
            String url = "http://api.openweathermap.org/data/2.5/weather?id=" + id + "&units=metric&appid=" + API;
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
        protected void onPostExecute(String result) { // ini method di panggil kalo yang di doInBackground udah selesai di jalanin
            try {  //ini untuk nge parse data json yang di dapet dari API nya
                JSONObject jsonObj = new JSONObject(result);
                JSONObject main = jsonObj.getJSONObject("main");
                JSONObject sys = jsonObj.getJSONObject("sys");
                JSONObject wind = jsonObj.getJSONObject("wind");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);

                weatherBackground = weather.getInt("id");

                if (weatherBackground == 800){
                    drawableBackground.setImageResource(R.drawable.clear);
                }

                else if (weatherBackground == 801 || weatherBackground == 802 || weatherBackground == 803 || weatherBackground == 804){
                    drawableBackground.setImageResource(R.drawable.clouds);
                }

                else if (weatherBackground == 701 || weatherBackground == 711 || weatherBackground == 721 ||
                        weatherBackground == 731 || weatherBackground == 741 || weatherBackground ==  751 || weatherBackground == 761 ||
                        weatherBackground == 762 || weatherBackground == 771 || weatherBackground == 781){
                    drawableBackground.setImageResource(R.drawable.atmosphere);
                }

                else if (weatherBackground == 600 || weatherBackground == 601 || weatherBackground == 602 ||
                        weatherBackground == 611 || weatherBackground == 612 || weatherBackground ==  613 || weatherBackground == 615 ||
                        weatherBackground == 616 || weatherBackground == 620 || weatherBackground == 621|| weatherBackground == 622){
                    drawableBackground.setImageResource(R.drawable.atmosphere);
                }

                else if (weatherBackground == 500 || weatherBackground == 501 || weatherBackground == 503 || weatherBackground == 504 ||
                        weatherBackground == 511 || weatherBackground == 520 || weatherBackground == 521 || weatherBackground == 522 || weatherBackground == 531){
                    drawableBackground.setImageResource(R.drawable.snow);
                }

                else if (weatherBackground == 300 || weatherBackground == 301 || weatherBackground ==  302 || weatherBackground == 310 ||
                        weatherBackground == 311 || weatherBackground == 312 || weatherBackground == 313 || weatherBackground == 314 || weatherBackground == 321){
                    drawableBackground.setImageResource(R.drawable.drizzle);
                }

                else if (weatherBackground == 200 || weatherBackground == 201 || weatherBackground == 202 || weatherBackground == 210 || weatherBackground == 211 ||
                        weatherBackground == 212 || weatherBackground == 221 || weatherBackground == 230 || weatherBackground == 231 || weatherBackground == 232){
                    drawableBackground.setImageResource(R.drawable.thunderstorm);
                }
                temp = main.getString("temp") + "°C";
                tempMin = "Min Temp: " + main.getString("temp_min") + "°C";
                tempMax = "Max Temp: " + main.getString("temp_max") + "°C";
                feelsLike = main.getString("feels_like") + "°C";


                timeZone = jsonObj.getLong("timezone")/3600; //ambil timezone dari lokasi
                sunriseUnix = sys.getLong("sunrise");
                sunsetUnix = sys.getLong("sunset");
                updatedAtUnix = jsonObj.getLong("dt");
                sunriseFormatted = new Date(sunriseUnix * 1000L);
                sunsetFormatted = new Date(sunsetUnix * 1000L);
                updatedAtFormated = new Date(updatedAtUnix * 1000L);


                //timezone untuk unix time formatting
                if (timeZone > 0){
                    timeZoneName = "GMT" + "+" + timeZone.toString();
                }

                else {
                    timeZoneName = "GMT" + timeZone.toString();
                }

                sunTime = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                sunTime.setTimeZone(TimeZone.getTimeZone(timeZoneName));
                updatedAtTime = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH);
                updatedAtTime.setTimeZone(TimeZone.getTimeZone(timeZoneName));

                windSpeed = wind.getString("speed") + " m/s";
                weatherDescription = weather.getString("description");

                countryCode = sys.getString("country");
                loc = new Locale("",countryCode);
                countryName = loc.getDisplayCountry();
                address = jsonObj.getString("name") + ", " + countryName;

                //isi semua view text nya
                addressTxt.setText(address);
                updated_atTxt.setText(updatedAtTime.format(updatedAtFormated) + " " + "(" + timeZoneName + ")");
                statusTxt.setText(weatherDescription.toUpperCase());
                tempTxt.setText(temp);
                temp_minTxt.setText(tempMin);
                temp_maxTxt.setText(tempMax);
                sunriseTxt.setText(sunTime.format(sunriseFormatted));
                sunsetTxt.setText(sunTime.format(sunsetFormatted));
                windTxt.setText(windSpeed);
                feelsLiketxt.setText(feelsLike);

                findViewById(R.id.spinner).setVisibility(View.GONE);
                findViewById(R.id.mainContainer).setVisibility(View.VISIBLE);
            }

            catch (JSONException e) {
                findViewById(R.id.spinner).setVisibility(View.GONE);
                findViewById(R.id.errorText).setVisibility(View.VISIBLE);
            }
        }
    }
}
