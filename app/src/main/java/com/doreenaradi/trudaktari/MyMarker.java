package com.doreenaradi.trudaktari;

/**
 * Created by johnolwamba on 5/12/2016.
 */
public class MyMarker {

    //variable declarations
    double Mlatitude;
    double Mlongitudes;
    String Mid;
    String Mname;
    String Mcategory;


    //this is the constructor
    public MyMarker(double latitude, double longitude, final String id, final String name, final String category)
    {
        this.Mlatitude=latitude;
        this.Mlongitudes=longitude;
        this.Mid = id;
        this.Mname = name;
        this.Mcategory = category;

    }

    //these are the getters and setters
    public double getMlatitude() {
        return Mlatitude;
    }

    public void setMlatitude(double mlatitude) {
        Mlatitude = mlatitude;
    }

    public double getMlongitudes() {
        return Mlongitudes;
    }

    public void setMlongitudes(double mlongitudes) {
        Mlongitudes = mlongitudes;
    }

    public String getMid() {
        return Mid;
    }

    public void setMid(String mid) {
        Mid = mid;
    }

    public String getMname() {
        return Mname;
    }

    public void setMname(String mname) {
        Mname = mname;
    }

    public String getMcategory() {
        return Mcategory;
    }

    public void setMcategory(String mcategory) {
        Mname = mcategory;
    }

}

