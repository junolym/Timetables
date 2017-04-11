package com.example.liwensheng.timetables;

/**
 * Created by 陈鑫 on 2016/12/17.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by liWensheng on 2016/12/14.
 */

public class countdownDB extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "countdownTable";

    public countdownDB(Context context, String name, SQLiteDatabase.CursorFactory factory,
                int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "CREATE TABLE if not exists " + TABLE_NAME +
                " (_id INTEGER PRIMARY KEY,name TEXT,address TEXT,year INT,month INT,day INT,hour INT,minute INT,reminderday INT,countdownday TEXT)";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase,int i,int j){}
}
