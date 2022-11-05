package com.example.javaceadminpanel;

import static com.example.javaceadminpanel.Category.category_index;
import static com.example.javaceadminpanel.Category.category_list;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;
import java.util.Map;

public class SetsAdapter extends RecyclerView.Adapter<SetsAdapter.Viewholder> {

    private List<String> setIds;

    public SetsAdapter(List<String> setIds) {
        this.setIds = setIds;
    }

    @NonNull
    @Override
    public SetsAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);


        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SetsAdapter.Viewholder holder, int position) {

        String setID = setIds.get(position);
        holder.setData(position, setID, this);
    }

    @Override
    public int getItemCount() {
        return setIds.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        private TextView setName;
        private ImageView setDelete;
        Dialog progressDialog;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            setName = itemView.findViewById(R.id.catname);
            setDelete = itemView.findViewById(R.id.deletebtn);

            progressDialog = new Dialog(itemView.getContext());
            progressDialog.setContentView(R.layout.loading_progressbar);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawableResource(R.drawable.progressbar_background);
            progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        private void setData(int pos, String setID, SetsAdapter setsAdapter) {

            setName.setText("SET " + String.valueOf(pos + 1));


            setDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog alertDialog = new AlertDialog.Builder(itemView.getContext())
                            .setTitle("Category Deletion")
                            .setMessage("Delete this category?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteSet(pos, setID, itemView.getContext(), setsAdapter);
                                }
                            }).setNegativeButton("Cancel", null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                    alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.RED);
                    alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.GRAY);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0,5, 35, 5);
                    alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setLayoutParams(params);
                    alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setLayoutParams(params);
                }
            });
        }
            private void deleteSet(int pos, String setID, Context context, SetsAdapter setsAdapter) {
                progressDialog.show();

                FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                firestore.collection("QUIZ").document(category_list.get(category_index).getId()).collection(setID)
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                WriteBatch writeBatch = firestore.batch();

                                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                    writeBatch.delete(queryDocumentSnapshot.getReference());
                                }
                                writeBatch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                       Map<String, Object> category_doc = new ArrayMap<>();
                                       int index = 1;

                                       for (int i = 0; i < setIds.size(); i++) {
                                           if (i != pos) {
                                               category_doc.put("SET" + String.valueOf(index) + "_ID", setIds.get(i));
                                               index++;
                                           }
                                       }
                                       category_doc.put("SETS", index - 1);
                                       firestore.collection("QUIZ").document(category_list.get(category_index).getId())
                                               .update(category_doc)
                                               .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                   @Override
                                                   public void onSuccess(Void unused) {

                                                       Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();

                                                       Sets.idOfSets.remove(pos);

                                                       category_list.get(category_index).setNoOfSets(String.valueOf(Sets.idOfSets.size()));

                                                       setsAdapter.notifyDataSetChanged();
                                                       progressDialog.dismiss();
                                                   }
                                               }).addOnFailureListener(new OnFailureListener() {
                                                   @Override
                                                   public void onFailure(@NonNull Exception e) {
                                                       Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                       progressDialog.dismiss();
                                                   }
                                               });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });
            }
        }
}
