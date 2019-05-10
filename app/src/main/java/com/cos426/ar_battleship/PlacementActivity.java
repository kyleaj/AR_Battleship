package com.cos426.ar_battleship;

import java.util.ArrayList;
import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class PlacementActivity extends Activity implements OnClickListener {
    private ArrayList<ToggleButton> mButtons = new ArrayList<>();
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.placement_activity);
        ToggleButton cb = null;
        for (int i =0; i<25; i++) {
            cb = new ToggleButton(this);
            cb.setOnClickListener(this);
            cb.setId(i);
            mButtons.add(cb);
        }
        GridView gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(new ButtonAdapter(mButtons));
    }
    @Override
    public void onClick(View v) {
        ToggleButton selection = (ToggleButton) v;
        selection.toggle();
        if(selection.isChecked()) Toast.makeText(getBaseContext(),  selection.getText()+ " was pressed!", Toast.LENGTH_SHORT).show();
        else Toast.makeText(getBaseContext(),  " butter me", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        ToggleButton b = (ToggleButton)v;
        menu.setHeaderTitle(b.getText());
        menu.add(0, v.getId(), 0, "Action 1");
        menu.add(0, v.getId(), 0, "Action 2");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ToggleButton selectedButton = mButtons.get(item.getItemId());
        Toast.makeText(getBaseContext(),  item.getTitle()+" of "+selectedButton.getText()+ " was pressed!", Toast.LENGTH_SHORT).show();
        return true;
    }
}
