package eu.uniek.wwy.maps.heat;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Point;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

import eu.uniek.wwy.location.GPSLocation;

public class HeatMap extends Overlay {

	private List<GPSLocation> locations;
	private List<GPSLocation> landmarks;
	private Projection projection;
	private boolean _mustDraw = true; 

	public HeatMap(List<GPSLocation> locations, List<GPSLocation> landmarks, Projection projection) {
		this.locations = locations;
		this.projection = projection;
		this.landmarks = landmarks;
	}
	public void draw(Canvas canvas, MapView mapv, boolean shadow){
		super.draw(canvas, mapv, shadow);
		if (shadow || !_mustDraw)
			return;
		Paint mPaint = new Paint();
		mPaint.setDither(true);
		mPaint.setColor(Color.RED);
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(2);

		for(int index = 0; index < locations.size(); index++) {
			if(index != locations.size() -1) {
				GeoPoint oldLocation = new GeoPoint((int)(locations.get(index).getLatitude() * 1e6), (int)(locations.get(index).getLongitude() * 1e6));
				GeoPoint newLocation = new GeoPoint((int)(locations.get(index + 1).getLatitude() * 1e6), (int)(locations.get(index + 1).getLongitude() * 1e6));
				createLine(oldLocation, newLocation, canvas, mPaint);
			} else {
				break;
			}
		}
		for(GPSLocation landmark : landmarks) {
			if(landmark != null) {
				GeoPoint landmarkGeoPoint = new GeoPoint((int)(landmark.getLatitude() * 1e6), (int)(landmark.getLongitude() * 1e6));
				createLandmark(landmarkGeoPoint, canvas, mPaint);
			}
		}
	}

	private void createLine(GeoPoint oldLocation, GeoPoint newLocation, Canvas canvas, Paint mPaint) {
		Point p1 = new Point();
		Point p2 = new Point();
		Path path = new Path();

		projection.toPixels(oldLocation, p1);
		projection.toPixels(newLocation, p2);

		path.moveTo(p2.x, p2.y);
		path.lineTo(p1.x,p1.y);
		canvas.drawPath(path, mPaint);
	}
	private void createLandmark(GeoPoint point, Canvas canvas, Paint mPaint) {
		Point p1 = new Point();
		Path path = new Path();
		projection.toPixels(point, p1);
		path.addCircle(p1.x, p1.y, 20, Direction.CCW);
		canvas.drawPath(path, mPaint);
	}
	@Override
	public boolean onTouchEvent(MotionEvent e, MapView mapView) {
		if (MotionEvent.ACTION_DOWN == e.getAction())
			_mustDraw = false;
		else if (e.getAction() == MotionEvent.ACTION_UP)
			_mustDraw = true;
		return super.onTouchEvent(e, mapView);
	}
}