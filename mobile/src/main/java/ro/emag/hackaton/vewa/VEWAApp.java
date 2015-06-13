package ro.emag.hackaton.vewa;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

public class VEWAApp extends Application {

    private static Context context;
    private Activity mCurrentActivity = null;

    public void onCreate() {
        super.onCreate();
        VEWAApp.context = getApplicationContext();
    }

    public Activity getCurrentActivity(){
        return mCurrentActivity;
    }

    public void setCurrentActivity(Activity mCurrentActivity){
        this.mCurrentActivity = mCurrentActivity;
    }

    public static Context getAppContext() {
        return VEWAApp.context;
    }
}
