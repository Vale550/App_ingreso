//Librería JDBC
package com.example.app_ingreso;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app_ingreso.bd.DbHelper;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String URL = "jdbc:mysql://localhost:3306/bdd";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    Button btnscan;
    Button btn_acp;
    EditText text;
    Button btn_bd;
    Button btn_menu;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnscan = findViewById(R.id.btn_scan);
        btn_acp = findViewById(R.id.btn_aceptar);
        text = findViewById(R.id.Texto);
        btn_bd = findViewById(R.id.btn_bd);
        btn_menu = findViewById(R.id.btn_menu);

        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        btn_acp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new Async().execute();
            }
        });


        btnscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Lector - CDP");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(true);
                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();

            }
        });
    }

    protected void onActivityResult (int requestCode, int resultcode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultcode, data);
        if (result != null){
            if (result.getContents()==null){
                Toast.makeText(this,"Scaner cancelado",Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                text.setText(result.getContents());
            }
        }else {
            super.onActivityResult(requestCode, resultcode, data);
        }
    }

    public void bdact(View view) {
        Intent intentbd = new Intent(this, BD.class);
        startActivity(intentbd);

    }


}

