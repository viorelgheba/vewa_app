package ro.emag.hackaton.vewa.Api;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

public class AddToWishListTask extends AsyncTask<String, String, String> {
    @Override
    protected String doInBackground(String... params) {
        String response = "";

        try {
            ApiRequest apiRequest = new ApiRequest();
            apiRequest.addParam("deviceId", params[1]);
            apiRequest.addParam("device", Build.MANUFACTURER + " " + Build.MODEL);
            apiRequest.addParam("productId", params[0]);
            apiRequest.sendRequest();

            Log.d(getClass().getName(), "Response Code: " + apiRequest.getResponseCode());
            Log.d(getClass().getName(), "Response: " + apiRequest.getResponse());
        } catch (Exception e) {
            Log.d(getClass().getName(), "Error: " + e.getMessage());
        }

        return response;
    }
}
