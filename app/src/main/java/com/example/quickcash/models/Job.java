package com.example.quickcash.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

@ParseClassName("Job")
public class Job extends ParseObject {
    public static final String KEY_JOB_NAME = "name";
    public static final String KEY_JOB_DESCRIPTION = "description";
    public static final String KEY_JOB_ADDRESS = "address";
    public static final String KEY_JOB_USER = "user";
    public static final String KEY_JOB_ISTAKEN = "isTaken";
    public static final String KEY_JOB_PRICE = "price";
    public static final String KEY_JOB_IMAGE = "image";
    public static final String KEY_JOB_REQUESTS = "requests";
    public static final String KEY_JOB_CREATEDAT = "createdAt";

    public String getName(){
        return getString(KEY_JOB_NAME);
    }

    public void setName(String name){
        put(KEY_JOB_NAME, name);
    }

    public String getDescription(){
        return getString(KEY_JOB_DESCRIPTION);
    }

    public void setDescription(String name){
        put(KEY_JOB_DESCRIPTION, name);
    }

    public String getAddress(){
        return getString(KEY_JOB_ADDRESS);
    }

    public void setAddress(String name){
        put(KEY_JOB_ADDRESS, name);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_JOB_USER);
    }

    public void setUser(ParseUser user){
        put(KEY_JOB_USER, user);
    }

    public boolean isTaken(){
        return getBoolean(KEY_JOB_ISTAKEN);
    }

    public void setIsTaken(boolean bool){
        put(KEY_JOB_ISTAKEN, bool);
    }

    public Double getPrice(){
        return getDouble(KEY_JOB_PRICE);
    }

    public void setPrice(double price){
        put(KEY_JOB_PRICE, price);
    }

    public ParseFile getImage(){
        return getParseFile(KEY_JOB_IMAGE);
    }

    public void setImage(ParseFile image){
        put(KEY_JOB_IMAGE, image);
    }

    public ArrayList<Request> getRequests(){ return (ArrayList<Request>) get(KEY_JOB_REQUESTS);}

    public void setJobRequests(ArrayList<Request> requests) {put(KEY_JOB_REQUESTS, requests);}

    public void addJobRequest(ParseUser user){ add(KEY_JOB_REQUESTS, user.getObjectId());}
}
