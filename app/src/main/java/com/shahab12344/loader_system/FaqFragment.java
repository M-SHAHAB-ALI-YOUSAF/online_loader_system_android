package com.shahab12344.loader_system;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FaqFragment extends Fragment {
    private RecyclerView recyclerView;

    private SessionManager sessionManager;
    private ParentAdapter parentAdapter;
    private List<QuestionAnswer> questionAnswers;
    public FaqFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_faq, container, false);

        //--------------------------------------------------------recycler view-----------------------------
        recyclerView = view.findViewById(R.id.recyclerViewforfaq);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //----------------------------------------------------------sesion manager-------------------
        sessionManager = new SessionManager(getContext());
        //--------------------------------------------- Create sample question-answer pairs
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



        //++++++++++++++++=back button++++++++++++++++++++++++++++++++++++++++++++
        ImageView backbutton = view.findViewById(R.id.faqBack);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sessionManager.getRole().equals("Driver")){
                    Fragment faq = new Driver_Homepage_Fragment();
                    FragmentTransaction faqtransaction = getFragmentManager().beginTransaction();
                    faqtransaction.replace(R.id.driver_fragment, faq);
                    faqtransaction.addToBackStack(null);
                    faqtransaction.commit();
                }
                else{
                    Fragment faq = new Dashbaord_Fragment();
                    FragmentManager faqManager = getFragmentManager();
                    FragmentTransaction faqtransaction = faqManager.beginTransaction();
                    faqtransaction.replace(R.id.bookingfragment, faq);
                    faqtransaction.addToBackStack(null);
                    faqtransaction.commit();
                }
            }
        });

        return view;
    }
}