package com.example.app_ingreso;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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

        ArrayList<String> lista = new ArrayList<String>();


        Cursor filas = dbr.rawQuery("SELECT * FROM eventos",null);

        do {
            String codigo= filas.getString(0);
            Log.d("SPINER",codigo);

        } while(filas.moveToNext());

    }
}