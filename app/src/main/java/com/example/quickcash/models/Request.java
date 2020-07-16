package com.example.quickcash.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Request")
public class Request extends ParseObject {
    public static final String KEY_REQUEST_USER = "user";
    public static final String KEY_REQUEST_COMMENT = "comment";

}
