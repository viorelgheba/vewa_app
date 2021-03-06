package ro.emag.hackaton.vewa.Helper;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.speech.RecognizerIntent;

import java.util.ArrayList;
import java.util.List;

import ro.emag.hackaton.vewa.Api.AddToWishListTask;
import ro.emag.hackaton.vewa.Api.ListWishListTask;
import ro.emag.hackaton.vewa.Api.RemoveFromWishListTask;
import ro.emag.hackaton.vewa.Api.SearchProductTask;

public class SpeechRecognitionHelper {

    // variable for checking Voice Recognition support on user device
    private static final int VR_REQUEST = 999;

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

    public static void showWishlist(Activity activity) {
        ListWishListTask task = new ListWishListTask(activity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getDeviceName(activity));
        } else {
            task.execute(getDeviceName(activity));
        }
    }

    public static void search(Activity activity, ArrayList<String> suggestedWords, String source) {
        SearchProductTask task = new SearchProductTask(activity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, suggestedWords.get(0), getDeviceName(activity), source);
        } else {
            task.execute(suggestedWords.get(0), getDeviceName(activity), source);
        }
    }

    public static void addToWishlist(Activity activity, Integer productId) {
        AddToWishListTask task = new AddToWishListTask(activity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, productId.toString(), getDeviceName(activity));
        } else {
            task.execute(productId.toString(), getDeviceName(activity));
        }
    }

    public static void removeToWishlist(Activity activity, Integer productId) {
        RemoveFromWishListTask task = new RemoveFromWishListTask(activity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, productId.toString(), getDeviceName(activity));
        } else {
            task.execute(productId.toString(), getDeviceName(activity));
        }
    }

    public static String getDeviceName(Activity activity) {
        return Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
