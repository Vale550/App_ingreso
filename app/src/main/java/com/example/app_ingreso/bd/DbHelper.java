package com.example.app_ingreso.bd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION=1;
    private static final String DATABASE_NAME="app.db";
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Crea BDlocal
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE `locales` (\n" +
                "  `nroLocal` int(11) NOT NULL,\n" +
                "  `nombreLocal` varchar(50) NOT NULL\n" +
                ")");
        sqLiteDatabase.execSQL("CREATE TABLE `eventos` (" +
                "  `nroEvento` int(11) NOT NULL," +
                "  `nroLocal` int(11) NOT NULL," +
                "  `nombreEvento` varchar(80) NOT NULL" +
                ")");
        sqLiteDatabase.execSQL("CREATE TABLE `admin` (" +
                "  `nroAdmin` int(11) NOT NULL," +
                "  `username` varchar(50) NOT NULL," +
                "  `password` varchar(30) NOT NULL," +
                "  `nroLocal` int(11) NOT NULL" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        onCreate(sqLiteDatabase);

    }
}
