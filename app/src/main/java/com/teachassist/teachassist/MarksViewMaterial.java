package com.teachassist.teachassist;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class MarksViewMaterial extends AppCompatActivity {
    private Context mContext;
    LinearLayout linearLayout;

    protected void onCreate(Bundle savedInstanceState) {
        // Request window feature action bar
        //requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marks_card_view);
        /*
        // Get the application context
        mContext = getApplicationContext();

        // Change the action bar color
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLUE));

        // Get the widgets reference from XML layout
        linearLayout = (LinearLayout) findViewById(R.id.LinearLayoutCard);

            // Initialize a new CardView
            CardView card = new CardView(mContext);

            // Set the CardView layoutParams
            CardView.LayoutParams params = new CardView.LayoutParams(
                    CardView.LayoutParams.WRAP_CONTENT,
                    CardView.LayoutParams.WRAP_CONTENT
            );
            card.setLayoutParams(params);

            // Set CardView corner radius
            card.setRadius(9);

            // Set cardView content padding
            card.setContentPadding(15, 15, 15, 15);

            // Set a background color for CardView
            card.setCardBackgroundColor(Color.parseColor("#FFC6D6C3"));

            // Set the CardView maximum elevation
            card.setMaxCardElevation(15);

            // Set CardView elevation
            card.setCardElevation(9);

            // Initialize a new TextView to put in CardView
            TextView tv = new TextView(mContext);
            tv.setLayoutParams(params);
            tv.setText("CardView\nProgrammatically");
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
            tv.setTextColor(Color.RED);

            // Put the TextView in CardView
            card.addView(tv);

            // Finally, add the CardView in root layout
            linearLayout.addView(card);
            */

    }
}
