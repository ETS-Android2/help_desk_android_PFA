package com.example.myapplication.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import com.example.myapplication.databinding.ActivityLoginScreenBinding;
import com.example.myapplication.utils.Constants;
import com.example.myapplication.utils.UserRole;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends ActivityBase {
    ActivityLoginScreenBinding binding;
       @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
           binding= ActivityLoginScreenBinding.inflate(getLayoutInflater());
           setContentView(binding.getRoot());
           logOut();
           binding.validerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoadingProgressDialog(true);
                startLoginRequest();

            }
        });

        binding.registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(i);
                finish();
            }
        });

    }
    private void openMainActivity(){
        Intent intent = new  Intent (LoginActivity.this,MainActivity.class);
        startActivity(intent);
        this.finish();
    }
    private void startLoginRequest() {
               Ion.getDefault(this).build(this).load("GET",
                       Constants.API_LOGIN + binding.username.getText().toString()
                               + "/" + binding.passtxt.getText()
                               .toString())
                       .asString().withResponse()
                       .setCallback(new FutureCallback<Response<String>>() {

                           @Override
                           public void onCompleted(Exception e, Response<String> result) {
                               if(e!=null)
                               {
                                   e.printStackTrace();
                                   showSnackBar("Error: "+e.getClass().getSimpleName());
                                   showLoadingProgressDialog(false);
                                   return;
                               }
                               if (result.getHeaders().code() == 200) {
                                   try {
                                       JSONObject o = new JSONObject(result.getResult());
                                       if(!o.has("Info"))
                                       {
                                           showSnackBar("invalide credantials !");
                                           showLoadingProgressDialog(false);
                                            return;

                                       }
                                       JSONObject res = o.getJSONObject("Info");
                                       if (res.getInt("STATUS") == 1) {
                                           //log in success
                                           String userId=res.getString("user_id");
                                           int role=res.getInt("user_role");
                                           login(userId,role);
                                           openMainActivity();
                                       } else {
                                           //informations are wrong
                                           showLoadingProgressDialog(false);
                                           showSnackBar(res.getString("message"));
                                       }
                                   } catch (JSONException jsonException) {
                                       showLoadingProgressDialog(false);
                                       jsonException.printStackTrace();
                                       showSnackBar("error:" + jsonException.toString());
                                   }catch (Exception ex){
                                       showLoadingProgressDialog(false);

                                       ex.printStackTrace();
                                   }
                               }
                               showLoadingProgressDialog(false);
                           }
                       });


    }

}