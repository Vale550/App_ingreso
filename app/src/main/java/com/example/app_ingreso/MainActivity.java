//Librería JDBC
package com.example.app_ingreso;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    ImageButton btnscan;
    String dni;
    ConstraintLayout navigationView;
    Button btn_acp;
    EditText text, nameEvento;
    TextView txtEntradasCompradas, txtEntradasUsadas;
    RequestQueue requestQueue;
    ImageView imgOk, imgError, imgHome, imgList, imgLogout, imgWifiNo, imgWifiSi;
    DbHelper dbHelper = new DbHelper(MainActivity.this);

    String [] estadoN, idticketN, estadoL, idticketL;

    //Crea el main
    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int nightModeFlags = getApplicationContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {/* si esta activo el modo oscuro lo desactiva */
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO);
        }
        btnscan = findViewById(R.id.btn_scan);
        btn_acp = findViewById(R.id.btn_aceptar);
        text = findViewById(R.id.Texto);
        imgOk = findViewById(R.id.imgOk);
        imgError = findViewById(R.id.imgError);
        imgHome = findViewById(R.id.imgHome);
        imgList = findViewById(R.id.imgList);
        imgLogout = findViewById(R.id.imgLogout);
        imgWifiNo = findViewById(R.id.imgWifiNo);
        imgWifiSi = findViewById(R.id.imgWifiSi);
        navigationView = findViewById(R.id.navigationView);
        nameEvento = findViewById(R.id.nameEvento);
        txtEntradasCompradas = findViewById(R.id.txtEntradasCompradas);
        txtEntradasUsadas = findViewById(R.id.txtEntradasUsadas);

        String eventoc = getIntent().getStringExtra("evento");
        String evento = "a" + eventoc;
        int TiempoTimer = 20;//Segundos
        Timer timerr = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                DbHelper bdobj = new DbHelper(MainActivity.this);
                SQLiteDatabase dbr = bdobj.getReadableDatabase();
                @SuppressLint("Recycle") Cursor filas = dbr.rawQuery("SELECT * FROM " + evento + "", null);
                idticketL = new String[filas.getCount()];
                estadoL = new String[filas.getCount()];
                int i = 0;
                if (filas.moveToNext()) {
                    do {
                        Log.d("Carga bd local", filas.getString(1));
                        Log.d("Carga bd local", filas.getString(3));

                        idticketL[i] = filas.getString(1);
                        estadoL[i] = filas.getString(3);
                        i++;
                    } while (filas.moveToNext());
                }
                Log.d("Update Nube", "Subir");
                sincronizacion1("https://appingresos.000webhostapp.com/Update.php", eventoc, idticketL, estadoL);
                Log.d("Update Nube", "Bajar");

            }
        };
        timerr.schedule(task, 10, TiempoTimer * 1000);

        nameEvento.setText(getIntent().getStringExtra("evento"));
        btn_acp.setOnClickListener(v -> recortar());
        imgHome.setOnClickListener(view -> {
            Intent act1 = new Intent(MainActivity.this, Selevento.class);
            startActivity(act1);
        });
        imgList.setOnClickListener(view -> {
            Intent act2 = new Intent(MainActivity.this, List1.class);
            startActivity(act2);
        });
        imgLogout.setOnClickListener(view -> {
            Intent act3 = new Intent(MainActivity.this, Menu.class);
            startActivity(act3);
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
        nroUsadasCompradas();


        try {
            if (conectadoAInternet()) {
                imgWifiSi.setVisibility(View.VISIBLE);
                imgWifiNo.setVisibility(View.INVISIBLE);
            } else {
                imgWifiNo.setVisibility(View.VISIBLE);
                imgWifiSi.setVisibility(View.INVISIBLE);
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        click();

    }
    public void click() {
        text.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN)
            {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    recortar();
                    return true;
                }
            }
            return false;
        });
    }


    ////tecla enter
    public void recortar(){
        try {
            dni = text.getText().toString();
            String[] parts = dni.split("&"); //divide
            int longitud = dni.length();
            if (longitud > 10){ //mas de 10
                String comprueba = parts[4]; //parte a comprobar
                if (Character.isDigit(Integer.parseInt(comprueba))) { //si es numero
                    dni = parts[1]; //dni viejo
                } else { //si no es numero
                    dni = parts[4]; //dni nuevo
                }
                text.setText(dni); //lo coloca en el editext
            }

            if (modifica(dni)) { //si modifica
//
                imgOk.setVisibility(View.VISIBLE);
                imgError.setVisibility(View.INVISIBLE);
            } else { //si no modifica
//
                imgError.setVisibility(View.VISIBLE);
                imgOk.setVisibility(View.INVISIBLE);
            }
            text.setText("");
        } catch (Exception ignored) {
//
        }
    }

    ///////////////////COMPRUEBA CONEXION
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

    public boolean modifica(String dni) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String eventoc = getIntent().getStringExtra("evento");
        String evento = "a"+eventoc;

        //--------------------------------------------------------------------------------------
        DbHelper bdobj = new DbHelper(this);
        SQLiteDatabase dbr = bdobj.getReadableDatabase();
        Cursor filas = dbr.rawQuery("SELECT * FROM "+evento+" WHERE estado='valida' AND (DNI= "+dni+"" +
                " OR idticket= "+dni+")" ,null);
        if (filas.moveToNext()){
            db.execSQL("UPDATE "+evento+" SET estado='invalida' WHERE DNI= "+dni+" OR idticket= "+dni+"");
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

                    } else { //si no es numero
                        dni = parts[4]; //dni nuevo

                    }
                }

                text.setText(dni); //lo coloca en el editext
                bdnpost("https://appingresos.000webhostapp.com/modificar.php?codigo=" + dni);
                if (modifica(dni)) { //si modifica
                        imgOk.setVisibility(View.VISIBLE);
                        imgError.setVisibility(View.INVISIBLE);
                } else { //si no modifica
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


        //Insertar/actualizar BDN
        private void bdnpost (String URL){
            try {

                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> Toast.makeText(getApplicationContext(), "Operacion exitosa", Toast.LENGTH_SHORT), error -> {
//
//                    imgError.setVisibility(View.VISIBLE);
//                    imgOk.setVisibility(View.INVISIBLE);

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

        public void sincronizacion1(String URL, String event, String[] idticketL,String[] estadoL){
        try {
            if (conectadoAInternet()) {
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, response -> {
                    estadoN = new String[response.length()];
                    idticketN = new String[response.length()];
                    for (int i = 0; i < response.length(); i++) {

                        Map<String, String> parametros = new HashMap<>();
                        parametros.put("tabla", event);
                        parametros.put("idticket", idticketL[i]);
                        parametros.put("estado", estadoL[i]);

                    }

                }, error -> Toast.makeText(getApplicationContext(), "Error de sincronizacion", Toast.LENGTH_SHORT).show()
                );
                requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(jsonArrayRequest);
            }
            else {
                //Mensaje "Se requiere internet"
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
