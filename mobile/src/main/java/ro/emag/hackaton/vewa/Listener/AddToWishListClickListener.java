package ro.emag.hackaton.vewa.Listener;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

import ro.emag.hackaton.vewa.Api.AddToWishListTask;
import ro.emag.hackaton.vewa.Helper.SpeechRecognitionHelper;

public class AddToWishListClickListener implements OnClickListener {

    private int productId;

    public AddToWishListClickListener(int productId) {
        this.productId = productId;
    }

    @Override
    public void onClick(View v) {
        AddToWishListTask addToWishListTask = new AddToWishListTask();
    }
}
