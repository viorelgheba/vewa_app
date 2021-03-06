package ro.emag.hackaton.vewa;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import ro.emag.hackaton.vewa.Entity.Product;
import ro.emag.hackaton.vewa.Helper.SpeechRecognitionHelper;
import ro.emag.hackaton.vewa.Listener.SpeechButtonClickListener;

public class MainActivity extends ActionBarActivity implements MessageApi.MessageListener {

    private String TAG = "VEWA_DEBUG_MOBILE";
    private GoogleApiClient mGoogleApiClient;
    public static final String VEWA_MESSAGE_PATH = "/vewa";

    // variable for checking Voice Recognition support on user device
    private static final int VR_REQUEST = 999;
    private Product selectedProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setLogo(R.mipmap.ic_launcher);
        } catch (Exception e) {}

        SpeechRecognitionHelper.showWishlist(this);

        ImageButton speechBtn = (ImageButton) findViewById(R.id.speech_btn);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(Bundle connectionHint) {
                    Log.d(TAG, "onConnected: " + connectionHint);
                    //sendMessageToWatch("connected");
                    Wearable.MessageApi.addListener(mGoogleApiClient, MainActivity.this);
                }

                @Override
                public void onConnectionSuspended(int cause) {
                    Log.d(TAG, "onConnectionSuspended: " + cause);
                }
            })
            .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                @Override
                public void onConnectionFailed(ConnectionResult result) {
                    Log.d(TAG, "onConnectionFailed: " + result);
                }
            })
            .addApi(Wearable.API)
            .build();
        mGoogleApiClient.connect();

        if (SpeechRecognitionHelper.isSpeechRecognitionActivityPresented(this)) {
            speechBtn.setOnClickListener(new SpeechButtonClickListener(this, speechBtn));

            Intent checkTTSIntent = new Intent();
            checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            startActivityForResult(checkTTSIntent, 0);
        } else {
            speechBtn.setEnabled(false);
            Toast.makeText(this, "Oops - Speech recognition not supported!", Toast.LENGTH_LONG).show();
        }
    }

    public void setSelectedProduct(Product selectedProduct) {
        this.selectedProduct = selectedProduct;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        switch (v.getId()) {
            case R.id.wish_list:
                getMenuInflater().inflate(R.menu.menu_wishlist , menu);
                break;
            case R.id.search_list:
                getMenuInflater().inflate(R.menu.menu_product , menu);
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (selectedProduct == null) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.add_to_wishlist:
                SpeechRecognitionHelper.addToWishlist(this, selectedProduct.getId());
                return true;
            case R.id.remove_from_wishlist:
                SpeechRecognitionHelper.removeToWishlist(this, selectedProduct.getId());
                return true;
            case R.id.open_in_browser:
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(selectedProduct.getProductLink()));
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                try {
                    ActionBar actionBar = getSupportActionBar();
                    actionBar.setDisplayUseLogoEnabled(true);
                    actionBar.setLogo(R.mipmap.ic_launcher);
                } catch (Exception e) {}

                SpeechRecognitionHelper.showWishlist(this);
                return true;
            case R.id.refresh:
                try {
                    ActionBar actionBar = getSupportActionBar();
                    actionBar.setDisplayUseLogoEnabled(true);
                    actionBar.setLogo(R.mipmap.ic_launcher);
                } catch (Exception e) {}

                SpeechRecognitionHelper.showWishlist(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check speech recognition result
        if (requestCode == VR_REQUEST && resultCode == RESULT_OK) {
            // store the returned word list as an ArrayList
            final ArrayList<String> suggestedWords = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            if (suggestedWords.size() > 0) {
                // search for products
                SpeechRecognitionHelper.search(this, suggestedWords, "mobile");
            }
        }

        // call superclass method
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        final String message = new String(messageEvent.getData());
        Log.v(TAG, "Message received from path " + messageEvent.getPath() + ": " + message + " | Full event: " + messageEvent.toString());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();

                // search for products
                ArrayList<String> suggestedWords = new ArrayList<String>();
                suggestedWords.add(message);

                SpeechRecognitionHelper.search(MainActivity.this, suggestedWords, "wear");

            }
        });
        //sendMessageToWatch("Received " + message);
    }

    public void sendMessageToWatch(final String message){
        new AsyncTask<Void, Void, List<Node>>(){
            @Override
            protected List<Node> doInBackground(Void... params) {
                return getNodes();
            }
            @Override
            protected void onPostExecute(List<Node> nodeList) {
                for(Node node : nodeList) {
                    Log.v(TAG, "Sending " + node.getId() + ": " + message);
                    PendingResult<MessageApi.SendMessageResult> result = Wearable.MessageApi.sendMessage(
                            mGoogleApiClient,
                            node.getId(),
                            VEWA_MESSAGE_PATH,
                            message.getBytes()
                    );
                    result.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                            Log.v(TAG, "Phone: " + sendMessageResult.getStatus().getStatusMessage());
                        }
                    });
                }
            }
        }.execute();
    }

    private List<Node> getNodes() {
        List<Node> nodes = new ArrayList<Node>();
        NodeApi.GetConnectedNodesResult rawNodes =
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
        for (Node node : rawNodes.getNodes()) {
            nodes.add(node);
        }
        return nodes;
    }



}
