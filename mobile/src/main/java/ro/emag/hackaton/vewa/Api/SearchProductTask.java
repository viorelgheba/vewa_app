package ro.emag.hackaton.vewa.Api;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ro.emag.hackaton.vewa.Adapter.WishlistAdapter;
import ro.emag.hackaton.vewa.Entity.Product;
import ro.emag.hackaton.vewa.MainActivity;
import ro.emag.hackaton.vewa.R;

public class SearchProductTask extends AsyncTask<String, String, String> {

    private Activity activity;
    private ArrayList<Product> products;

    public SearchProductTask(Activity activity) {
        this.activity = activity;
        this.products = new ArrayList<Product>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        /*ProgressBar progressBar = (ProgressBar) activity.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);*/
    }

    @Override
    protected String doInBackground(String... params) {
        String response = "";

        try {
            for (String param : params) {
                Log.d(getClass().getName(), param);
            }

            ApiRequest apiRequest = new ApiRequest();
            apiRequest.addParam("term", params[0]);
            apiRequest.addParam("deviceId", params[1]);
            apiRequest.addParam("device", Build.MANUFACTURER + " " + Build.MODEL);
            apiRequest.addParam("max", "0");
            apiRequest.sendRequest();

            response = apiRequest.getResponse();

            Log.d(getClass().getName(), "Response Code: " + apiRequest.getResponseCode());
            Log.d(getClass().getName(), "Response: " + apiRequest.getResponse());

            if (!response.isEmpty()) {
                JSONObject jsonObj = new JSONObject(apiRequest.getResponse());
                JSONObject data = jsonObj.getJSONObject("data");
                JSONObject resp = data.getJSONObject("response");
                JSONArray entries = resp.getJSONArray("entries");

                products.clear();

                for (int i = 0; i < entries.length(); i++) {
                    JSONObject entry = entries.getJSONObject(i);

                    Product product = new Product();

                    if (entry.has("id"))
                        product.setId(entry.getInt("id"));

                    if (entry.has("title"))
                        product.setProductName(entry.getString("title"));

                    if (entry.has("link"))
                        product.setProductLink(entry.getString("link"));

                    if (entry.has("price"))
                        product.setProductPrice(entry.getDouble("price"));

                    if (entry.has("image_link"))
                        product.setImageLink(entry.getString("image_link"));

                ((MainActivity)activity).sendMessageToWatch(entry.getString("title"));
                    products.add(product);
                }
            } else {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(activity, "Oops - No product was found!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        } catch (Exception e) {
            Log.d(getClass().getName(), "Error: " + e.getMessage());
        }

        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        /*ProgressBar progressBar = (ProgressBar) activity.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);*/

        WishlistAdapter adapter = new WishlistAdapter(activity, products);
        ListView wordList = (ListView) activity.findViewById(R.id.word_list);
        wordList.setAdapter(adapter);
        //adapter.notifyDataSetChanged();
    }
}
