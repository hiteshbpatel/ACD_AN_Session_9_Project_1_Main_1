package com.acadgild.balu.to_do;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by BALU on 4/12/2016.
 */
public class ToDo_CustomAdapter extends BaseAdapter
{
    ArrayList<ToDo> arrayList_todo;
    LayoutInflater layoutInflater_todo;

    public ToDo_CustomAdapter(Context context, ArrayList<ToDo> arrayList_todo)
    {
        this.arrayList_todo = arrayList_todo;
        layoutInflater_todo = LayoutInflater.from(context);
    }

    @Override
    public int getCount()
    {
        return arrayList_todo.size();
    }

    @Override
    public Object getItem(int position)
    {
        return arrayList_todo.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = layoutInflater_todo.inflate(R.layout.todo_listitem, parent, false);
        }

        TextView tv_heading = (TextView) convertView.findViewById(R.id.textView_heading);
        TextView tv_title = (TextView) convertView.findViewById(R.id.textView_title);
        TextView tv_description = (TextView) convertView.findViewById(R.id.textView_description);
        TextView tv_date = (TextView) convertView.findViewById(R.id.textView_date);

        ImageView iv_status = (ImageView) convertView.findViewById(R.id.imageView_status);

        ToDo toDo = arrayList_todo.get(position);

        tv_heading.setText(db_to_ui_date(toDo.getDate()));
        tv_title.setText(toDo.getTitle());
        tv_description.setText(toDo.getDescription());
        tv_date.setText(db_to_ui_date(toDo.getDate()));

        if (toDo.getStatus() == 0)
            iv_status.setImageResource(R.drawable.incomplete);
        else
            iv_status.setImageResource(R.drawable.complete);

        return convertView;
    }

    private String db_to_ui_date(String input_date)
    {
        String str_date_array[] = input_date.split("-");

        int get_year = Integer.parseInt(str_date_array[0]);
        int get_month = Integer.parseInt(str_date_array[1]);
        int get_day = Integer.parseInt(str_date_array[2]);

        Calendar calendar_temp = Calendar.getInstance();
        calendar_temp.set(get_year, get_month - 1, get_day);
        SimpleDateFormat date_ui_format = new SimpleDateFormat("dd/MM/yyyy");
        return (date_ui_format.format(calendar_temp.getTime()));
    }
}
