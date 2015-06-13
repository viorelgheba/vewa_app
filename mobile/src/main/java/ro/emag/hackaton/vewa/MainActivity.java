package ro.emag.hackaton.vewa;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener, OnInitListener {

    // variable for checking Voice Recognition support on user device
    private static final int VR_REQUEST = 999;

    // ListView for displaying suggested words
    private ListView wordList;

    // Log tag for output information
    private final String LOG_TAG = "MainActivity";

    // variable for checking TTS engine data on user device
    private int MY_DATA_CHECK_CODE = 0;

    // Text To Speech instance
    private TextToSpeech repeatTTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button speechBtn = (Button) findViewById(R.id.speech_btn);
        wordList = (ListView) findViewById(R.id.word_list);
        wordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView wordView = (TextView) view;
                String word = (String) wordView.getText();
                Log.v(LOG_TAG, "chosen: " + word);
                Toast.makeText(MainActivity.this, "You said: " + word, Toast.LENGTH_SHORT).show();
                repeatTTS.speak(word, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> intActivities = packageManager.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (intActivities.size() != 0) {
            speechBtn.setOnClickListener(this);

            Intent checkTTSIntent = new Intent();
            checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
        } else {
            speechBtn.setEnabled(false);
            Toast.makeText(this, "Oops - Speech recognition not supported!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void listenToSpeech() {
        // start the speech recognition intent passing required data
        Intent listenIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // indicate package
        listenIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
        // message to display while listening
        listenIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say a word!");
        // set speech model
        listenIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        // specify number of results to retrieve
        listenIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        // set language
        listenIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ro-RO");

        // start listening
        startActivityForResult(listenIntent, VR_REQUEST);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.speech_btn) {
            // listen for results
            listenToSpeech();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check speech recognition result
        if (requestCode == VR_REQUEST && resultCode == RESULT_OK) {
            // store the returned word list as an ArrayList
            ArrayList<String> suggestedWords = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            // set the retrieved list to display in the ListView using an ArrayAdapter
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.word, suggestedWords);
            wordList.setAdapter(adapter);
        }

        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                repeatTTS = new TextToSpeech(this, this);
            }
        }

        // call superclass method
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = repeatTTS.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "This Language is not supported", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Initialization Failed!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (repeatTTS != null) {
            repeatTTS.stop();
            repeatTTS.shutdown();
        }

        super.onDestroy();
    }
}
