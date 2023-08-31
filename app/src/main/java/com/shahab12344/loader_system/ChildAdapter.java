package com.shahab12344.loader_system;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ChildViewHolder> {

    private List<String> answers;

    public ChildAdapter(List<String> answers) {
        this.answers = answers;
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.faq_child_row, parent, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {
        String answer = answers.get(position);
        holder.childText.setText(answer);
    }

    @Override
    public int getItemCount() {
        return answers.size();
    }

    static class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView childText;

        ChildViewHolder(View itemView) {
            super(itemView);
            childText = itemView.findViewById(R.id.childText);
        }
    }
}

