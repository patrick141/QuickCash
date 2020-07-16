package com.example.quickcash;

import android.app.Application;

import com.example.quickcash.models.Job;
import com.example.quickcash.models.Request;
import com.parse.Parse;
import com.parse.ParseObject;
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

        /**This is calling our Heroku Parse DB based off of
         * our parameters.
         */
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("patrick-quickcash")
                .clientKey("CodepathMoveFaster")
                .server("https://patrick-quickcash.herokuapp.com/parse").build());
    }
}
