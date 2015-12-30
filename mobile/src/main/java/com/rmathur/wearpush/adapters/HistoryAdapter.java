package com.rmathur.wearpush.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rmathur.wearpush.R;
import com.rmathur.wearpush.models.Push;

import java.util.List;

public class HistoryAdapter extends ArrayAdapter<Push> {

    private static final String TAG = HistoryAdapter.class.getSimpleName();

    Context context;
    int layoutResourceId;
    List<Push> data;

    public HistoryAdapter(Context context, int layoutResourceId, List<Push> data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.name = (TextView) row.findViewById(R.id.workout_title);
            holder.time = (TextView) row.findViewById(R.id.workout_time);

            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        Push entry = data.get(position);

        if(entry != null) {
            try {
                holder.name.setText(entry.getTitle());
                holder.time.setText(entry.getDate().toGMTString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return row;
    }


    static class ViewHolder {
        TextView name;
        TextView time;
    }
}