package com.example.earth_quake_app;

//import androidx.annotation.NonNull;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.loader.app.LoaderManager;
//import androidx.loader.app.LoaderManager;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.loader.app.LoaderManager;
//import androidx.loader.content.AsyncTaskLoader;
//import androidx.loader.content.Loader;
//import androidx.loader.content.Loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
//import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.ListView;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.widget.ProgressBar;
import android.widget.TextView;

//import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<ListObject>>{
    private static final String URL_OF_API = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2014-01-01&endtime=2014-01-02";
    private static final int ID_LOADER = 1;
    private ProgressBar myBarView;
    private Adaptermodified myAdapter;
    private TextView elseView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        myAdapter = new Adaptermodified(this, new ArrayList<ListObject>());
        earthquakeListView.setAdapter(myAdapter);


        ConnectivityManager cnmr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo gettingInfo = cnmr.getActiveNetworkInfo();



        elseView = findViewById(R.id.Else_View);
        earthquakeListView.setEmptyView(elseView);
        myBarView = findViewById(R.id.loading_indicator);


        if (gettingInfo != null && gettingInfo.isConnected()){
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(ID_LOADER,null,this);
        }
        else {
            elseView.setText("No Internet Connectivity");
            myBarView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public Loader<List<ListObject>> onCreateLoader(int ID_LOADER, @Nullable @org.jetbrains.annotations.Nullable Bundle args) {
        return new myTask1(this,URL_OF_API);
    }

    @Override
    public void onLoadFinished(@NonNull @NotNull Loader<List<ListObject>> loader, List<ListObject> data) {

        elseView.setText("No Earthquakes found");
        myBarView.setVisibility(View.INVISIBLE);
        myAdapter.clear();
        if (data != null && !data.isEmpty()) {
            myAdapter.addAll(data);
        }

    }

    @Override
    public void onLoaderReset(@NonNull @NotNull Loader<List<ListObject>> loader) {

    }


    private static class myTask1 extends AsyncTaskLoader<List<ListObject>> {
        private String mUrl;

        public myTask1(Context context,String url){
            super(context);
            mUrl = url;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public List<ListObject> loadInBackground() {
            List<ListObject> listViewList = FetchData(mUrl);
            return listViewList;
        }
    }

    private static URL createURL(String stringurl) {
        URL url1 = null;
        try {
            url1 = new URL(stringurl);
        } catch (MalformedURLException e) {
            Log.e("LOG_TAG", "problem building the url ", e);
        }
        return url1;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader reader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader Breader = new BufferedReader(reader);
            String line = Breader.readLine();
            while (line != null) {
                output.append(line);
                line = Breader.readLine();
            }
        }
        return output.toString();
    }

    private static String makehttpREq(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e("LOG_TAG", "Error with response code " + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e("LOG_TAG", "req exception ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;

    }

    private static ArrayList<ListObject> myArrayToDisplay(String jsonResponse) {

        ArrayList<ListObject> myArray1 = new ArrayList<>();
        try {
            JSONObject earthQuakeArray = new JSONObject(jsonResponse);
            JSONArray myArray = new JSONArray();
            myArray = earthQuakeArray.getJSONArray("features");
//            ArrayList<ListObject> myArrayList = new ArrayList<ListObject>();
            for (int i = 0; i < myArray.length(); i++) {
                JSONObject jObject = new JSONObject();
                jObject = myArray.getJSONObject(i);
                JSONObject properties = jObject.getJSONObject("properties");
                String mag = properties.getString("mag");
                String Location = properties.getString("place");
                int Time = properties.getInt("time");
                Time = Time/60000;
                String time = String.valueOf(Time);
                myArray1.add(new ListObject(mag, Location, time));
            }
        } catch (JSONException e) {
            Log.e("LOG_TAG", "JSON parsing error", e);
        }

        return myArray1;

    }

    public static List<ListObject> FetchData(String requestURL) {
        URL url = createURL(requestURL);
        String JsonResponse = null;
        try {
            JsonResponse = makehttpREq(url);
        } catch (IOException e) {
            Log.e("LOG_TAG", "JSON fetching error ", e);
        }

        List<ListObject> earthquakes = myArrayToDisplay(JsonResponse);

        return earthquakes;
    }

}