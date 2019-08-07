package com.yang.example.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.yang.example.bean.LogDataBean;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String log_table_name = "tlog";
    private static DatabaseHelper helper;
    private static SQLiteDatabase reader, writer;

    public static void init(Context context) {
        if (helper == null || reader == null || writer == null) {
            helper = new DatabaseHelper(context);
            reader = helper.getReadableDatabase();
            writer = helper.getWritableDatabase();
        }
    }

    private DatabaseHelper(Context context) {
        super(context, "example_db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.w("DatabaseHelper", "------onCreate------");
        String s = createLogTabelSql();
        try {
            Log.w("DatabaseHelper", "执行SQL ： " + s);
            db.execSQL(s);
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private String createLogTabelSql() {
        return "create table " + log_table_name + "(lid INTEGER PRIMARY KEY AUTOINCREMENT,ltype  TEXT, ltime TEXT,lcontent TEXT,ltag TEXT)";
    }

    private void insert(LogDataBean bean) {

    }

    private void select() {

    }

    public static void closeDB() {

    }

    public synchronized static void insertData(LogDataBean bean) {
        ContentValues values = new ContentValues();
        values.put("ltype", bean.getType());
        values.put("ltime", bean.getTime());
        values.put("lcontent", bean.getContent());
        values.put("ltag", bean.getTag());
        writer.insert(log_table_name, null, values);
    }


}
