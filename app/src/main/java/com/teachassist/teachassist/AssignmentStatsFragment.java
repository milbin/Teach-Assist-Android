package com.teachassist.teachassist;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import org.paoloconte.smoothchart.SmoothLineChartEquallySpaced;

import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
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
        renderGraphs(assignments);
    }
    private void renderGraphs(JSONObject assignments){
        //GraphView graphK = fragment.findViewById(R.id.graphK);
        //GraphView graphT = fragment.findViewById(R.id.graphT);
        //GraphView graphC = fragment.findViewById(R.id.graphC);
        //GraphView graphA = fragment.findViewById(R.id.graphA);
        ArrayList data = calculateAveragesOverTime(assignments);
        if(data.get(0) != null) { //average over time is null
            SmoothLineChartEquallySpaced[] grids = new SmoothLineChartEquallySpaced[]{
                    fragment.findViewById(R.id.courseAverageGraph),
                    fragment.findViewById(R.id.KAverageGraph),
                    fragment.findViewById(R.id.TAverageGraph),
                    //fragment.findViewById(R.id.courseAverageGraph),
                    //fragment.findViewById(R.id.courseAverageGraph)

            };
            int gridNumber = 0;
            for (SmoothLineChartEquallySpaced grid : grids) {
                Typeface typeface = ResourcesCompat.getFont(context, R.font.sandstone);
                int lineGraphColour = resolveColorAttr(context, R.attr.primaryGreen);
                if(gridNumber == 0){
                    lineGraphColour = resolveColorAttr(context, R.attr.primaryPink);
                }
                grid.setGraphColours(
                        lineGraphColour,
                        resolveColorAttr(context, R.attr.Background),
                        resolveColorAttr(context, R.attr.textColor),
                        resolveColorAttr(context, R.attr.unhighlightedTextColor),
                        typeface
                );
                grid.setData((float[]) data.get(0));
                //grid.setData(new float[]{95f, 96f, 97f, 98f, 99f, 100f, 1f});
                gridNumber++;
            }
        }
    }

    private ArrayList calculateAveragesOverTime(JSONObject assignments){
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
        ArrayList gridDataSets = new ArrayList();
        gridDataSets.add(averageOverTime);
        return gridDataSets;
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
