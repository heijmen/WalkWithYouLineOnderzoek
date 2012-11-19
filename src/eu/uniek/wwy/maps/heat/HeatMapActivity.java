package eu.uniek.wwy.maps.heat;

import java.io.IOException;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Projection;

import eu.uniek.wwy.R;
import eu.uniek.wwy.database.OnderzoekDatabase;
import eu.uniek.wwy.maps.KMlExport;
import eu.uniek.wwy.utils.ToastUtil;

public class HeatMapActivity extends MapActivity {
	private HeatMap overlay;
	private Projection projection;
	private MapView mapView;
	private OnderzoekDatabase onderzoekDatabase;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_heat_map);
		onderzoekDatabase = new OnderzoekDatabase(this);
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		
		projection = mapView.getProjection();
		this.overlay = new HeatMap(onderzoekDatabase, projection);
		mapView.getOverlays().add(overlay);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_map, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) { 
		switch (item.getItemId()) {
		case R.id.exportToKMZ:
			KMlExport kmzExporter = new KMlExport();
			String result = null;
			try {
				result = kmzExporter.exportToKMl(this, onderzoekDatabase);
			} catch (IOException e) {
				ToastUtil.showToast(getApplicationContext(), e.getMessage());
			}
			ToastUtil.showToast(getApplicationContext(), result);
			return true;
		case R.id.Sattelite:
			mapView.setSatellite(!mapView.isSatellite());
			return true;
		default:
			return false;
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}