package ro.emag.hackaton.vewa.Api;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.ImageButton;

import ro.emag.hackaton.vewa.R;

public class AddToWishListTask extends AsyncTask<String, String, String> {

    // add to wishlist api url
    private static final String API_URL = "http://vewa.birkof.ro/api/add_product";

    private Activity activity;

    public AddToWishListTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... params) {
        String response = "";

        Log.d(getClass().getName(), "Product ID: " + params[0]);

        try {
            ApiRequest apiRequest = new ApiRequest(API_URL);
            apiRequest.addParam("productId", params[0]);
            apiRequest.addParam("deviceId", params[1]);
            apiRequest.addParam("device", Build.MANUFACTURER + " " + Build.MODEL);
            apiRequest.post();

            Log.d(getClass().getName(), "Response Code: " + apiRequest.getResponseCode());
            Log.d(getClass().getName(), "Response: " + apiRequest.getResponse());
        } catch (Exception e) {
            Log.d(getClass().getName(), "Error: " + e.getMessage());
        }

        return response;
    }
}
