package com.teachassist.teachassist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;

public class AssignmentStatsActivity extends AppCompatActivity {
    Button assignmentsButton;
    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPreferences.getBoolean("lightThemeEnabled", false)){
            setTheme(R.style.LightTheme);
        }else{
            setTheme(R.style.DarkTheme);
        }
        setContentView(R.layout.assignments_stats_view);

        assignmentsButton = findViewById(R.id.assignmentsButton);
        assignmentsButton.setOnClickListener(new onAssignmentsButtonClick());
    }
    public class onAssignmentsButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            assignmentsButton.setTextColor(resolveColorAttr(context, R.attr.textColor));
            ((Button)findViewById(R.id.statisticsButton)).setTextColor(resolveColorAttr(context, R.color.unhighlightedTextColor));
            finish();
            overridePendingTransition(0, 0);
        }
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
