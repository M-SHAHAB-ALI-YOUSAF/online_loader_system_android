package com.shahab12344.loader_system;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Faq extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ParentAdapter parentAdapter;
    private List<QuestionAnswer> questionAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create sample question-answer pairs
        questionAnswers = new ArrayList<>();
        questionAnswers.add(new QuestionAnswer("How do I make a booking?", Arrays.asList("You can make a booking by selecting the desired service and date, then providing your details.")));
        questionAnswers.add(new QuestionAnswer("Can I modify my booking?", Arrays.asList("Yes, you can modify your booking through your account or by contacting customer support.")));
        questionAnswers.add(new QuestionAnswer("What payment methods are accepted?", Arrays.asList("We accept major credit and debit cards.", "Mobile payment methods like Apple Pay and Google Pay are also accepted.")));
        questionAnswers.add(new QuestionAnswer("Is my personal information secure?", Arrays.asList("Yes, we take your privacy seriously.", "We use advanced security measures to protect your data.")));
        questionAnswers.add(new QuestionAnswer("Can I cancel a booking?", Arrays.asList("Yes, you can cancel a booking before the cancellation deadline.", "Please refer to our cancellation policy for details.")));
        questionAnswers.add(new QuestionAnswer("How do I know if my booking is confirmed?", Arrays.asList("You will receive a confirmation email and/or SMS with the details of your booking.")));
        questionAnswers.add(new QuestionAnswer("Do you offer refunds?", Arrays.asList("Refund policies vary depending on the type of service and the timing of the cancellation.", "Check our refund policy for more details.")));
        questionAnswers.add(new QuestionAnswer("Can I book multiple services at once?", Arrays.asList("Yes, you can add multiple services to your booking before finalizing it.")));
        questionAnswers.add(new QuestionAnswer("Is there a loyalty program?", Arrays.asList("Yes, we offer a loyalty program that rewards frequent customers with discounts and special offers.")));
        questionAnswers.add(new QuestionAnswer("How far in advance can I book?", Arrays.asList("Booking windows vary depending on the type of service.", "Some services can be booked months in advance.")));
        questionAnswers.add(new QuestionAnswer("How do I know if my booking is confirmed?", Arrays.asList("You will receive a confirmation email and/or SMS with the details of your booking.")));
        questionAnswers.add(new QuestionAnswer("Do you offer refunds?", Arrays.asList("Refund policies vary depending on the type of service and the timing of the cancellation.", "Check our refund policy for more details.")));
        questionAnswers.add(new QuestionAnswer("Can I book multiple services at once?", Arrays.asList("Yes, you can add multiple services to your booking before finalizing it.")));
        questionAnswers.add(new QuestionAnswer("Is there a loyalty program?", Arrays.asList("Yes, we offer a loyalty program that rewards frequent customers with discounts and special offers.")));
        questionAnswers.add(new QuestionAnswer("How far in advance can I book?", Arrays.asList("Booking windows vary depending on the type of service.", "Some services can be booked months in advance.")));

        parentAdapter = new ParentAdapter(questionAnswers);
        recyclerView.setAdapter(parentAdapter);
    }
}
