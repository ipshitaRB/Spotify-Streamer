package com.example.ipshita.mymasterdetailtestapplication.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ipshita on 02-07-2015.
 */
public class Artist implements Parcelable {
    public static final Parcelable.Creator<Artist> CREATOR
            = new Parcelable.Creator<Artist>() {
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };
    // create String instance variable for artist's name
    private final String artistName;
    // create string instance variable for artist's image
    private final String artistThumbnailLink;
    // create artist id to request artist's top tracks
    private final String artistId;

    // create private constructor using fields
    private Artist(Builder builder) {
        this.artistName = builder.artistName;
        this.artistThumbnailLink = builder.artistThumbnailLink;
        this.artistId = builder.artistId;
    }

    private Artist(Parcel parcel) {
        this.artistName = parcel.readString();
        this.artistThumbnailLink = parcel.readString();
        this.artistId = parcel.readString();
    }

    public String getArtistName() {
        return artistName;
    }

    public String getArtistThumbnailLink() {
        return artistThumbnailLink;
    }

    public String getArtistId() {
        return artistId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(artistName);
        dest.writeString(artistThumbnailLink);
        dest.writeString(artistId);
    }

    // creating Builder Pattern
    public static class Builder {
        private String artistName;
        private String artistThumbnailLink;
        private String artistId;

        // builder methods for setting property
        public Builder artistName(String artistName) {
            this.artistName = artistName;
            return this;
        }

        public Builder artistThumbnailLink(String artistThumbnailLink) {
            this.artistThumbnailLink = artistThumbnailLink;
            return this;
        }

        public Builder artistId(String artistId) {
            this.artistId = artistId;
            return this;
        }

        public Artist build() {
            return new Artist(this);
        }

    }
}
