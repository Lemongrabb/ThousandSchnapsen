import android.app.Application;
import android.content.Context;

import com.example.thousandschnapsen.DbSchnapsen.DbManager;

public class SchnapsenApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        DbManager.setConfig(context);
    }

    public static Context getContext() {
        return context;
    }
}