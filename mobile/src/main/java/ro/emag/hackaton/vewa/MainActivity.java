package ro.emag.hackaton.vewa;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;

import ro.emag.hackaton.vewa.Adapter.WishlistAdapter;
import ro.emag.hackaton.vewa.Helper.SpeechRecognitionHelper;
import ro.emag.hackaton.vewa.Listener.SpeechButtonClickListener;

public class MainActivity extends Activity {

    // variable for checking Voice Recognition support on user device
    private static final int VR_REQUEST = 999;

    // ListView for displaying suggested words
    private ListView wordList;

    // Log tag for output information
    private static final String LOG_TAG = "MainActivity";

    // variable for checking TTS engine data on user device
    private int MY_DATA_CHECK_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton speechBtn = (ImageButton) findViewById(R.id.speech_btn);

        wordList = (ListView) findViewById(R.id.word_list);
        /*wordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView wordView = (TextView) view;
                String word = (String) wordView.getText();
                Log.v(LOG_TAG, "chosen: " + word);
                Toast.makeText(MainActivity.this, "You said: " + word, Toast.LENGTH_SHORT).show();
            }
        });*/

        if (SpeechRecognitionHelper.isSpeechRecognitionActivityPresented(this)) {
            speechBtn.setOnClickListener(new SpeechButtonClickListener(this, speechBtn));

            Intent checkTTSIntent = new Intent();
            checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
        } else {
            speechBtn.setEnabled(false);
            Toast.makeText(this, "Oops - Speech recognition not supported!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check speech recognition result
        if (requestCode == VR_REQUEST && resultCode == RESULT_OK) {
            // store the returned word list as an ArrayList
            final ArrayList<String> suggestedWords = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            // set the retrieved list to display in the ListView using an ArrayAdapter
            //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.wish_list_item, suggestedWords);
            WishlistAdapter adapter = new WishlistAdapter(this, R.layout.wish_list_item, suggestedWords);
            wordList.setAdapter(adapter);

            if (suggestedWords.size() > 0) {
                String text = suggestedWords.get(0);
                Log.v(LOG_TAG, text);
                SpeechRecognitionHelper.search(text);
            }
        }

        // call superclass method
        super.onActivityResult(requestCode, resultCode, data);
    }
}
