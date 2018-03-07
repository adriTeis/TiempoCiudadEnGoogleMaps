package com.example.adrianmontes.aplicacionseleccionpersonal.Modelo;

/**
 * Created by adrian.montes on 28/2/18.
 */

public class Ciudad {
    String name;
    String Region;
    String east,north,south,west;
    int[] Temperatura;
    private double Longitude;
    private double Latitude;

    public String getEast() {
        return this.east;
    }

    public void setEast(String east) {
        this.east = east;
    }

    public String getNorth() {
        return north;
    }

    public void setNorth(String north) {
        this.north = north;
    }

    public String getSouth() {
        return this.south;
    }

    public void setSouth(String south) {
        this.south = south;
    }

    public String getWest() {
        return this.west;
    }

    public void setWest(String west) {
        this.west = west;
    }

    public Ciudad() {
    }

    public int[] getTemperatura() {
        return Temperatura;
    }

    public void setTemperatura(int[] temperatura) {
        Temperatura = temperatura;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return Region;
    }

    public void setRegion(String region) {
        Region = region;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }
}
