package eu.uniek.wwy.walkwithyouonderzoek;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import eu.uniek.wwy.R;
import eu.uniek.wwy.database.HerkenningPunt;
import eu.uniek.wwy.database.OnderzoekDatabase;
import eu.uniek.wwy.maps.KMlExport;
import eu.uniek.wwy.utils.ToastUtil;

public class WalkWithYouOnderzoek extends Activity {

	private Handler updateHandler;
	private Runnable updateRunnable;
	private LocationManager locationManager;
	private GPSLocationListener gpsLocationListener = new GPSLocationListener();
	private Context context = this;
	private OnderzoekDatabase onderzoekDatabase;
	private boolean checked = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_walk_with_you_onderzoek);

		onderzoekDatabase = new OnderzoekDatabase(this);
		checkEmailIsSet();
		final Button button = (Button) findViewById(R.id.AanUitKnop);
		updateHandler = new Handler();
		updateRunnable = new Runnable () {
			public void run() {
				if(gpsLocationListener.getCurrentLocation() != null) {
					onderzoekDatabase.addBreadCrumb(gpsLocationListener.getCurrentLocation());
				}
				updateHandler.postDelayed(this, 10000);
			}
		};

		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(!isChecked()) {
					button.setBackgroundDrawable(getResources().getDrawable(R.drawable.fuckingdrawables));
					button.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.heijmenvinkje), null, null, null);
					button.setText("App uitschakelen");
					on();

				} else {
					button.setBackgroundDrawable(getResources().getDrawable(R.drawable.redgradient));
					button.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.redcross), null, null, null);
					button.setText("App inschakelen");
					stop();
				}
				checked = !checked;
			}
		});

		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		final Context c = this;
		
		Button mapButton = (Button) findViewById(R.id.map);
		mapButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
//				Intent i = new Intent(c, HeatMapActivity.class);
//				startActivity(i);
				KMlExport kmzExporter = new KMlExport();
				String result = null;
				try {
					result = kmzExporter.exportToKMl(context, onderzoekDatabase);
				} catch (IOException e) {
					ToastUtil.showToast(getApplicationContext(), e.getMessage());
				}
				ToastUtil.showToast(getApplicationContext(), result);
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
					}
				} catch (Exception e) {
					ToastUtil.showToast(v.getContext(), e.getMessage());
				}
			}
		});
	}

	protected boolean isChecked() {
		return checked;
	}

	private void checkEmailIsSet() {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		String email = settings.getString("email", null);
		if(email == null || email.equals("")) {
			//			Intent i = new Intent(this, AskEmail.class);
			//			startActivity(i);
			openChangeEmailDialog();
		}
	}

	private void openChangeEmailDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);

		alert.setTitle("Email voor Export");
		alert.setMessage("De gegevens die worden verzameld worden opgestuurt naar een email, uw kunt het hier invullen of later s" +
				"nog veranderen in het menu");

		// Set an EditText view to get user input 
		final EditText input = new EditText(context);
		input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		alert.setView(input);

		alert.setPositiveButton("Oke!", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("email", value);
				editor.commit();
				Intent onderzoekIntent = new Intent(context, WalkWithYouOnderzoek.class);
				startActivity(onderzoekIntent);
			}
		});

		alert.show();
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
				public void onClick(DialogInterface dialogInterface, int arg1) {
					onderzoekDatabase.deleteCrumbs();
					onderzoekDatabase.deleteHerkenningsPunten();
				}
			})
			.setNegativeButton("Nee alsjeblieft niet doen..", null)
			.show();
			return true;
		case R.id.veranderEmail:
			openChangeEmailDialog();
			return true;
		default:
			return false;
		}
	}

}
