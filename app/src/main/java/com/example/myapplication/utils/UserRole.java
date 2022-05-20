package com.example.myapplication.utils;

public class UserRole {
    public static final String ADMIN="1";
    public static final String SUPPORT_AGENT="2";
    public static final String USER="3";
    public static String asString(String role){
        String str="";
        if(role.equals(USER) )
                str="Client";
        else if(role.equals( ADMIN))
                str="admin";
        else if(role.equals(SUPPORT_AGENT))
                str="support";
        else throw  new IllegalStateException("role value no allowed!");
        return str;
    }
}
