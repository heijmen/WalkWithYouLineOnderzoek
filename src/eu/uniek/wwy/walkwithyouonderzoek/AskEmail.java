package eu.uniek.wwy.walkwithyouonderzoek;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import eu.uniek.wwy.R;

public class AskEmail extends Activity {

	public static final String PREFS_NAME = "Wwy";
	private Context context = this;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ask_email);
		final EditText field = (EditText) findViewById(R.id.emailAdressTextField);
		Button button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("email", field.getText().toString());
				editor.commit();
				Intent onderzoekIntent = new Intent(arg0.getContext(), WalkWithYouOnderzoek.class);
				startActivity(onderzoekIntent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_ask_email, menu);
		return true;
	}
}
