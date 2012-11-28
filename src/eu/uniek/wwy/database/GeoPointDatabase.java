package eu.uniek.wwy.database;

import com.google.android.maps.GeoPoint;

public interface GeoPointDatabase {
	
	public int getGeoPointRowCount();
	public GeoPoint getGeoPoint(int index);
	public void addGeoPoint(GeoPoint geoPoint);

}
