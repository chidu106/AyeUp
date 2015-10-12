package org.ayeup.rest;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Represents an Patient")
public class Patient {

    private int id;
    private String name;

    public Patient() {
    }

    public Patient(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @ApiModelProperty(value = "The id of the Patient resource", required = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ApiModelProperty(value = "The name of the patient", required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

