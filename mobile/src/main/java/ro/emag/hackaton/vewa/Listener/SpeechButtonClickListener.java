package ro.emag.hackaton.vewa.Listener;

import android.view.View;
import android.view.View.OnClickListener;

import ro.emag.hackaton.vewa.Helper.SpeechRecognitionHelper;
import ro.emag.hackaton.vewa.MainActivity;

public class SpeechButtonClickListener implements OnClickListener {

    MainActivity activity;

    View target;

    public SpeechButtonClickListener(MainActivity activity, View target)
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
