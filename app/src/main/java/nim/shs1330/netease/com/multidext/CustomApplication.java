package nim.shs1330.netease.com.multidext;

import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.util.Log;

/**
 * Created by shs1330 on 2018/1/8.
 */

public class CustomApplication extends Application {
    private static final String TAG = "CustomApplication";
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //MultiDex.install(base);
        Log.d(TAG, "attachBaseContext: " + Process.myPid());
    }
}
