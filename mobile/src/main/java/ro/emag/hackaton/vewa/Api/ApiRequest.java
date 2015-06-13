package ro.emag.hackaton.vewa.Api;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class ApiRequest {

    private ArrayList<NameValuePair> params;
    private ArrayList <NameValuePair> headers;

    private String url;

    private int responseCode;
    private String response;
    private String errorMessage;

    public ApiRequest(String url) throws MalformedURLException {
        this.url = url;
        params = new ArrayList<NameValuePair>();
        headers = new ArrayList<NameValuePair>();
    }

    public String getResponse() {
        return response;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void addParam(String name, String value) {
        params.add(new BasicNameValuePair(name, value));
    }

    public void addHeader(String name, String value) {
        headers.add(new BasicNameValuePair(name, value));
    }

    public void get() throws Exception {
        //add parameters
        String combinedParams = "";

        if (!params.isEmpty()) {
            combinedParams += "?";

            for (NameValuePair p : params) {
                String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(), HTTP.UTF_8);
                if(combinedParams.length() > 1) {
                    combinedParams  +=  "&" + paramString;
                } else {
                    combinedParams += paramString;
                }
            }
        }

        HttpGet httpGet = new HttpGet(url + combinedParams);

        //add headers
        for(NameValuePair h : headers) {
            httpGet.addHeader(h.getName(), h.getValue());
        }

        executeRequest(httpGet, url);
    }

    public void post() throws Exception {
        HttpPost httpPost = new HttpPost(url);

        //add headers
        for (NameValuePair h : headers) {
            httpPost.addHeader(h.getName(), h.getValue());
        }

        if(!params.isEmpty()){
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
        }

        executeRequest(httpPost, url);
    }

    private void executeRequest(HttpUriRequest request, String url) {
        HttpClient client = new DefaultHttpClient();

        HttpResponse httpResponse;

        try {
            httpResponse = client.execute(request);
            responseCode = httpResponse.getStatusLine().getStatusCode();
            errorMessage = httpResponse.getStatusLine().getReasonPhrase();

            HttpEntity entity = httpResponse.getEntity();

            if (entity != null) {
                InputStream instream = entity.getContent();
                response = convertStreamToString(instream);

                // Closing the input stream will trigger connection release
                instream.close();
            }
        } catch (Exception e)  {
            client.getConnectionManager().shutdown();
            e.printStackTrace();
        }
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;

        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
}
