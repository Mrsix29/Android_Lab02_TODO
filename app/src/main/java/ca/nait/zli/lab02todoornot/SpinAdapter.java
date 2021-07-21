package ca.nait.zli.lab02todoornot;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SpinAdapter extends ArrayAdapter
{
    private Context context;
    private ArrayList aTodoNames;

    public SpinAdapter(Context context, int resourceId, ArrayList objects)
    {
        super(context, resourceId, objects);
        this.context = context;
        this.aTodoNames = objects;
    }
    public int getCount()
    {
        return aTodoNames.size();
    }

    public TodoName getItem(int index)
    {
        return (TodoName) aTodoNames.get(index);
    }

    @Override
    public View getView(int index, View row, ViewGroup parent)
    {
        TextView label = new TextView(context);

        label.setTextColor(Color.parseColor("#127855"));
        label.setTextSize(20);

        TodoName todoName = getItem(index);
        label.setText(todoName.getTodoName());

        return label;
    }

    @Override
    public View getDropDownView(int index, View row,  ViewGroup parent) {
        MainActivity activity = (MainActivity)context;
        LayoutInflater inflater = activity.getLayoutInflater();
        View spinnerRow = inflater.inflate(R.layout.spinner_todo_name_row, null);

        TextView tvTodoId = (TextView) spinnerRow.findViewById(R.id.textview_todo_id);
        TextView tvTodoName = (TextView) spinnerRow.findViewById(R.id.textview_todo_name);
        TodoName todoName = getItem(index);

        tvTodoName.setText(todoName.getTodoName());
        tvTodoId.setText(todoName.getId() + "");
        tvTodoId.setVisibility(TextView.INVISIBLE);

        return spinnerRow;

    }
}
