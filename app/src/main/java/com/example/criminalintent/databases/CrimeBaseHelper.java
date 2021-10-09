package com.example.criminalintent.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.criminalintent.databases.CrimeDBSchema.CrimeTable;

public class CrimeBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION= 1;
    private static final String DATABASE_NAME="crimeBase.db";
    public CrimeBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //建表语句后面记得加上空格
        db.execSQL("create table "+ CrimeTable.NAME+"("+
                "_id integer primary key autoincrement,"+
                CrimeTable.Cols.UUID+","+CrimeTable.Cols.TITLE+","+
                CrimeTable.Cols.DATE+","+CrimeTable.Cols.SOLVED+","+
                CrimeTable.Cols.SUSPECT+ ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
