package com.example.liwensheng.timetables;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 陈鑫 on 2016/12/17.
 */

public class addCountdown extends AppCompatActivity {
    private static final String TABLE_NAME = "countdownTable";
    private countdownDB cDB = new countdownDB(this, "countdown.db", null,1);
    private SQLiteDatabase sqLiteDatabase;
    private Button yes, no;
    private TextView time;
    private int year, month, day, rday, hour, minute;
    private Spinner spinner;
    private EditText name, address;

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_countdown);
        cDB = new countdownDB(this, "countdown.db", null,1);
        sqLiteDatabase = cDB.getWritableDatabase();
        //初始值为今日
        /*Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH)+1;
        day = calendar.get(Calendar.DATE);
        rday = 1;
        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);*/

        android.text.format.Time calendar = new android.text.format.Time();
        calendar.setToNow();
        year = calendar.year;
        month = calendar.month+1;
        day = calendar.monthDay;
        hour = calendar.hour;
        minute = calendar.minute;
        rday = 1;

        name = (EditText)findViewById(R.id.add_countdown_event);
        time = (TextView)findViewById(R.id.add_countdown_time);
        address = (EditText) findViewById(R.id.add_countdown_address);
        yes = (Button)findViewById(R.id.yes);
        no = (Button)findViewById(R.id.no);

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater factory = LayoutInflater.from(addCountdown.this);
                final View datepicker_view = factory.inflate(R.layout.datepicker_dialog, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(addCountdown.this);

                builder.setView(datepicker_view).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatePicker datePicker = (DatePicker)datepicker_view.findViewById(R.id.datepicker);
                        addCountdown.this.year = datePicker.getYear();
                        addCountdown.this.month = datePicker.getMonth()+1;
                        addCountdown.this.day = datePicker.getDayOfMonth();

                        TimePicker timePicker = (TimePicker) datepicker_view.findViewById(R.id.timepicker);
                        addCountdown.this.hour = timePicker.getCurrentHour();
                        addCountdown.this.minute = timePicker.getCurrentMinute();
                        time.setText(addCountdown.this.year+"年"+addCountdown.this.month+"月"+addCountdown.this.day+"日"+" "+addCountdown.this.hour+":"+addCountdown.this.minute);
                    }
                }).create().show();
            }
        });

        spinner = (Spinner)findViewById(R.id.add_countdown_remindertime);
        final List<String> list = new ArrayList<String>();
        list.add("提前一天提醒");
        list.add("提前两天提醒");
        list.add("提前三天提醒");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(addCountdown.this, R.layout.support_simple_spinner_dropdown_item, list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(addCountdown.this, list.get(i), Toast.LENGTH_SHORT).show();
                rday = i+1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String addname = name.getText().toString();
                String addaddress = address.getText().toString();

                //名字查空
                if (name.getText().toString().equals(""))
                    Toast.makeText(addCountdown.this, "名字为空，请完善", Toast.LENGTH_SHORT).show();
                else {
                    //名字查重
                    Cursor cr = sqLiteDatabase.query(TABLE_NAME, null, "name='" + name.getText().toString() + "'", null, null, null, null);
                    if (cr.getCount() != 0)
                        Toast.makeText(addCountdown.this, "名字重复啦，请核查", Toast.LENGTH_SHORT).show();
                    else {
                        //数据库信息插入
                        INSERT(addname, addaddress, year, month, day, rday, hour, minute);
                        Intent intent = new Intent();
                        intent.setClass(addCountdown.this, MainActivity.class);
                        startActivity(intent);
                        addCountdown.this.finish();
                    }
                }


            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(addCountdown.this, MainActivity.class);
                startActivity(intent);
                addCountdown.this.finish();
            }
        });
    }

    public void INSERT(String name, String address, int year, int month, int day, int reminderday, int hour, int minute) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("address", address);
        contentValues.put("year", year);
        contentValues.put("month", month);
        contentValues.put("day", day);
        contentValues.put("reminderday", reminderday);
        contentValues.put("hour", hour);
        contentValues.put("minute", minute);

        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);

        UPDATE();

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
            hour = cursor.getInt(cursor.getColumnIndex("hour"));
            minute = cursor.getInt(cursor.getColumnIndex("minute"));
            //Toast.makeText(this.getContext(), year + "year" + month+"month" + day+ "day", Toast.LENGTH_SHORT).show();

            /*Calendar now = Calendar.getInstance();
            Calendar calendar = Calendar.getInstance();
            calendar.clear();
            calendar.set(year, month-1, day);*/

            android.text.format.Time now = new android.text.format.Time();
            now.setToNow();
            android.text.format.Time calendar = new android.text.format.Time();
            calendar.set(0, minute, hour, day, month-1, year);

            long time1 = now.toMillis(false);
            long time2 = calendar.toMillis(false);

            //long diff = (time2-time1)/(24*60*60*1000)+1;
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
}
