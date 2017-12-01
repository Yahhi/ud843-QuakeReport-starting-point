/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.quakereport.data.DBContract;
import com.example.android.quakereport.data.DBHelper;

public class EarthquakeActivity extends AppCompatActivity
        implements QueryUtils.VolleyReponceCalled, NavigationView.OnNavigationItemSelectedListener,
        Preference.OnPreferenceChangeListener, EarthquakeAdapterOnClickHandler {
    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    LocationManager locationManager;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int EARTHQUAKE_LOADER_ID = 1;
    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private static String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=1&limit=10000";
    private QuakeAdapter mAdapter;
    private TextView mEmptyStateTextView;
    private NavigationView navigationView;
    public List<String> magnitude;
    public List<String> sortBy;
    public List<String> itemsDisp;
    private DBHelper mDbHelper;
    String minMagnitude = "1";
    String orderBy = DBContract.EqEntry.COLUMN_MAG + " DESC";
    String itDisp = "10";
    double longitudeNetwork = 0, latitudeNetwork = 0;
    String latNet;
    String longNet;
    private List<Quake> earthquakes;
    private RecyclerView mRecyclerView;
    private EarthquakeAdapter mEarthquakeAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        navigationView.setNavigationItemSelectedListener(this);
        requestPermission();
        toggleNetworkUpdates();
        new QueryUtils(this).initiVolley(USGS_REQUEST_URL, this);
        // RecyclerView stuff
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mEarthquakeAdapter = new EarthquakeAdapter();
        mRecyclerView.setAdapter(mEarthquakeAdapter);
        //old listView
        final ListView earthquakeListView = (ListView) findViewById(R.id.list);
        mAdapter = new QuakeAdapter(this, new ArrayList<Quake>());
        earthquakeListView.setAdapter(mAdapter);
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Quake currentQuake = mAdapter.getItem(position);
                Toast.makeText(EarthquakeActivity.this, currentQuake.getKdistance(), Toast.LENGTH_SHORT).show();
                Intent details = new Intent(EarthquakeActivity.this, DetailsActivity.class);
                details.putExtra("Magnitude", currentQuake.getMag());
                details.putExtra("Date", currentQuake.getDate());
                details.putExtra("Time", currentQuake.getTime());
                details.putExtra("LocationOff", currentQuake.getLocationOff());
                details.putExtra("Location", currentQuake.getLocation());
                //double dtLongitude = currentQuake.getLongitude();
                details.putExtra("Longitude", currentQuake.getLongitude());
                //double dtLatitude = currentQuake.getLatitude();
                details.putExtra("Latitude", currentQuake.getLatitude());
                details.putExtra("Depth", currentQuake.getDepth());
                details.putExtra("LongNet", longNet);
                details.putExtra("LatNet", latNet);
                String felt = currentQuake.getFelt();
                details.putExtra("Felt", felt);
                double calc1 = (Math.sqrt(Math.pow((currentQuake.getLongitude() - longitudeNetwork), 2) + Math.pow((currentQuake.getLatitude() - latitudeNetwork), 2))) * 110.5;
                //double calc1 = currentQuake.getDistance();
                details.putExtra("Distance", calc1);
                startActivity(details);
            }
        });
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        earthquakeListView.setEmptyView(mEmptyStateTextView);
//Spinners in nav drawer
        magnitude = new ArrayList<String>();
        magnitude.add("Magnitude: 1+ ");
        magnitude.add("Magnitude: 2+ ");
        magnitude.add("Magnitude: 3+ ");
        magnitude.add("Magnitude: 4+ ");
        magnitude.add("Magnitude: 5+ ");
        magnitude.add("Magnitude: 6+ ");
        magnitude.add("Magnitude: 7+ ");
        magnitude.add("Magnitude: 8+ ");
        magnitude.add("Magnitude: 9+ ");
        Spinner magSpinner = (Spinner) navigationView.getMenu().findItem(R.id.nav_drawer_mag).getActionView();
        magSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, magnitude));
        magSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        minMagnitude = "1";
                        break;
                    case 1:
                        minMagnitude = "2";
                        break;
                    case 2:
                        minMagnitude = "3";
                        break;
                    case 3:
                        minMagnitude = "4";
                        break;
                    case 4:
                        minMagnitude = "5";
                        break;
                    case 5:
                        minMagnitude = "6";
                        break;
                    case 6:
                        minMagnitude = "7";
                        break;
                    case 7:
                        minMagnitude = "8";
                        break;
                    case 8:
                        minMagnitude = "9";
                        break;
                }
                displayDbQuakes();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        sortBy = new ArrayList<String>();
        sortBy.add("Sort by: Magnitude");
        sortBy.add("Sort by: Most Recent");
        sortBy.add("Sort by: Nearest");
        sortBy.add("just show location");
        Spinner sortSpinner = (Spinner) navigationView.getMenu().findItem(R.id.nav_drawer_sortby).getActionView();
        sortSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, sortBy));
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        orderBy = DBContract.EqEntry.COLUMN_MAG + " DESC";
                        break;
                    case 1:
                        orderBy = DBContract.EqEntry.COLUMN_DATE + " DESC";
                        break;
                    case 2:
                        orderBy= DBContract.EqEntry.COLUMN_DISTANCE + " DESC";
                        break;
                    case 3:
                        calculateDistance();
                        break;
                }
                displayDbQuakes();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        itemsDisp = new ArrayList<String>();
        itemsDisp.add("Items to display: 10");
        itemsDisp.add("Items to display: 20");
        itemsDisp.add("Items to display: 30");
        itemsDisp.add("Items to display: 50");
        itemsDisp.add("Items to display: 100");
        itemsDisp.add("Items to display: 10000");
        Spinner itemsDispSpinner = (Spinner) navigationView.getMenu().findItem(R.id.nav_drawer_itemsDisp).getActionView();
        itemsDispSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, itemsDisp));
        itemsDispSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        itDisp = "10";
                        break;
                    case 1:
                        itDisp = "20";
                        break;
                    case 2:
                        itDisp = "30";
                        break;
                    case 3:
                        itDisp = "50";
                        break;
                    case 4:
                        itDisp = "100";
                        break;
                    case 5:
                        itDisp = "10000";
                        break;
                }
                displayDbQuakes();
                //cleanStuff();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mDbHelper = new DBHelper(this);
    }

    private void displayDatabaseInfo() {
        DBHelper mDbHelper = new DBHelper(this);

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //Perform this raw SQL query "SELECT * FROM pets"
        //to get a Cursor that contains all rows from the pets table.
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBContract.EqEntry.TABLE_NAME, null);
        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).
            Log.e(LOG_TAG, "Number of rows in pets database table: " + cursor.getCount());
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(String weatherForDay) {
        Context context = this;
        Toast.makeText(context, weatherForDay, Toast.LENGTH_SHORT)
                .show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_drawer_mag) {

        } else if (id == R.id.nav_drawer_sortby) {

        } else if (id == R.id.nav_drawer_itemsDisp) {

        } else if (id == R.id.nav_drawer_aboutApp) {
            Intent aboutApp = new Intent(this, AboutAppActivity.class);
            startActivity(aboutApp);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

//    @Override
//    public Loader<List<Quake>> onCreateLoader(int i, Bundle bundle) {
//        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
//        Uri.Builder uriBuilder = baseUri.buildUpon();
//
//        uriBuilder.appendQueryParameter("format", "geojson");
//        uriBuilder.appendQueryParameter("limit", itDisp);
//        uriBuilder.appendQueryParameter("minmag", minMagnitude);
//        uriBuilder.appendQueryParameter("orderby", orderBy);
//
//        return new EarthquakeLoader(this, uriBuilder.toString());
//    }
    // Create a new loader for the given URL
    //return new EarthquakeLoader(this, USGS_REQUEST_URL);

    //    @Override
//    public void onLoadFinished(Loader<List<Quake>> loader, List<Quake> earthquakes) {
//        ProgressBar spinner = (ProgressBar) findViewById(R.id.loading_spinner);
//        spinner.setVisibility(View.GONE);
//        if (isNetworkAvailable() != true){
//            mEmptyStateTextView.setText(R.string.no_connection);
//        } else {
//        // Set empty state text to display "No earthquakes found."
//        mEmptyStateTextView.setText(R.string.no_earthquakes);
//        }
//        // Clear the adapter of previous earthquake data
//        mAdapter.clear();
//
//        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
//        // data set. This will trigger the ListView to update.
//        if (earthquakes != null && !earthquakes.isEmpty()) {
//            mAdapter.addAll(earthquakes);
//        }
//    }
//    @Override
//    public void onLoaderReset(Loader<List<Quake>> loader) {
//        // Loader reset, so we can clear out our existing data.
//        mAdapter.clear();
//    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                CharSequence[] labels = listPreference.getEntries();
                preference.setSummary(labels[prefIndex]);
            }
        } else {
            preference.setSummary(stringValue);
        }
        return true;
    }

    //    public void cleanStuff(){
//        Log.e(LOG_TAG, "it should work");
//        getLoaderManager().restartLoader(EARTHQUAKE_LOADER_ID, null, this);
//    }
    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this function")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    public void toggleNetworkUpdates() {
        if (!checkLocation())
            return;
        else if (checkPermission())
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 60 * 1000, 10, locationListenerNetwork);
        Toast.makeText(this, "Network provider started running", Toast.LENGTH_LONG).show();
    }

    private final LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitudeNetwork = location.getLongitude();
            latitudeNetwork = location.getLatitude();
            //calculateDistance();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(EarthquakeActivity.this,
                            "Permission accepted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(EarthquakeActivity.this,
                            "Permission denied", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
    }

    private boolean checkPermission() {
//Check for READ_EXTERNAL_STORAGE access, using ContextCompat.checkSelfPermission()//

        int result = ContextCompat.checkSelfPermission(EarthquakeActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION);
//If the app does have this permission, then return true//

        //If the app doesn’t have this permission, then return false//
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void makeMagic() {
        DecimalFormat formatter = new DecimalFormat("0.00");
        longNet = formatter.format(longitudeNetwork);
        latNet = formatter.format(latitudeNetwork);
        Toast.makeText(EarthquakeActivity.this, longNet + " " + latNet, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onVolleyReponceCalled(List<Quake> mDataSet) {
        mAdapter.clear();
        if (mDataSet != null && !mDataSet.isEmpty()) {
            mAdapter.addAll(mDataSet);
        }
        ProgressBar spinner = (ProgressBar) findViewById(R.id.loading_spinner);
        spinner.setVisibility(View.GONE);
    }

    public void displayDbQuakes() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        earthquakes = new ArrayList<>();
        String[] projection = {
                DBContract.EqEntry.COLUMN_ID,
                DBContract.EqEntry.COLUMN_MAG,
                DBContract.EqEntry.COLUMN_LOCATION,
                DBContract.EqEntry.COLUMN_DATE,
                DBContract.EqEntry.COLUMN_URL,
                DBContract.EqEntry.COLUMN_FELT,
                DBContract.EqEntry.COLUMN_LONGITUDE,
                DBContract.EqEntry.COLUMN_LATITUDE,
                DBContract.EqEntry.COLUMN_DEPTH,
                DBContract.EqEntry.COLUMN_DISTANCE
        };
        Cursor cursor = db.query(
                DBContract.EqEntry.TABLE_NAME,   // The table to query
                projection,            // The columns to return
                DBContract.EqEntry.COLUMN_MAG + ">" + minMagnitude,   // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                orderBy,                  // The sort order
                itDisp);
    try

    {
        // Create a header in the Text View that looks like this:
        //
        // The pets table contains <number of rows in Cursor> pets.
        // _id - name - breed - gender - weight
        //
        // In the while loop below, iterate through the rows of the cursor and display
        // the information from each column in this order.

        // Figure out the index of each column
        int idColumnIndex = cursor.getColumnIndex(DBContract.EqEntry.COLUMN_ID);
        int magColumnIndex = cursor.getColumnIndex(DBContract.EqEntry.COLUMN_MAG);
        int locationColumnIndex = cursor.getColumnIndex(DBContract.EqEntry.COLUMN_LOCATION);
        int dateColumnIndex = cursor.getColumnIndex(DBContract.EqEntry.COLUMN_DATE);
        int urlColumnIndex = cursor.getColumnIndex(DBContract.EqEntry.COLUMN_URL);
        int feltColumnIndex = cursor.getColumnIndex(DBContract.EqEntry.COLUMN_FELT);
        int longitudeColumnIndex = cursor.getColumnIndex(DBContract.EqEntry.COLUMN_LONGITUDE);
        int latitudeColumnIndex = cursor.getColumnIndex(DBContract.EqEntry.COLUMN_LATITUDE);
        int depthColumnIndex = cursor.getColumnIndex(DBContract.EqEntry.COLUMN_DEPTH);
        int distanceColumnIndex = cursor.getColumnIndex(DBContract.EqEntry.COLUMN_DISTANCE);

        // Iterate through all the returned rows in the cursor
        while (cursor.moveToNext()) {
            // Use that index to extract the String or Int value of the word
            // at the current row the cursor is on.
            int currentID = cursor.getInt(idColumnIndex);
            Double currentMag = cursor.getDouble(magColumnIndex);
            String currentLocation = cursor.getString(locationColumnIndex);
            String currentDate = cursor.getString(dateColumnIndex);
            String currentURL = cursor.getString(urlColumnIndex);
            int currentFelt = cursor.getInt(feltColumnIndex);
            Double currentLongitude = cursor.getDouble(longitudeColumnIndex);
            Double currentLatitude = cursor.getDouble(latitudeColumnIndex);
            int currentDepth = cursor.getInt(depthColumnIndex);
            int currentDistance = cursor.getInt(distanceColumnIndex);
            // Display the values from each column of the current row in the cursor in the TextView
            earthquakes.add(new Quake(currentMag, currentLocation, currentDate, currentURL, currentFelt, currentLongitude, currentLatitude, currentDepth, currentDistance));
        }
    } finally    {
        mAdapter.clear();
        mAdapter.addAll(earthquakes);
        // Always close the cursor when you're done reading from it. This releases all its
        // resources and makes it invalid.
        cursor.close();
    }
}
private void calculateDistance(){
    SQLiteDatabase db = mDbHelper.getWritableDatabase();
    String[] projection = {
            DBContract.EqEntry.COLUMN_ID,
            DBContract.EqEntry.COLUMN_LONGITUDE,
            DBContract.EqEntry.COLUMN_LATITUDE,
            DBContract.EqEntry.COLUMN_DEPTH,
            DBContract.EqEntry.COLUMN_DISTANCE
    };
    Cursor cursor = db.query(
            DBContract.EqEntry.TABLE_NAME,   // The table to query
            projection,            // The columns to return
            null,   // The columns for the WHERE clause
            null,                  // The values for the WHERE clause
            null,                  // Don't group the rows
            null,                  // Don't filter by row groups
            null,                  // The sort order
            null);
    try

    {
        int idColumnIndex = cursor.getColumnIndex(DBContract.EqEntry.COLUMN_ID);
        int longitudeColumnIndex = cursor.getColumnIndex(DBContract.EqEntry.COLUMN_LONGITUDE);
        int latitudeColumnIndex = cursor.getColumnIndex(DBContract.EqEntry.COLUMN_LATITUDE);
        int distanceColumnIndex = cursor.getColumnIndex(DBContract.EqEntry.COLUMN_DISTANCE);

        // Iterate through all the returned rows in the cursor
        while (cursor.moveToNext()) {
            // Use that index to extract the String or Int value of the word
            // at the current row the cursor is on.
            int currentID = cursor.getInt(idColumnIndex);
            double currentLongitude = cursor.getDouble(longitudeColumnIndex);
            double currentLatitude = cursor.getDouble(latitudeColumnIndex);
            double distance = (Math.sqrt(Math.pow((currentLongitude - longitudeNetwork), 2) + Math.pow((currentLatitude - latitudeNetwork), 2))) * 110.5;
            ContentValues values = new ContentValues();
            values.put(DBContract.EqEntry.COLUMN_DISTANCE, distance);
            db.update(DBContract.EqEntry.TABLE_NAME, values, null, null);
        }
    } finally    {
        // Always close the cursor when you're done reading from it. This releases all its
        // resources and makes it invalid.
        cursor.close();
    }
}
@Override
protected void onStop(){
    super.onStop();
    //delete DB
}
}


