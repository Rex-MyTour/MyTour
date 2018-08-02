package apps.dabinu.com.tourapp.Geofencing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.maps.model.LatLng;

import apps.dabinu.com.tourapp.activities.HomeMapActivity;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = GeofenceBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the Geofence Event from the Intent sent through
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(TAG, String.format("Error code : %d", geofencingEvent.getErrorCode()));
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        double lat = geofencingEvent.getTriggeringLocation().getLatitude();
        double lng = geofencingEvent.getTriggeringLocation().getLongitude();

        String place = geofencingEvent.getTriggeringLocation().toString();

        LatLng latlng = new LatLng(lat, lng);
        // Check which transition type has triggered this event
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            HomeMapActivity.addMarkerToLocation(latlng, place);
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            HomeMapActivity.removeMarkerAtLocation(latlng);
        } else {
            // Log the error.
            Log.e(TAG, String.format("Unknown transition : %d", geofenceTransition));
            // No need to do anything else
//            return;
        }
    }

}
