package com.example.app_ingreso;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.app_ingreso.bd.DbHelper;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class BD extends AppCompatActivity {

    Button btm_cargar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bd);

        btm_cargar = findViewById(R.id.btn_cargar);

        btm_cargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DbHelper dbHelper = new DbHelper(BD.this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                if (db != null){
                    Toast.makeText(BD.this, "Base de datos creada", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(BD.this, "Error", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    public void Volver (View view){
        Intent intentbd = new Intent(this, MainActivity.class);
        startActivity(intentbd);
    }
}