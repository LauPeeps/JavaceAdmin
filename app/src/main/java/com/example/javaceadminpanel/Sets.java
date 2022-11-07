package com.example.javaceadminpanel;

import static com.example.javaceadminpanel.Category.category_index;
import static com.example.javaceadminpanel.Category.category_list;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.collection.ArrayMap;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Sets extends AppCompatActivity {

    RecyclerView setsView;
    Button addSetBtn;
    SetsAdapter adapter;
    FirebaseFirestore firestore;
    Dialog progressDialog;

    public static List<String> idOfSets = new ArrayList<>();
    public static int set_index = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sets);

        Toolbar toolbar = findViewById(R.id.setstoolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Sets");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setsView = findViewById(R.id.sets_recycler);
        addSetBtn = findViewById(R.id.addSetBtn);

        progressDialog = new Dialog(Sets.this);
        progressDialog.setContentView(R.layout.loading_progressbar);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(R.drawable.progressbar_background);
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);




        addSetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addSet();
            }
        });

        firestore = FirebaseFirestore.getInstance();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        setsView.setLayoutManager(linearLayoutManager);

        fetchSets();




    }

    private void fetchSets() {

        idOfSets.clear();

        progressDialog.show();

        firestore.collection("QUIZ").document(category_list.get(category_index).getId())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        long noOfSets = (long) documentSnapshot.get("SETS");

                        for (int i = 1; i <= noOfSets; i++) {
                            idOfSets.add(documentSnapshot.getString("SET" + String.valueOf(i) + "_ID"));

                        }
                        category_list.get(category_index).setSetBase(documentSnapshot.getString("BASE"));
                        category_list.get(category_index).setNoOfSets(String.valueOf(noOfSets));

                        adapter = new SetsAdapter(idOfSets);
                        setsView.setAdapter(adapter);

                        progressDialog.dismiss();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(Sets.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        
                    }
                });


    }

    private void addSet() {
        progressDialog.show();

        String current_category_id = category_list.get(category_index).getId();
        String current_questionNo = category_list.get(category_index).getSetBase();

        Map<String, Object> question_data = new ArrayMap<>();
        question_data.put("QNO", "0");

        firestore.collection("QUIZ").document(current_category_id)
                .collection(current_questionNo).document("QUESTION_LIST")
                .set(question_data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Map<String, Object> category_doc = new ArrayMap<>();
                        category_doc.put("BASE", String.valueOf(Integer.parseInt(current_questionNo) + 1));
                        category_doc.put("SET" + String.valueOf(idOfSets.size() + 1) + "_ID", current_questionNo);
                        category_doc.put("SETS", idOfSets.size() + 1);


                        firestore.collection("QUIZ").document(current_category_id)
                                .update(category_doc)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(Sets.this, "Set added successfully", Toast.LENGTH_SHORT).show();
                                        idOfSets.add(current_questionNo);
                                        category_list.get(category_index).setNoOfSets(String.valueOf(idOfSets.size()));
                                        category_list.get(category_index).setSetBase(String.valueOf(Integer.parseInt(current_questionNo) + 1));

                                        adapter.notifyItemInserted(idOfSets.size());
                                        progressDialog.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e){

                                        Toast.makeText(Sets.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();

                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(Sets.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}