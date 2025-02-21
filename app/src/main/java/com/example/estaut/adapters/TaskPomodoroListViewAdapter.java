package com.example.estaut.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.estaut.Pomodoro;
import com.example.estaut.R;
import com.example.estaut.entities.TaskCard;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class TaskPomodoroListViewAdapter extends BaseAdapter {

    public Context context;
    public ArrayList<TaskCard> list;
    public TaskPomodoroListViewAdapter(Context context, ArrayList<TaskCard> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        TaskCard taskCard = list.get(i);

        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.listview_tasks_pomodoro, null);

        TextView textViewTask = view.findViewById(R.id.textViewTask);
        textViewTask.setText(taskCard.getTask());

        if(taskCard.isCompleted()){
            textViewTask.setPaintFlags(textViewTask.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            textViewTask.setTextColor(ContextCompat.getColor(inflater.getContext(), R.color.gray_text_checked_pomodoro_taks));
        }

        CheckBox checkBoxTask = view.findViewById(R.id.checkBoxTask);
        checkBoxTask.setChecked(taskCard.isCompleted());
        checkBoxTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkBoxTask.isChecked()) {

                    textViewTask.setPaintFlags(textViewTask.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    textViewTask.setTextColor(ContextCompat.getColor(inflater.getContext(), R.color.gray_text_checked_pomodoro_taks));
                    Pomodoro.tasks.get(i).setCompleted(true);

                } else {

                    textViewTask.setPaintFlags(0);
                    textViewTask.setTextColor(ContextCompat.getColor(inflater.getContext(), R.color.white));
                    Pomodoro.tasks.get(i).setCompleted(false);

                }

            }
        });

        return view;
    }
}
