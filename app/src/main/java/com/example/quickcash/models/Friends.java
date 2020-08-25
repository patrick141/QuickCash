package com.example.quickcash.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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
    public static final String KEY_FRIEND_PENDING = "friendsPending";
    public static final String KEY_FRIEND_REQUEST = "friendsRequests";

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

    public ArrayList<ParseUser> getFriendsList(){
        try {
            return (ArrayList<ParseUser>) fetchIfNeeded().get(KEY_FRIEND_LIST);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setFriendList(ArrayList<ParseUser> friendList){
        put(KEY_FRIEND_LIST, friendList);
    }

    public ArrayList<ParseUser> getFriendsPendingList(){
        try {
            return (ArrayList<ParseUser>) fetchIfNeeded().get(KEY_FRIEND_PENDING);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setPendingList(ArrayList<ParseUser> users){
        put(KEY_FRIEND_PENDING, users);
    }

    public ArrayList<ParseUser> getFriendRequests(){
        try {
            return (ArrayList<ParseUser>) fetchIfNeeded().get(KEY_FRIEND_REQUEST);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setFriendRequests(ArrayList<ParseUser> users){
        put(KEY_FRIEND_REQUEST, users);
    }

    public void addFriend(ParseUser otherUser){
        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.add(KEY_FRIEND_LIST, otherUser);
        removeAll(KEY_FRIEND_REQUEST, Arrays.asList(otherUser));
        try {
            Friends userFriends = (Friends) otherUser.fetchIfNeeded().get(User.KEY_USER_FRIENDS);
            userFriends.add(KEY_FRIEND_LIST, currentUser);
            userFriends.removeAll(KEY_FRIEND_PENDING, Arrays.asList(currentUser));
            userFriends.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e != null){
                        e.printStackTrace();
                    }
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void removeFriend(ParseUser otherUser){
        ParseUser currentUser = ParseUser.getCurrentUser();
        removeAll(KEY_FRIEND_LIST, Arrays.asList(otherUser));
        try {
            Friends friends = (Friends) otherUser.fetchIfNeeded().get(User.KEY_USER_FRIENDS);
            friends.removeAll(KEY_FRIEND_LIST, Arrays.asList(currentUser));
            friends.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e != null){
                        e.printStackTrace();
                    }
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void sendFriendRequest(ParseUser otherUser){
        ParseUser currentUser = ParseUser.getCurrentUser();
        add(KEY_FRIEND_PENDING, otherUser);
        try {
            Friends friends = (Friends) otherUser.fetchIfNeeded().get(User.KEY_USER_FRIENDS);
            friends.add(KEY_FRIEND_REQUEST, currentUser);
            friends.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e != null){
                        e.printStackTrace();
                    }
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
