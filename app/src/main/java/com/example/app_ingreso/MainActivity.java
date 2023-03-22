//Librería JDBC
package com.example.app_ingreso;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import androidx.appcompat.app.AlertDialog;
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

    String [] idticketN;
    String [] estadoL;
    String [] idticketL;

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
        int TiempoTimer = 120;//Segundos
        Timer timerr = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                DbHelper bdobj = new DbHelper(MainActivity.this);
                SQLiteDatabase dbr = bdobj.getReadableDatabase();
                Cursor filas = dbr.rawQuery("SELECT * FROM "+evento+"",null);
                idticketL = new String[filas.getCount()];
                estadoL = new String[filas.getCount()];
                int i = 0;
                if (filas.moveToNext()){
                    do {
                        Log.d("Carga bd DBN",filas.getString(1));
                        Log.d("Carga bd DBN",filas.getString(3));
                        Log.d("Carga bd DBN",eventoc);

                        idticketL[i]= filas.getString(1);
                        estadoL[i]= filas.getString(3);
                        sincronizacion1("https://appingresos.000webhostapp.com/uppdate.php?tabla=" + eventoc + "&idticket="+idticketL[i]+ "&estado="+estadoL[i]);
                        i++;
                    }while (filas.moveToNext());
                }
                sincronizacion2("https://appingresos.000webhostapp.com/Cargartabla.php?tabla="+eventoc,evento);


            }
        };
        timerr.schedule(task, 10, TiempoTimer * 1000);

        nameEvento.setText(getIntent().getStringExtra("evento"));
        btn_acp.setOnClickListener(v -> recortar());
        imgHome.setOnClickListener(view -> {
            Intent act1 = new Intent(MainActivity.this, Selevento.class);
            startActivity(act1);
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
        new CountDownTimer(1000000000, 5000) {

            @Override
            public void onTick(long l) {
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
            }

            @Override
            public void onFinish() {

            }

        }.start();

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
            String eventoc = getIntent().getStringExtra("evento");
            String evento = "a"+eventoc;
            DbHelper bdobj = new DbHelper(this);
            SQLiteDatabase dbr = bdobj.getReadableDatabase();
            Cursor filas = dbr.rawQuery("SELECT DNI FROM "+evento+" WHERE DNI= '"+dni+"'" ,null);
            if (filas.getCount()>1){
                alerta().show();
            } else {
                bdnpost("https://appingresos.000webhostapp.com/modificar.php?codigo=" + dni);
                if (modifica(dni)) { //si modifica
                    imgOk.setVisibility(View.VISIBLE);
                    imgError.setVisibility(View.INVISIBLE);
                } else { //si no modifica
                    imgError.setVisibility(View.VISIBLE);
                    imgOk.setVisibility(View.INVISIBLE);
                }
                text.setText("");
            }
        } catch (Exception ignored) {
//
        }
        text.findFocus();
    }

    public AlertDialog alerta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Atención")
                .setMessage("Más de una entrada asociada a este DNI. Ingrese idTicket")
                .setPositiveButton("OK",
                        (dialog, which) -> {

                        });
        text.setText("");
        return builder.create();
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
            nroUsadasCompradas();
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
    //No tocar
    public void sincronizacion1 (String URL){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, response -> {
            JSONObject jsonObject;
            for (int i = 0; i < response.length(); i++) {
                try {
                    jsonObject = response.getJSONObject(i);

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        }, error -> Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT)
        );
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);

    }
    public void sincronizacion2 (String URL, String eve){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, response -> {
            JSONObject jsonObject;
            for (int i = 0; i    < response.length(); i++) {
                try {
                    jsonObject = response.getJSONObject(i);
                    String estado = jsonObject.getString("estado");
                    String idticket = jsonObject.getString("idticket");
                    DbHelper bdobj = new DbHelper(this);
                    SQLiteDatabase dbr = bdobj.getReadableDatabase();
                    Cursor filas = dbr.rawQuery("SELECT * FROM "+eve+" WHERE idticket='"+idticket+"'", null);
                    Log.d("Test2",estado);
                    if (filas.moveToFirst()) {
                        String estad = filas.getString(3);
                        Log.d("Test3",estad);
                        if (estado.equals("invalida")){
                            dbr.execSQL("UPDATE "+eve+" SET estado='invalida' WHERE idticket='"+idticket+"'");
                        }

                    }

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        }, error -> Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT)
        );
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);

    }
}
