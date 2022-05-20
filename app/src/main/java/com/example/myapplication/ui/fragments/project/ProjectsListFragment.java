package com.example.myapplication.ui.fragments.project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapters.ProjectsAdapter;
import com.example.myapplication.adapters.TicketAdapter;
import com.example.myapplication.databinding.FragmentProjectsListBinding;
import com.example.myapplication.models.ProjectItem;
import com.example.myapplication.models.ProjectItem;
import com.example.myapplication.models.TicketItem;
import com.example.myapplication.ui.MainActivity;
import com.example.myapplication.ui.fragments.BaseFragment;
import com.example.myapplication.utils.Constants;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import java.util.ArrayList;
import java.util.List;

public class ProjectsListFragment extends BaseFragment {
    FragmentProjectsListBinding binding;
    TicketItem selectedTicket;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= FragmentProjectsListBinding.inflate(inflater);
        if(getArguments()!=null && getArguments().containsKey(Constants.SELECTED_ITEM_KEY))
            selectedTicket= (TicketItem) getArguments().getSerializable(Constants.SELECTED_ITEM_KEY);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }



    @Override
    public void onResume() {
        super.onResume();
        loadTickets();

    }

    private void loadTickets(){
        getBaseActivity().showLoadingProgressDialog(true);
        String url=Constants.API_URL_BASE+"projectList";
        Ion.with(getActivity()).load("GET",url
                )
                .as(new TypeToken<ArrayList<ProjectItem>>(){}).withResponse().setCallback(new FutureCallback<Response<ArrayList<ProjectItem>>>() {
            @Override
            public void onCompleted(Exception e, Response<ArrayList<ProjectItem>> result) {
                if(e!=null)
                {
                    e.printStackTrace();
                    getBaseActivity().showSnackBar("Error: "+e.getClass().getSimpleName());
                    getBaseActivity().showLoadingProgressDialog(false);
                    return;
                }
                if (result.getHeaders().code() == 200) {
                    List<ProjectItem> projectItems=result.getResult();
                    if(selectedTicket!=null){
                    for(int i=0;i<projectItems.size(); i++ ){
                        if(!projectItems.get(i).getId_ticket().equals(selectedTicket.getId()))
                               projectItems.remove(i);                             ;
                    }}
                    if(projectItems==null)
                        projectItems=new ArrayList<>();
                   binding.recyclerView.setAdapter(new ProjectsAdapter((MainActivity) getActivity(),projectItems));
                }
                getBaseActivity().showLoadingProgressDialog(false);
            }
        });
    }
}
