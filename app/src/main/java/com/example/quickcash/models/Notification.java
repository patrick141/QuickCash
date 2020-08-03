package com.example.quickcash.models;

import android.content.Context;

import com.example.quickcash.R;
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

    public static final String SEND_REQUEST = "sendReq";
    public static final String APPROVED_USER = "approveUser";
    public static final String DENY_REQ = "denyReq";
    public static final String LEAVE_JOB = "leaveJob";
    public static final String JOB_DONE = "jobDone";


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

    /**
     * This methods creates a notification based off the parameters.
     * @param text
     * @param job
     * @param request
     * @param context
     * @return
     */
    public static Notification generateMyNotification(String text, Job job, Request request, Context context){
        Notification notification = new Notification();
        ParseUser currentUser = ParseUser.getCurrentUser();
        notification.setSender(currentUser);
        switch (text){
            case SEND_REQUEST:
                notification.setRecipient(job.getUser());
                String messenge = currentUser.getUsername() + " " + context.getString(R.string.notif_req)
                        + " " + job.getName();
                notification.setMessage(messenge);
                notification.setJob(job);
                notification.setRequest(request);
                break;
            case APPROVED_USER:
                notification.setRecipient(request.getUser());
                String messenge2 = currentUser.getUsername() + " " + context.getString(R.string.notif_approv)
                        + " " + job.getName();
                notification.setJob(job);
                notification.setRequest(request);
                notification.setMessage(messenge2);
                break;
            case JOB_DONE:
                notification.setRecipient(job.getUser());
                String messenge3 = currentUser.getUsername() + " " + context.getString(R.string.notif_finish)
                        + " " + job.getName();
                notification.setJob(job);
                notification.setMessage(messenge3);
                break;
            default:
                break;
        }
        return notification;
    }

}
