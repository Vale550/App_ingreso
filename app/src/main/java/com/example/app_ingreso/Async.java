package com.example.app_ingreso;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Async extends AsyncTask<Void, Void, Void> {
    String records = "", error = "";

    int[] resultado;
    private String filas;

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            String[] datosbn;
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://sql10.freesqldatabase.com:3306/sql10600738", "sql10600738", "seqEnpF4C3");
            Statement statement = connection.createStatement();
            Statement statement2 = connection.createStatement();
            ResultSet array = statement2.executeQuery("SELECT MAX(id) FROM 28enero");
            ResultSet resultSet = statement.executeQuery("SELECT DNI FROM 28enero");

            if (array.next()) {
                filas = array.getString(1);
                int numero = Integer.parseInt(filas);
                Log.d("Cantidad de filas", String.valueOf(numero));
            }

            while (resultSet.next()) {
                records += resultSet.getString(1) + "\n";
            }
        } catch (Exception e) {
            error = e.toString();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        //testtex.setText(records);
        if (error != "")
            //text.setText(error);
            super.onPostExecute(aVoid);
    }
}
