package com.example.quickcash.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Payment")
public class Payment extends ParseObject {
    public static final String KEY_BUYER = "buyer";
    public static final String KEY_RECIEVER = "recipient";
    public static final String KEY_HAS_PAID = "paymentFinished";
    public static final String KEY_REFER_JOB = "job";
    public static final String KEY_AMOUNT = "amount";

    public ParseUser getBuyer(){
        try {
            return fetchIfNeeded().getParseUser(KEY_BUYER);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setBuyer(ParseUser buyer){
        put(KEY_BUYER, buyer);
    }

    public ParseUser getRecipient(){
        try {
            return fetchIfNeeded().getParseUser(KEY_RECIEVER);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setRecipient(ParseUser recipient){
        put(KEY_RECIEVER, recipient);
    }

    public boolean hasPaid(){
        return getBoolean(KEY_HAS_PAID);
    }

    public void setHasPaid(boolean paid){
        put(KEY_HAS_PAID, paid);
    }

    public Double getAmount(){
        return getDouble(KEY_AMOUNT);
    }

    public void setAmount(double amount){
        put(KEY_AMOUNT, amount);
    }

    public Job getJob(){
        return (Job) getParseObject(KEY_REFER_JOB);
    }

    public void setJob(Job job){
        put(KEY_REFER_JOB, job);
    }

}
