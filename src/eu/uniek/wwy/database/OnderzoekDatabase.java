package eu.uniek.wwy.database;

import java.util.List;

import android.content.Context;

import com.google.android.maps.GeoPoint;

public class OnderzoekDatabase {

	private DatabaseHandler mDatabaseHandler;
	private static final String DATABASE_NAME = "onderzoekdatabase";
	private static final int DATABASE_VERSION = 2;


	public OnderzoekDatabase(Context context) {
		mDatabaseHandler = new DatabaseHandler(context, DATABASE_NAME, DATABASE_VERSION);
	}

	public void addBreadCrumb(GeoPoint geoPoint) {
		mDatabaseHandler.addGeoPoint(geoPoint);
	}

	public void addHerkenningPunt(HerkenningPunt herkenningPunt) {
		mDatabaseHandler.addHerkenningPunt(herkenningPunt);
	}
	
	public List<HerkenningPunt> getHerkenningPunten() {
		return mDatabaseHandler.getHerkenningPunten();
	}
	public int getBreadcrumbsRowCount() {
		return mDatabaseHandler.getGeoPointRowCount();
	}
	public GeoPoint getBreadCrumb(int index) {
		return mDatabaseHandler.getGeoPoint(index);
	}

	public void deleteCrumbs() {
		mDatabaseHandler.deleteCrumbs();
	}
	public void deleteHerkenningsPunten() {
		mDatabaseHandler.deleteHerkenningsPunten();
	}

}
