package ro.emag.hackaton.vewa.Api;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import ro.emag.hackaton.vewa.Adapter.WishlistAdapter;
import ro.emag.hackaton.vewa.Entity.Product;
import ro.emag.hackaton.vewa.R;
import ro.emag.hackaton.vewa.Utils.ParseJsonResponse;

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
                ParseJsonResponse.parseProductsResponse(response, products);
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
