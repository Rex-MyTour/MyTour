package apps.dabinu.com.tourapp.models;

public class TourLocationModel {

    public String locationName, locationType, locationStory;
    public double latitude, longitude;

    public TourLocationModel(){

    }

    public TourLocationModel(String locationName, String locationType, String locationStory, double latitude, double longitude){
        this.locationName = locationName;
        this.locationType = locationType;
        this.locationStory = locationStory;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getLocationType() {
        return locationType;
    }

    public String getLocationStory() {
        return locationStory;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}