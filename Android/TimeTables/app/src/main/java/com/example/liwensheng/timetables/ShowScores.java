package com.example.liwensheng.timetables;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ShowScores extends AppCompatActivity {
    private ListView listView;
    private List<Object> scores;
    private String xnd = "2015-2016", xq = "3";
    private Spinner xndSpinner, xqSpinner;
    private ArrayAdapter<String> adapterXnd = null;
    private ArrayAdapter<String> adapterXq2 = null;
    private ArrayAdapter<String> adapterXq3 = null;
    private static final String [] xnds = {"2013-2014","2014-2015","2015-2016","2016-2017","2017-2018"};
    private static final String [] xqs2 = {"第一学期", "第二学期"};
    private static final String [] xqs3 = {"第一学期", "第二学期", "第三学期"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_scores);

        Intent intent = getIntent();
        String jsonString = intent.getStringExtra("jsonString");

        listView = (ListView) findViewById(R.id.listView);

        xqSpinner = (Spinner) findViewById(R.id.xq);
        xndSpinner = (Spinner) findViewById(R.id.xnd);

        adapterXnd = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, xnds);
        adapterXq2 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, xqs2);
        adapterXq3 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, xqs3);

        xndSpinner.setAdapter(adapterXnd);
        xndSpinner.setVisibility(View.VISIBLE);
        xndSpinner.setSelection(2);
        xndSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                xnd = ((TextView) arg1).getText().toString();
                if (xnd.indexOf("2014") >= 0 || xnd.indexOf("2015") >= 0) {
                    if (!xqSpinner.getAdapter().equals(adapterXq3)) {
                        xqSpinner.setAdapter(adapterXq3);
                    }
                } else {
                    if (!xqSpinner.getAdapter().equals(adapterXq2)) {
                        xqSpinner.setAdapter(adapterXq2);
                    }
                }
                displayScores();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        xqSpinner.setAdapter(adapterXq3);
        xqSpinner.setVisibility(View.VISIBLE);
        xqSpinner.setSelection(2);
        xqSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                xq = "" + (arg2 + 1);
                displayScores();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        parseScore(jsonString);
    }

    private void parseScore(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            Map<String, Object> result = parseJSONObject(json);
            scores = (List<Object>)((Map<String, Object>)((Map<String, Object>)
                    ((Map<String, Object>)((Map<String, Object>)
                            result.get("body")).get("dataStores")).get("kccjStore")).get("rowSet")).get("primary");

            displayScores();
        } catch(Exception e) {
            toast("无法解析成绩数据");
            e.printStackTrace();
        }
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
            Log.i("Toast", str);
            Toast.makeText(ShowScores.this, str, Toast.LENGTH_LONG).show();
        } catch (Exception e) {}
    }

    private void displayScores() {
        ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < scores.size(); i++) {
            final Map<String, String> score = (Map<String, String>)scores.get(i);
            if (score.get("xnd").equals(xnd) && score.get("xq").equals(xq)) {
                HashMap<String, String> map = new HashMap<String, String>(){{
                    put("kcmc", score.get("kcmc"));
                    put("xf", score.get("xf"));
                    put("zzcj", score.get("zzcj"));
                    put("jd", score.get("jd"));
                }};
                mylist.add(map);
            }
        }

        SimpleAdapter mSchedule = new SimpleAdapter(this, mylist, R.layout.score_item,
                new String[] {"kcmc", "xf", "zzcj", "jd"},
                new int[] {R.id.kcmc, R.id.xf, R.id.zzcj, R.id.jd});
        listView.setAdapter(mSchedule);
    }
}
