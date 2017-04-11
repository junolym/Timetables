package com.example.liwensheng.timetables;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ImportActivity extends AppCompatActivity {
    private myDB mydb;
    private  sqlOperate sql = new sqlOperate();
    private SQLiteDatabase sqLiteDatabase;
    private String getTableFromElect = "http://uems.sysu.edu.cn/elect/";

    private ImageView jImg;
    private EditText studentNumber, password, jCode;
    private Button importBtn;
    private WebView jWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        studentNumber = (EditText) findViewById(R.id.studentNumber);
        password = (EditText) findViewById(R.id.password);
        jCode = (EditText) findViewById(R.id.jCode);
        importBtn = (Button) findViewById(R.id.importBtn);

        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (studentNumber.getText().toString().equals("")) {
                    toast("请输入学号");
                } else if (password.getText().toString().equals("")) {
                    toast("请输入密码");
                } else if (jCode.getText().toString().equals("")) {
                    toast("请输入验证码");
                } else {
                    importClick(studentNumber.getText().toString(),
                            password.getText().toString(),
                            jCode.getText().toString());
                }
            }
        });

        jWebView = (WebView) findViewById(R.id.jWebView);

        getJCodeImage();
    }

    private void getJCodeImage() {
        WebSettings webSettings = jWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(jWebView, true);
        }
        jWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                jWebView.loadUrl(url);
                return true;
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                jWebView.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                return showMyHtml(url);
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return showMyHtml(request.getUrl().toString());
            }
        });
        jWebView.addJavascriptInterface(new JavaScriptInterface(), "App");
        jWebView.loadUrl(getTableFromElect);
        jWebView.setBackgroundColor(0);
    }


    private void importClick(final String sid,final String pwd,final String jcd) {
        String js = "submitClick('" + sid + "', '" + pwd + "', '" + jcd + "');";
        jWebView.loadUrl("javascript:"+js);
    }

    private WebResourceResponse showMyHtml(String url) {
        if (url.equals(getTableFromElect)) {
            try {
                String newUrl = "https://kcb.chenjx.cn/getTableFromElect.html";
                URLConnection connection = new URL(newUrl).openConnection();
                connection.setRequestProperty("Host", "kcb.chenjx.cn");
                return new WebResourceResponse("text/html", "UTF-8", connection.getInputStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                String jsonString = msg.obj.toString();
                Log.i("result", jsonString);
                try {
                    JSONArray json = new JSONArray(jsonString);
                    List<Object> result = parseJSONArray(json);
                    if (result.size() == 1) {
                        toast(((HashMap)result.get(0)).get("error").toString());
                        getJCodeImage();
                        return;
                    }
                    clearDatabase();
                    for (int i = 0; i < result.size(); i++) {
                        addCourse(((HashMap)result.get(i)).get("name").toString(),
                                ((HashMap)result.get(i)).get("room").toString(),
                                ((HashMap)result.get(i)).get("teacher").toString(),
                                ((HashMap)result.get(i)).get("time").toString(),
                                ((HashMap)result.get(i)).get("weeks").toString(),
                                ((HashMap)result.get(i)).get("date").toString());
                    }
                    toast("导入成功");
                    importFinish();
                } catch(Exception e) {
                    toast("无法解析课表数据");
                    e.printStackTrace();
                }
            }
        }
    };

    private void addCourse(String name, String room, String teacher, String jie, String week, String time) {
        if (!name.equals("") && !room.equals("") && !teacher.equals("") && !jie.equals("") && !week.equals("") && !time.equals(""))
        sql.INSERT(sqLiteDatabase, name, room, teacher, jie, week, time);
    }

    private void clearDatabase() {
        mydb = new myDB(this, "timetable.db", null, 1);
        sqLiteDatabase = mydb.getWritableDatabase();
        sql.clear(sqLiteDatabase);
    }

    private Object parseValue(Object inputObject) throws JSONException {
        Object outputObject = null;

        if (null != inputObject) {

            if (inputObject instanceof JSONArray) {
                outputObject = parseJSONArray((JSONArray) inputObject);
            } else if (inputObject instanceof JSONObject) {
                outputObject = parseJSONObject((JSONObject) inputObject);
            } else if (inputObject instanceof String || inputObject instanceof Boolean || inputObject instanceof Integer) {
                outputObject = inputObject;
            }

        }

        return outputObject;
    }

    private List<Object> parseJSONArray(JSONArray jsonArray) throws JSONException {

        List<Object> valueList = null;

        if (null != jsonArray) {
            valueList = new ArrayList<Object>();

            for (int i = 0; i < jsonArray.length(); i++) {
                Object itemObject = jsonArray.get(i);
                if (null != itemObject) {
                    valueList.add(parseValue(itemObject));
                }
            }
        }

        return valueList;
    }

    private Map<String, Object> parseJSONObject(JSONObject jsonObject) throws JSONException {

        Map<String, Object> valueObject = null;
        if (null != jsonObject) {
            valueObject = new HashMap<String, Object>();

            Iterator<String> keyIter = jsonObject.keys();
            while (keyIter.hasNext()) {
                String keyStr = keyIter.next();
                Object itemObject = jsonObject.opt(keyStr);
                if (null != itemObject) {
                    valueObject.put(keyStr, parseValue(itemObject));
                }

            }
        }

        return valueObject;
    }

    private void toast(String str) {
        try {
            Toast.makeText(ImportActivity.this, str,  Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void importFinish() {
        Intent intent = new Intent();
        this.finish();
        intent.setClass(this.getBaseContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    final class JavaScriptInterface {
        @JavascriptInterface
        public void log(String str) {
            Log.i("WebView", str);
        }

        @JavascriptInterface
        public void androidtoast(String str) {
            toast(str);
        }

        @JavascriptInterface
        public void importclasses(String json) {
            Message message = new Message();
            message.what = 1;
            message.obj = json;
            handler.sendMessage(message);
        }
    }
}
