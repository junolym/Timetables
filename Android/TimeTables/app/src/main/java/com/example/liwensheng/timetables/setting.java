package com.example.liwensheng.timetables;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by liWensheng on 2016/12/17.
 */

public class setting extends Fragment {
    private Button addButton, importButton, moreScores,
            setBackground, clearBackground, changeTheme,
            aboutUs, checkUpdate, comment;
    private View layout;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.setting_layout, null);
        init(layout);
        return layout;
    }

    private void init(final View layout) {
        addButton = (Button)layout.findViewById(R.id.addCourseSetting);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(setting.this.getContext(), addCourseActivity.class);
                startActivity(intent);
            }
        });

        importButton = (Button)layout.findViewById(R.id.importCourses);
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(setting.this.getContext(), ImportActivity.class);
                startActivity(intent);
            }
        });

        aboutUs = (Button) layout.findViewById(R.id.aboutUs);
        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(setting.this.getContext(), WebviewActivity.class);
                intent.putExtra("url", "https://kcb.chenjx.cn/about.html");
                startActivity(intent);
            }
        });

        checkUpdate = (Button) layout.findViewById(R.id.checkUpdate);
        checkUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(setting.this.getContext(), WebviewActivity.class);
                intent.putExtra("url", "https://kcb.chenjx.cn/update?current="+getCurrentVersion());
                startActivity(intent);
            }
        });

        comment = (Button) layout.findViewById(R.id.comment);
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(setting.this.getContext(), WebviewActivity.class);
                intent.putExtra("url", "https://kcb.chenjx.cn/comment.html");
                startActivity(intent);
            }
        });

        moreScores = (Button) layout.findViewById(R.id.moreScores);
        moreScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(setting.this.getContext(), WebviewActivity.class);
                intent.putExtra("url", "https://kcb.chenjx.cn/moreScores.html");
                startActivity(intent);
            }
        });

        setBackground = (Button) layout.findViewById(R.id.setBackground);
        setBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectBackground(true);
            }
        });

        clearBackground = (Button) layout.findViewById(R.id.clearBackground);
        clearBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectBackground(false);
            }
        });

        changeTheme = (Button) layout.findViewById(R.id.changeTheme);
        changeTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String theme = getSetting(layout.getContext(), "theme", "Color");
                if (theme.equals("Color")) {
                    saveSetting(layout.getContext(), "theme", "Simple");
                    Toast.makeText(layout.getContext(), "主题已设置为“简约”", Toast.LENGTH_SHORT).show();
                } else {
                    saveSetting(layout.getContext(), "theme", "Color");
                    Toast.makeText(layout.getContext(), "主题已设置为“多彩”", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getCurrentVersion() {
        try {
            PackageManager packageManager = layout.getContext().getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(layout.getContext().getPackageName(), 0);
            String version = packInfo.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private void selectBackground(boolean wantToSet) {
        if (wantToSet) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 2);
        } else {
            ImageView background = (ImageView) ((RelativeLayout)layout.getParent().getParent()).findViewById(R.id.background);
            background.setVisibility(View.INVISIBLE);
            saveSetting(layout.getContext(), "background", "");
            Toast.makeText(layout.getContext(), "背景已设置为白色", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2 && data != null) {
            try {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = layout.getContext().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                Log.i("path", picturePath);

                ImageView background = (ImageView) ((RelativeLayout)layout.getParent().getParent()).findViewById(R.id.background);
                background.setImageDrawable(Drawable.createFromPath(picturePath));
                background.setVisibility(View.VISIBLE);

                saveSetting(layout.getContext(), "background", picturePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    static public void saveSetting(Context context, String name, String value) {
        SharedPreferences settings = context.getSharedPreferences("setting", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(name, value);
        editor.commit();
    }

    static public String getSetting(Context context, String name, String defaultValue) {
        SharedPreferences settings = context.getSharedPreferences("setting", 0);
        return settings.getString(name, defaultValue);
    }


}
