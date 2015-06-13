package ro.emag.hackaton.vewa.Api;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONArray;

import ro.emag.hackaton.vewa.R;

public class ApiAsyncTask extends AsyncTask<String, String, String> {

    private Activity activity;

    public ApiAsyncTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        ProgressBar progressBar = (ProgressBar) activity.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... params) {
        String response = "";

        try {
            for (String param : params) {
                Log.d("ApiAsyncTask", param);
            }

            ApiRequest apiRequest = new ApiRequest();
            apiRequest.addParam("term", params[0]);
            apiRequest.addParam("deviceId", params[1]);
            apiRequest.addParam("device", Build.MANUFACTURER + " " + Build.MODEL);
            apiRequest.addParam("max", "0");
            apiRequest.sendRequest();

            response = apiRequest.getResponse();

            Log.d("ApiAsyncTask", "Response Code: " + apiRequest.getResponseCode());
            Log.d("ApiAsyncTask", "Response: " + apiRequest.getResponse());

            //JSONArray jsonArray = new JSONArray(apiRequest.getResponse());
            Thread.sleep(500);
        } catch (Exception e) {
            Log.d("ApiAsyncTask", "Error: " + e.getMessage());
        }

        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        ProgressBar progressBar = (ProgressBar) activity.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        //ListView listView = (ListView) activity.findViewById(R.id.word_list);
    }
}
