package com.example.app_ingreso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


public class Menu extends AppCompatActivity {
    Button btnIngreso;
    EditText textUser, textPass;
    TextView txtUsuario, txtPass;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnIngreso = findViewById(R.id.btnIngreso);
        textUser= findViewById(R.id.textUser);
        textPass=findViewById(R.id.textPass);
        txtUsuario=findViewById(R.id.txtUsuario);
        txtPass=findViewById(R.id.txtPass);

        btnIngreso.setOnClickListener(view -> validarUsuario());
    }
    private  void validarUsuario (){

        StringRequest stringRequest=new StringRequest(Request.Method.POST, "http://192.168.0.84:8080/PruebaAndroid/validar_usuario.php", response -> {

            if (!response.isEmpty()) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                Toast.makeText(Menu.this, "Bien", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Menu.this, "Usuario o contraseña incorrecta", Toast.LENGTH_SHORT).show();
            }
        }, error -> Toast.makeText(Menu.this, error.toString(), Toast.LENGTH_SHORT).show()){
            @NonNull
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parametros= new HashMap<>();
                parametros.put("usuario",txtUsuario.getText().toString());
                parametros.put("password",txtPass.getText().toString());
                return parametros;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new RetryPolicy() {

            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) {

            }
        });
    }
}