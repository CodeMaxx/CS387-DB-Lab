package me.harshithgoka.socmed.Misc;

import android.app.Application;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import me.harshithgoka.socmed.Network.MyCookieStore;
import me.harshithgoka.socmed.Storage.UserStorage;

/**
 * Created by harshithgoka on 03/10/17.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        CookieManager cookieManager = new CookieManager(new MyCookieStore(getApplicationContext()), CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        UserStorage.Init(getApplicationContext());
    }
}
