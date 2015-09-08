package com.example.ipshita.mymasterdetailtestapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ipshita.mymasterdetailtestapplication.R;
import com.example.ipshita.mymasterdetailtestapplication.adapters.viewholder.ArtistViewHolder;
import com.example.ipshita.mymasterdetailtestapplication.models.Artist;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Ipshita on 02-07-2015.
 */
public class ArtistAdapter extends ArrayAdapter<Artist> {
    private static final int IMAGE_HEIGHT = 200;
    private static final int IMAGE_WIDTH = 200;
    private ArtistViewHolder holder;

    public ArtistAdapter(Context context, List<Artist> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_artist, parent, false);
            holder = new ArtistViewHolder();
            holder.artistName = (TextView) convertView.findViewById(R.id.list_item_name_textview);
            holder.artistThumbnail = (ImageView) convertView.findViewById(R.id.list_item_artist_image_view);
            convertView.setTag(holder);
        } else {
            holder = (ArtistViewHolder) convertView.getTag();
        }
        // get current artist using position
        Artist artist = getItem(position);
        // Set imageView in the list item
        if (null != artist.getArtistThumbnailLink() && !artist.getArtistThumbnailLink().isEmpty())
            Picasso.with(getContext()).load(artist.getArtistThumbnailLink()).resize(IMAGE_WIDTH, IMAGE_HEIGHT).centerCrop().into(holder.artistThumbnail);
        // Set text in the list item with artist's name
        holder.artistName.setText(artist.getArtistName());

        return convertView;
    }
}
