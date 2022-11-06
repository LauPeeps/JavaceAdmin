package com.example.javaceadminpanel;

import static com.example.javaceadminpanel.Category.category_index;
import static com.example.javaceadminpanel.Category.category_list;
import static com.example.javaceadminpanel.Sets.idOfSets;
import static com.example.javaceadminpanel.Sets.set_index;

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

import java.util.List;
import java.util.Map;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.Viewholder> {

    private List<QuestionsModel> questionsModelList;

    public QuestionsAdapter(List<QuestionsModel> questionsModelList) {
        this.questionsModelList = questionsModelList;
    }

    @NonNull
    @Override
    public QuestionsAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionsAdapter.Viewholder holder, int position) {
        holder.setData(position, this);
    }

    @Override
    public int getItemCount() {
        return questionsModelList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        private TextView title;
        private ImageView deleteBtn;
        private Dialog progressDialog;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.catname);
            deleteBtn = itemView.findViewById(R.id.deletebtn);

            progressDialog = new Dialog(itemView.getContext());
            progressDialog.setContentView(R.layout.loading_progressbar);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawableResource(R.drawable.progressbar_background);
            progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        }

        private void setData(int pos, QuestionsAdapter questionsAdapter) {
            title.setText("Question " + String.valueOf(pos + 1));

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog alertDialog = new AlertDialog.Builder(itemView.getContext())
                            .setTitle("Question Deletion")
                            .setMessage("Delete this question?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteQuestion(pos, itemView.getContext(), questionsAdapter);
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
        private void deleteQuestion(int position, Context context, QuestionsAdapter questionsAdapter) {
            progressDialog.show();

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            firestore.collection("QUIZ").document(category_list.get(category_index).getId())
                    .collection(idOfSets.get(set_index)).document(questionsModelList.get(position).getQuestion_id())
                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Map<String, Object> question_doc = new ArrayMap<>();
                            int index = 1;
                            for (int i = 0; i < questionsModelList.size(); i++) {
                                if (i != position) {
                                    question_doc.put("Q" + String.valueOf(index) + "_ID", questionsModelList.get(i).getQuestion_id());
                                    index++;
                                }
                            }
                            question_doc.put("QNO", String.valueOf(index - 1));

                            firestore.collection("QUIZ").document(category_list.get(category_index).getId())
                                    .collection(idOfSets.get(set_index)).document("QUESTION_LIST")
                                    .set(question_doc)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(context, "Question Deleted!", Toast.LENGTH_SHORT).show();

                                            questionsModelList.remove(position);

                                            questionsAdapter.notifyDataSetChanged();

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
    }
}
