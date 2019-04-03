package com.yang.example.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteTestHelper extends SQLiteOpenHelper {
    public SQLiteTestHelper(Context context) {
        super(context, "test_db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table test (tid INTEGER PRIMARY KEY AUTOINCREMENT,ttype  TEXT, ttime TEXT,tcontent TEXT,ttag TEXT)";
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
