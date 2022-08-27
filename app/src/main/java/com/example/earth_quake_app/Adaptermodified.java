package com.example.earth_quake_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Adaptermodified extends ArrayAdapter<ListObject> {

    public Adaptermodified(Context context,ArrayList<ListObject> arrayList){
        super(context,0,arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_layout, parent, false);
        }
        ListObject listObject = getItem(position);
        TextView textView1 = (TextView) listItemView.findViewById(R.id.view1);
        textView1.setText(String.valueOf(listObject.getFirstNum()));
        TextView textView2 = (TextView) listItemView.findViewById(R.id.view2);
        textView2.setText(listObject.getmState());
        TextView textView3 = (TextView) listItemView.findViewById(R.id.view3);
        textView3.setText(listObject.getmSecondNum());
        return listItemView;
    }
}
