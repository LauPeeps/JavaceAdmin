package com.example.javaceadminpanel;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.List;
import java.util.Map;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<CategoryModel> category_list;

    public CategoryAdapter(List<CategoryModel> category_list) {
        this.category_list = category_list;
    }


    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);



        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {

        String category_title = category_list.get(position).getName();
        holder.setData(category_title, position, this);

    }

    @Override
    public int getItemCount() {
        return category_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView categoryName;
        ImageView deleteBtn;
        Dialog progressDialog;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryName = itemView.findViewById(R.id.catname);
            deleteBtn = itemView.findViewById(R.id.deletebtn);

            progressDialog = new Dialog(itemView.getContext());
            progressDialog.setContentView(R.layout.loading_progressbar);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawableResource(R.drawable.progressbar_background);
            progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        private void setData(String title, int position, CategoryAdapter categoryAdapter) {

            categoryName.setText(title);

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog alertDialog = new AlertDialog.Builder(itemView.getContext())
                            .setTitle("Category Deletion")
                            .setMessage("Delete this category?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteCategory(position, itemView.getContext(), categoryAdapter);
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

        private void deleteCategory(final int id, Context context, CategoryAdapter categoryAdapter) {
            progressDialog.show();

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            Map<String, Object> category_doc = new ArrayMap<>();

            int index = 1;

            for (int i = 0; i < category_list.size(); i++) {
                if (i != id) {
                    category_doc.put("CATEGORY" + String.valueOf(index) + "_ID", category_list.get(i).getId());
                    category_doc.put("CATEGORY" + String.valueOf(index) + "_NAME", category_list.get(i).getName());
                    index++;
                }
            }

            category_doc.put("COUNT", index - 1);

            firestore.collection("QUIZ").document("Categories").set(category_doc)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            Toast.makeText(context, "Category deleted successfully", Toast.LENGTH_SHORT).show();

                            Category.category_list.remove(id);

                            categoryAdapter.notifyDataSetChanged();

                            progressDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

        }
    }
}
