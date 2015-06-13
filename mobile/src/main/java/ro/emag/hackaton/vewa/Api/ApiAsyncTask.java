package ro.emag.hackaton.vewa.Api;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class ApiAsyncTask extends AsyncTask<String, Void, String> {

    // api url
    private static final String API_URL = "http://vewa.birkof.ro/api/search";

    private HashMap<String, String> params;
    private HashMap<String, String> headers;
    private URL url;
    private int responseCode;
    private String response;

    public ApiAsyncTask() throws MalformedURLException {
        url = new URL(API_URL);
        params = new HashMap<String, String>();
        headers = new HashMap<String, String>();
    }

    public String getResponse() {
        return response;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void addParam(String name, String value) {
        params.put(name, value);
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(this.params));

            writer.flush();
            writer.close();
            os.close();

            this.responseCode = conn.getResponseCode();

            if (this.responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                while ((line = br.readLine()) != null) {
                    this.response += line;
                }

                br.close();
            } else {
                this.response = "";
            }
        } catch (Exception e) {
            this.response = "";
        }

        Log.d("ApiAsyncTask", "Response: " + response);

        return this.response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, String> entry : params.entrySet()){
            if (first) {
                first = false;
            } else {
                result.append("&");
            }

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
