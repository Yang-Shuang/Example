package com.yang.example.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yang.example.bean.LogDataBean;

public class SQLiteTestHelper extends SQLiteOpenHelper {
    private SQLiteDatabase reader, writer;

    public SQLiteTestHelper(Context context) {
        super(context, "test_db", null, 1);
        reader = getReadableDatabase();
        writer = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table test_table (tid INTEGER PRIMARY KEY AUTOINCREMENT,ttype  TEXT, ttime TEXT,tcontent TEXT,ttag TEXT)";
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public synchronized void insertData(LogDataBean bean) {
        ContentValues values = new ContentValues();
        values.put("ltype", bean.getType());
        values.put("ltime", bean.getTime());
        values.put("lcontent", bean.getContent());
        values.put("ltag", bean.getTag());
        writer.insert("test_table", null, values);
    }

    public  void closeDB() {
        reader.close();
        writer.close();
    }
}
