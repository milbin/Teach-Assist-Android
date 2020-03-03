package com.teachassist.teachassist;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import org.paoloconte.smoothchart.SmoothLineChartEquallySpaced;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Double.parseDouble;
import static java.lang.Float.parseFloat;
import static java.lang.String.valueOf;

public class AssignmentStatsFragment extends Fragment {
    Context context;
    View fragment;
    AppCompatActivity activity;
    String courseCode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.assignments_stats_view, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getContext();
        fragment = getView();
        activity = (AppCompatActivity) getActivity();
        JSONObject assignments = ((CourseInfoActivity)activity).assignments;
        if(assignments == null){
            return;
        }
        System.out.println(assignments);
        System.out.println("HERE");
        renderGraphs(assignments);
    }
    private void renderGraphs(JSONObject assignments){
        //GraphView graphK = fragment.findViewById(R.id.graphK);
        //GraphView graphT = fragment.findViewById(R.id.graphT);
        //GraphView graphC = fragment.findViewById(R.id.graphC);
        //GraphView graphA = fragment.findViewById(R.id.graphA);
        HashMap data = calculateAveragesOverTime(assignments);
        if(data.get("average") != null) {
            SmoothLineChartEquallySpaced grid = fragment.findViewById(R.id.courseAverageGraph);
            grid.setGraphColours(resolveColorAttr(context, R.attr.primaryPink),resolveColorAttr(context, R.attr.Background) );
            grid.setData((float[])data.get("average"));
            /*GraphView graph = fragment.findViewById(R.id.courseAverageGraph);
            LineGraphSeries<DataPoint> lineSeries = new LineGraphSeries<DataPoint>((DataPoint[]) data.get("average"));
            //PointsGraphSeries<DataPoint> pointSeries = new PointsGraphSeries<DataPoint>((DataPoint[]) data.get("average"));
            //pointSeries.setSize(10);
            lineSeries.setAnimated(true);
            lineSeries.setDrawDataPoints(true);
            lineSeries.setDataPointsRadius(15);
            //lineSeries.setThickness(10);
            //lineSeries.setColor(resolveColorAttr(context, R.attr.primaryPink));
            // custom paint to make a dotted line
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(10);
            paint.setPathEffect(new CornerPathEffect(20));
            paint.setColor(resolveColorAttr(context, R.attr.primaryPink));
            lineSeries.setCustomPaint(paint);

            graph.addSeries(lineSeries);
            //graph.addSeries(pointSeries);
            //graph.getViewport().setMinY(0);
            //graph.getViewport().setMaxY(100);
            //graph.getViewport().setYAxisBoundsManual(true);
            graph.getViewport().setBackgroundColor(resolveColorAttr(context, R.attr.Background));
            graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
            //graph.getGridLabelRenderer().setHu*/
        }
    }

    private HashMap calculateAveragesOverTime(JSONObject assignments){
        TA ta = new TA();
        float[] averageOverTime = new float[assignments.length()-1];
        ArrayList kOverTime = new ArrayList();
        ArrayList tOverTime = new ArrayList();
        ArrayList cOverTime = new ArrayList();
        ArrayList aOverTime = new ArrayList();

        JSONObject assignmentsSoFar = new JSONObject();
        try {
            assignmentsSoFar.put("categories", assignments.getJSONObject("categories"));
        }catch (Exception e){}
        for (int i=0; i<(assignments.length()-1); i++) { //the minus 1 is meant to take care of the 'categories' key
            try {
                JSONObject assignment = assignments.getJSONObject(valueOf(i));
                assignmentsSoFar.put(valueOf(i), assignment);
                try {
                    float courseAverage = parseFloat(ta.CalculateCourseAverageFromAssignments(assignmentsSoFar, 0));
                    averageOverTime[i] = courseAverage;
                }catch (Exception ignore){}
            }catch (Exception ignore){}
        }
        HashMap returnMap = new HashMap();
        returnMap.put("average", averageOverTime);
        return returnMap;
    }
    @ColorInt
    public static int resolveColorAttr(Context context, @AttrRes int colorAttr) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(colorAttr, typedValue, true);
        TypedArray arr =context.obtainStyledAttributes(typedValue.data, new int[]{
                colorAttr});
        return arr.getColor(0, -1);
    }
}
