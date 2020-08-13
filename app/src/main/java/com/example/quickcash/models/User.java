package com.example.quickcash.models;

import com.parse.ParseObject;

/**
 * This class is to hold variable constants as we cannot extend ParseUser.
 */
public class User extends ParseObject {
    public static final String KEY_USER_RATING = "userRating";
    public static final String KEY_USER_PHONE = "phoneNumber";
    public static final String KEY_USER_IMAGE = "profilePic";
    public static final String KEY_USER_JOBS = "myJobs";
    public static final String KEY_USER_DESCRIPTION = "description";
    public static final String KEY_USER_COMPLETED_JOBS = "completedJobs";
    public static final String KEY_USER_FRIENDS = "friends";
}
