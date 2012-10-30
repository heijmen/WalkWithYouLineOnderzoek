package eu.uniek.wwy.maps.heat;

import java.io.File;
import java.io.IOException;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Projection;

import eu.uniek.wwy.R;
import eu.uniek.wwy.database.BreadcrumbsDAO;
import eu.uniek.wwy.database.DataWrapper;
import eu.uniek.wwy.maps.KMlExport;
import eu.uniek.wwy.utils.ToastUtil;

public class HeatMapActivity extends MapActivity {
	private HeatMap overlay;
	private DataWrapper dataWrapper = new DataWrapper();
	private BreadcrumbsDAO dao = new BreadcrumbsDAO();
	private Projection projection;
	private MapView mapView;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_heat_map); 
		try {
			File file = new File(getFile());
			if(file.exists()) {
				dataWrapper = dao.getData(getFile());
			} else {
				dataWrapper = new DataWrapper();
			}
		} catch (Exception e) {
			ToastUtil.showToast(this, e.getMessage());
		}
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		
		projection = mapView.getProjection();
		this.overlay = new HeatMap(dataWrapper.getBreadcrumbs(), dataWrapper.getPointsOfInterest(), projection);
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
				result = kmzExporter.exportToKMl(this, dataWrapper);
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
	public String getFile() {
		File root = getExternalFilesDir(null);
		File wwydaba = new File(root + "/wwydaba.obj");
		return "" + wwydaba;
	}

}