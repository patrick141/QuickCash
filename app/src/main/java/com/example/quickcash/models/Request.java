package com.example.quickcash.models;

import com.parse.ParseClassName;
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
        return getParseUser(KEY_REQUEST_USER);
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

    public ParseObject getJob(){
        return getParseObject(KEY_REQUEST_JOB);
    }

    public void setJob(ParseObject job){
        put(KEY_REQUEST_JOB, job);
    }
}
