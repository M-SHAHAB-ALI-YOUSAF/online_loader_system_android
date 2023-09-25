package com.shahab12344.loader_system;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class review_and_rating extends Fragment {

    private RatingBar ratingBar;
    private EditText reviewEditText;
    private Button submitButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review_and_rating, container, false);

        ratingBar = view.findViewById(R.id.ratingBar);
        reviewEditText = view.findViewById(R.id.reviewEditText);
        submitButton = view.findViewById(R.id.submitButton);

        TextInputLayout textInputLayout = view.findViewById(R.id.review);
        TextInputEditText textInputEditText = view.findViewById(R.id.reviewEditText);

        textInputLayout.setHint("Enter Review"); // Set hint for the TextInputLayout


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float rating = ratingBar.getRating();
                String review = reviewEditText.getText().toString().trim();

                if (rating < 2) {
                    ComplaintFragment fragment = new ComplaintFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.feedbackarea, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else {
                    if (review.isEmpty()) {
                        Toast.makeText(getContext(), "Please provide a review.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Review submitted successfully!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        return view;
    }
}
