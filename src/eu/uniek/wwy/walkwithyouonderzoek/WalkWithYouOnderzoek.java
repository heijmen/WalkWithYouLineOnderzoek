package eu.uniek.wwy.walkwithyouonderzoek;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.wwy.database.HerkenningPunt;
import com.wwy.gps.GPSLocationListener;

import eu.uniek.wwy.R;
import eu.uniek.wwy.database.OnderzoekDatabase;
import eu.uniek.wwy.maps.heat.HeatMapActivity;
import eu.uniek.wwy.utils.ToastUtil;

public class WalkWithYouOnderzoek extends Activity {

	private Handler updateHandler;
	private Runnable updateRunnable;
	private LocationManager locationManager;
	private GPSLocationListener gpsLocationListener = new GPSLocationListener();
	private Context context = this;
	private OnderzoekDatabase onderzoekDatabase;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_walk_with_you_onderzoek);
		onderzoekDatabase = new OnderzoekDatabase(this);
		checkEmailIsSet();
		ToggleButton button = (ToggleButton) findViewById(R.id.AanUitKnop);
		updateHandler = new Handler();
		updateRunnable = new Runnable () {
			public void run() {
				if(gpsLocationListener.getCurrentLocation() != null) {
					onderzoekDatabase.addBreadCrumb(gpsLocationListener.getCurrentLocation());
				}
				updateHandler.postDelayed(this, 3000);
			}
		};
		button.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					on();
				} else {
					stop();
				}
			}
		});
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		final Context c = this;
		Button mapButton = (Button) findViewById(R.id.map);
		mapButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent(c, HeatMapActivity.class);
				startActivity(i);
			}
		});

		Button herkingButton = (Button) findViewById(R.id.herkenningspunt_button);
		herkingButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					if(gpsLocationListener != null && gpsLocationListener.getCurrentLocation() != null) {
						AlertDialog.Builder alert = new AlertDialog.Builder(context);

						alert.setTitle("Herkennings punt toevoegen");
						alert.setMessage("We zouden graag willen weten waarom u dit punt nou toevoegt als herkenningspunt, dit kunt u overslaan.");

						// Set an EditText view to get user input 
						final EditText input = new EditText(context);
						alert.setView(input);

						alert.setPositiveButton("Oke!", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
						  String value = input.getText().toString();
						  	HerkenningPunt herkenningsPunt = new HerkenningPunt(gpsLocationListener.getCurrentLocation(), value);
						  	onderzoekDatabase.addHerkenningPunt(herkenningsPunt);
						  }
						});

						alert.setNegativeButton("Overslaan!", new DialogInterface.OnClickListener() {
						  public void onClick(DialogInterface dialog, int whichButton) {
							  HerkenningPunt herkenningsPunt = new HerkenningPunt(gpsLocationListener.getCurrentLocation(), "Gebruiker drukte op overslaan!");
							  	onderzoekDatabase.addHerkenningPunt(herkenningsPunt);
						  }
						});

						alert.show();
//						dataWrapper.getPointsOfInterest().add(gpsLocationListener.getCurrentLocation());
//						dao.saveData(dataWrapper, getFile());
//						ToastUtil.showToast(v.getContext(), "Het herkenningspunt is toegevoed");
					}
				} catch (Exception e) {
					ToastUtil.showToast(v.getContext(), e.getMessage());
				}
			}
		});
	}

	private void checkEmailIsSet() {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		String email = settings.getString("email", null);
		if(email == null || email.equals("")) {
			Intent i = new Intent(this, AskEmail.class);
			startActivity(i);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_walk_with_you_onderzoek, menu);
		return true;
	}

	public void stop() {
		updateHandler.removeCallbacks(updateRunnable);
		locationManager.removeUpdates(gpsLocationListener);
	}

	public void on() {
		updateRunnable.run();
		Criteria c = new Criteria();
		c.setAccuracy(Criteria.ACCURACY_FINE);
		String provider =locationManager.getBestProvider(c, true);
		ToastUtil.showToast(this, provider);
		locationManager.requestLocationUpdates(provider, 500, 1, gpsLocationListener);
	}
	


	@Override
	public boolean onOptionsItemSelected(MenuItem item) { 
		switch (item.getItemId()) {
		case R.id.verwijderGegegevensMenuItem:
			new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle("Verwijderen van Gegevens")
			.setMessage("Waarom zou je je gevens willen verwijderen?")
			.setPositiveButton("Doe toch maar!", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					onderzoekDatabase.deleteCrumbs();
				}
			})
			.setNegativeButton("Nee alsjeblieft niet doen..", null)
			.show();
			return true;
		case R.id.veranderEmail:
			Intent i = new Intent(this, AskEmail.class);
			startActivity(i);
			return true;
		default:
			return false;
		}
	}

}
