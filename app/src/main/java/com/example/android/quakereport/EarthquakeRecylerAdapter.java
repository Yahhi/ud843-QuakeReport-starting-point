package com.example.android.quakereport;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Konis on 11/30/2017.
 */
public class EarthquakeRecylerAdapter extends RecyclerView.Adapter<EarthquakeRecylerAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";
    private List<Quake> earthquakes = new ArrayList<Quake>();

    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    private final Context mContext;
    int magnitudeFloor;
    final private EarthquakeRecylerAdapterOnClickHandler mClickHandler;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public interface EarthquakeRecylerAdapterOnClickHandler {
        void onClick(int position);

    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param mDataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public EarthquakeRecylerAdapter(@NonNull Context context, EarthquakeRecylerAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {


        viewHolder.getmagTextView().setText(earthquakes.get(position).getMag());
        viewHolder.getLocationPrimTV().setText(earthquakes.get(position).getLocation());
        viewHolder.getLocationOffTV().setText(earthquakes.get(position).getLocationOff());
        viewHolder.getDateTV().setText(earthquakes.get(position).getDate());
        viewHolder.getTimeTV().setText(earthquakes.get(position).getTime());
        magnitudeFloor = (int) Math.floor(earthquakes.get(position).getOrigMag());
        switch (magnitudeFloor) {
            case 0:
            case 1:
                viewHolder.getmagTextView().setTextColor(mContext.getResources().getColor(R.color.magnitude1));
                break;
            case 2:
                viewHolder.getmagTextView().setTextColor(mContext.getResources().getColor(R.color.magnitude2));
                break;
            case 3:
                viewHolder.getmagTextView().setTextColor(mContext.getResources().getColor(R.color.magnitude3));
                break;
            case 4:
                viewHolder.getmagTextView().setTextColor(mContext.getResources().getColor(R.color.magnitude4));
                break;
            case 5:
                viewHolder.getmagTextView().setTextColor(mContext.getResources().getColor(R.color.magnitude5));
                break;
            case 6:
                viewHolder.getmagTextView().setTextColor(mContext.getResources().getColor(R.color.magnitude6));
                break;
            case 7:
                viewHolder.getmagTextView().setTextColor(mContext.getResources().getColor(R.color.magnitude7));
                break;
            case 8:
                viewHolder.getmagTextView().setTextColor(mContext.getResources().getColor(R.color.magnitude8));
                break;
            case 9:
                viewHolder.getmagTextView().setTextColor(mContext.getResources().getColor(R.color.magnitude9));
                break;
            default:
                viewHolder.getmagTextView().setTextColor(mContext.getResources().getColor(R.color.magnitude10plus));
                break;
        }
    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    //TODO here it gets null error
    @Override
    public int getItemCount() {
        if (earthquakes.isEmpty()) return 0;
        return earthquakes.size();
    }

    void swapCursor(List<Quake> earthquake) {
        earthquakes = earthquake;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView magTextView;
        private final TextView locationPrimTV;
        private final TextView locationOffTV;
        private final TextView dateTV;
        private final TextView timeTV;


        public ViewHolder(View v) {
            super(v);

            magTextView = (TextView) v.findViewById(R.id.magnitude);
            locationPrimTV = (TextView) v.findViewById(R.id.primary_location);
            locationOffTV = (TextView) v.findViewById(R.id.location_offset);
            dateTV = (TextView) v.findViewById(R.id.date);
            timeTV = (TextView) v.findViewById(R.id.time);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mClickHandler.onClick(position);

        }

        public TextView getmagTextView() {
            return magTextView;
        }

        public TextView getLocationPrimTV() {
            return locationPrimTV;
        }

        public TextView getLocationOffTV() {
            return locationOffTV;
        }

        public TextView getDateTV() {
            return dateTV;
        }

        public TextView getTimeTV() {
            return timeTV;
        }
    }
}