package ro.emag.hackaton.vewa.Listener;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import ro.emag.hackaton.vewa.Entity.Product;
import ro.emag.hackaton.vewa.MainActivity;
import ro.emag.hackaton.vewa.R;

public class AddToWishListClickListener implements OnClickListener {

    private Activity activity;
    private ListView listView;
    private Product product;

    public AddToWishListClickListener(Activity activity, ListView listView, Product product) {
        this.activity = activity;
        this.listView = listView;
        this.product = product;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.product_menu) {
            MainActivity mainActivity = (MainActivity) activity;
            mainActivity.setSelectedProduct(product);
            mainActivity.openContextMenu(listView);
        }
    }
}
