package com.example.quickcash;

import android.app.Application;

import com.example.quickcash.models.Request;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Request.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("patrick-quickcash")
                .clientKey("CodepathMoveFaster")
                .server("https://patrick-quickcash.herokuapp.com/parse").build());
    }
}
