package com.example.app_ingreso;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.app_ingreso.bd.DbHelper;

import java.util.ArrayList;

public class Selevento extends AppCompatActivity {

    Spinner selector;
    Button btncont;
    DbHelper dbHelper = new DbHelper(Selevento.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selevento);
        selector = findViewById(R.id.selector);
        btncont=findViewById(R.id.btncont);
        DbHelper bdobj = new DbHelper(this);
        SQLiteDatabase dbr = bdobj.getReadableDatabase();
        Log.d("SPINER","emp");

        Cursor filas = dbr.rawQuery("SELECT * FROM eventos",null);
        int cont = filas.getCount();
        String[] eventos = new String[cont];
        int i = 0;
        if (filas.moveToFirst()) {
            do {
                String codigo= filas.getString(2);
                Log.d("SPINER",codigo);
                eventos[i] = codigo;
                i++;
            } while(filas.moveToNext());
        }
        selector.setAdapter(new ArrayAdapter<String>(Selevento.this, android.R.layout.simple_spinner_dropdown_item, eventos));
        String evet = (String) selector.getSelectedItem();
        Log.d("Select",evet);

        btncont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent act = new Intent(Selevento.this, MainActivity.class);
                act.putExtra("evento",evet);
                startActivity(act);
            }
        });
    }
}