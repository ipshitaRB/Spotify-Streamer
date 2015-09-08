package com.example.ipshita.mymasterdetailtestapplication.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ipshita on 09-07-2015.
 */
public class Track implements Parcelable {
    // create String instance variable for album name
    private final String albumName;
    // create string instance variable for album cover image
    private final String albumThumbnailLink;
    // create string instance variable for the track's name
    private final String trackName;
    // create String instance variable for artist name
    private final String artistName;



    // create String instance variable for previewURL
    private final String previewURL;


    // create private constructor using fields
    private Track(Builder builder) {
        this.albumName = builder.albumName;
        this.albumThumbnailLink = builder.albumThumbnailLink;
        this.trackName = builder.trackName;
        this.artistName = builder.artistName;
        this.previewURL = builder.previewURL;

    }

    protected Track(Parcel in) {
        albumName = in.readString();
        albumThumbnailLink = in.readString();
        trackName = in.readString();
        artistName = in.readString();
        previewURL = in.readString();
    }


    public static final Creator<Track> CREATOR = new Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };

    public String getAlbumName() {
        return albumName;
    }

    public String getAlbumThumbnailLink() {
        return albumThumbnailLink;
    }

    public String getTrackName() {
        return trackName;
    }

    public String getPreviewURL() {
        return previewURL;
    }

    public String getArtistName() {
        return artistName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(albumName);
        dest.writeString(albumThumbnailLink);
        dest.writeString(trackName);
        dest.writeString(artistName);
        dest.writeString(previewURL);
    }


    // creating Builder Pattern
    public static class Builder {
        private String albumName;
        private String albumThumbnailLink;
        private String trackName;
        private String artistName;
        private String previewURL;


        // builder methods for setting property
        public Builder albumName(String albumName) {
            this.albumName = albumName;
            return this;
        }

        public Builder albumThumbnailLink(String albumThumbnailLink) {
            this.albumThumbnailLink = albumThumbnailLink;
            return this;
        }

        public Builder trackName(String trackName) {
            this.trackName = trackName;
            return this;
        }

        public Builder artistName(String artistName) {
            this.artistName = artistName;
            return this;
        }

        public Builder previewURL(String previewURL) {
            this.previewURL = previewURL;
            return this;
        }

        public Track build() {
            return new Track(this);
        }

    }
}
