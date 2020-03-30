package com.bullb.ctf.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Rate implements Parcelable {
    // define object properties
    private String id;
    private String name;
    private String position;
    private String location;
    private String image;

    // Constructor
    public Rate(String id, String name, String position, String location, String image){
        this.id = id;
        this.name = name;
        this.position = position;
        this.location = location;
        this.image = image;
    }

    // Takes a Parcel and produce an object populated with it's values
    private Rate(Parcel in) {
        String[] data = new String[5];

        in.readStringArray(data);
        this.id = data[0];
        this.name = data[1];
        this.position = data[2];
        this.location = data[3];
        this.image = data[4];
    }

    // Getters
    public String getId(){
        return this.id;
    }
    public String getName(){
        return this.name;
    }
    public String getPosition(){
        return this.position;
    }
    public String getLocation(){
        return this.location;
    }
    public String getImage(){
        return this.image;
    }


    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeStringArray(new String[] {
                this.id,
                this.name,
                this.position,
                this.location,
                this.image});
    }

    public static final Parcelable.Creator<Rate> CREATOR = new Parcelable.Creator<Rate>() {
        public Rate createFromParcel(Parcel in) {
            return new Rate(in);
        }

        public Rate[] newArray(int size) {
            return new Rate[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}