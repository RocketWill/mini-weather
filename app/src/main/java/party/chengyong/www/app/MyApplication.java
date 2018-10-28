package party.chengyong.www.app;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import party.chengyong.www.bean.City;
import party.chengyong.www.db.CityDB;

public class MyApplication extends Application{
    private static final String TAG = "MyApp";

    private static MyApplication myApplication;
    private CityDB mCityDB;
    private List<City> mCityList;

    private void initCityList(){
        mCityList = new ArrayList<City>(); //city list
        new Thread(new Runnable() {
            @Override
            public void run() {
                prepareCityList();
            }
        }).start();
    }

    private boolean prepareCityList(){
        mCityList = mCityDB.getAllCity();
        int i = 0;
        for (City city: mCityList){
            i++;
            String cityName = city.getCity();
            String cityCode = city.getNumber();
            Log.d(TAG, cityName + ":"+ cityCode);
        }
        Log.d(TAG, "i: "+i);
        return true;
    }

    public List<City> getCityList(){
        return mCityList;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TAG","MyApplication -> OnCreate");
        myApplication = this;
        mCityDB = openCityDB();
        initCityList();
    }

    public static MyApplication getInstance(){
        return myApplication;
    }

    private CityDB openCityDB() {
        String path = "/data"
                + Environment.getDataDirectory().getAbsolutePath()
                + File.separator + getPackageName()
                + File.separator + "database1"
                + File.separator
                + CityDB.CITY_DB_NAME;

        File db = new File(path);
        Log.d(TAG, path);

        if (!db.exists()){
            String pathfolder = "/data"
                    + Environment.getDataDirectory().getAbsolutePath()
                    + File.separator + getPackageName()
                    + File.separator + "database1"
                    + File.separator;

            File dirFirstFolder = new File(pathfolder);

            if (!dirFirstFolder.exists()){
                dirFirstFolder.mkdirs();
                Log.i(TAG,"mkdirs");
            }
            Log.i(TAG, "db is not exists");
            try{
                InputStream is = getAssets().open("city.db");
                FileOutputStream fos = new FileOutputStream(db);
                int len = -1;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1){
                    fos.write(buffer, 0, len);
                    fos.flush();
                }
                fos.close();
                is.close();
            }catch (IOException e){
                e.printStackTrace();
                System.exit(0);
            }
        }
        return new CityDB(this, path);
    }
}
