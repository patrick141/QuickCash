package com.example.quickcash.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Request")
public class Request extends ParseObject {
    public static final String KEY_NAME = "name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_USER = "user";
    public static final String KEY_ISTAKEN = "isTaken";
    public static final String KEY_PRICE = "price";
    public static final String KEY_TASK_IMAGE = "image";

    public String getName(){
        return getString(KEY_NAME);
    }

    public void setName(String name){
        put(KEY_NAME, name);
    }

    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String name){
        put(KEY_DESCRIPTION, name);
    }

    public String getAddress(){
        return getString(KEY_ADDRESS);
    }

    public void setAddress(String name){
        put(KEY_ADDRESS, name);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

    public boolean isTaken(){
        return getBoolean(KEY_ISTAKEN);
    }

    public void setIsTaken(boolean bool){
        put(KEY_ISTAKEN, bool);
    }

    public Double getPrice(){
        return getDouble(KEY_PRICE);
    }

    public void setPrice(double price){
        put(KEY_PRICE, price);
    }

    public ParseFile getImage(){
        return getParseFile(KEY_TASK_IMAGE);
    }

    public void setImage(ParseFile image){
        put(KEY_TASK_IMAGE, image);
    }
}
