package com.example.liwensheng.timetables;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by liWensheng on 2016/12/17.
 */

public class addCourseActivity extends AppCompatActivity {
    private static final String TABLE_NAME = "courseTable";
    private myDB mydb = new myDB(this, "timetable.db", null,1);
    private  sqlOperate sql = new sqlOperate();
    private SQLiteDatabase sqLiteDatabase;

    private EditText addCourseName, addCourseRoom, addCourseTeacher,  addCourseweek;
    private Spinner jieSpinnerStart, jieSpinnerEnd, timeSpinner;
    private Button confirmAdd, cancleAdd;

    private String startjie, endjie, timeString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addcourse_layout);

        final Intent intent = new Intent();

        confirmAdd = (Button) findViewById(R.id.addButton);
        cancleAdd = (Button) findViewById(R.id.noAddButton);

        timeSpinner = (Spinner) findViewById(R.id.addtimeSpinner);
        jieSpinnerStart = (Spinner) findViewById(R.id.addjieSpinnerStart);
        jieSpinnerEnd = (Spinner) findViewById(R.id.addjieSpinnerEnd);

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

        jieSpinnerStart.setAdapter(adapter2);

        jieSpinnerEnd.setAdapter(adapter2);

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


        cancleAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setClass(addCourseActivity.this, MainActivity.class);
                startActivity(intent);
                addCourseActivity.this.finish();
            }
        });

        confirmAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addCourse()) {
                    intent.setClass(addCourseActivity.this, MainActivity.class);
                    startActivity(intent);
                    addCourseActivity.this.finish();
                }
            }
        });
    }

    private boolean addCourse() {
        mydb = new myDB(this, "timetable.db", null,1);
        sqLiteDatabase = mydb.getWritableDatabase();

        addCourseName = (EditText)findViewById(R.id.addName);
        addCourseRoom = (EditText) findViewById(R.id.addRoom);
        addCourseTeacher = (EditText) findViewById(R.id.addTeacher);
        addCourseweek = (EditText) findViewById(R.id.addweek);

        String addjie = startjie+"~"+endjie+"节";
        String name = addCourseName.getText().toString();
        String room = addCourseRoom.getText().toString();
        String teacher = addCourseTeacher.getText().toString();
        String week = addCourseweek.getText().toString();

        if (Integer.parseInt(endjie) - Integer.parseInt(startjie) <= 0) {
            Toast.makeText(addCourseActivity.this, "请正确设置上课节数！！", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (name.length() == 0) {
            Toast.makeText(addCourseActivity.this, "课程名称不能为空！！", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (room.length() == 0) {
            Toast.makeText(addCourseActivity.this, "教室信息不能为空！！", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (teacher.length() == 0) {
            Toast.makeText(addCourseActivity.this, "教师信息不能为空！！", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (week.length() == 0) {
            Toast.makeText(addCourseActivity.this, "上课周数不能为空！！", Toast.LENGTH_SHORT).show();
            return false;
        }
        else  {

            sql.INSERT(sqLiteDatabase, name, room, teacher, addjie, week, timeString);
            return true;
        }
    }
}
