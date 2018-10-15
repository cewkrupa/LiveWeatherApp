package com.example.connor.LiveWeatherAppPartIII;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    MyDBHandler cv_db;
    final int DOWNLOAD_URL_MAX_LENGTH = 750;

    String cv_tempCityZip, cv_tempCityState;

    String[] cv_lvDefaultCityZips = {
        "48197", "85365", "99703"
    };

    ArrayList<City> lv_listData;
    ArrayList<City> lv_savedCities;

    EditText cv_searchZip;

    private String OPENWEATHERMAP_APPID = "&appid=" + BuildConfig.OpenWeatherMapAppID;
    private MyCityListAdapter lv_adapter;
    private String weatherStringUrl = "http://api.openweathermap.org/data/2.5/weather?zip=";
    private String zipUrl = "http://api.zippopotam.us/us/";

    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(R.string.app_name);


        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        
        cv_db = new MyDBHandler(this);
        
        if(cv_db.getReadableDatabase() != null) {
            cv_db.cf_initRows();
        }

        lv_savedCities = cv_db.getAllCities();
        lv_listData = new ArrayList<City>();

        for (int i = 0; i < lv_savedCities.size(); i++){
            City city = lv_savedCities.get(i);
            new MyDownloadWeatherTask().execute(weatherStringUrl + city.getCityZip() + OPENWEATHERMAP_APPID, city.getCityZip(), city.getCityState());
        }

        cv_searchZip = (EditText)findViewById(R.id.vv_etSearchZip);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cv_searchZip.getVisibility() == View.VISIBLE)
                {
                    String cv_searchText = cv_searchZip.getText().toString();
                    cv_tempCityZip = cv_searchText;
                    if(cv_searchText.equals("") || cv_searchText == null){
                        Toast.makeText(MainActivity.this, "ZIP cannot be null.", Toast.LENGTH_SHORT).show();
                    }
                    else if (networkInfo != null && networkInfo.isConnected()) {
                        if(!cv_db.hasCityZip(cv_tempCityZip)) {
                            new MyDownloadZipTask().execute(zipUrl + cv_searchText, cv_tempCityZip);
                                //the search was successful, hide the edit text and the keyboard
                                cv_searchZip.setVisibility(View.INVISIBLE);
                                cv_searchZip.getText().clear();
                                if (view != null) {
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                }
                            }
                        else {
                            Toast.makeText(MainActivity.this, "ZIP already exists in database", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "No Network Connection Available", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    cv_searchZip.setVisibility(View.VISIBLE);
                    if (view != null)
                    {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(view, 0);
                    }
                }

            }
        });

        if(getIntent() != null) {
            Bundle extras = getIntent().getExtras();
        }

        //Create and set the ListView adapter
        lv_adapter = new MyCityListAdapter(this, lv_listData);
        ListView lv_cityListItems = (ListView) findViewById(R.id.v1v_lvCities);
        lv_cityListItems.setAdapter(lv_adapter);

        //Add click for intent
        lv_cityListItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent lv_it = new Intent(MainActivity.this, MyDetailActivity.class);

                /* Send the position of the selected list item to the new intent.
                Send the ArrayList of the weather data to the new intent (if we pass the weather data back and forth between intents, we don't need to recreate the array list every time.)
                 */
                lv_it.putExtra("Position", position);
                lv_it.putParcelableArrayListExtra("MyCityData", lv_listData);

                startActivity(lv_it);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
            }
        });

        lv_cityListItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //delete the item
                City cityToDelete = lv_listData.get(position);
                cv_db.deleteCity(cityToDelete);
                lv_listData.remove(position);
                lv_adapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "City Deleted!", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        lv_cityListItems.setLongClickable(true);
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


        if (id == R.id.action_reset) {
            cfp_resetDatabase();
            return true;
        }

        if (id == R.id.action_temp) {

            boolean isCheckedBeforeSelect = item.isChecked();
            //if it's true, it was checked before they selected it, we need to uncheck and change all to celsius,
            // same for reverse basically
            if (isCheckedBeforeSelect) {
                for (City city :
                        lv_listData) {
                    city.setCityTempFormat(1);
                }
                item.setChecked(false);
                lv_adapter.notifyDataSetChanged(); //reload all the cells to get the updated temperature
            }
            // otherwise we're going to Fahrenheit
            else {
                for (City city :
                        lv_listData) {
                    city.setCityTempFormat(2);
                }
                item.setChecked(true);
                lv_adapter.notifyDataSetChanged();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    private class MyDownloadWeatherTask extends AsyncTask<String, Void, String> {
        private String cv_downloadWeatherTempZip, cv_downloadWeatherTempState;
        @Override
        protected String doInBackground(String... params) {
            cv_downloadWeatherTempZip = params[1];
            cv_downloadWeatherTempState = params[2];
            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(params[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            if(cv_downloadWeatherTempState != null) {
                City city = cfp_parseWeatherJson(result, cv_downloadWeatherTempZip, cv_downloadWeatherTempState);
                lv_listData.add(city);
                //ONLY ADD TO THE DATABASE IF THE CITY DOESN'T ALREADY EXIST
                if(!cv_db.hasCity(city)) {
                    cv_db.addCity(city);
                }
                lv_adapter.notifyDataSetChanged();
            }

            else
                Toast.makeText(MainActivity.this, "Invalid ZIP", Toast.LENGTH_SHORT).show();
        }

        // Given a URL, establishes an HttpUrlConnection and retrieves
        // the web page content as a InputStream, which it returns as
        // a string.
        private String downloadUrl(String myurl) throws IOException {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                ////Log.d(DEBUG_TAG, "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = readIt(is, DOWNLOAD_URL_MAX_LENGTH);
                return contentAsString;

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        // Reads an InputStream and converts it to a String.
        public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }


    }

    private class MyDownloadZipTask extends AsyncTask<String, Void, String> {
        private String cv_downloadZipTempZip;
        @Override
        protected String doInBackground(String... params) {
            cv_downloadZipTempZip = params[1]; //copy the ZIP code from the params
            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(params[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            String cv_asyncTempState = cfp_parseZipJsonGetState(result);
            new MyDownloadWeatherTask().execute(weatherStringUrl + cv_downloadZipTempZip + OPENWEATHERMAP_APPID, cv_downloadZipTempZip, cv_asyncTempState)
            ;

        }

        // Given a URL, establishes an HttpUrlConnection and retrieves
        // the web page content as a InputStream, which it returns as
        // a string.
        private String downloadUrl(String myurl) throws IOException {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                ////Log.d(DEBUG_TAG, "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = readIt(is, DOWNLOAD_URL_MAX_LENGTH);
                return contentAsString;

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        // Reads an InputStream and converts it to a String.
        public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }


    }

    private City cfp_parseWeatherJson(String input, String zip, String state) {
        City output = null;
        try {
            JSONObject jsonRootObject = new JSONObject(input);
            JSONArray jsonWeatherArray = jsonRootObject.getJSONArray("weather");
            String cond = jsonWeatherArray.getJSONObject(0).getString("main");
            JSONObject  jsonMainObject = jsonRootObject.getJSONObject("main");
            double temp = jsonMainObject.getDouble("temp");
            double tempHigh = jsonMainObject.getDouble("temp_max");
            double tempLow = jsonMainObject.getDouble("temp_min");
            String name = jsonRootObject.optString("name");
            String weatherId = jsonWeatherArray.getJSONObject(0).getString("id");
            output = new City(zip, name, state, temp, tempHigh, tempLow, 1, weatherId, cond);
            Log.d("Parsed JSON - ", output.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return output;
    }
    private String cfp_parseZipJsonGetState(String input) {
        String output = null;
        try {
            JSONObject jsonRootObject = new JSONObject(input);
            JSONArray jsonZipArray = jsonRootObject.getJSONArray("places");
            output = jsonZipArray.getJSONObject(0).getString("state");
            Log.d("Parsed JSON - ", output.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return output;
    }

    private String cfp_parseZipJsonGetZip(String input) {
        String output = null;
        try {
            JSONObject jsonRootObject = new JSONObject(input);
            JSONArray jsonZipArray = jsonRootObject.getJSONArray("places");
            output = jsonZipArray.getJSONObject(0).getString("state");
            Log.d("Parsed JSON - ", output.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return output;
    }

    private void cfp_resetDatabase(){
        cv_db.deleteAllAndReinit();

        lv_savedCities = cv_db.getAllCities();
        lv_listData.clear();
        for (int i = 0; i < lv_savedCities.size(); i++){
            City city = lv_savedCities.get(i);
            new MyDownloadWeatherTask().execute(weatherStringUrl + city.getCityZip() + OPENWEATHERMAP_APPID, city.getCityZip(), city.getCityState());
        }
        lv_adapter.notifyDataSetChanged();
    }
}
