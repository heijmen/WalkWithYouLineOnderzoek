package eu.uniek.wwy.maps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.Toast;
import eu.uniek.wwy.database.OnderzoekDatabase;
import eu.uniek.wwy.export.XMLExport;
import eu.uniek.wwy.maps.heat.HeatMapActivity;

public class KMlExport {
	
	private String filePath;

	public String exportToKMl(Context context, OnderzoekDatabase onderzoekDatabase) throws IOException {
		File file = new File(Environment.getExternalStorageDirectory().getPath()+"/wwy");
		file.mkdir();
		filePath = Environment.getExternalStorageDirectory().getPath()+"/wwy/heatmap.kml";
		String toExport = new XMLExport().getXMLString(onderzoekDatabase);
		FileOutputStream fileOutputStream = new FileOutputStream(filePath);
		fileOutputStream.write(toExport.getBytes());
		fileOutputStream.close();
		sendToEmail(context);
		return "Send export to emailadress";
	}
	
	private void sendToEmail(Context context) {
		Intent i = createMailIntent(context);
		startActivity(context, i);
	}
	
	private Intent createMailIntent(Context context) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		String email = settings.getString("email", null);
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL  ,new String[] { email });
		i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
		i.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + filePath));
		return i;
	}

	private void startActivity(Context context, Intent i) {
		try {
			context.startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
	}

}
