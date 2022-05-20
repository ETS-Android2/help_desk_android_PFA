package com.example.myapplication.models;

import java.io.Serializable;

public class TicketItem implements Serializable {

    String id;
    String name_ticket;
    String description_ticket;
    String date_ticket;
    String id_project;
    String assign_to;
    String date_of_assignment;
    String status="0";


    public String getStatus() {
        return status;
    }


    public String isStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate_of_assignment() {
        return date_of_assignment;
    }

    public void setDate_of_assignment(String date_of_assignment) {
        this.date_of_assignment = date_of_assignment;
    }

    public String getAssign_to() {
        return assign_to;
    }

    public void setAssign_to(String assign_to) {
        this.assign_to = assign_to;
    }

    public TicketItem(){
        id ="0";
        id_project ="0";
    }
    public boolean hasProject(){
        return !id_project.equals("0");
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_project() {
        return id_project;
    }

    public void setId_project(String id_project) {
        this.id_project = id_project;
    }

    public String getName_ticket() {
        return name_ticket;
    }

    public void setName_ticket(String name_ticket) {
        this.name_ticket = name_ticket;
    }

    public String getDescription_ticket() {
        return description_ticket;
    }

    public void setDescription_ticket(String description_ticket) {
        this.description_ticket = description_ticket;
    }

    public String getDate_ticket() {
        return date_ticket;
    }

    public void setDate_ticket(String date_ticket) {
        this.date_ticket = date_ticket;
    }
}
