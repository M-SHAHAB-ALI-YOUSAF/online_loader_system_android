package com.shahab12344.loader_system;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class PaymentMethod extends AppCompatActivity {

    PaymentSheet paymentSheet;
    private ProgressDialog progressDialog;

    String paymentintent;
    PaymentSheet.CustomerConfiguration customerConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..."); // Set the message for the progress dialog
        progressDialog.setCancelable(false); // Make it not cancellable


        // Initialize the PaymentSheet
        paymentSheet = new PaymentSheet(this, this::onPaymentResult);
        // Fetch payment information from your API
        fetchapi();
    }

    private void onPaymentResult(final PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
        }
        if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        }
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
            review_and_rating fragment = new review_and_rating();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.feedbackarea, fragment);
            transaction.commit();
        }
    }

    public void fetchapi() {
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://10.0.2.2/stripe/index.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            customerConfiguration = new PaymentSheet.CustomerConfiguration(
                                    jsonObject.getString("customer"),
                                    jsonObject.getString("ephemeralKey")
                            );
                            paymentintent = jsonObject.getString("paymentIntent");
                            PaymentConfiguration.init(getApplication(), jsonObject.getString("publishableKey"));

                            // Present the payment sheet immediately after fetching data
                            paymentSheet.presentWithPaymentIntent(paymentintent, new PaymentSheet.Configuration("Cargo Go"));

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplication(), "Volley error", Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("authkey", "abc");
                paramV.put("paymentAmount", String.valueOf("10000")); // Add payment amount
                return paramV;
            }
        };
        queue.add(stringRequest);
    }
}
