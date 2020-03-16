package org.paoloconte.smoothchart;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;


public class SmoothLineChartEquallySpaced extends View {
	
	private int CHART_COLOR = 0xFF0099CC;
	private int CHART_BACKGROUND_COLOUR = 0xffffff;
	private int CHART_AXES_COLOUR = 0xffffff;
	private int CHART_INTERVAL_COLOUR;
	private Typeface GRAPH_FONT;
	private final int CIRCLE_SIZE = 8;
	private final int STROKE_SIZE = 2;
	private final float DATAPOINTS_SCALE_COEFF = 1.2f; //shrink the actual line graph based on this coefficient
	private final int DATAPOINTS_MARGIN_START = 60; //shrink the actual line graph based on this coefficient
	private final float SMOOTHNESS = 0.35f; // the higher the smoother, but don't go over 0.5
	
	private final Paint mPaint;
	private final Path mPath;
	private final float mCircleSize;
	private final float mStrokeSize;
	private final float mBorder;
	
	private float[] mValues;
	private float mMinY;
	private float mMaxY;
	private int incrementValue = 20;
	

	public SmoothLineChartEquallySpaced(Context context) {
		this(context, null, 0);
	}

	public SmoothLineChartEquallySpaced(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SmoothLineChartEquallySpaced(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);



		float scale = context.getResources().getDisplayMetrics().density;
		
		mCircleSize = scale * CIRCLE_SIZE;
		mStrokeSize = scale * STROKE_SIZE;
		mBorder = 1.5f*mCircleSize;
		
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(mStrokeSize);
		
		mPath = new Path();
	}
	public void setGraphColours(int primaryColour, int backgroundColour, int axesColour, int intervalColour, Typeface font){
		CHART_COLOR = primaryColour;
		CHART_BACKGROUND_COLOUR = backgroundColour;
		CHART_AXES_COLOUR = axesColour;
		CHART_INTERVAL_COLOUR = intervalColour;
		GRAPH_FONT = font;
		invalidate();
	}
	
	public void setData(float[] values) {
		ArrayList newValues = new ArrayList();

		for(float value: values){
			if(value != -1f) { //if it equals -1 then that means there are currently no
				// assignments in this category and therefore this mark should not count as a zero
				newValues.add(value);
			}
		}
		float[] valuesArray = new float[newValues.size()];
		for(int i=0;i<newValues.size(); i++){
			valuesArray[i] = (float) newValues.get(i);
		}

		mValues = valuesArray;
		
		if (mValues != null && mValues.length > 1) {
			mMinY = mValues[0];
			mMaxY = mValues[0];
			for (float y : mValues) {
				if (y >= mMaxY) {
					if ((y % incrementValue) == 0) {
						mMaxY = y;
					} else
						mMaxY = y - (y % incrementValue) + incrementValue; //rounds up to the nearest increment value
				}
				if (y <= mMinY) {
					mMinY = y - (y % incrementValue); //rounds down to the nearest increment value
				}
			}
			if(mMaxY == mMinY){
				mMaxY += 10;
				mMinY -= 10;
			}

			if(mMaxY - mMinY <= 20){
				incrementValue = 5;
			}else if(mMaxY- mMinY <= 70){
				incrementValue = 10;
			}else{
				incrementValue = 20;
			}
		}else{
			mMaxY = 100;
			mMinY = 0;
			incrementValue = 20;
		}
				
		invalidate();
	}
	
	public void draw(Canvas canvas) {
		super.draw(canvas);

		final float height = getMeasuredHeight() - 2*mBorder;
		final float width = getMeasuredWidth() - 2*mBorder;

		final float dX = mValues.length > 1 ? DATAPOINTS_SCALE_COEFF*(mValues.length-1)  : (2);
		final float dY = (mMaxY-mMinY) > 0 ? (mMaxY-mMinY) : (2);

		mPath.reset();

		// draw axes
		Paint axisPaint = new Paint();
		Path axisPath = new Path();
		axisPaint.setColor(CHART_AXES_COLOUR);
		axisPaint.setStyle(Style.STROKE);
		axisPaint.setStrokeWidth(mStrokeSize);
		axisPath.moveTo(0, 0);
		axisPath.lineTo(0, height+mBorder);
		axisPath.lineTo(width+mBorder, height+mBorder);
		canvas.drawPath(axisPath, axisPaint);

		//draw data intervals
		float[] intervals = new float[200/incrementValue + 1];
		int index = 0;
		for(float i=0.0f; i<=200f; i+=incrementValue){
			intervals[index] = i;
			index++;
		}
		axisPaint.setColor(CHART_INTERVAL_COLOUR);
		axisPaint.setStrokeWidth(mStrokeSize/2);

		Paint textPaint = new Paint();
		textPaint.setTypeface(GRAPH_FONT);
		textPaint.setColor(CHART_AXES_COLOUR);
		textPaint.setTextSize(30);
		for(float yPoint : intervals){
			float y = height+mBorder - (yPoint-mMinY)*height/dY;
			axisPath.moveTo(0, y);
			canvas.drawText(((int)yPoint)+"%", 6, y-5, textPaint);
			axisPath.lineTo(width+mBorder, y);
		}
		canvas.drawPath(axisPath, axisPaint);
		
		if (mValues == null || mValues.length == 0) {
			float y = height+mBorder - (60-mMinY)*height/dY;
			textPaint.setColor(CHART_INTERVAL_COLOUR);
			canvas.drawText("No Assignments Yet!", 90, y-5, textPaint);
			return;
		}
		int size = mValues.length;
				
		// calculate point coordinates
		List<PointF> points = new ArrayList<PointF>(size);		
		for (int i=0; i<size; i++) {
			float x = mBorder + DATAPOINTS_MARGIN_START + i * width / dX;
			float y = mBorder + height - (mValues[i] - mMinY) * height / dY;
			points.add(new PointF(x, y));
		}

		// calculate smooth path
		float lX = 0, lY = 0;
		mPath.moveTo(points.get(0).x, points.get(0).y);
		for (int i=1; i<size; i++) {	
			PointF p = points.get(i);	// current point
			
			// first control point
			PointF p0 = points.get(i-1);	// previous point
			float x1 = p0.x + lX; 	
			float y1 = p0.y + lY;
	
			// second control point
			PointF p1 = points.get(i+1 < size ? i+1 : i);	// next point
			lX = (p1.x-p0.x)/2*SMOOTHNESS;		// (lX,lY) is the slope of the reference line 
			lY = (p1.y-p0.y)/2*SMOOTHNESS;
			float x2 = p.x - lX;	
			float y2 = p.y - lY;

			// add line
			mPath.cubicTo(x1,y1,x2, y2, p.x, p.y);		
		}
		

		// draw path
		mPaint.setColor(CHART_COLOR);
		mPaint.setStyle(Style.STROKE);
		canvas.drawPath(mPath, mPaint);


		/*// draw area
		if (size > 0) {
			mPaint.setStyle(Style.FILL);
			mPaint.setColor((CHART_COLOR & 0xFFFFFF) | 0x10000000);
			mPath.lineTo(points.get(size-1).x, height+mBorder);
			mPath.lineTo(points.get(0).x, height+mBorder);
			mPath.close();
			canvas.drawPath(mPath, mPaint);
		}*/


		// draw circles
		mPaint.setColor(CHART_COLOR);
		mPaint.setStyle(Style.FILL_AND_STROKE);
		for (PointF point : points) {
			canvas.drawCircle(point.x, point.y, mCircleSize/2, mPaint);
		}
		mPaint.setStyle(Style.FILL);
		mPaint.setColor(CHART_BACKGROUND_COLOUR);
		for (PointF point : points) {
			canvas.drawCircle(point.x, point.y, (mCircleSize-mStrokeSize)/2, mPaint);
		}
		
	}
}
