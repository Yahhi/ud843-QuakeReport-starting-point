package com.example.android.quakereport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.graphics.drawable.GradientDrawable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.support.v4.content.ContextCompat;

/**
 * Created by Konis on 8/2/2017.
 */

public class QuakeAdapter extends ArrayAdapter<Quake> {
    public QuakeAdapter(Context context, ArrayList<Quake> earthquakes){
super(context, 0, earthquakes);
    }
    private static final String LOCATION_SEPARATOR = " of ";
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View listItemView = convertView;
        if (listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }
        Quake currentQuake = getItem(position);
        //magnitude
        String FrMagnitude = currentQuake.getMag();
        TextView magnitude = (TextView) listItemView.findViewById(R.id.magnitude);
        magnitude.setText(FrMagnitude);
        //GradientDrawable magnitudeCircle = (GradientDrawable) magnitude.getBackground();
        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(currentQuake.getOrigMag());
        // Set the color on the magnitude circle
        magnitude.setTextColor(magnitudeColor);
        //location
        String primaryLocation = currentQuake.getLocation();
        String locationOffset = currentQuake.getLocationOff();
        String letstry = currentQuake.getKdistance();
        //time and date
        String dateToDisplay = currentQuake.getDate();
        String timeToDisplay = currentQuake.getTime();
        //setting views
        //this one got about 10 views to work with while recycler view deals only with one
        TextView primaryLocationView = null;
        primaryLocationView.setText(primaryLocation);
        TextView locationOffsetView = null;
        locationOffsetView.setText(locationOffset);
        TextView date = (TextView) listItemView.findViewById(R.id.date);
        date.setText(dateToDisplay);
        TextView time = (TextView) listItemView.findViewById(R.id.time);
        time.setText(timeToDisplay);
        return listItemView;
    }
    ///old adapter, just white

    public int getMagnitudeColor(double mag){
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(mag);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }
}
