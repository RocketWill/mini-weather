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


//Application实例拥有着与APP一样长的生命周期：在APP开启的时候首先就会实例化它，然后才是入口的Activity或者Service等
//Application与APP“同生共死”，在一个APP的生命周期只实例化一次，所以它“天生”就是一个单例，不需要使用单例模式去实现它
public class MyApplication extends Application{
    private static final String TAG = "MyApp";

    private static MyApplication myApplication;
    private CityDB mCityDB;
    private List<City> mCityList;

//    用多線程方式獲取程式列表
    private void initCityList(){
        mCityList = new ArrayList<City>(); //city list
        new Thread(new Runnable() {
            @Override
            public void run() {
                prepareCityList();
            }
        }).start();
    }

//  獲取程式列表，元素為City類型
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

//   返回程式列表，元素為City類型
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

//    返回一個myApplication實例
    public static MyApplication getInstance(){
        return myApplication;
    }

//    開啟程式列表據庫，並讀取內容，返回CityDB類型
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

//            如果文件夾不存在，則創建新的
            if (!dirFirstFolder.exists()){
                dirFirstFolder.mkdirs();
                Log.i(TAG,"mkdirs");
            }
            Log.i(TAG, "db is not exists");

//            讀取數據庫資料
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
