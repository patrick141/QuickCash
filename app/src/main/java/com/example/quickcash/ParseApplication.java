package com.example.quickcash;

import android.app.Application;

import com.example.quickcash.models.Friends;
import com.example.quickcash.models.Job;
import com.example.quickcash.models.Notification;
import com.example.quickcash.models.Payment;
import com.example.quickcash.models.Request;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.facebook.ParseFacebookUtils;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * ParseApplication
 *
 * This class initializes our ParseDB into our Application
 *
 * @author Patrick Amaro Rivera
 */
public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /**Registering our Job and Request class so we
         * can query posts.
         */
        ParseObject.registerSubclass(Job.class);
        ParseObject.registerSubclass(Request.class);
        ParseObject.registerSubclass(Notification.class);
        ParseObject.registerSubclass(Friends.class);
        ParseObject.registerSubclass(Payment.class);

        /**
         * This is to monitor our Parse network traffic
         */
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);

        /**This is calling our Heroku Parse DB based off of
         * our parameters.
         */
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("patrick-quickcash")
                .clientBuilder(builder)
                .clientKey("CodepathMoveFaster")
                .server("https://patrick-quickcash.herokuapp.com/parse").build());

        /**
         * This enables us to use the Facebook SDK and ParseFacebook SDK to log in via Facebook.
         */
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        ParseFacebookUtils.initialize(this);
    }
}
