package ro.emag.hackaton.vewa.Api;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ro.emag.hackaton.vewa.Adapter.WishlistAdapter;
import ro.emag.hackaton.vewa.Entity.Product;
import ro.emag.hackaton.vewa.Helper.SpeechRecognitionHelper;
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
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Searching products ...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        super.onPreExecute();
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

            if (!response.isEmpty() && apiRequest.getResponseCode() == 200) {
                JSONObject jsonObj = new JSONObject(response);
                JSONObject data = jsonObj.getJSONObject("data");
                JSONObject resp = data.getJSONObject("response");
                JSONArray entries = resp.getJSONArray("entries");

                products.clear();

                if ((entries.length() == 0)&&(params[2].equals("wear"))) {
                    ((MainActivity) activity).sendMessageToWatch("Nu a fost gasit niciun produs.");
                }

                if (entries.length() > 0) {
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

                        if ((i == 0)&&(params[2].equals("wear"))) {
                            ((MainActivity) activity).sendMessageToWatch(product.getProductName()+", "+product.getProductPrice()+" lei");
                            SpeechRecognitionHelper.addToWishlist(activity, product.getId());
                        }

                        Log.d(getClass().getName(), "Product id: " + product.getId());
                        Log.d(getClass().getName(), "Product image: " + product.getImageLink());
                        Log.d(getClass().getName(), "Product name: " + product.getProductName());
                        Log.d(getClass().getName(), "Product link: " + product.getProductLink());
                        Log.d(getClass().getName(), "Product price: " + product.getProductPrice());

                        products.add(product);
                    }
                } else {
                    noProductsFound();
                }
            } else {
                noProductsFound();
            }
        } catch (Exception e) {
            Log.d(getClass().getName(), "Error: " + e.getMessage());
        }

        return response;
    }

    protected void noProductsFound() {
        activity.runOnUiThread(new Runnable() {
            public void run() {
            Toast.makeText(activity, "Oops - No product was found!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onPostExecute(String result) {
        progressDialog.dismiss();

        ListView wishList = (ListView) activity.findViewById(R.id.wish_list);
        wishList.setVisibility(View.INVISIBLE);
        activity.unregisterForContextMenu(wishList);

        ListView searchList = (ListView) activity.findViewById(R.id.search_list);
        searchList.setVisibility(View.VISIBLE);
        activity.registerForContextMenu(searchList);

        WishlistAdapter adapter = new WishlistAdapter(activity, products, searchList);
        searchList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        try {
            MainActivity mainActivity = (MainActivity) activity;
            ActionBar actionBar = mainActivity.getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        } catch (Exception e) {}

        super.onPostExecute(result);
    }
}
