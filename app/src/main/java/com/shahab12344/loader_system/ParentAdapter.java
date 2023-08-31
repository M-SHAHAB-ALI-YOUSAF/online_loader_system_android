package com.shahab12344.loader_system;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ParentAdapter extends RecyclerView.Adapter<ParentAdapter.ParentViewHolder> {

    private List<QuestionAnswer> questionAnswers;
    private boolean[] expandedStates;

    public ParentAdapter(List<QuestionAnswer> questionAnswers) {
        this.questionAnswers = questionAnswers;
        this.expandedStates = new boolean[questionAnswers.size()];
    }

    @NonNull
    @Override
    public ParentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.faq_parent_row, parent, false);
        return new ParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParentViewHolder holder, int position) {
        QuestionAnswer qa = questionAnswers.get(position);
        holder.parentTitle.setText(qa.getQuestion());

        // Set click listener to toggle answer visibility
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickedPosition = holder.getAdapterPosition();
                expandedStates[clickedPosition] = !expandedStates[clickedPosition];
                notifyItemChanged(clickedPosition);
            }
        });

        // Show/hide childRecyclerView based on expanded state
        if (expandedStates[position]) {
            holder.childRecyclerView.setVisibility(View.VISIBLE);
        } else {
            holder.childRecyclerView.setVisibility(View.GONE);
        }

        ChildAdapter childAdapter = new ChildAdapter(qa.getAnswers());
        holder.childRecyclerView.setAdapter(childAdapter);
    }

    @Override
    public int getItemCount() {
        return questionAnswers.size();
    }

    static class ParentViewHolder extends RecyclerView.ViewHolder {
        TextView parentTitle;
        RecyclerView childRecyclerView;

        ParentViewHolder(View itemView) {
            super(itemView);
            parentTitle = itemView.findViewById(R.id.parentTitle);
            childRecyclerView = itemView.findViewById(R.id.childRecyclerView);
            childRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }
    }
}
