package com.example.android.quakereport;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.quakereport.data.DBContract;
import com.example.android.quakereport.data.DBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public class QueryUtils {
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;
    private List<Quake> earthquakes;
    private Context context;
    private VolleyReponceCalled volleyReponceCalled;
    public QueryUtils(VolleyReponceCalled volleyReponceCalled) {
        this.volleyReponceCalled = volleyReponceCalled;
    }

    public interface VolleyReponceCalled{
        public void onVolleyReponceCalled(List<Quake> mDataSet);
    }

    public void initiVolley(String conn_url, Context context) {
// If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(conn_url)) {
            return ;
        }
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
        // Create an empty ArrayList that we can start adding earthquakes to
        earthquakes = new ArrayList<>();
        DBHelper mDbHelper = new DBHelper(QueryUtils.this.context);
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, conn_url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    Log.d(LOG_TAG, "onResponse");
                    try {
                        JSONArray features = response.getJSONArray("features");
                        for (int i = 0; i < features.length(); i++) {
                            double distance = 0;

                            JSONObject currentEarthquake = features.getJSONObject(i);
                            JSONObject properties = currentEarthquake.getJSONObject("properties");

                            int felt = 0;
                            if (!properties.isNull("felt")) {
                                felt = properties.getInt("felt");
                            }
                            JSONArray coordinates = currentEarthquake.getJSONObject("geometry").getJSONArray("coordinates");
                            // Create a new map of values, where column names are the keys
                            ContentValues values = new ContentValues();
                            values.put(DBContract.EqEntry.COLUMN_MAG, properties.getDouble("mag"));
                            values.put(DBContract.EqEntry.COLUMN_LOCATION, properties.getString("place"));
                            values.put(DBContract.EqEntry.COLUMN_DATE, properties.getString("time"));
                            values.put(DBContract.EqEntry.COLUMN_URL, properties.getString("url"));
                            values.put(DBContract.EqEntry.COLUMN_FELT, felt);
                            values.put(DBContract.EqEntry.COLUMN_LONGITUDE, coordinates.getInt(0));
                            values.put(DBContract.EqEntry.COLUMN_LATITUDE, coordinates.getInt(1));
                            values.put(DBContract.EqEntry.COLUMN_DEPTH, coordinates.getInt(2));
                            values.put(DBContract.EqEntry.COLUMN_DISTANCE, distance);
// Insert the new row, returning the primary key value of the new row
                            long newRowId = db.insert(DBContract.EqEntry.TABLE_NAME, null, values);
                            earthquakes.add(new Quake(properties.getDouble("mag"), properties.getString("place"), properties.getString("time"), properties.getString("url"), felt, coordinates.getDouble(0), coordinates.getDouble(1), coordinates.getInt(2), distance));
                        }
                        
                        volleyReponceCalled.onVolleyReponceCalled(earthquakes);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(LOG_TAG, "onErrorResponse " + error.getMessage());
                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
        }
    }
}