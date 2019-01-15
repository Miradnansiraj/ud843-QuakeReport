package com.example.android.quakereport.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.quakereport.EarthQuake;
import com.example.android.quakereport.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EarthQuakeAdapter extends ArrayAdapter<EarthQuake> {
    private static final String LOCATION_SEPARATOR = " of ";
    public EarthQuakeAdapter(@NonNull Context context, ArrayList<EarthQuake> earthQuakes) {
        super(context, 0, earthQuakes);
    }

    //ViewHolder class to reduce search
    static class ViewHolder
    {
        private TextView mag,location, country, date, time;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.card_item, parent, false);
            holder = new ViewHolder();
            holder.mag = convertView.findViewById(R.id.mag_string);
            holder.location = convertView.findViewById(R.id.location_string);
            holder.country = convertView.findViewById(R.id.country_string);
            holder.date = convertView.findViewById(R.id.date_string);
            holder.time = convertView.findViewById(R.id.time_string);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        //Set TextViews to smallcaps if api is above 21 and font as times new roman for api 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            holder.mag.setFontFeatureSettings("smcp");
            holder.country.setFontFeatureSettings("smcp");
            holder.date.setFontFeatureSettings("smcp");
            holder.time.setFontFeatureSettings("smcp");
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                Typeface typeface = convertView.getResources().getFont(R.font.tnr);
                holder.mag.setTypeface(typeface);
                holder.country.setTypeface(typeface);
                holder.date.setTypeface(typeface);
                holder.time.setTypeface(typeface);
            }
        }
        EarthQuake currentQuake = getItem(position);
        String originalLocation = currentQuake.getTitle();
        String location;
        String country;
        if (originalLocation.contains(LOCATION_SEPARATOR)) {
            String[] parts = originalLocation.split(LOCATION_SEPARATOR);
            location = parts[0] + LOCATION_SEPARATOR;
            country = parts[1];
        } else {
            location = getContext().getString(R.string.near_the);
            country = originalLocation;
        }
        Date date = new Date(currentQuake.getTimestamp());
        String formattedDate = formatDate(date);
        String formattedTime = formatTime(date);
        GradientDrawable gradientDrawable = (GradientDrawable) holder.mag.getBackground();
        int magColor = getMagnitudeColor(currentQuake.getMag());
        gradientDrawable.setColor(magColor);

        holder.mag.setText(String.format("%2.1f",currentQuake.getMag()));//Double.toString(currentQuake.getMag())
        holder.location.setText(location);
        holder.country.setText(country);
        holder.date.setText(formattedDate);
        holder.time.setText(formattedTime);
        return convertView;
    }

    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    /**
     * Return the formatted date string (i.e. "4:30 PM") from a Date object.
     */
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }

    private int getMagnitudeColor(double magnitude) {
        Log.d("magnitude:",Double.toString(magnitude));
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                return ContextCompat.getColor(getContext(), R.color.magnitude1);
            case 2:
                return ContextCompat.getColor(getContext(), R.color.magnitude2);
            case 3:
                return ContextCompat.getColor(getContext(), R.color.magnitude3);
            case 4:
                return ContextCompat.getColor(getContext(), R.color.magnitude4);
            case 5:
                return ContextCompat.getColor(getContext(), R.color.magnitude5);
            case 6:
                return ContextCompat.getColor(getContext(), R.color.magnitude6);
            case 7:
                return ContextCompat.getColor(getContext(), R.color.magnitude7);
            case 8:
                return ContextCompat.getColor(getContext(), R.color.magnitude8);
            case 9:
                return ContextCompat.getColor(getContext(), R.color.magnitude9);
            default:
                return ContextCompat.getColor(getContext(), R.color.magnitude10plus);
        }
    }
}
