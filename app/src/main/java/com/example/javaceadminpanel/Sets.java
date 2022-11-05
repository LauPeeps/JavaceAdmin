package com.example.javaceadminpanel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class Sets extends AppCompatActivity {

    RecyclerView setsView;
    Button addSetBtn;
    SetsAdapter adapter;

    public static List<String> idOfSets = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sets);

        setsView = findViewById(R.id.sets_recycler);
        addSetBtn = findViewById(R.id.addSetBtn);



        addSetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        setsView.setLayoutManager(linearLayoutManager);

        fetchSets();




    }

    private void fetchSets() {

        idOfSets.clear();

        idOfSets.add("A");
        idOfSets.add("B");
        idOfSets.add("C");

        adapter = new SetsAdapter(idOfSets);
        setsView.setAdapter(adapter);

    }
}