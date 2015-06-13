package ro.emag.hackaton.vewa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

public class MainActivity extends Activity implements MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks {

    private TextView mTextView;

    private static final int SPEECH_RECOGNIZER_REQUEST_CODE = 0;
    private static final String VEWA_WISHLIST_CAPABILITY_NAME = "vewa_wishlist";
    private GoogleApiClient googleApiClient;
    public static final String VEWA_MESSAGE_PATH = "/vewa";
    private final String TAG = "VEWA_DEBUG_WEAR";
    public String recognizedMessage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                setupCapability();
                startSpeechRecognition();
            }
        });
    }

    private void startSpeechRecognition() {
        mTextView.setText("Waiting for info...");
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
                //mTextView.setText(recognizedText);
                sendVewaMessage(recognizedText);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onClick(View target) {
        startSpeechRecognition();
    }

    private void setupCapability() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        mTextView.setText(result.toString());
                        Log.d(TAG, "onConnectionFailed: " + result);
                    }
                })
                .addApi(Wearable.API)
                .build();
        googleApiClient.connect();
    }

    private void sendVewaMessage(String recognizedMessage) {
        this.recognizedMessage = recognizedMessage;
        PendingResult<NodeApi.GetConnectedNodesResult> result = Wearable.NodeApi.getConnectedNodes(googleApiClient);
        result.setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                for (final Node node : getConnectedNodesResult.getNodes()) {
                    Log.v(TAG, "Sending message \"" + MainActivity.this.recognizedMessage + "\" to node " + node.getDisplayName());
                    PendingResult<MessageApi.SendMessageResult> sendMessageResult = Wearable.MessageApi.sendMessage(googleApiClient, node.getId(),
                            VEWA_MESSAGE_PATH, MainActivity.this.recognizedMessage.getBytes());

                    sendMessageResult.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                            Log.v(TAG, sendMessageResult.getStatus().getStatusMessage());
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.v(TAG, "Connected to Google Play Services on Wear!");
        Wearable.MessageApi.addListener(googleApiClient, this);
    }

    @Override
    public void onMessageReceived(final MessageEvent messageEvent) {
        final String message = new String(messageEvent.getData());
        Log.v(TAG, "Message received on wear on path " + messageEvent.getPath() + ": " + message);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextView.setText(message);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Wearable.MessageApi.removeListener(googleApiClient, this);
        Log.v(TAG, "onStop");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");
    }
}
