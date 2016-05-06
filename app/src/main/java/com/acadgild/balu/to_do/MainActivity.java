package com.acadgild.balu.to_do;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
                                                               AdapterView.OnItemLongClickListener
{
    ArrayList<ToDo> arrayList_todo;
    ToDo_CustomAdapter customAdapter_todo;
    ListView listView_todo_alltasks;
    ToDo toDo_update;

    TextView textView_date;
    EditText editText_title, editText_description;
    ImageButton imageButton_date;
    Button button_save, button_cancel;

    ToDo_DBHelper dbHelper = new ToDo_DBHelper(this);

    int current_year, current_month, current_day;
    int mode;
    String new_date, current_date;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arrayList_todo = new ArrayList<>();

//        dbHelper.onUpgrade(dbHelper.getWritableDatabase(), 1, 2);

        listView_todo_alltasks = (ListView) findViewById(R.id.listView_todo_alltasks);
        show_listview();
        listView_todo_alltasks.setOnItemClickListener(this);
        listView_todo_alltasks.setOnItemLongClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_todo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.menu_item_add_task)
        {
            mode = 1; // Insert a New Task
            open_alert_dialog(0);
            show_listview();
        }
        else if (item.getItemId() == R.id.menu_item_completed_tasks)
        {
            Intent intent = new Intent(getApplicationContext(), ToDo_CompletedTasks.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        mode = 2;
        open_alert_dialog(position);
        show_listview();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
    {
        int i = dbHelper.mark_task_completed(arrayList_todo.get(position).getId());
        if (i != 0)
        {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.task_completed),
                    Toast.LENGTH_SHORT).show();
            show_listview();
            return true;
        }
        return false;
    }

    private void show_listview()
    {
        arrayList_todo.clear();
        arrayList_todo = dbHelper.get_todo_tasks();

        customAdapter_todo = new ToDo_CustomAdapter(getApplicationContext(), arrayList_todo);

        listView_todo_alltasks.setAdapter(customAdapter_todo);
    }

    private void open_alert_dialog(int pos)
    {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        View viewDialog = inflater.inflate(R.layout.todo_addtask, null);

        alertDialog.setView(viewDialog);

        final AlertDialog dialog_display = alertDialog.create();
        dialog_display.show();

        editText_title = (EditText) viewDialog.findViewById(R.id.editText_title);
        editText_description = (EditText) viewDialog.findViewById(R.id.editText_description);
        textView_date = (TextView) viewDialog.findViewById(R.id.textView_date);
        imageButton_date = (ImageButton) viewDialog.findViewById(R.id.imageButton_date);
        button_save = (Button) viewDialog.findViewById(R.id.button_save);
        button_cancel = (Button) viewDialog.findViewById(R.id.button_cancel);

        Calendar calendar_current = Calendar.getInstance();
        current_day = calendar_current.get(Calendar.DAY_OF_MONTH);
        current_month = calendar_current.get(Calendar.MONTH);
        current_year = calendar_current.get(Calendar.YEAR);

        final SimpleDateFormat currentDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        current_date = currentDateFormat.format(calendar_current.getTime()).toString();

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        if (mode == 1) //Insert a New Task
        {
            Calendar calendar_new = Calendar.getInstance();
            calendar_new.set(current_year, current_month, current_day);
            textView_date.setText(simpleDateFormat.format(calendar_new.getTime()));
        }
        else if (mode == 2) //Update an Existing Task
        {
            toDo_update = dbHelper.get_task(arrayList_todo.get(pos).getId());

            editText_title.setText(toDo_update.getTitle());
            editText_description.setText(toDo_update.getDescription());
            textView_date.setText(db_to_ui_date(toDo_update.getDate()));
        }

        imageButton_date.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mode == 1) // Insert a New Task
                {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                                {
                                    Calendar calendar_new_picker = Calendar.getInstance();
                                    calendar_new_picker.set(year, monthOfYear, dayOfMonth);
                                    textView_date.setText(simpleDateFormat.format(calendar_new_picker.getTime()));
                                }
                            }, current_year, current_month, current_day);
                    datePickerDialog.show();
                }
                else if (mode == 2) // Update an Existing Task
                {
                    String task_date = textView_date.getText().toString();
                    String task_date_array[] = task_date.split("/");

                    int task_day = Integer.parseInt(task_date_array[0]);
                    int task_month = Integer.parseInt(task_date_array[1]);
                    int task_year = Integer.parseInt(task_date_array[2]);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                                {
                                    Calendar calendar_update_picker = Calendar.getInstance();
                                    calendar_update_picker.set(year, monthOfYear, dayOfMonth);
                                    textView_date.setText(simpleDateFormat.format(calendar_update_picker.getTime()));
                                }
                            }, task_year, task_month-1, task_day);
                    datePickerDialog.show();
                }
            }
        });

        button_save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (TextUtils.isEmpty(editText_title.getText().toString()))
                {
                    editText_title.setError(getResources().getString(R.string.blank_title));
                }
                else if (TextUtils.isEmpty(editText_description.getText().toString()))
                {
                    editText_description.setError(getResources().getString(R.string.blank_description));
                }
                else
                {
                    if (mode == 1) //Insert a New Task
                    {
                        ToDo toDo_new = new ToDo();
                        toDo_new.setTitle(editText_title.getText().toString());
                        toDo_new.setDescription(editText_description.getText().toString());
                        new_date = ui_to_db_date(textView_date.getText().toString());
                        try {
                            Date c_date = currentDateFormat.parse(current_date);
                            Date n_date = currentDateFormat.parse(new_date);
                            if (n_date.before(c_date))
                            {
                                textView_date.setError(getResources().getString(R.string.date_valid));
                                Toast.makeText(getApplicationContext(),
                                        getResources().getString(R.string.date_valid),
                                        Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                toDo_new.setDate(new_date);
                                dbHelper.add_new_task(toDo_new);
                                Toast.makeText(getApplicationContext(),
                                        getResources().getString(R.string.task_added),
                                        Toast.LENGTH_LONG).show();
                                dialog_display.dismiss();
                                show_listview();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    else if (mode == 2) // Update an Existing Task
                    {
                        toDo_update.setTitle(editText_title.getText().toString());
                        toDo_update.setDescription(editText_description.getText().toString());
                        new_date = ui_to_db_date(textView_date.getText().toString());
                        try {
                            Date c_date = currentDateFormat.parse(current_date);
                            Date n_date = currentDateFormat.parse(new_date);
                            if (n_date.before(c_date))
                            {
                                textView_date.setError(getResources().getString(R.string.date_valid));
                                Toast.makeText(getApplicationContext(),
                                        getResources().getString(R.string.date_valid),
                                        Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                toDo_update.setDate(new_date);
                                dbHelper.update_task(toDo_update);
                                Toast.makeText(getApplicationContext(),
                                        getResources().getString(R.string.task_updated),
                                        Toast.LENGTH_LONG).show();
                                dialog_display.dismiss();
                                show_listview();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        button_cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog_display.dismiss();
            }
        });
    }

    private String ui_to_db_date(String input_date)
    {
        String str_date_array[] = input_date.split("/");

        int get_day = Integer.parseInt(str_date_array[0]);
        int get_month = Integer.parseInt(str_date_array[1]);
        int get_year = Integer.parseInt(str_date_array[2]);

        Calendar calendar_temp = Calendar.getInstance();
        calendar_temp.set(get_year, get_month-1, get_day);
        SimpleDateFormat date_db_format = new SimpleDateFormat("yyyy-MM-dd");
        return(date_db_format.format(calendar_temp.getTime()));
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




