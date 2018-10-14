package party.chengyong.www.mini_weather;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import party.chengyong.www.mini_weather.util.NetUtil;

public class MainActivity extends Activity implements View.OnClickListener {

    private ImageView mUpdateBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        mUpdateBtn = (ImageView)findViewById(R.id.title_refresh);
        mUpdateBtn.setOnClickListener(this);


//        NetUtil netutil = new NetUtil();
//
//        if (netutil.getNetworkState(this) != netutil.NETWORN_NONE){
//            Log.d("mini_weather","網路ok");
//            queryWeatherCode("101010100");
//        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.title_refresh){
            SharedPreferences sharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code", "101010100");
            Log.d("mini_weather",cityCode);

            NetUtil netutil = new NetUtil();
            if (netutil.getNetworkState(this) != netutil.NETWORN_NONE){
                Log.d("mini_weather","網路ok");
                queryWeatherCode(cityCode);
            }
        }
    }

    private void queryWeatherCode(String cityCode) {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("mini_weather",address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con = null;
                try{
                    URL url = new URL(address);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while ((str=reader.readLine()) != null){
                        response.append(str);
                        Log.d("mini_weather",str);
                    }
                    String responseStr = response.toString();
                    Log.d("mini_weather", responseStr);
                    parseXML(responseStr);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if (con != null){
                        con.disconnect();
                    }
                }
            }
        }).start();
    }

    private void parseXML(String xmldata){
        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;

        try{
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int evenType = xmlPullParser.getEventType();
            Log.d("mini_weather","parseXML");
            while (evenType != XmlPullParser.END_DOCUMENT){
                switch (evenType){
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("city")){
                            evenType = xmlPullParser.next();
                            Log.d("mini_weather","city: " + xmlPullParser.getText());
                        }else if (xmlPullParser.getName().equals("updatetime")){
                            evenType = xmlPullParser.next();
                            Log.d("mini_weather","updatetime: " + xmlPullParser.getText());
                        }else if (xmlPullParser.getName().equals("shidu")){
                            evenType = xmlPullParser.next();
                            Log.d("mini_weather","shidu: " + xmlPullParser.getText());
                        }else if (xmlPullParser.getName().equals("wendu")){
                            evenType = xmlPullParser.next();
                            Log.d("mini_weather","wendu: " + xmlPullParser.getText());
                        }else if (xmlPullParser.getName().equals("pm25")){
                            evenType = xmlPullParser.next();
                            Log.d("mini_weather","pm25: " + xmlPullParser.getText());
                        }else if (xmlPullParser.getName().equals("quality")){
                            evenType = xmlPullParser.next();
                            Log.d("mini_weather","quality: " + xmlPullParser.getText());
                        }else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0){
                            evenType = xmlPullParser.next();
                            Log.d("mini_weather","fengxiang: " + xmlPullParser.getText());
                            fengxiangCount ++;
                        }else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0){
                            evenType = xmlPullParser.next();
                            Log.d("mini_weather","fengli: " + xmlPullParser.getText());
                            fengliCount ++;
                        }else if (xmlPullParser.getName().equals("date") && dateCount == 0){
                            evenType = xmlPullParser.next();
                            Log.d("mini_weather","date: " + xmlPullParser.getText());
                            dateCount ++;
                        }else if (xmlPullParser.getName().equals("high") && highCount == 0){
                            evenType = xmlPullParser.next();
                            Log.d("mini_weather","high: " + xmlPullParser.getText());
                            highCount ++;
                        }else if (xmlPullParser.getName().equals("low") && lowCount == 0){
                            evenType = xmlPullParser.next();
                            Log.d("mini_weather","low: " + xmlPullParser.getText());
                            lowCount ++;
                        }else if (xmlPullParser.getName().equals("type") && typeCount == 0){
                            evenType = xmlPullParser.next();
                            Log.d("mini_weather","type: " + xmlPullParser.getText());
                            typeCount ++;
                        }

                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                evenType = xmlPullParser.next();
            }
        }
        catch (XmlPullParserException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
