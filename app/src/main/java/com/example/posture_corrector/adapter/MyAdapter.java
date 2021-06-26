package com.example.posture_corrector.adapter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.posture_corrector.R;

import java.util.List;

public class MyAdapter extends ArrayAdapter<BluetoothDevice> {
    private int selectedIndex;
    private final Context context;
    private final int selectedColor = Color.parseColor("#abcdef");
    private List<BluetoothDevice> myList;

    public MyAdapter(Context ctx, int resource, int textViewResourceId, List<BluetoothDevice> objects) {
        super(ctx, resource, textViewResourceId, objects);
        context = ctx;
        myList = objects;
        selectedIndex = -1;
    }

    public void setSelectedIndex(int position) {
        selectedIndex = position;
        notifyDataSetChanged();
    }

    public BluetoothDevice getSelectedItem() {
        return myList.get(selectedIndex);
    }

    @Override
    public int getCount() {
        return myList.size();
    }

    @Override
    public BluetoothDevice getItem(int position) {
        return myList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class ViewHolder {
        TextView tv;
    }

    public void replaceItems(List<BluetoothDevice> list) {
        myList = list;
        notifyDataSetChanged();
    }

    public List<BluetoothDevice> getEntireList() {
        return myList;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;
        if (convertView == null) {
            vi = LayoutInflater.from(context).inflate(R.layout.list_item, null);
            holder = new ViewHolder();

            holder.tv = (TextView) vi.findViewById(R.id.lstContent);

            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }

        if (selectedIndex != -1 && position == selectedIndex) {
            holder.tv.setBackgroundColor(selectedColor);
        } else {
            holder.tv.setBackgroundColor(Color.WHITE);
        }
        BluetoothDevice device = myList.get(position);
        holder.tv.setText(device.getName() + "\n " + device.getAddress());

        return vi;
    }
}
