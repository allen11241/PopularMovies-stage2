package com.example.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class MyTrailerAdapter extends RecyclerView.Adapter<MyTrailerAdapter.MyTrailerAdapterViewHolder> {

    private List<String> mTrailerData;
    private final Context mContext;
    private final MyTrailerAdapterOnClickHandler mClickHandler;

    public interface MyTrailerAdapterOnClickHandler {
        void onClick(String movieInfo);
    }

    public MyTrailerAdapter(Context context, MyTrailerAdapterOnClickHandler clickHandler) {
        mTrailerData = new ArrayList<>();
        mContext = context;
        mClickHandler = clickHandler;
    }

    public class MyTrailerAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        final TextView mTrailerTextView;

        MyTrailerAdapterViewHolder(View view) {
            super(view);
            mTrailerTextView = view.findViewById(R.id.trailer_tv);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String selectedTrailer = mTrailerData.get(adapterPosition);
            mClickHandler.onClick(selectedTrailer);
        }
    }

    @Override
    public MyTrailerAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new MyTrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyTrailerAdapterViewHolder movieAdapterViewHolder, int position) {
        int trailerNumber = position + 1;
        String trailerNumString = Integer.toString(trailerNumber);
        String displayedTrailerLabel = mContext.getString(R.string.trailer) + " " + trailerNumString;
        movieAdapterViewHolder.mTrailerTextView.setText(displayedTrailerLabel);
    }

    @Override
    public int getItemCount() {
        if (null == mTrailerData) return 0;
        return mTrailerData.size();
    }

    public void setTrailerData(List<String> movieData) {
        Log.v("LOG", "movieData: " + movieData.size());
        mTrailerData = movieData;
        notifyDataSetChanged();
    }

    public void clearData(){
        mTrailerData.clear();
        notifyDataSetChanged();
    }
}