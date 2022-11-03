package com.example.javaceadminpanel;

public class CategoryModel {

    private String id,name;
    private int sets;


    public CategoryModel(String id, String name, int sets) {
        this.id = id;
        this.name = name;
        this.sets = sets;
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

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }
}
