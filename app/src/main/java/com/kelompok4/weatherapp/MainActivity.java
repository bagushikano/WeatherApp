package com.kelompok4.weatherapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private int INPUT_LOKASI = 0; //key untuk nentuin activity yang di jalanin

    private ArrayList<LocationModel.Location> locationArray; //deklarasi arraylist bertipe model Location pada class LocationModel

    private LocationList adapter; //implementasi adapter untuk recycleview dari kelas LocationList
    private RecyclerView recyclerView; //implementasi recycleview
    private LinearLayoutManager linearLayoutManager; //implementasi linear layout, biar listnya menurun kebawah secara linear

    private TextView listKosong;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //on create ini selalu di jalanin setiap activity di buat di lifecycle android
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //untuk nge set layout activity nya dengan xml activity_main


        listKosong = findViewById(R.id.empty_view);

        locationArray = new ArrayList<>(); //di dalem locationArray di buatin arraylist nantinya arraylist ini di tampilin

        recyclerView = findViewById(R.id.recyclerview); //untuk nge set layout recycleview nya sesuai xml recycleview

        adapter = new LocationList(this, locationArray); //ngehubungin adapter sama data di array locationArray nya
        linearLayoutManager = new LinearLayoutManager(this); // menampilkan item berupa list

        listKosong.setVisibility(View.VISIBLE);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                if (adapter.getItemCount() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    listKosong.setVisibility(View.VISIBLE);
                }
            }
        });

        recyclerView.setLayoutManager(linearLayoutManager); // menset layoutmanager yg telah kita buat
        recyclerView.setAdapter(adapter); // menset adapter-nya ke recycleview nya sendiri


        FloatingActionButton fab = findViewById(R.id.add_location_fab); //fab button utk nambah lokasi
        fab.setOnClickListener(new View.OnClickListener() { //listener untuk fab buttonnya tiap di klik
            @Override
            public void onClick(View view) { //aksi ketika fab buttonnya di klik
                Intent addlocation = new Intent(getApplicationContext(), LocationInput.class); //pembuatan intent, intent ini gunanya buat jembatan antar activity
                startActivityForResult(addlocation, INPUT_LOKASI);
            }
        });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent cuaca) {  // ini method di jalanin kalo child activity yang di jalanain activity ini udah selesai
        super.onActivityResult(requestCode, resultCode, cuaca);
        if (resultCode == Activity.RESULT_OK && requestCode == INPUT_LOKASI) { // ini untuk nangkep result activity dari activity yang key nya INPUT_LOKASI (addlocation kalo disni)
            if (cuaca.hasExtra("KOTA") && cuaca.hasExtra("NEGARA")) {  //untuk ngecek apakah activitynya ada nerima data dengan key KOTA dan NEGARA
                if (locationArray.contains(new LocationModel.Location(cuaca.getExtras().getString("KOTA"), cuaca.getExtras().getString("NEGARA"), cuaca.getExtras().getString("LOCATIONID")))){
                    Toast.makeText(this, getResources().getString(R.string.location_exist), Toast.LENGTH_SHORT).show();
                }
                else{
                    locationArray.add(new LocationModel.Location(cuaca.getExtras().getString("KOTA"), cuaca.getExtras().getString("NEGARA"), cuaca.getExtras().getString("LOCATIONID")));
                    recyclerView.setVisibility(View.VISIBLE);
                    listKosong.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            Intent about = new Intent(this, AboutActivity.class);
            startActivity(about);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
