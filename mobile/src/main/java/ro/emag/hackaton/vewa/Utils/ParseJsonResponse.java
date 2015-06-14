package ro.emag.hackaton.vewa.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ro.emag.hackaton.vewa.Entity.Product;

public class ParseJsonResponse {
    public static void parseProductsResponse(String response, ArrayList<Product> products) throws JSONException {
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

                products.add(product);
            }
        }
    }
}
