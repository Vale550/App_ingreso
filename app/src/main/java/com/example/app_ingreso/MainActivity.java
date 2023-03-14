//Librería JDBC
package com.example.app_ingreso;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.app_ingreso.bd.DbHelper;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Window window;
    String primary = "#0E7827";
    String primary2 = "#DC3F11";
    String primary3 = "#FF8000";
    private static final String URL = "jdbc:mysql://localhost:3306/bdd";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    ImageButton btnscan;
    ConstraintLayout navigationView;
    Button btn_acp;
    EditText text;
    RequestQueue requestQueue;
    ImageView imgOk, imgError, imgHome, imgList;

    DbHelper dbHelper = new DbHelper(MainActivity.this);

    //Crea el main
    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.window = getWindow();
        DbHelper bdobj = new DbHelper(this);
        //getReadableDatabase(); //solo para leer
        SQLiteDatabase db = bdobj.getWritableDatabase();
        //db.execSQL("");//(no trae datos pero ejecuta


        btnscan = findViewById(R.id.btn_scan);
        btn_acp = findViewById(R.id.btn_aceptar);
        text = findViewById(R.id.Texto);
        imgOk= findViewById(R.id.imgOk);
        imgError= findViewById(R.id.imgError);
        imgHome= findViewById(R.id.imgHome);
        imgList= findViewById(R.id.imgList);
        navigationView= findViewById(R.id.navigationView);


        btn_acp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    //bdnpost("https://appingresos.000webhostapp.com/post.php");

                    buscarUsuarios("https://appingresos.000webhostapp.com/busquedawhile.php");
                    //buscarUsuarios("https://appingresos.000webhostapp.com/busqueda.php?idticket="+text.getText().toString());
                }
                catch (Exception e){

                }
            }
        });


        //Scaner
        btnscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setOrientationLocked(false);
                integrator.setPrompt("Lector QR");
                integrator.setBeepEnabled(true);
                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();

            }
        });
    }

    //Scaner
    protected void onActivityResult (int requestCode, int resultcode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultcode, data);
        if (result != null){
            if (result.getContents()==null){
                Toast.makeText(this,"Scaner cancelado",Toast.LENGTH_LONG).show();
            }else {
                String res= String.valueOf(result);
                String[] parts = res.split("@");
                String dni = parts[4]; // 654321
                bdnpost("https://appingresos.000webhostapp.com/modificar.php?codigo="+dni);
                text.setText(dni);
                modifica(dni);
                if (modifica(dni)){
                    cambiaColorOK(primary);
                    imgOk.setVisibility(View.VISIBLE);
                    imgError.setVisibility(View.INVISIBLE);
                } else {
                    cambiaColorOK(primary2);
                }
            }
        }else {
            super.onActivityResult(requestCode, resultcode, data);
        }
    }
    private void cambiaColorOK (String primary){
        window.setStatusBarColor(Color.parseColor(primary));
        window.setNavigationBarColor(Color.parseColor(primary));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(primary)));
        btnscan.setBackgroundColor(Color.parseColor(primary));
        btn_acp.setBackgroundColor(Color.parseColor(primary));
    }


    //Insertar/actualizar BDN
    private void bdnpost (String URL){
        try {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(getApplicationContext(), "Operacion exitosa", Toast.LENGTH_SHORT).show();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                    cambiaColorOK(primary2);
                    imgError.setVisibility(View.VISIBLE);
                    imgOk.setVisibility(View.INVISIBLE);

                }
            }){
                @Nullable
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> parametros = new HashMap<String, String>();
                    Log.d("codigo",text.getText().toString());
                    parametros.put("codigo",text.getText().toString());

                    return parametros;

                }
            };
            requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
            new CountDownTimer(1500, 1500) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    cambiaColorOK(primary3);
                    //imgOk.setVisibility(View.INVISIBLE);
                }

            }.start();
        } catch (Exception e) {
            Log.d("D1","Error");
        }

    }
    public boolean modifica (String dni){

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("UPDATE '28enero' SET estado='invalida' WHERE estado='invalida' AND DNI=dni");
            return true;
        }catch (Exception e){return false;}
    }

    //Consultas BDN
    public void buscarUsuarios(String URL){
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        String [] resl = new String[response.length()];
                        jsonObject = response.getJSONObject(i);
                        Log.d("Debug while",jsonObject.getString("id"));
                        text.setText(jsonObject.getString("id"));

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error de Conexión", Toast.LENGTH_SHORT).show();
            }
        }
        );
        requestQueue=Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);

    }

}