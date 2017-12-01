//package com.example.android.quakereport;
//
//import android.content.AsyncTaskLoader;
//import android.content.Context;
//
//import java.util.List;
//
///**
// * Created by Konis on 8/24/2017.
// */
//
//public class EarthquakeLoader  extends AsyncTaskLoader<List<Quake>> {
//    /** Tag for log messages */
//    private static final String LOG_TAG = EarthquakeLoader.class.getName();
//
//    /** Query URL */
//    private String mUrl;
//    public EarthquakeLoader(Context context, String url) {
//        super(context);
//        mUrl = url;
//    }
//
//    @Override
//    protected void onStartLoading() {
//        forceLoad();
//    }
//
//    @Override
//    public List<Quake> loadInBackground() {
//        if (mUrl == null) {
//            return null;
//        }
//
//        // Perform the network request, parse the response, and extract a list of earthquakes.
//        List<Quake> earthquakes = QueryUtils.fetchEarthquakeData(mUrl);
//        return earthquakes;
//    }
//}