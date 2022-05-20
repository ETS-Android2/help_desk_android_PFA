package com.example.myapplication.models;

import java.io.Serializable;

public class ProjectItem  implements Serializable {
    String id;
    String name_project, description_project,status,id_ticket;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId_ticket() {
        return id_ticket;
    }

    public void setId_ticket(String id_ticket) {
        this.id_ticket = id_ticket;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName_project() {
        return name_project;
    }

    public void setName_project(String name_project) {
        this.name_project = name_project;
    }

    public String getDescription_project() {
        return description_project;
    }

    public void setDescription_project(String description_project) {
        this.description_project = description_project;
    }

}
