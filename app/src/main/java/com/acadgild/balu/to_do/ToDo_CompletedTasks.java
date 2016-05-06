package com.acadgild.balu.to_do;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ToDo_CompletedTasks extends AppCompatActivity implements AdapterView.OnItemLongClickListener {

    ArrayList<ToDo> arrayList_todo;
    ToDo_CustomAdapter customAdapter_todo;
    ListView listView_todo_completed_tasks;

    ToDo_DBHelper dbHelper = new ToDo_DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_completedtasks);

        arrayList_todo = new ArrayList<>();

        listView_todo_completed_tasks = (ListView) findViewById(R.id.listView_todo_completedtasks);
        show_listview();
        listView_todo_completed_tasks.setOnItemLongClickListener(this);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
    {
        int i = dbHelper.delete_task(arrayList_todo.get(position).getId());
        if (i != 0)
        {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.task_deleted),
                    Toast.LENGTH_SHORT).show();
            show_listview();
            return true;
        }
        return false;
    }

    private void show_listview()
    {
        arrayList_todo.clear();
        arrayList_todo = dbHelper.get_completed_tasks();

        customAdapter_todo = new ToDo_CustomAdapter(getApplicationContext(), arrayList_todo);
        listView_todo_completed_tasks.setAdapter(customAdapter_todo);
    }
}
