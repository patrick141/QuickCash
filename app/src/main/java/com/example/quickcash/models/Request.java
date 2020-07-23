package com.example.quickcash.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Request class
 *
 * This class defines our Request class and its attributes.
 */
@ParseClassName("Request")
public class Request extends ParseObject {
    public static final String KEY_REQUEST_USER = "user";
    public static final String KEY_REQUEST_COMMENT = "comment";
    public static final String KEY_REQUEST_JOB = "jobPost";

    public ParseUser getUser() {
        try{
            return fetchIfNeeded().getParseUser(KEY_REQUEST_USER);
        }
        catch(ParseException e){
            e.printStackTrace();
        }
        return null;
    }

    public void setUser(ParseUser user) {
        put(KEY_REQUEST_USER, user);
    }

    public String getComment(){
        return getString(KEY_REQUEST_COMMENT);
    }

    public void setComment(String comment){
        put(KEY_REQUEST_COMMENT, comment);
    }

    public Job getJob(){
        return (Job) getParseObject(KEY_REQUEST_JOB);
    }

    public void setJob(Job job){
        put(KEY_REQUEST_JOB, job);
    }
}
