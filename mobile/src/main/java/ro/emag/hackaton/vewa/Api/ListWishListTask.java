package ro.emag.hackaton.vewa.Api;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ro.emag.hackaton.vewa.Adapter.WishlistAdapter;
import ro.emag.hackaton.vewa.Entity.Product;
import ro.emag.hackaton.vewa.MainActivity;
import ro.emag.hackaton.vewa.R;

public class ListWishListTask extends AsyncTask<String, String, String> {

    // add to wishlist api url
    private static final String API_URL = "http://vewa.birkof.ro/api/wishlist";

    private Activity activity;
    private ArrayList<Product> products;
    private ProgressDialog progressDialog;

    public ListWishListTask(Activity activity) {
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
            ApiRequest apiRequest = new ApiRequest(API_URL);
            apiRequest.addParam("deviceId", params[0]);
            apiRequest.addParam("device", Build.MANUFACTURER + " " + Build.MODEL);
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

                        if (i == 0) {
                            ((MainActivity) activity).sendMessageToWatch(entry.getString("title"));
                        }

                        Log.d(getClass().getName(), "Product id: " + product.getId());
                        Log.d(getClass().getName(), "Product image: " + product.getImageLink());
                        Log.d(getClass().getName(), "Product name: " + product.getProductName());
                        Log.d(getClass().getName(), "Product link: " + product.getProductLink());
                        Log.d(getClass().getName(), "Product price: " + product.getProductPrice());

                        products.add(product);
                    }
                }
            }
        } catch (Exception e) {
            Log.d(getClass().getName(), "Error: " + e.getMessage());
        }

        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        progressDialog.dismiss();

        ListView searchList = (ListView) activity.findViewById(R.id.search_list);
        searchList.setVisibility(View.INVISIBLE);
        activity.unregisterForContextMenu(searchList);

        ListView wishList = (ListView) activity.findViewById(R.id.wish_list);
        wishList.setVisibility(View.VISIBLE);
        activity.registerForContextMenu(wishList);

        WishlistAdapter adapter = new WishlistAdapter(activity, products, wishList);
        wishList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        adapter = new WishlistAdapter(activity, new ArrayList<Product>(), searchList);
        searchList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        super.onPostExecute(result);
    }

}
