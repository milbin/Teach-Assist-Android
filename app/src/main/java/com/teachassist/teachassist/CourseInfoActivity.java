package com.teachassist.teachassist;

import android.content.Context;
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
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import org.json.JSONObject;

import static java.lang.Math.abs;

public class CourseInfoActivity extends AppCompatActivity {
    Button assignmentsButton;
    Button statisticsButton;
    Context context = this;
    private static final int NUM_PAGES = 2;
    private ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;
    private View currentPageSlider;
    private Button editButton;
    public JSONObject assignments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPreferences.getBoolean("lightThemeEnabled", false)){
            setTheme(R.style.LightTheme);
        }else{
            setTheme(R.style.DarkTheme);
        }
        setContentView(R.layout.course_info_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        findViewById(R.id.backButton).setOnClickListener(new onBackPressed());
        statisticsButton = findViewById(R.id.statisticsButton);
        statisticsButton.setOnClickListener(new onStatisticsButtonClick());
        assignmentsButton = findViewById(R.id.assignmentsButton);
        assignmentsButton.setOnClickListener(new onAssignmentsButtonClick());

        // Instantiate a ViewPager2 and a PagerAdapter.
        viewPager = findViewById(R.id.pager);
        pagerAdapter = new PagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        ViewPager2 viewPager = findViewById(R.id.pager);
        viewPager.setPageTransformer(new AnimatePageChange());
        currentPageSlider = findViewById(R.id.currentPageSlider);
        editButton = findViewById(R.id.editButton);
    }
    public class onAssignmentsButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            viewPager.setCurrentItem(0);
        }
    }
    public class onStatisticsButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            viewPager.setCurrentItem(1);
        }
    }
    public class onBackPressed implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    private class PagerAdapter extends FragmentStateAdapter {
        public PagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            if(position == 0){
                return new AssignmentsFragment();
            }else{
                return new AssignmentStatsFragment();
            }
        }
        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }
    public class AnimatePageChange implements ViewPager2.PageTransformer {
        public void transformPage(View view, float position) {
            int pageNumber = viewPager.getCurrentItem();
            if(pageNumber == 0){
                assignmentsButton.setTextColor(resolveColorAttr(context, R.attr.textColor));
                statisticsButton.setTextColor(resolveColorAttr(context, R.attr.unhighlightedTextColor));
                editButton.setVisibility(View.VISIBLE);
            }else{
                assignmentsButton.setTextColor(resolveColorAttr(context, R.attr.unhighlightedTextColor));
                statisticsButton.setTextColor(resolveColorAttr(context, R.attr.textColor));
                editButton.setVisibility(View.INVISIBLE);
            }
            if(position != 0 && position != 1) {
                currentPageSlider.setTranslationX(view.getWidth() * (1 - position) * 0.545f);
            }
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
