package ro.emag.hackaton.vewa.Api;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import ro.emag.hackaton.vewa.Helper.SpeechRecognitionHelper;

public class RemoveFromWishListTask extends AsyncTask<String, String, String> {

    // add to wishlist api url
    private static final String API_URL = "http://vewa.birkof.ro/api/remove_product";

    private Activity activity;
    private ProgressDialog progressDialog;

    public RemoveFromWishListTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Searching products ...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        super.onPreExecute();
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

    @Override
    protected void onPostExecute(String result) {
        progressDialog.dismiss();
        SpeechRecognitionHelper.showWishlist(activity);

        super.onPostExecute(result);
    }
}
