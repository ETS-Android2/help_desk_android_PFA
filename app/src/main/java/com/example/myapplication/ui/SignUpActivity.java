package com.example.myapplication.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.example.myapplication.databinding.ActivitySignupScreenBinding;
import com.example.myapplication.utils.Constants;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUpActivity extends ActivityBase {
    ActivitySignupScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivitySignupScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFormValide()) {
                    Log.i(TAG, "sign up clicked");
                    showLoadingProgressDialog(true);
                    startSignUpRequest();
                }
            }});

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }
    private  boolean isFormValide(){
        return true;
    }

    private void openLogIn(){
        Intent intent = new  Intent (this,LoginActivity.class);
        startActivity(intent);
        this.finish();
    }
    private void startSignUpRequest(){
        Ion.getDefault(this).build(this).load("POST",
                Constants.API_SIGN_UP)
                .addQuery("email",binding.email.getText().toString())
                .addQuery("username",binding.username.getText().toString())
                .addQuery("password",binding.passtxt.getText().toString())
                .addQuery("role_as","3")
                .addQuery("phone_number",binding.phone.getText().toString())
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {

                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        if(e!=null)
                        {
                            e.printStackTrace();
                            showSnackBar("error:"+e.getCause());
                            return;
                        }
                        if (result.getHeaders().code() == 200) {
                            try {
                                JSONObject res = new JSONObject(result.getResult());
                                if (res.getString("message").equals("User Created")) {
                                    //success
                                    openLogIn();
                                    showSnackBar("account created you can now login!");
                                    log("signup success !");
                                } else {
                                    showSnackBar(res.getString("message"));
                                    log("error:" + res.getString("message"));
                                }
                            } catch (JSONException jsonException) {
                                jsonException.printStackTrace();
                                showSnackBar("error:" + jsonException.toString());
                            }catch (Exception ex){
                                ex.printStackTrace();
                                showSnackBar("error:" + ex.getMessage());
                            }
                        }
                        showLoadingProgressDialog(false);

                    }
                });
    }


}