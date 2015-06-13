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

import ro.emag.hackaton.vewa.Api.DownloadImageTask;
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

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.wish_list_item, parent);
        }

        ImageView productImage = (ImageView) rowView.findViewById(R.id.product_image);
        TextView productName = (TextView) rowView.findViewById(R.id.product_name);
        TextView productPrice = (TextView) rowView.findViewById(R.id.product_price);

        Product product = values.get(position);

        Log.d("WishlistAdapter", "Product id: " + product.getId());
        Log.d("WishlistAdapter", "Product image: " + product.getImageLink());
        Log.d("WishlistAdapter", "Product name: " + product.getProductName());
        Log.d("WishlistAdapter", "Product link: " + product.getProductLink());
        Log.d("WishlistAdapter", "Product price: " + product.getProductPrice());

        productName.setText(product.getProductName());
        productPrice.setText(product.getProductPrice().toString());

        if (product.getImageLink() != null) {
            DownloadImageTask downloadImageTask = new DownloadImageTask(productImage);
            //downloadImageTask.execute(product.getImageLink());
            downloadImageTask.execute("http://s3emagst.akamaized.net/products/984/983970/images/res_ed0c5304e7b524aad16926084caad4a8_350x350c_9da6.jpg");
        }

        return rowView;
    }

}
