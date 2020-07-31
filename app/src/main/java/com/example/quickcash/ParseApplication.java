package com.example.quickcash;

import android.app.Application;

import com.example.quickcash.models.Job;
import com.example.quickcash.models.Notification;
import com.example.quickcash.models.Request;
import com.parse.Parse;
import com.parse.ParseObject;

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

        /**
         * This is to monitor our Parse network traffic
         */
        // Use for monitoring Parse network traffic
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
    }
}
