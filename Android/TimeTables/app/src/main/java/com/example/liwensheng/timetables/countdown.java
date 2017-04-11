package com.example.liwensheng.timetables;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.DateTimePatternGenerator;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by liWensheng on 2016/12/14.
 */

public class countdown extends Fragment {

    private static final String TABLE_NAME = "countdownTable";
    private countdownDB cDB = new countdownDB(this.getContext(), "countdown.db", null,1);
    private SQLiteDatabase sqLiteDatabase;
    private View layout;
    private int tempyear, tempmonth, tempday, temphour, tempminute, temprday;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.countdown_layout, null);
        cDB = new countdownDB(this.getContext(), "countdown.db", null,1);
        sqLiteDatabase = cDB.getWritableDatabase();
        UPDATE();

        updatelistview();

        final ListView listview = (ListView)layout.findViewById(R.id.countdown_listview);
        //长按删除
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, final long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(countdown.this.getContext());
                builder.setTitle("是否删除？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //数据库中删除对应名字相关信息
                        LinearLayout layout = (LinearLayout)listview.getChildAt(i);
                        TextView itemtextview = (TextView)layout.findViewById(R.id.item_name);
                        int res = sqLiteDatabase.delete(TABLE_NAME, "name='" + itemtextview.getText().toString() + "'", null);
                        updatelistview();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.create().show();
                return true;
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final View diaview = LayoutInflater.from(countdown.this.getContext()).inflate(R.layout.countdown_listview_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(countdown.this.getContext());
                builder.setView(diaview);

                final LinearLayout linearLayout = (LinearLayout)listview.getChildAt(i);
                final TextView itemname = (TextView)linearLayout.findViewById(R.id.item_name);
                final TextView itemaddress = (TextView)linearLayout.findViewById(R.id.item_address);
                final TextView itemyear = (TextView)linearLayout.findViewById(R.id.item_year);
                final TextView itemmonth = (TextView)linearLayout.findViewById(R.id.item_month);
                final TextView itemday = (TextView)linearLayout.findViewById(R.id.item_day);
                final TextView itemhour = (TextView)linearLayout.findViewById(R.id.item_hour);
                final TextView itemminute = (TextView)linearLayout.findViewById(R.id.item_minute);

                final TextView dianame = (TextView)diaview.findViewById(R.id.edit_countdown_dialog_event);
                final EditText diaaddress = (EditText) diaview.findViewById(R.id.edit_countdown_dialog_address);
                final TextView diatime = (TextView)diaview.findViewById(R.id.edit_countdown_dialog_time);
                final Spinner diaremindertime = (Spinner) diaview.findViewById(R.id.edit_countdown_dialog_remindertime);
                dianame.setText(itemname.getText().toString());
                diaaddress.setText(itemaddress.getText().toString());
                final int year = Integer.parseInt(itemyear.getText().toString());
                int month = Integer.parseInt(itemmonth.getText().toString());
                int day = Integer.parseInt(itemday.getText().toString());
                int hour = Integer.parseInt(itemhour.getText().toString());
                int minute = Integer.parseInt(itemminute.getText().toString());
                diatime.setText(year+"年"+month+"月"+day+"日 "+hour+":"+minute);

                tempyear = year;
                tempmonth = month;
                tempday = day;
                temphour = hour;
                tempminute = minute;

                //获取提醒天数
                int reminderdays = 0;
                sqLiteDatabase = cDB.getWritableDatabase();
                Cursor cursor = sqLiteDatabase.rawQuery("select * from " + TABLE_NAME, null);
                while (cursor.moveToNext()) {
                    if(cursor.getString(cursor.getColumnIndex("name")).equals(itemname.getText().toString())) {
                        reminderdays = cursor.getInt(cursor.getColumnIndex("reminderday"));
                        break;
                    }
                }
                temprday = reminderdays;

                diatime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LayoutInflater factory = LayoutInflater.from(diaview.getContext());
                        final View datepicker_view = factory.inflate(R.layout.datepicker_dialog, null);
                        final AlertDialog.Builder builder = new AlertDialog.Builder(diaview.getContext());
                        final DatePicker datePicker = (DatePicker)datepicker_view.findViewById(R.id.datepicker);
                        datePicker.updateDate(tempyear, tempmonth-1, tempday);
                        final TimePicker timePicker = (TimePicker) datepicker_view.findViewById(R.id.timepicker);
                        timePicker.setCurrentHour(temphour);
                        timePicker.setCurrentMinute(tempminute);

                        builder.setView(datepicker_view).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.M)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                countdown.this.tempyear = datePicker.getYear();
                                countdown.this.tempmonth = datePicker.getMonth()+1;
                                countdown.this.tempday = datePicker.getDayOfMonth();
                                countdown.this.temphour = timePicker.getCurrentHour();
                                countdown.this.tempminute = timePicker.getCurrentMinute();
                                diatime.setText(countdown.this.tempyear+"年"+countdown.this.tempmonth+"月"+countdown.this.tempday+"日"+" "+countdown.this.temphour+":"+countdown.this.tempminute);
                            }
                        }).create().show();
                    }
                });

                /*year = tempyear;
                month = tempmonth;
                day = tempday;
                hour = temphour;
                minute = tempminute;*/

                final List<String> list = new ArrayList<String>();
                list.add("提前一天提醒");
                list.add("提前两天提醒");
                list.add("提前三天提醒");
                diaremindertime.setSelection(reminderdays-1);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), R.layout.support_simple_spinner_dropdown_item, list);
                diaremindertime.setAdapter(adapter);
                diaremindertime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        //Toast.makeText(view.getContext(), list.get(i), Toast.LENGTH_SHORT).show();
                        countdown.this.temprday = i+1;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
               // reminderdays = temprday;

                //update
                builder.setPositiveButton("保存修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //数据库更新
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("address", diaaddress.getText().toString());
                        contentValues.put("year", tempyear);
                        contentValues.put("month", tempmonth);
                        contentValues.put("day", tempday);
                        contentValues.put("reminderday", temprday);
                        contentValues.put("hour", temphour);
                        contentValues.put("minute", tempminute);
                        int res = sqLiteDatabase.update(TABLE_NAME, contentValues, "name='" + dianame.getText().toString() + "'", null);
                        updatelistview();
                    }
                });

                builder.setNegativeButton("放弃修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.create().show();

            }
        });

        final Button add_countdown = (Button)layout.findViewById(R.id.add_countdown_button);
        add_countdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(countdown.this.getContext(), addCountdown.class);
                startActivity(intent);
                countdown.this.getActivity().finish();
            }
        });

        return layout;
    }


    //更新数据库中倒计时剩余天数
    @TargetApi(Build.VERSION_CODES.N)
    public void UPDATE() {
        int year, month, day, countdownday;
        sqLiteDatabase = cDB.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + TABLE_NAME, null);
        while (cursor.moveToNext()) {
            year = cursor.getInt(cursor.getColumnIndex("year"));
            month = cursor.getInt(cursor.getColumnIndex("month"));
            day = cursor.getInt(cursor.getColumnIndex("day"));
            int hour = cursor.getInt(cursor.getColumnIndex("hour"));
            int minute = cursor.getInt(cursor.getColumnIndex("minute"));
            //Toast.makeText(this.getContext(), year + "year" + month+"month" + day+ "day", Toast.LENGTH_SHORT).show();

            /*Calendar now = Calendar.getInstance();
            Calendar calendar = Calendar.getInstance();
            calendar.clear();
            calendar.set(year, month-1, day);*/

            android.text.format.Time now = new android.text.format.Time();
            now.setToNow();
            android.text.format.Time calendar = new android.text.format.Time();
            //calendar.set(day, month-1, year, hour, minute, 0);
            calendar.set(0, minute, hour, day, month-1, year);

            long time1 = now.toMillis(false);
            long time2 = calendar.toMillis(true);
            //long diff = (time2-time1)/(24*60*60*1000)+1;
            //Toast.makeText(this.getContext(), "time1:" + time1 + "time2:" + time2 , Toast.LENGTH_SHORT).show();
            long diff = time2/(24*60*60*1000)-time1/(24*60*60*1000);
            //Toast.makeText(this.getContext(), diff + "day", Toast.LENGTH_SHORT).show();

            ContentValues contentValues = new ContentValues();

            if (diff >= 0 && time2 > time1)
                contentValues.put("countdownday", "还剩" + diff + "天");
            else
                contentValues.put("countdownday", "已结束");

            sqLiteDatabase.update(TABLE_NAME, contentValues, "name=?", new String[]{cursor.getString(cursor.getColumnIndex("name"))});

            //Toast.makeText(this.getContext(), cursor.getString(cursor.getColumnIndex("countdownday")), Toast.LENGTH_SHORT).show();
        }
    }

    //更新listview
    public void updatelistview() {
        UPDATE();
        final ListView listview = (ListView)layout.findViewById(R.id.countdown_listview);
        final Cursor cr = sqLiteDatabase.query("countdownTable", null, null, null, null, null, null);
        String[] ColumnNames = cr.getColumnNames();
        ListAdapter adapter = new SimpleCursorAdapter(getContext(), R.layout.countdown_listview_item, cr, ColumnNames, new int[] {R.id.item_name, R.id.item_name, R.id.item_address, R.id.item_year, R.id.item_month, R.id.item_day, R.id.item_hour, R.id.item_minute,  R.id.item_reminderday, R.id.item_reminderday});
        listview.setAdapter(adapter);
    }

}
