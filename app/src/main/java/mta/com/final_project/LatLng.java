package mta.com.final_project;

public class LatLng {
    private Double latitude;
    private Double longitude;

    public LatLng() {}
    public LatLng (Double _latitude,Double _longitude)
    {
        latitude = _latitude;
        longitude = _longitude;
    }

    public  LatLng (com.google.android.gms.maps.model.LatLng _latLng)
    {
        latitude= _latLng.latitude;
        longitude=_latLng.longitude;
    }
    public Double getLatitude() {
        return latitude;
    }



    public Double getLongitude() {
        return longitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
