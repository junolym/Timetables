package com.example.liwensheng.timetables;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.jpeng.jptabbar.JPTabBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liWensheng on 2016/12/14.
 */

public class classesTable  extends Fragment {

    private TextView emptyColum, monColum, tuesColum, wedColum, thursColum, friColum, satColum, sunColum;
    private RelativeLayout courseRL;
    private int screenWidth, aveWidth, gridHeight;
    private int[]background =  {R.drawable.blue_bg, R.drawable.green_bg,
            R.drawable.red_bg, R.drawable.yellow_bg};
    private List<TextView> allCourse = new ArrayList<>();

    private List<String> weekDay = new ArrayList<>();

    private Button editButton;

    private JPTabBar mJPTabBar;
    private TextView dialogName, dialogRoom, dialogTeacher, dialogjie, dialogweek, dialogtime;

    private static final String TABLE_NAME = "courseTable";
    private myDB mydb = new myDB(this.getContext(), "timetable.db", null,1);
    private  sqlOperate sql = new sqlOperate();
    private SQLiteDatabase sqLiteDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.table_layout, null);
        init(layout);
        return layout;
    }

    private void test() {
        mydb = new myDB(this.getContext(), "timetable.db", null,1);
        sqLiteDatabase = mydb.getWritableDatabase();
        sql.INSERT(sqLiteDatabase, "手机平台应用开发", "东C301", "刘宁", "4~5节", "1-18周", "周三10:45 ~ 12:25");
        sql.INSERT(sqLiteDatabase, "手机平台应用开发", "东C301", "刘宁", "4~5节", "1-18周", "周五10:45 ~ 12:25");
        sql.INSERT(sqLiteDatabase, "计网", "C202", "谢逸", "3~5节", "1-18周", "周一9:45~12:25");
    }

    private void init(View layout) {
        mJPTabBar = ((MainActivity)getActivity()).getTabbar();
        mydb = new myDB(this.getContext(), "timetable.db", null,1);
        sqLiteDatabase = mydb.getWritableDatabase();

        weekDay.add("一");
        weekDay.add("二");
        weekDay.add("三");
        weekDay.add("四");
        weekDay.add("五");
        weekDay.add("六");
        weekDay.add("日");



//        test();
        initTable(layout);
        updateTable();
    }

    private void initTable(View layout) { //初始化表格
        emptyColum = (TextView) layout.findViewById(R.id.text_empty);
        monColum = (TextView) layout.findViewById(R.id.monday_course);
        tuesColum = (TextView) layout.findViewById(R.id.tuesday_course);
        wedColum = (TextView) layout.findViewById(R.id.wednesday_course);
        thursColum = (TextView) layout.findViewById(R.id.thursday_course);
        friColum = (TextView) layout.findViewById(R.id.friday_course);
        satColum = (TextView) layout.findViewById(R.id.saturday_course);
        sunColum = (TextView) layout.findViewById(R.id.sunday_course);
        courseRL = (RelativeLayout) layout.findViewById(R.id.course_rl);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        //屏幕宽度
        screenWidth = dm.widthPixels;
        //平均宽度
        aveWidth = screenWidth / 8;
        //第一个空白格子设置为25宽
        emptyColum.setWidth(aveWidth * 3/4);
        monColum.setWidth(aveWidth * 33/32 + 1);
        tuesColum.setWidth(aveWidth * 33/32 + 1);
        wedColum.setWidth(aveWidth * 33/32 + 1);
        thursColum.setWidth(aveWidth * 33/32 + 1);
        friColum.setWidth(aveWidth * 33/32 + 1);
        satColum.setWidth(aveWidth * 33/32 + 1);
        sunColum.setWidth(aveWidth * 33/32 + 1);

        gridHeight = dm.heightPixels / 15;

        for (int i = 1; i <= 15; i++) {
            for (int j = 1; j <= 8; j++) {
                TextView tx = new TextView(classesTable.this.getContext());
                tx.setId((i - 1) * 8  + j); // id

                tx.setBackgroundResource(R.drawable.course_text_bg);
                //相对布局参数
                RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(
                        aveWidth * 33 / 32 + 1,
                        gridHeight);
                //文字对齐方式
                tx.setGravity(Gravity.CENTER);
                //字体样式
                tx.setTextAppearance(classesTable.this.getContext(), R.style.weekdayStyle);
                //如果是第一列，需要设置课的序号（1 到 12）
                if(j == 1)  {
                    tx.setText(String.valueOf(i));
                    rp.width = aveWidth * 3/4;
                    //设置他们的相对位置
                    if(i == 1)
                        rp.addRule(RelativeLayout.BELOW, emptyColum.getId());
                    else
                        rp.addRule(RelativeLayout.BELOW, (i - 1) * 8);
                }
                else {
                    rp.addRule(RelativeLayout.RIGHT_OF, (i - 1) * 8  + j - 1);
                    rp.addRule(RelativeLayout.ALIGN_TOP, (i - 1) * 8  + j - 1);
                    tx.setText("");
                }

                tx.setLayoutParams(rp);
                courseRL.addView(tx);
            }
        }
    }


    private void updateTable() {
        removeAllCourseTable();
        mydb = new myDB(this.getContext(), "timetable.db", null,1);
        sqLiteDatabase = mydb.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME,
                new String[]{"_id", "name", "room", "teacher", "jie", "week", "time"},
                null, null, null, null, null);
        if (cursor.moveToFirst())
            while(cursor!=null ) {
                int i = cursor.getColumnCount();
                Log.i("num", i+"");
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                Log.i("name", name);
                String room = cursor.getString(2);
                Log.i("room", room);
                String time = cursor.getString(6);
                String day = time.substring(1,2);
                int week = weekDay.indexOf(day)+1;
                Log.i("week", week+"");
                String jie = cursor.getString(4);
                int index = jie.indexOf("~");
                int start = Integer.parseInt(jie.substring(0,index));
                Log.i("start", start+"");
                int end = Integer.parseInt(jie.substring(index+1, jie.length()-1));
                int howlong = end - start+1;
                Log.i("howlong", howlong+"");

                showInTable(id, name, room, week, start, howlong);
                if (cursor.isLast())
                    break;
                cursor.moveToNext();
            }
    }

    private void showInTable(final int id, final String name, String room, int week, int start, int howlong) {
        // 添加课程信息
        final TextView courseInfo = new TextView(classesTable.this.getContext());
        courseInfo.setText(name+"\n"+room);
        //该textview的高度根据其节数的跨度来设置
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                aveWidth * 31 / 32,
                (gridHeight - 5) * howlong );
        //textview的位置由课程开始节数和上课的时间（day of week）确定
        rlp.topMargin = 5 + (start - 1) * gridHeight;
        rlp.leftMargin = 1;
        // 偏移由这节课是星期几决定
        rlp.addRule(RelativeLayout.RIGHT_OF, week);
        //字体剧中
        courseInfo.setGravity(Gravity.CENTER);
        // 设置一种背景
        String theme = setting.getSetting(getContext(), "theme", "Color");
        if (theme.equals("Color")) {
            courseInfo.setBackgroundResource(background[(week*3+start)%4]);
            courseInfo.setTextColor(Color.WHITE);
        } else {
            courseInfo.setBackgroundColor(0xFFEEEEEE);
            courseInfo.setTextColor(Color.BLACK);
        }
        courseInfo.setTextSize(12);
        courseInfo.setLayoutParams(rlp);
        //设置不透明度
        courseInfo.getBackground().setAlpha(222);

        courseInfo.setClickable(true);
        courseInfo.setFocusable(true);
        final String coursename = name;
        courseInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(id);
            }
        });
        courseInfo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(classesTable.this.getContext());
                builder.setTitle("删除课程");
                builder.setMessage("确定删除课程" +name + "?");

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sql.DELETE(sqLiteDatabase ,id);
                        updateTable();
                    }
                });
                builder.create();
                builder.show();
                return true;
            }
        });
        allCourse.add(courseInfo);
        courseRL.addView(courseInfo);
    }


    private void showDialog(final int id) {
//        mydb = new myDB(this.getContext(), "timetable.db", null,1);
        sqLiteDatabase = mydb.getWritableDatabase();
        LayoutInflater factory = LayoutInflater.from(classesTable.this.getContext());
        View view = factory.inflate(R.layout.course_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(classesTable.this.getContext());


        dialogName = (TextView) view.findViewById(R.id.dialogName);
        dialogRoom = (TextView) view.findViewById(R.id.dialogRoom);
        dialogTeacher = (TextView) view.findViewById(R.id.dialogTeacher);
        dialogjie = (TextView) view.findViewById(R.id.dialogjie);
        dialogweek = (TextView) view.findViewById(R.id.dialogweek);
        dialogtime = (TextView) view.findViewById(R.id.dialogTime);

        dialogName.setText(sql.QUERY(sqLiteDatabase, id, "name"));
        dialogRoom.setText(sql.QUERY(sqLiteDatabase, id, "room"));
        dialogTeacher.setText(sql.QUERY(sqLiteDatabase,id,"teacher"));
        dialogjie.setText(sql.QUERY(sqLiteDatabase,id,"jie"));
        dialogweek.setText(sql.QUERY(sqLiteDatabase,id, "week"));
        dialogtime.setText(sql.QUERY(sqLiteDatabase,id,"time"));

        builder.setView(view).setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        }).create().show();

        editButton = (Button)view.findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = dialogName.getText().toString();
                Log.i("name", name);
                Intent intent = new Intent();
                intent.putExtra("id", id);
                intent.setClass(classesTable.this.getContext(), editClass.class);
                startActivity(intent);
            }
        });
    }

    private void removeAllCourseTable() {
        for (int i = 0; i < allCourse.size(); i++) {
            TextView l = allCourse.get(i);
            courseRL.removeView(l);
        }
    }

    public Context getcontext() {
        return this.getContext();
    }
}
