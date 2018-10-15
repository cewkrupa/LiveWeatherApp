package com.example.connor.LiveWeatherAppPartIII;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Connor on 10/18/2016.
 */

public class MyCityListAdapter extends BaseAdapter {
    private Context c2v_context;
    private ArrayList<City> c2v_listData;

    public MyCityListAdapter(Context context, ArrayList<City> listItems){
        this.c2v_context = context;
        this.c2v_listData = listItems;
    }

    @Override
    public int getCount() {
        return c2v_listData.size();
    }

    @Override
    public Object getItem(int position) {
        return c2v_listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    c2v_context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.my_citylistitem, null);
        }

        City city = c2v_listData.get(position);



        TextView lv_modelLbl = (TextView) convertView.findViewById(R.id.vv_tvCityName);
        lv_modelLbl.setText(city.getCityName());

        TextView lv_nameLbl = (TextView) convertView.findViewById(R.id.vv_tvCityState);
        lv_nameLbl.setText(city.getCityZip());

        TextView lv_StateLbl = (TextView) convertView.findViewById(R.id.vv_tvCityZip);
        lv_StateLbl.setText(city.getCityState());

        TextView lv_priceLbl = (TextView) convertView.findViewById(R.id.vv_tvCityTemp);
        lv_priceLbl.setText(""+city.getCityTemp());

        TextView lv_tempAbv = (TextView) convertView.findViewById((R.id.vv_tvTempAbv));
        lv_tempAbv.setText(city.getCityTempFormatAbv());

        return convertView;
    }

}
