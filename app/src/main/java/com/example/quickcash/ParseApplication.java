package com.example.quickcash;

import android.app.Application;

import com.example.quickcash.models.Job;
import com.example.quickcash.models.Request;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //Registering our Job Class
        ParseObject.registerSubclass(Job.class);
        ParseObject.registerSubclass(Request.class);

        //This is calling our Heroku Parse DB
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("patrick-quickcash")
                .clientKey("CodepathMoveFaster")
                .server("https://patrick-quickcash.herokuapp.com/parse").build());
    }
}
