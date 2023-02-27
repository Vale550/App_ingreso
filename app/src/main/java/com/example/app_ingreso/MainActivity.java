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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;

public class MainActivity extends AppCompatActivity {

    private static final String URL = "jdbc:mysql://localhost:3306/bdd";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    Button btnscan;
    Button btn_acp;
    EditText text;
    TextView testtex;
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

    public void bdact(View view) {
        Intent intentbd = new Intent(this, BD.class);
        startActivity(intentbd);
    }

    class Async extends AsyncTask<Void, Void, Void> {

        String records = "",error="";
        @Override
        protected Void doInBackground(Void... voids) {
            try
            {
                String[] datosbn;
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection("jdbc:mysql://sql10.freesqldatabase.com:3306/sql10600738", "sql10600738", "seqEnpF4C3");
                Statement statement = connection.createStatement();
                Statement statement2 = connection.createStatement();
                ResultSet array = statement2.executeQuery("SELECT MAX(id) FROM 11Febrero;");
                ResultSet resultSet = statement.executeQuery("SELECT DNI FROM 11Febrero");

                if (array.next()){

                    String num = array.getString(1);
                    int numero = Integer.parseInt(num);
                    datosbn = new String[numero];

                    Log.d("Cantidad de filas", String.valueOf(numero));
                }

                while(resultSet.next()) {
                    records += resultSet.getString(1) + "\n";
                }
            }
            catch(Exception e)
            {
                error = e.toString();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
        testtex.setText(records);
            if(error != "")
                text.setText(error);
            super.onPostExecute(aVoid);
        }
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



//public class MainActivity extends AppCompatActivity {
//    private static final String URL = "jdbc:mysql://192.168.0.107:3306/kodejava";
//    private static final String USER = "kodejava";
//    private static final String PASSWORD = "kodejava";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        new InfoAsyncTask().execute();
//    }
//
//    @SuppressLint("StaticFieldLeak")
//    public class InfoAsyncTask extends AsyncTask<Void, Void, Map<String, String>> {
//        @Override
//        protected Map<String, String> doInBackground(Void... voids) {
//            Map<String, String> info = new HashMap<>();
//
//            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
//                String sql = "SELECT name, address, phone_number FROM school_info LIMIT 1";
//                PreparedStatement statement = connection.prepareStatement(sql);
//                ResultSet resultSet = statement.executeQuery();
//                if (resultSet.next()) {
//                    info.put("name", resultSet.getString("name"));
//                    info.put("address", resultSet.getString("address"));
//                    info.put("phone_number", resultSet.getString("phone_number"));
//                }
//            } catch (Exception e) {
//                Log.e("InfoAsyncTask", "Error reading school information", e);
//            }
//
//            return info;
//        }
//
//        @Override
//        protected void onPostExecute(Map<String, String> result) {
//            if (!result.isEmpty()) {
//                TextView textViewName = findViewById(R.id.textViewName);
//                TextView textViewAddress = findViewById(R.id.textViewAddress);
//                TextView textViewPhoneNumber = findViewById(R.id.textViewPhone);
//
//                textViewName.setText(result.get("name"));
//                textViewAddress.setText(result.get("address"));
//                textViewPhoneNumber.setText(result.get("phone_number"));
//            }
//        }
//    }
//}
