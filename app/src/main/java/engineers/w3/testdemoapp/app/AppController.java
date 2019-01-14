package engineers.w3.testdemoapp.app;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
/**
 * Created by Md. Imtiyaj on 1/13/2019.
 */
public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();
    public static final String BaseUrl = "https://parseapi.back4app.com/";
    public static final String EndUrl = "functions/items";
    private static Context sContext;
    private RequestQueue mRequestQueue;
    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        sContext = getApplicationContext();
    }

    public static Context getContext() {
        return sContext;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public static final String getItemUrl() {
        return BaseUrl + EndUrl;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }
}
