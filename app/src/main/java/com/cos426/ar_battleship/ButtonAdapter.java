package com.cos426.ar_battleship;

import java.util.ArrayList;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ToggleButton;

public class ButtonAdapter extends BaseAdapter {
    private ArrayList<ToggleButton> mButtons = null;
    public ButtonAdapter(ArrayList<ToggleButton> b)
    {
        mButtons = b;
    }
    @Override
    public int getCount() {
        return mButtons.size();
    }
    @Override
    public Object getItem(int position) {
        return (Object) mButtons.get(position);
    }
    @Override
    public long getItemId(int position) {
//in our case position and id are synonymous
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ToggleButton button;
        if (convertView == null) {
            button = mButtons.get(position);
        } else {
            button = (ToggleButton) convertView;
        }
        return button;
    }
}
