package ru.test.intech.androidsongsearch.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public Song createFromParcel(Parcel parcel)
        {
            return new Song(parcel);
        }

        public Song[] newArray(int i)
        {
            return new Song[i];
        }
    };

    private String name;
    private String artist;
    private String artworkSmall;
    private String artworkLarge;
    private String previewUrl;

    public Song() {}

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getArtworkSmall() {
        return artworkSmall;
    }

    public String getArtworkLarge() {
        return artworkLarge;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setArtworkSmall(String artworkSmall) {
        this.artworkSmall = artworkSmall;
    }

    public void setArtworkLarge(String artworkLarge) {
        this.artworkLarge = artworkLarge;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    Song(Parcel in) {
        name = in.readString();
        artist = in.readString();
        artworkSmall = in.readString();
        artworkLarge = in.readString();
        previewUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(artist);
        parcel.writeString(artworkSmall);
        parcel.writeString(artworkLarge);
        parcel.writeString(previewUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
