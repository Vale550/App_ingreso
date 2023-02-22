//Librería JDBC
package com.example.app_ingreso;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.sql.DriverManager;
import java.sql.Statement;


public class MainActivity extends AppCompatActivity {

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


        btn_acp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                try {
                    int num = Integer.parseInt(text.getText().toString().trim());
                    if (num == 5) {
                        text.setText("a");
                    }
                } catch (Exception e){
                    text.setText(" ");
                };
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null){
            if (result.getContents() == null){
                Toast.makeText (this, "Lectora cancelada", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                text.setText(result.getContents());
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    };
    public void bdact (View view){
        Intent intentbd = new Intent(this, BD.class);
        startActivity(intentbd);
    }

    class Task extends AsyncTask<Void, Void, Void>{
        String recorders="", error="";

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection("192.168.0.4:3306/bdd", "test", "test");
                Statement st = conn.createStatement();
            }catch (Exception e){
                error = e.toString();
            }
            return null;
        }
    }



    //metodo para insertar registros con texts
    //comando para ejecutarlo: ejecutarserv("https://192.168.0.150/phpmyadmin/index.php");
//    public void ejecutarserv (String URL){
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Toast.makeText(getApplicationContext(), "OPERACION EXITOSA", Toast.LENGTH_LONG).show();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
//            }
//        }){
//            @Nullable
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> parametros= new HashMap<String, String>();
//                //carga cosas
//                parametros.put("nroLocal", text.getText().toString());
//                return parametros;
//            }
//        };
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(stringRequest);
//    }

}
