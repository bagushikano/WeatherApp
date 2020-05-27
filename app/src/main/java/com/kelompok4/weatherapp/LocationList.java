package com.kelompok4.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LocationList extends RecyclerView.Adapter<LocationList.ViewHolder> {

    private Context mContext;
    private ArrayList<LocationModel.Location> lokasi;

    public LocationList(Context context, ArrayList<LocationModel.Location> lokasi) {
        mContext = context;
        this.lokasi = lokasi;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txt_country.setText(lokasi.get(position).getCountryName());
        holder.txt_city.setText(lokasi.get(position).getCityName());
    }

    @Override
    public int getItemCount() {
        return lokasi.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        private AppCompatTextView txt_country;
        private AppCompatTextView txt_city;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_country = itemView.findViewById(R.id.txt_country);
            txt_city = itemView.findViewById(R.id.txt_city);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { // ini listener untuk listnya jadi tiap listnya di klik, ini method di panggil
                    int position = getAdapterPosition(); // untuk mendapatkan posisi di adapter
                    LocationModel.Location modelLokasi = lokasi.get(position); //untuk dapetin posisi list yang di klik
                    Log.d("country_value", "Value: " + modelLokasi.getCountryName()); //untuk debugging aja, nge print di logcat
                    Log.d("city_value", "Value: " + modelLokasi.getCityName());
                    Intent cuaca = new Intent(mContext, CuacaActivity.class);
                    cuaca.putExtra("KOTA", modelLokasi.getCityName());
                    cuaca.putExtra("NEGARA", modelLokasi.getCountryName());
                    cuaca.putExtra("LOCATIONID", modelLokasi.getLocationId());
                    mContext.startActivity(cuaca); //nge start activitynya dengan intent cuaca
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    lokasi.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, lokasi.size());
                    return false;
                }
            });
        }
    }
}
