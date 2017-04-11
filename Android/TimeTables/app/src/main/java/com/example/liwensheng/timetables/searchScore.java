package com.example.liwensheng.timetables;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import java.util.List;
import java.util.Map;


/**
 * Edited by robin on 2016/12/21.
 */

public class searchScore extends Fragment {

    WebView webView;
    EditText username, password;
    View layout;
    String geetestURL = "http://wjw.sysu.edu.cn/";
    String UA = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.score_layout, null);

        username = (EditText) layout.findViewById(R.id.studentNumber);
        password = (EditText) layout.findViewById(R.id.password);

        webView = (WebView) layout.findViewById(R.id.webview);
        loadGeetest();

        Button searchBtn = (Button) layout.findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchClick();
            }
        });

        return layout;
    }

    private void loadGeetest() {
        clearCookies(this.getContext());
        webView.getSettings().setUserAgentString(UA);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ((WebView)view).requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        webView.addJavascriptInterface(new JavaScriptInterface(), "App");
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.setWebViewClient(new WebViewClient() {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        }

        webView.loadUrl(geetestURL);
        webView.setBackgroundColor(0);
    }

    private WebResourceResponse showMyHtml(String url) {
        if (url.equals(geetestURL)) {
            try {
                String newUrl = "https://kcb.chenjx.cn/geetest.html";
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

    private void searchClick() {
        String uStr = username.getText().toString();
        String pStr = password.getText().toString();
        if (uStr.equals("")) {
            toast("请输入学号");
        } else if (pStr.equals("")) {
            toast("请输入密码");
        } else {
            String js = "submitClick('" + uStr + "', '" + pStr + "');";
            webView.loadUrl("javascript:"+js);
        }
    }

    private void toast(String str) {
        try {
            Log.i("Toast", str);
            Message message = new Message();
            message.what = 1;
            message.obj = str;
            handler.sendMessage(message);
        } catch (Exception e) {}
    }


    public static void clearCookies(Context context) {
        @SuppressWarnings("unused")
        CookieSyncManager cookieSyncMngr =
                CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
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
        public void submit(final String challenge, final String validate, final String seccode,
                           final String username, final String password,
                           final String geetest_challenge,final String geetest_validate, final String geetest_seccode,
                           final String cookie) {

            clearCookies(getContext());
            try {
                String _challenge = URLEncoder.encode(challenge, "utf-8");
                String _validate = URLEncoder.encode(validate, "utf-8");
                String _seccode = URLEncoder.encode(seccode, "utf-8");
                String _username = URLEncoder.encode(username, "utf-8");
                String _password = URLEncoder.encode(password, "utf-8");
                String _geetest_challenge = URLEncoder.encode(geetest_challenge, "utf-8");
                String _geetest_validate = URLEncoder.encode(geetest_validate, "utf-8");
                String _geetest_seccode = URLEncoder.encode(geetest_seccode, "utf-8");
                final String formData = "challenge=" + _challenge +
                        "&validate=" + _validate +
                        "&seccode=" + _seccode +
                        "&username=" + _username +
                        "&password=" + _password +
                        "&geetest_challenge=" + _geetest_challenge +
                        "&geetest_validate=" + _geetest_validate +
                        "&geetest_seccode=" + _geetest_seccode;

                new Thread(){
                    @Override
                    public void run() {
                        HttpURLConnection connection = null;
                        try {
                            connection = (HttpURLConnection) ((new URL(geetestURL+"mjwxt/sign_in")).openConnection());
                            connection.setRequestMethod("POST");
                            connection.setConnectTimeout(8000);
                            connection.setReadTimeout(8000);
                            connection.setInstanceFollowRedirects(false);
                            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                            connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
                            connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
                            connection.setRequestProperty("Cache-Control", "max-age=0");
                            connection.setRequestProperty("Connection", "keep-alive");
                            connection.setRequestProperty("Content-Length", ""+formData.getBytes().length);
                            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                            connection.setRequestProperty("Cookie", cookie);
                            connection.setRequestProperty("Host", "wjw.sysu.edu.cn");
                            connection.setRequestProperty("Origin", "http://wjw.sysu.edu.cn");
                            connection.setRequestProperty("Referer", "http://wjw.sysu.edu.cn/");
                            connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
                            connection.setRequestProperty("User-Agent", UA);

                            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                            out.writeBytes(formData);
                            Map<String, List<String>> headers = connection.getHeaderFields();
                            if (headers.toString().indexOf("200 OK") >= 0) {
                                toast("请确认用户名和密码是否有误");
                            } else {
                                if (headers.get("Set-Cookie") != null) {
                                    toast("登录成功，正在查询");
                                    Log.i("Cookie", headers.get("Set-Cookie").toString());
                                    String httpCookie = headers.get("Set-Cookie").toString();
                                    String sno = getCookieValue(httpCookie, "sno");
                                    String name = getCookieValue(httpCookie, "name");
                                    String school = getCookieValue(httpCookie, "sno");
                                    String major = getCookieValue(httpCookie, "major");
                                    getScoreData(cookie, sno, name, school, major);
                                } else {
                                    toast("未知错误，请重试");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            toast("无法连接服务器");
                        } finally {
                            if (connection != null) {
                                connection.disconnect();
                            }
                        }
                    }
                }.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @JavascriptInterface
        public void showscores(String jsonString) {
            Message message = new Message();
            message.what = 0;
            message.obj = jsonString;
            handler.sendMessage(message);
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                String jsonString = msg.obj.toString();
                Intent intent = new Intent();
                intent.setClass(getContext(), ShowScores.class);
                intent.putExtra("jsonString", jsonString);
                startActivity(intent);
            } else if (msg.what == 1) {
                Toast.makeText(layout.getContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private String getCookieValue(String httpCookie, String name) {
        String value = httpCookie.substring(httpCookie.indexOf(name+"=")+name.length()+1, httpCookie.indexOf(";", httpCookie.indexOf(name+"=")));
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length()-1);
        }
        return value;
    }

    private void getScoreData(String _cookie, final String sno, final String name, final String school, final String major) {
        final  String cookie = _cookie + "; sno=" + sno + "; name=" + name + "; school=" + school + "; major=" + major + ";";
        Log.i("new cookie", cookie);
        try {
            new Thread(){
                @Override
                public void run() {
                    HttpURLConnection connection = null;
                    try {
                        connection = (HttpURLConnection) ((new URL(geetestURL+"api/score")).openConnection());
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(8000);
                        connection.setReadTimeout(8000);
                        connection.setInstanceFollowRedirects(false);
                        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                        connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
                        connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
                        connection.setRequestProperty("Cache-Control", "max-age=0");
                        connection.setRequestProperty("Connection", "keep-alive");
                        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        connection.setRequestProperty("Cookie", cookie);
                        connection.setRequestProperty("Host", "wjw.sysu.edu.cn");
                        connection.setRequestProperty("Origin", "http://wjw.sysu.edu.cn");
                        connection.setRequestProperty("Referer", "http://wjw.sysu.edu.cn/score");
                        connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
                        connection.setRequestProperty("User-Agent", UA);

                        InputStream in = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        String res = response.toString();
                        if (!res.equals("expired")) {
                            Message message = new Message();
                            message.what = 0;
                            message.obj = response.toString();
                            handler.sendMessage(message);
                        } else {
                            toast("未知错误，请重试");
                        }
                        Log.i("res", res);

                    } catch (Exception e) {
                        e.printStackTrace();
                        toast("无法连接服务器");
                    } finally {
                        if (connection != null) {
                            connection.disconnect();
                        }
                    }
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
