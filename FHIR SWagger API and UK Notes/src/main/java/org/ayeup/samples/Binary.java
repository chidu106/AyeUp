package org.ayeup.samples;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Represents an Binary resource")
public class Binary {

    private int id;
    private String name;

    public Binary() {
    }

    public Binary(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @ApiModelProperty(value = "The id of the Binary resource", required = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ApiModelProperty(value = "The name of the Binary document", required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

