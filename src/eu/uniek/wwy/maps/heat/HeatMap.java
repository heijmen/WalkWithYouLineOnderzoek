package eu.uniek.wwy.maps.heat;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Point;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MapView.ReticleDrawMode;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

import eu.uniek.wwy.database.OnderzoekDatabase;

public class HeatMap extends Overlay {

	private Projection projection;
	private OnderzoekDatabase mOnderzoekDatabase;
	private boolean _mustDraw = true; 
	private Path path;
	private Point lastPoint;
	private boolean cantCall = false;

	public HeatMap(OnderzoekDatabase onderzoekDatabase, Projection projection) {
		this.mOnderzoekDatabase = onderzoekDatabase;
		this.projection = projection;
	}
	public void draw(Canvas canvas, MapView mapv, boolean shadow){
		super.draw(canvas, mapv, shadow);
		if (shadow || !_mustDraw)
			return;
		mapv.setReticleDrawMode(ReticleDrawMode.DRAW_RETICLE_UNDER);
		drawGFX(canvas);
		cantCall = true;
		
	}
	
	public void drawGFX(Canvas canvas) {
		createPath();
		canvas.drawPath(path, createPaint());
	}
	
	private void createPath() {
		path = new Path();
		boolean firstLocationSet = false;
		for(int index = 1; index < mOnderzoekDatabase.getBreadcrumbsRowCount(); index++) {
			GeoPoint newLocation = mOnderzoekDatabase.getBreadCrumb(index);
			if(!firstLocationSet) {
				firstLocationSet = true;
				lastPoint = new Point();
				projection.toPixels(newLocation, lastPoint);
			} else {
				if(newLocation != null) {
					createLine(newLocation);
				}
			}
		}
		for(GeoPoint landmark : mOnderzoekDatabase.getHerkenningPunten()) {
			if(landmark != null) {
				createLandmark(landmark);
			}
		}
	}

	private void createLine(GeoPoint newLocation) {
		Point newPoint = new Point();
		projection.toPixels(newLocation, newPoint);
		path.moveTo(lastPoint.x, lastPoint.y);
		path.lineTo(newPoint.x,newPoint.y);
		lastPoint = newPoint;

	}

	private void createLandmark(GeoPoint point) {
		Point p1 = new Point();
		projection.toPixels(point, p1);
		path.addCircle(p1.x, p1.y, 20, Direction.CCW);
	}

	@Override
	public boolean onTouchEvent(MotionEvent e, MapView mapView) {
		if (MotionEvent.ACTION_DOWN == e.getAction())
			_mustDraw = false;
		else if (MotionEvent.ACTION_MOVE == e.getAction())
			_mustDraw = false;
		else if (e.getAction() == MotionEvent.ACTION_UP)
			_mustDraw = true;
		return super.onTouchEvent(e, mapView);
	}
	private Paint createPaint() {
		Paint mPaint = new Paint();
		mPaint.setDither(false);
		mPaint.setColor(Color.RED);
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(2);
		return mPaint;
	}
}