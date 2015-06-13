package ro.emag.hackaton.vewa.Helper;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.RecognizerIntent;

import org.apache.http.client.methods.HttpPost;

import java.util.List;

import ro.emag.hackaton.vewa.Api.RestClient;

public class SpeechRecognitionHelper {

    // variable for checking Voice Recognition support on user device
    private static final int VR_REQUEST = 999;

    private static final String API_URL = "http://vewa.birkof.ro/api/search";

    public static boolean isSpeechRecognitionActivityPresented(Activity activity) {
        try {
            PackageManager packageManager = activity.getPackageManager();
            List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

            if (resolveInfos.size() != 0) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }

    public static void startRecognitionActivity(Activity activity) {
        // start the speech recognition intent passing required data
        Intent listenIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // indicate package
        listenIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, activity.getClass().getPackage().getName());
        // message to display while listening
        listenIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say a word!");
        // set speech model
        listenIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        // specify number of results to retrieve
        listenIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        // set language
        listenIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ro-RO");

        activity.startActivityForResult(listenIntent, VR_REQUEST);
    }

    public static void search(String text) {
        RestClient client = new RestClient(API_URL);
        client.addParam("term", text);
        client.addParam("max", "10");

        try {
            client.execute(HttpPost.METHOD_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
