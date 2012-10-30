package eu.uniek.wwy.maps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;
import eu.uniek.wwy.database.DataWrapper;
import eu.uniek.wwy.export.XMLExport;
import eu.uniek.wwy.maps.heat.HeatMapActivity;
import eu.uniek.wwy.walkwithyouonderzoek.AskEmail;

public class KMZExport {
	
	private String filePath;

	public String exportToKMZ(HeatMapActivity heatMapActivity, DataWrapper wrapper) throws IOException {
		File file = new File(Environment.getExternalStorageDirectory().getPath()+"/wwy");
		file.mkdir();
		filePath = Environment.getExternalStorageDirectory().getPath()+"/wwy/heatmap.kml";
		String toExport = new XMLExport().getXMLString(wrapper);
		FileOutputStream fileOutputStream = new FileOutputStream(filePath);
		fileOutputStream.write(toExport.getBytes());
		fileOutputStream.close();
		sendToEmail(heatMapActivity);
		return "Send export to emailadress";
	}
	
	private void sendToEmail(HeatMapActivity h) {
		Intent i = createMailIntent(h);
		startActivity(h, i);
	}
	
	private Intent createMailIntent(HeatMapActivity h) {
		SharedPreferences settings = h.getSharedPreferences(AskEmail.PREFS_NAME, 0);
		String email = settings.getString("email", null);
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL  , email);
		i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
		i.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + filePath));
		return i;
	}

	private void startActivity(HeatMapActivity h, Intent i) {
		try {
			h.startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(h, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
	}

}
