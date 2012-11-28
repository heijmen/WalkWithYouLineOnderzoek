package eu.uniek.wwy.database;

import com.google.android.maps.GeoPoint;

public class HerkenningPunt extends GeoPoint {

	private String mComment; 
	
	public HerkenningPunt(GeoPoint geoPoint, String mComment) {
		super(geoPoint.getLatitudeE6(), geoPoint.getLongitudeE6());
		this.mComment = mComment;
	}

	public String getmComment() {
		return mComment;
	}
	

}
