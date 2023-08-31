package com.shahab12344.loader_system;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

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

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float rating = ratingBar.getRating();
                String review = reviewEditText.getText().toString().trim();

                if (rating == 0 || review.isEmpty()) {
                    Toast.makeText(getContext(), "Please provide a rating and review.", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle the submission of the review and feedback here
                    // For example, you can send the data to a server or save it locally.
                    Toast.makeText(getContext(), "Review submitted successfully!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
