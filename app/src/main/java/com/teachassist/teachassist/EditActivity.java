package com.teachassist.teachassist;

import android.content.ClipData;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;


public class EditActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //setup toolbar for delete button and set title
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Drag and Drop to delete");

        RelativeLayout relativeLayout = findViewById(R.id.relativeLayout);
        RelativeLayout relativeLayout1 = findViewById(R.id.relativeLayout1);
        RelativeLayout relativeLayout2 = findViewById(R.id.relativeLayout2);
        RelativeLayout relativeLayout3 = findViewById(R.id.relativeLayout3);

        relativeLayout.setOnLongClickListener(longClickListener );
        relativeLayout1.setOnLongClickListener(longClickListener );
        relativeLayout2.setOnLongClickListener(longClickListener );
        relativeLayout3.setOnLongClickListener(longClickListener );



    }




    View.OnLongClickListener longClickListener = new View.OnLongClickListener(){
        @Override
        public boolean onLongClick(View v){
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder myShadowBuilder = new View.DragShadowBuilder(v);
            v.startDrag(data, myShadowBuilder, v, 0);
            return true;
        }
    };

    View.OnDragListener dragListener = new View.OnDragListener(){

        @Override
        public boolean onDrag(View v, DragEvent event) {
            int dragEvent = event.getAction();

            switch (dragEvent){
                case DragEvent.ACTION_DRAG_STARTED:
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    final View view = (View) event.getLocalState();

                    if(view.getId() == R.id.relativeLayout)

                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                case DragEvent.ACTION_DROP:
                    break;


            }

            return true;
        }
    };

}
