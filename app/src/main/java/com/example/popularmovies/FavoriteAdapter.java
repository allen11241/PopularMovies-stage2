package com.example.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    final private ItemClickListener mItemClickListener;
    private List<FavoriteEntry> mFavoriteEntries;
    private final Context mContext;

    public FavoriteAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        mItemClickListener = listener;
    }

    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.favorite_list_item, parent, false);

        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavoriteViewHolder holder, int position) {
        FavoriteEntry favoriteEntry = mFavoriteEntries.get(position);
        String imageURL = "https://image.tmdb.org/t/p/w500" + favoriteEntry.getImage();
        //Checks to see if the movie has a poster
        if(imageURL.length() < 36){
            holder.mMovieImageView.setImageResource(R.drawable.no_poster_available);
        } else {
            Picasso.get().load(imageURL).into(holder.mMovieImageView);
        }
        holder.mMovieTitleTv.setText(favoriteEntry.getMovieName());
        holder.mMovieReleaseDateTv.setText(favoriteEntry.getReleaseDate());
    }

    @Override
    public int getItemCount() {
        if (mFavoriteEntries == null) {
            return 0;
        }
        return mFavoriteEntries.size();
    }

    public List<FavoriteEntry> getFavorites() {
        return mFavoriteEntries;
    }

    public void setFavorites(List<FavoriteEntry> favoriteEntries) {
        mFavoriteEntries = favoriteEntries;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView mMovieImageView;
        final TextView mMovieTitleTv;
        final TextView mMovieReleaseDateTv;

        FavoriteViewHolder(View view) {
            super(view);

            mMovieImageView = view.findViewById(R.id.iv_poster);
            mMovieTitleTv = view.findViewById(R.id.tv_title);
            mMovieReleaseDateTv = view.findViewById(R.id.tv_release_date);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int elementId = mFavoriteEntries.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }
    }
}