//Librería JDBC
package com.example.app_ingreso;


import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
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

import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String URL = "jdbc:mysql://localhost:3306/bdd";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    Button btnscan;
    Button btn_acp;
    EditText text;
    Button btn_bd;
    Button btn_menu;
    RequestQueue requestQueue;

    //Crea el main
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DbHelper bdobj = new DbHelper(this);
        //getReadableDatabase(); //solo para leer
        SQLiteDatabase db = bdobj.getWritableDatabase();
        //db.execSQL("");//(no trae datos pero ejecuta todo)

        btnscan = findViewById(R.id.btn_scan);
        btn_acp = findViewById(R.id.btn_aceptar);
        text = findViewById(R.id.Texto);
        btn_bd = findViewById(R.id.btn_bd);
        btn_menu = findViewById(R.id.btn_menu);
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
                integrator.setPrompt("Lector - CDP");
                integrator.setCameraId(0);
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
            }
        }else {
            super.onActivityResult(requestCode, resultcode, data);
        }
    }

    //Cambio de ventana
    public void bdact(View view) {
//        Intent intentbd = new Intent(this, BD.class);
//        startActivity(intentbd);
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
                }
            }){
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> parametros = new HashMap<String, String>();
                    Log.d("codigo",text.getText().toString());
                    parametros.put("codigo",text.getText().toString());
                    return parametros;
                }
            };
            requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

        } catch (Exception e) {
            Log.d("D1","Error");
        }

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

