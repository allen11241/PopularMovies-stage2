package com.example.popularmovies;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends ArrayAdapter<Review> {

    public ReviewAdapter(Activity context, List<Review> reviews) {
        super(context, 0, reviews);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.review__list_item, parent, false);
        }
        // Get the {@link Song} object located at this position in the list
        Review currentReview = getItem(position);

        // Find the TextView in the list_item.xml layout with the ID version_name
        TextView authorTextView = listItemView.findViewById(R.id.review_author);
        authorTextView.setText(currentReview.getAuthor());

        TextView reviewTextView = listItemView.findViewById(R.id.review_content);
        reviewTextView.setText(currentReview.getContent());

        return listItemView;
    }
}