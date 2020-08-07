package com.example.quickcash.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;

/**
 * Job class
 *
 * This class define our Job class and its attributes.
 */

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
    public static final String KEY_JOB_DATE = "dateOfJob";
    public static final String KEY_JOB_ASSIGNED_USER = "assignedUser";
    public static final String KEY_JOB_ISFINISHED = "isFinished";
    public static final String KEY_JOB_LOCATION = "location";
    public static final String KEY_JOB_PAYMENT = "payment";

    public String getName(){
        try {
            return fetchIfNeeded().getString(KEY_JOB_NAME);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
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
        try {
            return fetchIfNeeded().getParseUser(KEY_JOB_USER);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setUser(ParseUser user){
        put(KEY_JOB_USER, user);
    }

    public Date getJobDate() {
        return getDate(KEY_JOB_DATE);
    }

    public void setJobDate(Date date) {
        put(KEY_JOB_DATE, date);
    }

    public boolean isTaken(){
        return getBoolean(KEY_JOB_ISTAKEN);
    }

    public void setIsTaken(boolean taken){
        put(KEY_JOB_ISTAKEN, taken);
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

    public ArrayList<Request> getRequests(){
        return (ArrayList<Request>) get(KEY_JOB_REQUESTS);
    }

    public void setJobRequests(ArrayList<Request> requests) {
        put(KEY_JOB_REQUESTS, requests);
    }

    public void addJobRequest(Request request){
        add(KEY_JOB_REQUESTS, request);
    }

    public ParseUser getAssignedUser() {
        try {
            return fetchIfNeeded().getParseUser(KEY_JOB_ASSIGNED_USER);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setAssignedUser(ParseUser user) {
        put(KEY_JOB_ASSIGNED_USER, user);
    }

    public boolean isFinished() {
        try {
            return fetchIfNeeded().getBoolean(KEY_JOB_ISFINISHED);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setIsFinished(boolean finished){
        put(KEY_JOB_ISFINISHED, finished);
    }

    public ParseGeoPoint getLocation(){
        return getParseGeoPoint(KEY_JOB_LOCATION);
    }

    public void setLocation(ParseGeoPoint location){
        put(KEY_JOB_LOCATION, location);
    }

    public int getJobRequestCount(){
        if(getRequests() == null){
            return 0;
        } else{
            return getRequests().size();
        }
    }

    public Payment getPayment(){
        return (Payment) getParseObject(KEY_JOB_PAYMENT);
    }

    public void setPayment(Payment payment){
        put(KEY_JOB_PAYMENT, payment);
    }
}
