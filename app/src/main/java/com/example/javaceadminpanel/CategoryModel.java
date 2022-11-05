package com.example.javaceadminpanel;

public class CategoryModel {

    private String id,name, noOfSets, setBase;



    public CategoryModel(String id, String name, String noOfSets, String setBase) {
        this.id = id;
        this.name = name;
        this.noOfSets = noOfSets;
        this.setBase = setBase;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNoOfSets() {
        return noOfSets;
    }

    public void setNoOfSets(String noOfSets) {
        this.noOfSets = noOfSets;
    }

    public String getSetBase() {
        return setBase;
    }

    public void setSetBase(String setBase) {
        this.setBase = setBase;
    }
}
