package eu.uniek.wwy.walkwithyouonderzoek;


import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;

public class GPSLocationListener implements LocationListener {  
	private GeoPoint currentLocation;


	public void onLocationChanged(Location location) {  
		 int lat = (int) (location.getLatitude() * 1E6);
	     int lng = (int) (location.getLongitude() * 1E6);
	     GeoPoint point = new GeoPoint(lat, lng);
	     currentLocation = point;
	}
	
	public void onProviderDisabled(String provider) {}
	public void onProviderEnabled(String provider) {}
	public void onStatusChanged(String provider, int status, Bundle extras) {}

	public GeoPoint getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(GeoPoint currentLocation) {
		this.currentLocation = currentLocation;
	}
}
