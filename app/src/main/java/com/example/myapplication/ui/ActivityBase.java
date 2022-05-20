package com.example.myapplication.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.utils.UserRole;
import com.google.android.material.snackbar.Snackbar;

public class ActivityBase extends AppCompatActivity {
   private static final String LOGGED_IN_USER_ROLE_ID_KEY = "user_role";
   protected static String TAG="TEST";
   private ProgressDialog progressDialog;
   SharedPreferences mSharedPref;
   private final String LOGGED_IN_USER_ID_KEY="user_id";
   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
       mSharedPref = getSharedPreferences(
              getString(R.string.preference_file_key), Context.MODE_PRIVATE);
   }

   public void showLoadingProgressDialog(boolean b){
      if(!b && progressDialog!=null){
         progressDialog.dismiss();
      }else{
         progressDialog =new ProgressDialog(this);
         progressDialog.setTitle("");
         progressDialog.setMessage("Loading ...");
         progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
         progressDialog.setCancelable(false);
         progressDialog.show();
      }
   }

   public void log(String message){
      Log.i(TAG,message);
   }
   public void login(String id,int role){
      mSharedPref.edit().putString(LOGGED_IN_USER_ID_KEY,id).putString(LOGGED_IN_USER_ROLE_ID_KEY, String.valueOf(role)).commit();
   }
   public String getLoggedInUserId() throws IllegalStateException {
      if(!isLoggedIn())
         throw new IllegalStateException("you can't read user id before logging in!");
      return mSharedPref.getString(LOGGED_IN_USER_ID_KEY,null);
   }
   protected void logOut(){

      mSharedPref.edit().remove(LOGGED_IN_USER_ID_KEY).commit();
      mSharedPref.edit().remove(LOGGED_IN_USER_ROLE_ID_KEY).commit();

   }

   public boolean isLoggedIn(){
      return mSharedPref.contains(LOGGED_IN_USER_ID_KEY);
   }
   public String getLoggedInUserRole(){
      String role=mSharedPref.getString(LOGGED_IN_USER_ROLE_ID_KEY, String.valueOf(UserRole.USER));
      return role;
   }
   public void showSnackBar(String msg){
      Snackbar sb=Snackbar.make(this.findViewById(android.R.id.content)
              ,msg,Snackbar.LENGTH_SHORT);
      sb.setAction(R.string.got_it, new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            sb.dismiss();
         }
      });
      sb.setActionTextColor(getResources().getColor(R.color.purple_200));
      //sb.setTextColor(getResources().getColor(R.color.color_danger));
      sb.show();
   }
}
