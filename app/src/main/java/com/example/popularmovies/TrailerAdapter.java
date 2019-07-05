package com.example.popularmovies;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class TrailerAdapter extends ArrayAdapter<String> {

    public TrailerAdapter(Activity context, List<String> trailers) {
        super(context, 0, trailers);
    }

    public void clear(){
        //List.clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.trailer_list_item, parent, false);
        }
        // Get the {@link Song} object located at this position in the list
        final String currentTrailer = getItem(position);
        int trailerNumber = position + 1;
        String trailerNumString = Integer.toString(trailerNumber);

        // Find the TextView in the list_item.xml layout with the ID version_name
        TextView trailerTextView = listItemView.findViewById(R.id.trailer_tv);
        trailerTextView.setText(getContext().getString(R.string.trailer) + " " + trailerNumString);
        trailerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentTrailer));
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + currentTrailer));
                try {
                    getContext().startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    getContext().startActivity(webIntent);
                }
            }
        });

        return listItemView;
    }
}