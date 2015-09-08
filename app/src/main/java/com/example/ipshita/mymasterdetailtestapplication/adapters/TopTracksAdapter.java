package com.example.ipshita.mymasterdetailtestapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ipshita.mymasterdetailtestapplication.R;
import com.example.ipshita.mymasterdetailtestapplication.adapters.viewholder.TopTracksViewHolder;
import com.example.ipshita.mymasterdetailtestapplication.models.Track;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by Ipshita on 09-07-2015.
 */
public class TopTracksAdapter extends ArrayAdapter<Track> {
    private static final int IMAGE_WIDTH = 200;
    private static final int IMAGE_HEIGHT = 200;
    TopTracksViewHolder holder;

    public TopTracksAdapter(Context context, List<Track> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_tracks, parent, false);
            holder = new TopTracksViewHolder();
            holder.trackName = (TextView) convertView.findViewById(R.id.list_item_track_name_textview);

            holder.albumName = (TextView) convertView.findViewById(R.id.list_item_album_name_textview);
            holder.albumThumbnail = (ImageView) convertView.findViewById(R.id.list_item_album_thumbnail_imageview);
            convertView.setTag(holder);
        } else {
            holder = (TopTracksViewHolder) convertView.getTag();
        }
        // get current track using position
        Track track = getItem(position);
        // Set imageView in the list item
        if (null != track.getAlbumThumbnailLink() && !track.getAlbumThumbnailLink().isEmpty())
            Picasso.with(getContext()).load(track.getAlbumThumbnailLink()).resize(IMAGE_WIDTH, IMAGE_HEIGHT).centerCrop().into(holder.albumThumbnail);
        // Set text in the list item with album name

        holder.albumName.setText(track.getAlbumName());

        holder.trackName.setText(track.getTrackName());

        return convertView;
    }
}
