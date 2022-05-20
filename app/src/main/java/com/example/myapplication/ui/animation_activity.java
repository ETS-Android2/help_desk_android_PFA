package com.example.myapplication.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.utils.UserRole;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import java.sql.Struct;

public class animation_activity extends ActivityBase{

    private static  int welcome_screen=4000;

    //Animation variables
    Animation logoanim,txtanim;
    ImageView logo;
    TextView txt1,txt2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_animation);



        //calling The animations

        logoanim= AnimationUtils.loadAnimation(this,R.anim.logo_animation);
        txtanim= AnimationUtils.loadAnimation(this,R.anim.text_animation);


        //assign value to image and txt variables


        logo=(ImageView) findViewById(R.id.imageView2);
        txt1=(TextView) findViewById(R.id.textView2);
        txt2=(TextView) findViewById(R.id.textView3);


        //Assign animations
        logo.setAnimation(logoanim);
        txt1.setAnimation(txtanim);
        txt2.setAnimation(txtanim);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isLoggedIn()){
                    openMainActivity();
                }else
                    openLoginScreen();
                animation_activity.this.finish();
            }
        },welcome_screen);
    }
    private void openMainActivity(){
        Intent intent = new  Intent (animation_activity.this,MainActivity.class);
        startActivity(intent);
        this.finish();
    }
    public void openLoginScreen(){
        Intent intent=new Intent(this,MainActivity.class);
        if(!isLoggedIn()) {
            intent = new Intent(animation_activity.this, LoginActivity.class);
            Pair[] pairs=new Pair[3];
            pairs[0]=new Pair<View,String>(logo,"logo_image");
            pairs[1]=new Pair<View,String>(txt1,"logo_title");
            pairs[2]=new Pair<View,String>(txt2,"logo_soustitle");
            ActivityOptions options=ActivityOptions.makeSceneTransitionAnimation(animation_activity.this,pairs);
            startActivity(intent,options.toBundle());
        }else
            startActivity(intent);
            finish();
    }



}