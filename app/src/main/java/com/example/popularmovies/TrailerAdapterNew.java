package com.example.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class TrailerAdapterNew extends RecyclerView.Adapter<TrailerAdapterNew.TrailerAdapterNewViewHolder> {

    private List<String> mTrailerData;
    private Context mContext;
    final private TrailerAdapterNew.ItemClickListener mItemClickListener;

    public TrailerAdapterNew(Context context, TrailerAdapterNew.ItemClickListener listener) {
        mContext = context;
        mItemClickListener = listener;
    }

    @Override
    public TrailerAdapterNewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.trailer_list_item, parent, false);
        
        return new TrailerAdapterNewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapterNewViewHolder movieAdapterViewHolder, int position) {
        String currentTrailer = mTrailerData.get(position);

    }

    @Override
    public int getItemCount() {
        if (mTrailerData == null) {
            return 0;
        }
        return mTrailerData.size();
    }

    public List<String> getTrailers() {
        return mTrailerData;
    }

    public void setMovieData(List<String> trailerData) {
        mTrailerData = trailerData;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClickListener(String item);
    }

    class TrailerAdapterNewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Class variables for the favorite description and priority TextViews
        TextView mTrailerTextView;

        /**
         * Constructor for the TrailerAdapterNewViewHolders.
         *
         * @param view The view inflated in onCreateViewHolder
         */
        public TrailerAdapterNewViewHolder(View view) {
            super(view);
            
            mTrailerTextView = view.findViewById(R.id.trailer_tv);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            String selectedTrailer = mTrailerData.get(getAdapterPosition());
            mItemClickListener.onItemClickListener(selectedTrailer);
        }
    }
}