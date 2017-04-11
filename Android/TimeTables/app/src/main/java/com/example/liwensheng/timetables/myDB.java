package com.example.liwensheng.timetables;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by liWensheng on 2016/12/14.
 */

public class myDB extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "courseTable";

    public myDB(Context context, String name, SQLiteDatabase.CursorFactory factory,
                int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "CREATE TABLE if not exists " + TABLE_NAME +
                " (_id INTEGER PRIMARY KEY,name TEXT,room TEXT,teacher TEXT, jie TEXT, week TEXT, time TEXT)";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase,int i,int j){}
}
