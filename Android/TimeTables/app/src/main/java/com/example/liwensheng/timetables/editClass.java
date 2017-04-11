package com.example.liwensheng.timetables;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liWensheng on 2016/12/16.
 */

public class editClass extends AppCompatActivity {
    private static final String TABLE_NAME = "courseTable";
    private myDB mydb = new myDB(this, "timetable.db", null,1);
    private  sqlOperate sql = new sqlOperate();
    private SQLiteDatabase sqLiteDatabase;

    private EditText editRoom, editTeacher, editWeek;
    private TextView editName;
    private Spinner timeSpinner, jieSpinnerStart, jieSpinnerEnd;

    private String timeString, startjie, endjie, jie;

    private List<String> weekDay = new ArrayList<>();

    private Button yes, no;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editcourse_layout);

        mydb = new myDB(this, "timetable.db", null,1);
        sqLiteDatabase = mydb.getWritableDatabase();

        weekDay.add("一");
        weekDay.add("二");
        weekDay.add("三");
        weekDay.add("四");
        weekDay.add("五");
        weekDay.add("六");
        weekDay.add("日");

        Intent intent = getIntent();
        final int id = intent.getIntExtra("id", 0);
        editName = (TextView) findViewById(R.id.editName);
        editRoom = (EditText) findViewById(R.id.editRoom);
        editTeacher = (EditText) findViewById(R.id.editTeacher);
        editWeek = (EditText) findViewById(R.id.editWeek);
        timeSpinner = (Spinner) findViewById(R.id.timeSpinner);
        jieSpinnerStart = (Spinner) findViewById(R.id.jieSpinnerStart);
        jieSpinnerEnd = (Spinner) findViewById(R.id.jieSpinnerEnd);

        editName.setText(sql.QUERY(sqLiteDatabase,id,"name"));
        editRoom.setText(sql.QUERY(sqLiteDatabase, id, "room"));
        editTeacher.setText(sql.QUERY(sqLiteDatabase,id,"teacher"));
        editWeek.setText(sql.QUERY(sqLiteDatabase,id, "week"));

        timeString = sql.QUERY(sqLiteDatabase,id,"time");
        jie = sql.QUERY(sqLiteDatabase,id,"jie");

        int index = jie.indexOf("~");
        final int start = Integer.parseInt(jie.substring(0,index));
        final int end = Integer.parseInt(jie.substring(index+1, jie.length()-1));
        startjie = start + "";
        endjie = end + "";

        final int timepos = weekDay.indexOf(timeString.substring(1));

        //准备一个数组适配器
        ArrayAdapter adapter1 = ArrayAdapter.createFromResource(
                this, R.array.weektime, android.R.layout.simple_spinner_item);
        ArrayAdapter adapter2 = ArrayAdapter.createFromResource(
                this, R.array.jietime, android.R.layout.simple_spinner_dropdown_item);

        //设置下拉样式  android里面给大家提供了丰富的样式和功能图片
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //为下拉列表设置适配器
        timeSpinner.setAdapter(adapter1);
        timeSpinner.setSelection(timepos);

        jieSpinnerStart.setAdapter(adapter2);
        jieSpinnerStart.setSelection(start-1);

        jieSpinnerEnd.setAdapter(adapter2);
        jieSpinnerEnd.setSelection(end-1);

        //定义子元素选择监听器
        AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
//                parent.setSelection(timepos);
//                Toast.makeText(editClass.this,"选择时间:" +
//                        parent.getItemAtPosition(position).toString(), Toast.LENGTH_LONG).show();
                timeString = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
        //为下拉列表绑定事件监听器
        timeSpinner.setOnItemSelectedListener(onItemSelectedListener);

        AdapterView.OnItemSelectedListener onItemSelectedListener1 = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                startjie = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
        jieSpinnerStart.setOnItemSelectedListener(onItemSelectedListener1);

        AdapterView.OnItemSelectedListener onItemSelectedListener2 = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                endjie = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
        jieSpinnerEnd.setOnItemSelectedListener(onItemSelectedListener2);


        yes = (Button) findViewById(R.id.yes);
        no = (Button) findViewById(R.id.no);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editName.getText().toString();
                String room = editRoom.getText().toString();
                String teacher = editTeacher.getText().toString();
                jie = startjie+"~"+endjie+"节";
                String week = editWeek.getText().toString();
                String time = timeString;

                int jieJudge = Integer.parseInt(endjie) - Integer.parseInt(startjie);
                if (jieJudge <= 0) {
                    Toast.makeText(editClass.this, "请正确选择节数！！", Toast.LENGTH_SHORT).show();
                }
                else if (name.length() == 0) {
                    Toast.makeText(editClass.this, "课程名称不能为空！！", Toast.LENGTH_SHORT).show();
                }
                else if (room.length() == 0) {
                    Toast.makeText(editClass.this, "教室信息不能为空！！", Toast.LENGTH_SHORT).show();
                }
                else if (teacher.length() == 0) {
                    Toast.makeText(editClass.this, "教师信息不能为空！！", Toast.LENGTH_SHORT).show();
                }
                else if (week.length() == 0) {
                    Toast.makeText(editClass.this, "上课周数不能为空！！", Toast.LENGTH_SHORT).show();
                }
                else {
                    sql.UPDATE(sqLiteDatabase, id, name, room, teacher, jie, week, time);
                    Intent intent1 = new Intent();
                    intent1.setClass(editClass.this, MainActivity.class);
                    startActivity(intent1);
                    editClass.this.finish();
                }
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent();
                intent2.setClass(editClass.this, MainActivity.class);
                startActivity(intent2);
                editClass.this.finish();
            }
        });
    }
}
