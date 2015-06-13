package ro.emag.hackaton.vewa.Api;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ro.emag.hackaton.vewa.Adapter.WishlistAdapter;
import ro.emag.hackaton.vewa.Entity.Product;
import ro.emag.hackaton.vewa.MainActivity;
import ro.emag.hackaton.vewa.R;

public class SearchProductTask extends AsyncTask<String, String, String> {

    // search products api url
    private static final String API_URL = "http://vewa.birkof.ro/api/search";

    private Activity activity;
    private ArrayList<Product> products;
    private ProgressDialog progressDialog;

    public SearchProductTask(Activity activity) {
        this.activity = activity;
        this.products = new ArrayList<Product>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Searching products ...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        String response = "";

        try {
            for (String param : params) {
                Log.d(getClass().getName(), param);
            }

            ApiRequest apiRequest = new ApiRequest(API_URL);
            apiRequest.addParam("term", params[0]);
            apiRequest.addParam("deviceId", params[1]);
            apiRequest.addParam("device", Build.MANUFACTURER + " " + Build.MODEL);
            apiRequest.addParam("max", "10");
            apiRequest.post();

            response = apiRequest.getResponse();

            Log.d(getClass().getName(), "Response Code: " + apiRequest.getResponseCode());
            Log.d(getClass().getName(), "Response: " + apiRequest.getResponse());

            if (!response.isEmpty()) {
                JSONObject jsonObj = new JSONObject(response);
                JSONObject data = jsonObj.getJSONObject("data");
                JSONObject resp = data.getJSONObject("response");
                JSONArray entries = resp.getJSONArray("entries");

                products.clear();

                if (entries.length() == 0) {
                    ((MainActivity) activity).sendMessageToWatch("Nu a fost gasit niciun produs.");
                }

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

                    if (entry.has("image"))
                        product.setImageLink(entry.getString("image"));

                    if (i == 0) {
                        ((MainActivity) activity).sendMessageToWatch(product.getProductName() + ", " + product.getProductPrice() + " lei");
                    }

                    Log.d(getClass().getName(), "Product image: " + product.getProductName());

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

        MainActivity mainActivity = (MainActivity) activity;

        progressDialog.dismiss();

        WishlistAdapter adapter = new WishlistAdapter(activity, products);
        ListView wordList = (ListView) activity.findViewById(R.id.word_list);
        wordList.setAdapter(adapter);

        try {
            ActionBar actionBar = mainActivity.getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        } catch (Exception e) {}
    }
}
