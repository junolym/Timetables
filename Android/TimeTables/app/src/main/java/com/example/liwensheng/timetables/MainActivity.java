package com.example.liwensheng.timetables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jpeng.jptabbar.BadgeDismissListener;
import com.jpeng.jptabbar.JPTabBar;
import com.jpeng.jptabbar.OnTabSelectListener;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BadgeDismissListener, OnTabSelectListener{
    private JPTabBar mJPTabBar;

    private classesTable mClassTables;
    private searchScore mSearchScore;
    private countdown mCountdown;
    private setting mSetting;

    private List<Fragment> list = new ArrayList<>();

    private NoScrollViewPager mNoScrollViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendBroadcast();

        setmJPTabBar();
        initialTabs();

        setBackground();
    }

    @Override
    public void onDismiss(int position) {
    }

    @Override
    public void onTabSelect(int index) {}

    @Override
    public void onClickMiddle(View middleBtn) {

    }

    public JPTabBar getTabbar() {
        return mJPTabBar;
    }

    private void setmJPTabBar() {
        mNoScrollViewPager = (NoScrollViewPager)findViewById(R.id.view_pager);
        mJPTabBar = (JPTabBar) findViewById(R.id.tabbar);
        mJPTabBar.setTitles(R.string.tab1, R.string.tab2, R.string.tab3, R.string.tab4)
                .setNormalIcons(R.mipmap.calendar, R.mipmap.search, R.mipmap.time, R.mipmap.set)
                .setSelectedIcons(R.mipmap.calendar_selected, R.mipmap.search_selected, R.mipmap.time_select, R.mipmap.set_select)
                .generate();
        mNoScrollViewPager.setNoScroll(false);

    }

    private void initialTabs() {
        mClassTables = new classesTable();
        mSearchScore = new searchScore();
        mCountdown = new countdown();
        mSetting = new setting();

        list.add(mClassTables);
        list.add(mSearchScore);
        list.add(mCountdown);
        list.add(mSetting);

        mNoScrollViewPager.setAdapter(new Adapter(getSupportFragmentManager(), list));
        mJPTabBar.setContainer(mNoScrollViewPager);
    }

    String RECEIVER = "countdownReceiver";
    private static final String TABLE_NAME = "countdownTable";
    private countdownDB cDB = new countdownDB(this, "countdown.db", null,1);
    private SQLiteDatabase sqLiteDatabase;
    public void sendBroadcast() {
        sqLiteDatabase = cDB.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + TABLE_NAME, null);
        while (cursor.moveToNext()) {
            int reminderdays = cursor.getInt(cursor.getColumnIndex("reminderday"));
            String countdowndaysString = cursor.getString(cursor.getColumnIndex("countdownday"));
            if (countdowndaysString.indexOf("还剩") != -1) {
                countdowndaysString = countdowndaysString.replace("还剩", "");
                countdowndaysString = countdowndaysString.replace("天", "");
                int countdowndays = Integer.parseInt(countdowndaysString);
                //Toast.makeText(MainActivity.this, countdowndays + ":" + reminderdays, Toast.LENGTH_SHORT).show();
                if (reminderdays == countdowndays) {
                    Bundle bundle = new Bundle();
                    bundle.putString("name", cursor.getString(cursor.getColumnIndex("name")));
                    bundle.putString("counedownday", cursor.getString(cursor.getColumnIndex("countdownday")));
                    Intent intent = new Intent(RECEIVER);
                    intent.putExtras(bundle);
                    intent.setClass(MainActivity.this, countdownReceive.class);
                    sendBroadcast(intent);
                }
            }

        }
    }

    private void setBackground() {
        String picturePath = setting.getSetting(this, "background", "");
        if (!picturePath.equals("")) {
            try {
                ImageView background = (ImageView) findViewById(R.id.background);
                background.setImageDrawable(Drawable.createFromPath(picturePath));
                background.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
