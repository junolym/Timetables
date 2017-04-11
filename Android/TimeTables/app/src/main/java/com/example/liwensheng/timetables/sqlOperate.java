package com.example.liwensheng.timetables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by liWensheng on 2016/12/14.
 */

public class sqlOperate {
    private static final String TABLE_NAME = "courseTable";
    private SQLiteDatabase sql;

    public void INSERT(SQLiteDatabase sqLiteDatabase, String name, String room, String teacher, String jie, String week, String time) {
        ContentValues contentValues = new ContentValues();

        contentValues.put("name", name);
        contentValues.put("room", room);
        contentValues.put("teacher", teacher);
        contentValues.put("jie", jie);
        contentValues.put("week", week);
        contentValues.put("time", time);

        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }

    public void UPDATE(SQLiteDatabase sqLiteDatabase,int id, String name, String room, String teacher, String jie, String week, String time) {
        ContentValues contentValues = new ContentValues();

        contentValues.put("name", name);
        contentValues.put("room", room);
        contentValues.put("teacher", teacher);
        contentValues.put("jie", jie);
        contentValues.put("week", week);
        contentValues.put("time", time);

        String[] args = {String.valueOf(id)};
        sqLiteDatabase.update(TABLE_NAME, contentValues, "_id=?", args);
    }

    public void DELETE(SQLiteDatabase sqLiteDatabase ,int id) {
        String[] args = {String.valueOf(id)};
        sqLiteDatabase.delete(TABLE_NAME, "_id=?", args);
    }

    public String QUERY(SQLiteDatabase sqLiteDatabase, String name, String find) {
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME,
                new String[]{"_id", "name", "room", "teacher", "jie", "week", "time"},
                null, null, null, null, null);
        if (cursor.moveToFirst())
            while (cursor!=null) {
                if (cursor.getString(1).equals(name)) {
                    switch (find) {
                        case "room":
                            return cursor.getString(2);
                        case "teacher":
                            return cursor.getString(3);
                        case "jie":
                            return cursor.getString(4);
                        case "week":
                            return cursor.getString(5);
                        case "time":
                            return cursor.getString(6);
                        default:
                            return "";
                    }
                }
                if (cursor.isLast())
                    break;
                cursor.moveToNext();
            }
        return "";
    }

    public String QUERY(SQLiteDatabase sqLiteDatabase, int id, String find) {
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME,
                new String[]{"_id", "name", "room", "teacher", "jie", "week", "time"},
                null, null, null, null, null);
        if (cursor.moveToFirst())
            while (cursor!=null) {
                if (cursor.getInt(0) == id) {
                    switch (find) {
                        case "name":
                            return cursor.getString(1);
                        case "room":
                            return cursor.getString(2);
                        case "teacher":
                            return cursor.getString(3);
                        case "jie":
                            return cursor.getString(4);
                        case "week":
                            return cursor.getString(5);
                        case "time":
                            return cursor.getString(6);
                        default:
                            return "";
                    }
                }
                if (cursor.isLast())
                    break;
                cursor.moveToNext();
            }
        return "";
    }

    public void clear(SQLiteDatabase sqLiteDatabase) {
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME,
                new String[]{"_id", "name", "room", "teacher", "jie", "week", "time"},
                null, null, null, null, null);
        if (cursor.moveToFirst()) {
            while (cursor != null) {
                DELETE(sqLiteDatabase, cursor.getInt(0));
                if (cursor.isLast())
                    break;
                cursor.moveToNext();
            }
        }
    }

//    public boolean judge(SQLiteDatabase sqLiteDatabase, int flag1, int flag2) {
//
//    }
}
