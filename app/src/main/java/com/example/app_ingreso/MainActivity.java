//Librería JDBC
package com.example.app_ingreso;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.app_ingreso.bd.DbHelper;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private Window window;
    String primary = "#0E7827";
    String primary2 = "#DC3F11";
    String primary3 = "#FF8000";
    ImageButton btnscan;
    String dni;
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
        btnscan = findViewById(R.id.btn_scan);
        btn_acp = findViewById(R.id.btn_aceptar);
        text = findViewById(R.id.Texto);
        imgOk = findViewById(R.id.imgOk);
        imgError = findViewById(R.id.imgError);
        imgHome = findViewById(R.id.imgHome);
        imgList = findViewById(R.id.imgList);
        navigationView = findViewById(R.id.navigationView);


        btn_acp.setOnClickListener(v -> {
            try {
                dni = text.getText().toString();
                String res = text.getText().toString();
                String[] parts = res.split("@"); //divide
                int longitud = res.length();
                if (longitud <= 10) { //ticket
                    dni = res;
                    text.setText(dni); //lo coloca en el editext
                } else { //mas de 10
                    String comprueba = parts[4]; //parte a comprobar
                    if (Character.isDigit(Integer.parseInt(comprueba))) { //si es numero
                        dni = parts[1]; //dni viejo
                    } else { //si no es numero
                        dni = parts[4]; //dni nuevo
                    }
                    text.setText(dni); //lo coloca en el editext
                }
                bdnpost("https://appingresos.000webhostapp.com/modificar.php?codigo=" + dni);
                if (modifica(dni)) { //si modifica
                    cambiaColorOK(primary); //ok
                    imgOk.setVisibility(View.VISIBLE);
                    imgError.setVisibility(View.INVISIBLE);
                } else { //si no modifica
                    cambiaColorOK(primary2); //error
                    imgError.setVisibility(View.VISIBLE);
                    imgOk.setVisibility(View.INVISIBLE);
                }
                text.setText("");
            } catch (Exception ignored) {
                cambiaColorOK(primary2);
            }
        });


        //Scaner
        btnscan.setOnClickListener(view -> {
            IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
            integrator.setOrientationLocked(false);
            integrator.setPrompt("Lector QR");
            integrator.setBeepEnabled(true);
            integrator.setBarcodeImageEnabled(true);
            integrator.initiateScan();

        });
    }

    public boolean modifica(String dni) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String eventoc = getIntent().getStringExtra("evento");
        Log.d("EventoMain",eventoc);
        String evento = eventoc;
        //--------------------------------------------------------------------------------------
        DbHelper bdobj = new DbHelper(this);
        SQLiteDatabase dbr = bdobj.getReadableDatabase();

        Cursor filas = dbr.rawQuery("SELECT * FROM " + evento + " WHERE estado='valido' AND (DNI= " + dni + "" +
                " OR idticket= " + dni + ")", null);
        if (filas.moveToNext()) {
            db.execSQL("UPDATE " + evento + " SET estado='invalida' WHERE DNI= " + dni + " OR idticket= " + dni + "");
            return true;
        }
        return false;
    }

    //Scaner
    protected void onActivityResult(int requestCode, int resultcode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultcode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scaner cancelado", Toast.LENGTH_LONG).show();
            } else {
                String res = String.valueOf(result); //recibe
                Toast.makeText(this, "res:" + res, Toast.LENGTH_SHORT).show();
                String[] parts = res.split("@"); //divide
                int longitud = res.length();
                if (longitud <= 10) { //ticket
                    dni = res;
                } else { //mas de 10
                    String comprueba = parts[3]; //parte a comprobar

                    if (isNumeric(comprueba)) { //si es numero
                        dni = parts[1]; //dni viejo
                        Toast.makeText(this, "dni:" + dni, Toast.LENGTH_SHORT).show();
                    } else { //si no es numero
                        dni = parts[4]; //dni nuevo
                        Toast.makeText(this, "dni:" + dni, Toast.LENGTH_SHORT).show();
                    }
                }

                text.setText(dni); //lo coloca en el editext
                bdnpost("https://appingresos.000webhostapp.com/modificar.php?codigo=" + dni);
                if (modifica(dni)) { //si modifica
                        cambiaColorOK(primary); //ok
                        imgOk.setVisibility(View.VISIBLE);
                        imgError.setVisibility(View.INVISIBLE);
                } else { //si no modifica
                        cambiaColorOK(primary2); //error
                        imgError.setVisibility(View.VISIBLE);
                        imgOk.setVisibility(View.INVISIBLE);
                    }
                }
        }else{
            super.onActivityResult(requestCode, resultcode, data);
        }

    }
        public static boolean isNumeric (String comprueba){

            boolean resultado;

            try {
                Integer.parseInt(comprueba);
                resultado = true;
            } catch (NumberFormatException excepcion) {
                resultado = false;
            }

            return resultado;
        }

        private void cambiaColorOK (String primary){
            window.setStatusBarColor(Color.parseColor(primary));
            window.setNavigationBarColor(Color.parseColor(primary));
            Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.parseColor(primary)));
            btnscan.setBackgroundColor(Color.parseColor(primary));
            btn_acp.setBackgroundColor(Color.parseColor(primary));
        }


        //Insertar/actualizar BDN
        private void bdnpost (String URL){
            try {

                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> Toast.makeText(getApplicationContext(), "Operacion exitosa", Toast.LENGTH_SHORT).show(), error -> {
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    cambiaColorOK(primary2);
                    imgError.setVisibility(View.VISIBLE);
                    imgOk.setVisibility(View.INVISIBLE);

                }) {
                    @NonNull
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> parametros = new HashMap<>();
                        Log.d("codigo", text.getText().toString());
                        parametros.put("codigo", text.getText().toString());

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
                Log.d("D1", "Error");
            }

        }

        //Consultas BDN
        public void buscarUsuarios (String URL){
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, response -> {
                JSONObject jsonObject;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        String[] resl = new String[response.length()];
                        jsonObject = response.getJSONObject(i);
                        Log.d("Debug while", jsonObject.getString("id"));
                        text.setText(jsonObject.getString("id"));

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            }, error -> Toast.makeText(getApplicationContext(), "Error de Conexión", Toast.LENGTH_SHORT).show()
            );
            requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(jsonArrayRequest);

        }

    }
