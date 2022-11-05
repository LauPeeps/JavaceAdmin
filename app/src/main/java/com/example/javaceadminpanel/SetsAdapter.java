package com.example.javaceadminpanel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

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
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return setIds.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        private TextView setName;
        private ImageView setDelete;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            setName = itemView.findViewById(R.id.catname);
            setDelete = itemView.findViewById(R.id.deletebtn);
        }
        private void setData(int pos) {

            setName.setText("SET" + String.valueOf(pos + 1));
        }
    }
}
