package com.example.android.quakereport;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Konis on 11/30/2017.
 */

public class EarthquakeAdapter extends RecyclerView.Adapter<EarthquakeAdapter.EarthquakeAdapterViewHolder> {
String[] myData;
    private final EarthquakeAdapterOnClickHandler mClickHandler;

    public interface EarthquakeAdapterOnClickHandler {
        void onClick(String currentEarthquake);
    }
    public EarthquakeAdapter(EarthquakeAdapterOnClickHandler clickHandler){
        mClickHandler = clickHandler;
    }
    public class EarthquakeAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mMagnitudeTextView;

        public EarthquakeAdapterViewHolder(View view) {
            super(view);
            mMagnitudeTextView = (TextView) view.findViewById(R.id.magnitude);
            // COMPLETED (7) Call setOnClickListener on the view passed into the constructor (use 'this' as the OnClickListener)
            view.setOnClickListener(this);
        }

        // COMPLETED (6) Override onClick, passing the clicked day's data to mClickHandler via its onClick method
        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String selectedEarthquake = myData[adapterPosition];
            mClickHandler.onClick(selectedEarthquake);
        }
    }

    @Override
    public EarthquakeAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new EarthquakeAdapterViewHolder(view);
    }
    @Override
    public void onBindViewHolder(EarthquakeAdapterViewHolder earthquakeAdapterViewHolder, int position) {
        //String currentEarthquake = myData[position];
        //EarthquakeAdapterViewHolder.mMagnitudeTextView.setText(currentEarthquake);
    }
    @Override
    public int getItemCount() {
        //if (null == mWeatherData) return 0;
        //return myData.length;
        return 0;
    }
    public void setEarthquakeData(String[] myData) {
        //myData = weatherData;
        notifyDataSetChanged();
    }
}
