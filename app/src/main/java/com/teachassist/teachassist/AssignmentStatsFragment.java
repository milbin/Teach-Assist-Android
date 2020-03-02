package com.teachassist.teachassist;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONObject;

import java.util.ArrayList;

import static java.lang.Double.parseDouble;
import static java.lang.String.valueOf;

public class AssignmentStatsFragment extends Fragment {
    Context context;
    JSONObject assignments;
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
        assignments = ((CourseInfoActivity)activity).assignments;
        if(assignments == null){
            return;
        }
        System.out.println(assignments);
        System.out.println("HERE");
        calculateAveragesOverTime(assignments);
    }
    private void renderGraphs(){
        GraphView graph = fragment.findViewById(R.id.courseAverageGraph);
        //GraphView graphK = fragment.findViewById(R.id.graphK);
        //GraphView graphT = fragment.findViewById(R.id.graphT);
        //GraphView graphC = fragment.findViewById(R.id.graphC);
        //GraphView graphA = fragment.findViewById(R.id.graphA);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        graph.addSeries(series);
    }

    private void calculateAveragesOverTime(JSONObject assignments){
        TA ta = new TA();
        ArrayList averageOverTime = new ArrayList();
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
                    Double courseAverage = parseDouble(ta.CalculateCourseAverageFromAssignments(assignmentsSoFar, 0));
                    averageOverTime.add(courseAverage);
                }catch (Exception ignore){}
            }catch (Exception ignore){}
        }
        System.out.println(averageOverTime);
    }
}
