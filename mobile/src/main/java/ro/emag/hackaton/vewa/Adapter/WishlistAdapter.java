package ro.emag.hackaton.vewa.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ro.emag.hackaton.vewa.Entity.Product;
import ro.emag.hackaton.vewa.R;
import ro.emag.hackaton.vewa.Utils.ImageLoader;

public class WishlistAdapter extends BaseAdapter {

    private final Context context;
    private final List<Product> values;
    private ImageLoader imageLoader;

    public WishlistAdapter(Context context, List<Product> objects) {
        this.context = context;
        this.values = objects;
        imageLoader = new ImageLoader(context);
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Object getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return values.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.wish_list_item, parent, false);
        }

        ImageView productImage = (ImageView) rowView.findViewById(R.id.product_image);
        TextView productName = (TextView) rowView.findViewById(R.id.product_name);
        TextView productPrice = (TextView) rowView.findViewById(R.id.product_price);

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

        return rowView;
    }

}
