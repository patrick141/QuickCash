package com.example.quickcash.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Notification class
 *
 * This class defines our notification.
 */
@ParseClassName("Notification")
public class Notification extends ParseObject {
    public static final String KEY_NOTIF_MESSAGE = "message";
    public static final String KEY_NOTIF_HASREAD = "hasRead";
    public static final String KEY_NOTIF_RECIEVE = "recipient";
    public static final String KEY_NOTIF_SENDER = "messenger";
    public static final String KEY_NOTIF_REFER_JOB = "referJob";
    public static final String KEY_NOTIF_REFER_REQ = "referRequest";


    public ParseUser getRecipient(){
        try {
            return fetchIfNeeded().getParseUser(KEY_NOTIF_RECIEVE);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setRecipient(ParseUser user){
        put(KEY_NOTIF_RECIEVE, user);
    }

    public ParseUser getSender(){
        try {
            return fetchIfNeeded().getParseUser(KEY_NOTIF_SENDER);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setSender(ParseUser user){
        put(KEY_NOTIF_SENDER, user);
    }

    public boolean hasRead(){
        return getBoolean(KEY_NOTIF_HASREAD);
    }

    public void setHasRead(boolean read){
        put(KEY_NOTIF_HASREAD, read);
    }

    public String getMessage(){
        return getString(KEY_NOTIF_MESSAGE);
    }

    public void setMessage(String message){
        put(KEY_NOTIF_MESSAGE, message);
    }

    public Job getJob(){
        return (Job) getParseObject(KEY_NOTIF_REFER_JOB);
    }

    public void setJob(Job job) {
        put(KEY_NOTIF_REFER_JOB, job);
    }

    public Request getRequest(){
        return (Request) getParseObject(KEY_NOTIF_REFER_REQ);
    }

    public void setRequest(Request request) {
        put(KEY_NOTIF_REFER_REQ, request);
    }

}
