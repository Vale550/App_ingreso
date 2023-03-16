package com.example.app_ingreso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.app_ingreso.bd.DbHelper;

import java.io.IOException;

public class List1 extends AppCompatActivity {
    TextView txtEntradasCompradas, txtEntradasUsadas;
    ImageView imgHome, imgList, imgLogout, imgWifiNo, imgWifiSi;
    ConstraintLayout navigationView;
    EditText nameEvento;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list1);
        imgHome = findViewById(R.id.imgHome);
        imgList = findViewById(R.id.imgList);
        imgLogout= findViewById(R.id.imgLogout);
        imgWifiNo= findViewById(R.id.imgWifiNo);
        imgWifiSi= findViewById(R.id.imgWifiSi);
        navigationView = findViewById(R.id.navigationView);
        nameEvento= findViewById(R.id.nameEvento);
        txtEntradasCompradas=findViewById(R.id.txtEntradasCompradas);
        txtEntradasUsadas=findViewById(R.id.txtEntradasUsadas);
        nameEvento.setText(getIntent().getStringExtra("evento"));
        imgHome.setOnClickListener(view -> {
            Intent act1 = new Intent(List1.this, Selevento.class);
            startActivity(act1);
        });
        imgList.setOnClickListener(view -> {
            Intent act2 = new Intent(List1.this, List1.class);
            startActivity(act2);
        });
        imgLogout.setOnClickListener(view -> {
            Intent act3 = new Intent(List1.this, Menu.class);
            startActivity(act3);
        });
        nroUsadasCompradas();
        try {
            if (conectadoAInternet()){
                imgWifiSi.setVisibility(View.VISIBLE);
                imgWifiNo.setVisibility(View.INVISIBLE);
            }else {
                imgWifiNo.setVisibility(View.VISIBLE);
                imgWifiSi.setVisibility(View.INVISIBLE);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean conectadoAInternet() throws IOException, InterruptedException {
        String comando = "ping -c 1 google.com";
        return (Runtime.getRuntime().exec (comando).waitFor() == 0);
    }
    public void nroUsadasCompradas() {
        String eventoc = getIntent().getStringExtra("evento");
        String evento = "a" + eventoc;
        DbHelper bdobj = new DbHelper(this);
        SQLiteDatabase dbr = bdobj.getReadableDatabase();
        Cursor filas = dbr.rawQuery("SELECT COUNT('idticket') FROM " + evento + " WHERE estado='invalida'", null);
        if (filas.moveToFirst()) {
            do {
                String codigo = filas.getString(0);
                txtEntradasUsadas.setText(codigo);
            } while (filas.moveToNext());
        }
        Cursor filas1 = dbr.rawQuery("SELECT COUNT('idticket') FROM " + evento + "", null);
        if (filas1.moveToFirst()) {
            do {
                String codigo1 = filas1.getString(0);
                txtEntradasCompradas.setText(codigo1);
            } while (filas1.moveToNext());
        }
    }

}