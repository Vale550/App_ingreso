package com.example.app_ingreso;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.app_ingreso.bd.DbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;


public class Menu extends AppCompatActivity {
    Button btning;
    EditText usuario,contrasena;
    RequestQueue requestQueue;
    DbHelper dbHelper = new DbHelper(Menu.this);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        btning = findViewById(R.id.btnIngreso);
        usuario = findViewById(R.id.Usuario);
        contrasena = findViewById(R.id.Contrasena);

        btning.setOnClickListener(v -> {
            String user = usuario.getText().toString();
            String pass = contrasena.getText().toString();
            if (user != "" && pass != "") {
                Loaduser("https://appingresos.000webhostapp.com/busquedawhile.php", user, pass);
            }
        });

    }
    public boolean carga (String nom){
        //NOTA:no se puede cargar tablas en la local que vienen directamente
        //de la base de datos en la nube porque no reconoce las variables
        //porlotanto se deben cargar con "?"+variable
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String a = "a"+nom;
        try {
            db.execSQL("CREATE TABLE " + a + "(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "idticket INTEGER NOT NULL," +
                    "DNI INTEGER NOT NULL," +
                    "estado TEXT NOT NULL)");
            return true;
        }catch (Exception e){return false;}
    }

    public void Loaduser(String URL,String nombre,String contra){
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, response -> {
            JSONObject jsonObject = null;
            for (int i = 0; i < response.length(); i++) {
                try {
                    jsonObject = response.getJSONObject(i);
                    jsonObject.getString("username");

                    String nombreb=jsonObject.getString("username");
                    String contrab=jsonObject.getString("password");
                    String localb=jsonObject.getString("nroLocal");

                    if (Objects.equals(nombre, nombreb) && Objects.equals(contra,contrab)){
                        Log.d("D3","Paso");
                        Log.d("Local",localb);

                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        LoadLocales("https://appingresos.000webhostapp.com/loadeventos.php?cod="+localb);
                        LoadEventos("https://appingresos.000webhostapp.com/Busquedatablas.php",localb);

                    }else {
                        Log.d("D3","error");
                    }

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            Intent act = new Intent(this, Selevento.class);
            startActivity(act);

        }, error -> Toast.makeText(getApplicationContext(), "Error de Conexión", Toast.LENGTH_SHORT).show()
        );
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);

    }

    public void LoadEventos(String URL,String nrolocal){
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        String nrolocalb = jsonObject.getString("nroLocal");
                        String eventob = jsonObject.getString("nombreEvento");
                        if (Objects.equals(nrolocalb, nrolocal)) {
                            if (carga(eventob)) {
                                LoadTablas("https://appingresos.000webhostapp.com/Cargartabla.php?tabla=" + eventob, eventob);
                            }
                        }

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, error -> Toast.makeText(getApplicationContext(), "Error de 2", Toast.LENGTH_SHORT).show()
        );
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }


    public void LoadTablas(String URL,String event){
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        String idticket=jsonObject.getString("idticket");
                        String dni=jsonObject.getString("DNI");

                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        db.execSQL("INSERT INTO a"+event+" (idticket,DNI,estado) VALUES ('"+idticket+"','"+dni+"','valido') ");
                        Log.d("DFINAL",idticket);

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        }, error -> {

        }
        );
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);

    }

    public void LoadLocales(String URL){
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        String nrolocalb = jsonObject.getString("nroLocal");
                        String eventob = jsonObject.getString("nombreEvento");
                        String nroeventob = jsonObject.getString("nroEvento");

                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        SQLiteDatabase dbr = dbHelper.getReadableDatabase();
                        Cursor filas = dbr.rawQuery("SELECT * FROM eventos WHERE nroEvento='"+nroeventob+"'",null);
                        if (filas.moveToNext()) {
                        }else {
                            Log.d("D4",eventob);
                            db.execSQL("INSERT INTO eventos (nroLocal, nombreEvento, nroEvento) " +
                                    "VALUES ('" + nrolocalb + "','" + eventob + "','" + nroeventob + "') ");
                        }

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, error -> Toast.makeText(getApplicationContext(), "Error de 2", Toast.LENGTH_SHORT).show()
        );
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

}