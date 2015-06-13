package ro.emag.hackaton.vewa.Listener;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

import ro.emag.hackaton.vewa.Helper.SpeechRecognitionHelper;

public class SpeechButtonClickListener implements OnClickListener {

    Activity activity;

    View target;

    public SpeechButtonClickListener(Activity activity, View target)
    {
        this.activity = activity;
        this.target = target;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == this.target.getId()) {
            SpeechRecognitionHelper.startRecognitionActivity(activity);
        }
    }
}
