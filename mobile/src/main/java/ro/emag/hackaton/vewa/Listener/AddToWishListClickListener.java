package ro.emag.hackaton.vewa.Listener;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import ro.emag.hackaton.vewa.Api.AddToWishListTask;
import ro.emag.hackaton.vewa.Helper.SpeechRecognitionHelper;
import ro.emag.hackaton.vewa.R;

public class AddToWishListClickListener implements OnClickListener {

    private Context context;
    private Integer productId;

    public AddToWishListClickListener(Context context, Integer productId) {
        this.context = context;
        this.productId = productId;
    }

    @Override
    public void onClick(View v) {
        AddToWishListTask addToWishListTask = new AddToWishListTask(context);
        //addToWishListTask.execute(productId.toString());
    }
}
