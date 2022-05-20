package com.example.myapplication.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.WindowManager;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.utils.UserRole;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends ActivityBase{

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    NavController navController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        navController=Navigation.findNavController(this,R.id.nav_host_fragment_content_main);
        appBarConfiguration=new AppBarConfiguration.Builder(R.id.nav_tickets_list,R.id.nav_projects_list,R.id.nav_users_list,R.id.nav_logout,R.id.nav_profile).setOpenableLayout(binding.drawerLayout).build();
        NavigationUI.setupActionBarWithNavController(this,navController,appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView,navController);
        //remove users management option if the user is not an admin

        if(getLoggedInUserRole().equals( UserRole.USER) || getLoggedInUserRole().equals( UserRole.SUPPORT_AGENT)){
            binding.navView.getMenu().removeItem(R.id.nav_users_list);
            binding.navView.getMenu().removeItem(R.id.nav_statistics);
            binding.navView.getMenu().removeItem(R.id.nav_projects_list);
        }
}


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        List<Integer> homes= Arrays.asList(R.id.nav_tickets_list,R.id.nav_projects_list,R.id.nav_users_list,R.id.nav_profile);
        int currentDesId=navController.getCurrentDestination().getId();
        if(item.getItemId() == android.R.id.home){
             if(homes.indexOf(currentDesId)>=0) {
                binding.drawerLayout.openDrawer(Gravity.LEFT);
                return true;
            }else {
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
    public  NavController getNavController(){
        return this.navController;
    }
}