package com.example.connor.LiveWeatherAppPartIII;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Connor on 10/18/2016.
 */

public class City implements Parcelable {
    private String mv_cityName, mv_cityZip, mv_cityState, mv_cityWeatherDesc, mv_cityWeatherTypeIcon;
    private double mv_cityTemp, mv_cityTempHigh, mv_cityTempLow;
    private int mv_cityTempFormat = 1;
    //TempFormat refers to if it is Kelvin (0), Celsius (1), or Fahrenheit (2). Default is Celsius

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mv_cityName);
        out.writeString(mv_cityZip);
        out.writeString(mv_cityState);
        out.writeDouble(mv_cityTemp);
        out.writeDouble(mv_cityTempHigh);
        out.writeDouble(mv_cityTempLow);
        out.writeInt(mv_cityTempFormat);
        out.writeString(mv_cityWeatherTypeIcon);
        out.writeString(mv_cityWeatherDesc);
    }

    private City(Parcel in){
        mv_cityName = in.readString();
        mv_cityZip = in.readString();
        mv_cityState = in.readString();
        mv_cityTemp = in.readDouble();
        mv_cityTempHigh = in.readDouble();
        mv_cityTempLow = in.readDouble();
        mv_cityTempFormat = in.readInt();
        mv_cityWeatherTypeIcon = in.readString();
        mv_cityWeatherDesc = in.readString();
    }

    //empty constructor
    public City(){
    }

    //constructor with just a zip
    public City(String cityZip){
        this.mv_cityZip = cityZip;
    }

    //constructor with just the base info
    public City(String cityZip, String cityName, String cityState){
        this.mv_cityZip = cityZip;
        this.mv_cityName = cityName;
        this.mv_cityState = cityState;
    }

    //complete constructor
    public City(String cityZip, String cityName, String cityState, double cityTemp, double cityTempHigh, double cityTempLow, int cityTempFormat, String cityWeatherId, String cityWeatherDesc){
        this.mv_cityZip = cityZip;
        this.mv_cityName = cityName;
        this.mv_cityState = cityState;
        this.mv_cityTemp = cityTemp;
        this.mv_cityTempHigh = cityTempHigh;
        this.mv_cityTempLow = cityTempLow;
        this.mv_cityTempFormat = cityTempFormat;
        this.mv_cityWeatherTypeIcon = cfp_convertWeatherIdToType(cityWeatherId);
        this.mv_cityWeatherDesc = cityWeatherDesc;
    }

    private String cfp_convertWeatherIdToType(String id){
        if(id.matches("2\\d\\d"))
            return "F";
        else if(id.matches("3\\d\\d"))
            return "6";
        else if(id.matches("5\\d\\d"))
            return "0";
        else if(id.matches("6\\d\\d"))
            return "9";
        else if(id.matches("7\\d\\d"))
            return "Y";
        else if(id.matches("800"))
            return "I";
        else if(id.matches("8\\d\\d"))
            return "C";
        else if(id.matches("90\\d"))
            return "g";
        else if(id.matches("951"))
            return "I";
        else if(id.matches("9\\d\\d"))
            return "b";
        else
            return "H";
    }

    @Override
    public int describeContents(){
        return 0;
    }

    public static final Parcelable.Creator<City> CREATOR
            = new Parcelable.Creator<City>() {

        // This simply calls our new constructor (typically private) and
        // passes along the unmarshalled `Parcel`, and then returns the new object!
        @Override
        public City createFromParcel(Parcel in) {
            return new City(in);
        }

        // We just need to copy this and change the type to match our class.
        @Override
        public City[] newArray(int size) {
            return new City[size];
        }
    };

    public String getCityName() {
        return mv_cityName;
    }

    public void setCityName(String mv_cityName) {
        this.mv_cityName = mv_cityName;
    }

    public String getCityZip() {
        return mv_cityZip;
    }

    public void setCityZip(String mv_cityZip) {
        this.mv_cityZip = mv_cityZip;
    }

    public String getCityState() {
        return mv_cityState;
    }

    public void setCityState(String mv_cityState) {
        this.mv_cityState = mv_cityState;
    }

    public double getCityTemp() {
        return convertTemp(mv_cityTemp);
    }

    public void setCityTemp(double mv_cityTemp) {
        this.mv_cityTemp = mv_cityTemp;
    }

    public String getCityWeatherDesc() {
        return mv_cityWeatherDesc;
    }

    public void setCityWeatherDesc(String mv_cityWeatherDesc) {
        this.mv_cityWeatherDesc = mv_cityWeatherDesc;
    }

    public String getCityWeatherTypeIcon() {
        return mv_cityWeatherTypeIcon;
    }

    public void setCityWeatherTypeIcon(String mv_cityWeatherId) {
        this.mv_cityWeatherTypeIcon = cfp_convertWeatherIdToType(mv_cityWeatherId);
    }

    public double getCityTempHigh() {
        return convertTemp(mv_cityTempHigh);
    }

    public void setCityTempHigh(double mv_cityTempHigh) {
        this.mv_cityTempHigh = mv_cityTempHigh;
    }

    public double getCityTempLow() {
        return convertTemp(mv_cityTempLow);
    }

    public void setCityTempLow(double mv_cityTempLow) {
        this.mv_cityTempLow = mv_cityTempLow;
    }

    public int getCityTempFormat() {
        return mv_cityTempFormat;
    }

    public void setCityTempFormat(int mv_cityTempFormat) {
        this.mv_cityTempFormat = mv_cityTempFormat;
    }

    public String getCityTempFormatAbv(){
        switch (mv_cityTempFormat){
            case 0: return "K°";
            case 1: return "C°";
            case 2: return "F°";
            default: return "K°";

        }
    }

    //The temperature is always stored as kelvin, this just controls what is returned to the getters for the temp methods.
    private double convertTemp(double temp) {
        double newTemp = temp;
        switch (mv_cityTempFormat){
            case 0: return newTemp;
            case 1: return (double) Math.round(newTemp - 273.15); //convert to C
            case 2: return (double) Math.round(newTemp * 9/5 - 459.67); //convert to F
            default: return newTemp; //default is do nothing

        }
    }

    public String toString() {
        return "Name: " + mv_cityName +
                "\nZip: " + mv_cityZip +
                "\nState: " + mv_cityState +
                "\nTemp: " + mv_cityTemp +
                "\nTempHi: " + mv_cityTempHigh +
                "\nTempLo: " + mv_cityTempLow +
                "\nTempFormat: " + mv_cityTempFormat +
                "\nWeatherID: " + mv_cityWeatherTypeIcon +
                "\nWeatherDesc: " + mv_cityWeatherDesc;
    }

}
