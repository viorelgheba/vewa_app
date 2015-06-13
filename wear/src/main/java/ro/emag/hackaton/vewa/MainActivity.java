package ro.emag.hackaton.vewa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends Activity {

    private TextView mTextView;

    private static final int SPEECH_RECOGNIZER_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                startSpeechRecognition();
            }
        });
    }

    private void startSpeechRecognition() {
        // Create an intent to start the Speech Recognizer
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Start the activity
        startActivityForResult(intent, SPEECH_RECOGNIZER_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_RECOGNIZER_REQUEST_CODE) {
            // When the speech recognizer finishes its work, Android invokes this callback with requestCode equal to SPEECH_RECOGNIZER_REQUEST_CODE
            if (resultCode == RESULT_OK) {
                List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String recognizedText = results.get(0);
                // Display the recognized text
                mTextView.setText(recognizedText);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public  void onClick(View target) {
        startSpeechRecognition();
    }

}
