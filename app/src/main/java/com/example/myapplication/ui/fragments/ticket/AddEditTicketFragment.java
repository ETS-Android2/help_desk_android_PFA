package com.example.myapplication.ui.fragments.ticket;

import androidx.annotation.NonNull;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;

import com.example.myapplication.R;
import com.example.myapplication.adapters.ProjectsAdapter;
import com.example.myapplication.databinding.AddEditTicketFragBinding;
import com.example.myapplication.models.ProjectItem;
import com.example.myapplication.models.TicketItem;
import com.example.myapplication.models.UsersItem;
import com.example.myapplication.ui.MainActivity;
import com.example.myapplication.ui.fragments.BaseFragment;
import com.example.myapplication.utils.Constants;
import com.example.myapplication.utils.UserRole;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddEditTicketFragment extends BaseFragment implements View.OnClickListener {
    String TAG=AddEditTicketFragment.class.getSimpleName();
    AddEditTicketFragBinding binding;
    String mAction;
    TicketItem selectedTicketItem;
    List<UsersItem> agents=new ArrayList<>();
    UsersItem assinedToUser;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding=AddEditTicketFragBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.saveBtn.setOnClickListener(this);

        Log.v(TAG,"action "+getArguments().get(Constants.FRAGMENT_ACTION_KEY));
        mAction=getArguments().getString(Constants.FRAGMENT_ACTION_KEY);
        if(mAction.equals(Constants.ACTION_ADD)){
            setTitle(getString(R.string.add_ticket));
            //ticket id is only visible if the ticket already exists
            binding.ticketIdEt.setVisibility(View.GONE);
            //you cannot add a task to a project unless it's already created
            binding.projectBtn.setVisibility(View.GONE);
            binding.statusParent.setVisibility(View.GONE);

            binding.ticketAssignedToSpinner.setVisibility(View.GONE);

        }else if(mAction.equals(Constants.ACTION_EDIT)){
            selectedTicketItem= (TicketItem) getArguments().getSerializable(Constants.SELECTED_ITEM_KEY);
            if(UserRole.ADMIN.equals(getBaseActivity().getLoggedInUserRole())) {
                loadUsersListForSpinner();
            }else if(UserRole.SUPPORT_AGENT.equals(getBaseActivity().getLoggedInUserRole())){
                binding.ticketAssignedToSpinner.setVisibility(View.GONE);
                binding.saveBtn.setVisibility(View.GONE);
            }
            boolean hasProject=selectedTicketItem.hasProject();
            Log.e("ERR","test");
            binding.projectBtn.setText(hasProject?"manage project":"add project");
            binding.projectBtn.setOnClickListener(this);
            //edit an already existing ticket
            binding.ticketStatusCheckBox.setChecked(Boolean.parseBoolean(selectedTicketItem.getStatus()));
            binding.ticketIdEt.setText(""+selectedTicketItem.getId());
            binding.ticketDateEt.setText(selectedTicketItem.getDate_ticket());
            binding.ticketNameEt.setText(selectedTicketItem.getName_ticket());
            binding.ticketDescriptionEt.setText(selectedTicketItem.getDescription_ticket());
        }

    }

    @Override
    public void onClick(View view) {
        if(view.getId()==binding.projectBtn.getId()) {
            if(!selectedTicketItem.hasProject()) {
                Bundle b=new Bundle();
                b.putSerializable(Constants.SELECTED_ITEM_KEY,selectedTicketItem);
                b.putString(Constants.FRAGMENT_ACTION_KEY,Constants.ACTION_ADD);
                ((MainActivity) getBaseActivity()).getNavController().navigate(R.id.action_add_project_for_ticket,b);
            }else{
                loadProjectForThisTicket();
            }
        }else if(view.getId()==binding.saveBtn.getId()){
            if(mAction.equals(Constants.ACTION_ADD)){
                getBaseActivity().showLoadingProgressDialog(true);
                Ion.with(this).load("POST",
                        Constants.API_URL_BASE+"addticket")
                        .addQuery("name_ticket",binding.ticketNameEt.getText().toString())
                        .addQuery("description_ticket",binding.ticketDescriptionEt.getText().toString())
                        .addQuery("user_id",getBaseActivity().getLoggedInUserId())
                        .asString()
                        .withResponse()
                        .setCallback(new FutureCallback<Response<String>>() {

                            @Override
                            public void onCompleted(Exception e, Response<String> result) {
                                if(e!=null)
                                {
                                    getBaseActivity().log("error:"+e.getCause()+" !");
                                    getBaseActivity().showLoadingProgressDialog(false);
                                    e.printStackTrace();
                                    return;
                                }
                                if (result.getHeaders().code() == 200) {
                                    getBaseActivity().showSnackBar("ticket added !");
                                    getBaseActivity().onBackPressed();
                                }else{
                                    getBaseActivity().showSnackBar("failed to add add ticket !");
                                }

                                getBaseActivity().showLoadingProgressDialog(false);

                            }
                        });
            }else if(mAction.equals(Constants.ACTION_EDIT)){
                TicketItem ticketToSend=new TicketItem();
                ticketToSend=new Gson().fromJson(new Gson().toJson(selectedTicketItem),TicketItem.class);
                ticketToSend.setDate_ticket(binding.ticketDateEt.getText().toString());
                ticketToSend.setDescription_ticket(binding.ticketDescriptionEt.getText().toString());
                ticketToSend.setName_ticket(binding.ticketNameEt.getText().toString());
                String assignedTo="";
                int index=agents.indexOf(binding.ticketAssignedToSpinner.getSelectedItem());
                if(index==-1){
                    assignedTo="0";

                }else
                    assignedTo=agents.get(index).getId();
                    ticketToSend.setAssign_to(assignedTo);
                    ticketToSend.setDate_ticket(binding.ticketDateEt.getText().toString());
                    ticketToSend.setDate_of_assignment(Calendar.getInstance().getTime().toString().split(" ")[0]);
                    ticketToSend.setStatus(String.valueOf(binding.ticketStatusCheckBox.isChecked()));
                    Ion.with(getActivity()).load("PATCH",
                            Constants.API_URL_BASE+"updateticket/"+ticketToSend.getId()).addHeader("accept","application/json").addHeader("Content-Type","application/json")
                            .setJsonPojoBody(ticketToSend)
                            .asJsonObject().withResponse().setCallback(new FutureCallback<Response<JsonObject>>() {
                        @Override
                        public void onCompleted(Exception e, Response<JsonObject> result) {
                            if(e!=null)
                            {
                                e.printStackTrace();
                                getBaseActivity().showSnackBar("Error: "+e.getClass().getSimpleName());
                                getBaseActivity().showLoadingProgressDialog(false);
                                return;
                            }
                            if (result.getHeaders().code() == 200) {
                                //so changes are saved
                                getBaseActivity().showSnackBar("ticket changes saved!");
                            }else {
                                  getBaseActivity().showSnackBar("failed to save changes");
                            }
                            getBaseActivity().showLoadingProgressDialog(false);
                        }
                    });
            }
        }

        }

    private void loadProjectForThisTicket() {
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
                    ProjectItem project=null;
                    if(selectedTicketItem!=null){
                        for(int i=0;i<projectItems.size(); i++ ){
                            if(!projectItems.get(i).getId().equals(selectedTicketItem.getId_project())) {
                                project = projectItems.get(i);
                                break;
                            }
                        }}
                    if(project!=null){
                        Bundle b=new Bundle();
                        b.putSerializable(Constants.SELECTED_ITEM_KEY,project);
                        b.putString(Constants.FRAGMENT_ACTION_KEY,Constants.ACTION_EDIT);
                        ((MainActivity) getBaseActivity()).getNavController().navigate(R.id.action_edit_project_for_ticket,b);
                    }else{
                        getBaseActivity().log("project not found for this ticket!");
                    }
                }else
                    getBaseActivity().log("project not found for this ticket!");
                getBaseActivity().showLoadingProgressDialog(false);
            }
        });
    }

    private void loadUsersListForSpinner(){
        getBaseActivity().showLoadingProgressDialog(true);
        Ion.with(getActivity()).load("GET",
                Constants.API_URL_BASE+"usersList")
                .as(new TypeToken<List<UsersItem>>(){}).withResponse().setCallback(new FutureCallback<Response<List<UsersItem>>>() {
            @Override
            public void onCompleted(Exception e, Response<List<UsersItem>> result) {
                if(e!=null)
                {
                    e.printStackTrace();
                    getBaseActivity().showSnackBar("Error: "+e.getClass().getSimpleName());
                    getBaseActivity().showLoadingProgressDialog(false);
                    return;
                }
                if (result.getHeaders().code() == 200) {
                    List<UsersItem> users=result.getResult();
                    agents=new ArrayList<UsersItem>();
                    UsersItem noUserItem=new UsersItem();
                    noUserItem.setName("Not selected");
                    noUserItem.setId("0");
                    agents.add(noUserItem);
                    for(UsersItem u: users){
                        if(u.getRole_as().equals(UserRole.SUPPORT_AGENT)){
                            agents.add(u);
                            if(u.getId().equals(selectedTicketItem.getAssign_to()))
                                assinedToUser=u;
                                ;
                        }
                    }
                    ArrayAdapter ad
                            = new ArrayAdapter(getBaseActivity(),
                            android.R.layout.simple_spinner_item, agents)
                    ;ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                   binding.ticketAssignedToSpinner.setAdapter( ad);
                   if(assinedToUser!=null)
                   binding.ticketAssignedToSpinner.setSelection(agents.indexOf(assinedToUser));
                    else
                       binding.ticketAssignedToSpinner.setSelection(agents.indexOf(0));

                }
                getBaseActivity().showLoadingProgressDialog(false);
            }
        });



    }

    @Override
    public void onPause() {
        super.onPause();
    }
}

