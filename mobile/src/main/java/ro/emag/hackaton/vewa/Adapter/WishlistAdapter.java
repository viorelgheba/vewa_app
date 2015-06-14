package ro.emag.hackaton.vewa.Adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ro.emag.hackaton.vewa.Entity.Product;
import ro.emag.hackaton.vewa.Listener.AddToWishListClickListener;
import ro.emag.hackaton.vewa.R;
import ro.emag.hackaton.vewa.Utils.ImageLoader;

public class WishlistAdapter extends BaseAdapter {

    private final Activity activity;
    private final List<Product> values;
    private ListView listView;
    private ImageLoader imageLoader;

    public WishlistAdapter(Activity activity, List<Product> objects, ListView listView) {
        this.activity = activity;
        this.values = objects;
        this.listView = listView;
        imageLoader = new ImageLoader(activity);
    }

    @Override
    public int getCount() {
        if (values == null) {
            return 0;
        }

        return values.size();
    }

    @Override
    public Object getItem(int position) {
        if (values == null) {
            return null;
        }

        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (values == null) {
            return 0;
        }

        return values.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.wish_list_item, parent, false);
        }

        ImageView productImage = (ImageView) rowView.findViewById(R.id.product_image);
        TextView productName = (TextView) rowView.findViewById(R.id.product_name);
        TextView productPrice = (TextView) rowView.findViewById(R.id.product_price);
        ImageButton imageButton = (ImageButton) rowView.findViewById(R.id.product_menu);

        if (values != null) {
            Product product = values.get(position);

            Log.d(getClass().getName(), "Product id: " + product.getId());
            Log.d(getClass().getName(), "Product image: " + product.getImageLink());
            Log.d(getClass().getName(), "Product name: " + product.getProductName());
            Log.d(getClass().getName(), "Product link: " + product.getProductLink());
            Log.d(getClass().getName(), "Product price: " + product.getProductPrice());

            productName.setText(product.getProductName());
            productPrice.setText(product.getProductPrice().toString() + " Lei");

            if (product.getImageLink() != null) {
                imageLoader.displayImage(product.getImageLink(), productImage);
            }

            imageButton.setOnClickListener(new AddToWishListClickListener(activity, listView, product));
        }

        return rowView;
    }

}
