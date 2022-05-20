package com.example.myapplication.ui.fragments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.ui.ActivityBase;
import com.example.myapplication.ui.MainActivity;

public class BaseFragment extends Fragment {


    public void setTitle(String title){
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(title);
    }
    public ActivityBase getBaseActivity(){
        return (ActivityBase) getActivity();
    }
}
