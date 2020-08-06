package com.example.quickcash.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Friends
 *
 * The Friends class holds our friends list for each user.
 */
@ParseClassName("Friends")
public class Friends extends ParseObject {
    public static final String KEY_USER = "user";
    public static final String KEY_FRIEND_LIST = "friendsList";

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

    public ArrayList<ParseUser> getFriendsList(){
        return (ArrayList<ParseUser>) get(KEY_FRIEND_LIST);
    }

    public void setFriendList(ArrayList<ParseUser> friendList){
        put(KEY_FRIEND_LIST, friendList);
    }

    public void addFriend(ParseUser user){
        add(KEY_FRIEND_LIST, user);
    }

    public void removeFriend(ParseUser user){
        removeAll(KEY_FRIEND_LIST, Arrays.asList(user));
    }
}
