package apps.dabinu.com.tourapp.Geofencing;

public class TourPlace {

    private String placeId;
    private String placeName;
    private String placeSlogan;
    private double placeLat, placeLng;


    public TourPlace(String placeId, String placeName, String placeSlogan, double placeLat, double placeLng) {
        this.placeId = placeId;
        this.placeName = placeName;
        this.placeSlogan = placeSlogan;
        this.placeLat = placeLat;
        this.placeLng = placeLng;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getPlaceSlogan() {
        return placeSlogan;
    }

    public double getPlaceLat() {
        return placeLat;
    }

    public double getPlaceLng() {
        return placeLng;
    }
}
