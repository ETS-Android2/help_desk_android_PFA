package com.example.myapplication.ui.fragments.ticket;

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
import com.example.myapplication.adapters.TicketAdapter;
import com.example.myapplication.databinding.FragmentTicketsListBinding;
import com.example.myapplication.models.TicketItem;
import com.example.myapplication.ui.fragments.BaseFragment;
import com.example.myapplication.utils.Constants;
import com.example.myapplication.ui.MainActivity;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import java.util.ArrayList;
import java.util.List;

public class TicketsListFragment extends BaseFragment {
    FragmentTicketsListBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=FragmentTicketsListBinding.inflate(inflater);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.tickets_list_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_add_ticket:
                Bundle data=new Bundle();
                data.putString(Constants.FRAGMENT_ACTION_KEY,Constants.ACTION_ADD);
                ((MainActivity)getActivity()).getNavController().navigate(R.id.add_edit_ticket,data);
                break;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTickets();
    }

    private void loadTickets(){
        getBaseActivity().showLoadingProgressDialog(true);
        String url=Constants.API_URL_BASE+"ticketsList/"+getBaseActivity().getLoggedInUserId();
        Ion.with(getActivity()).load("GET",url
                )
                .as(new TypeToken<List<TicketItem>>(){}).withResponse().setCallback(new FutureCallback<Response<List<TicketItem>>>() {
            @Override
            public void onCompleted(Exception e, Response<List<TicketItem>> result) {
                if(e!=null)
                {
                    e.printStackTrace();
                    getBaseActivity().showSnackBar("Error: "+e.getClass().getSimpleName());
                    getBaseActivity().showLoadingProgressDialog(false);
                    return;
                }
                if (result.getHeaders().code() == 200) {
                    List<TicketItem> res=result.getResult();
                    if(res==null)
                        res=new ArrayList<>();
                    binding.recyclerView.setAdapter(new TicketAdapter((MainActivity) getActivity(),res));
                }
                getBaseActivity().showLoadingProgressDialog(false);
            }
        });
    }
}
