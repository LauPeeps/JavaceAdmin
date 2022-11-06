package com.example.javaceadminpanel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

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
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return questionsModelList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        private TextView title;
        private ImageView deleteBtn;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.catname);
            deleteBtn = itemView.findViewById(R.id.deletebtn);
        }

        private void setData(int pos) {
            title.setText("Question " + String.valueOf(pos + 1));
        }
    }
}
