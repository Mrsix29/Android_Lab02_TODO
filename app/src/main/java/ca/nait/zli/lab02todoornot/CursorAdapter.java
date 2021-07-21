package ca.nait.zli.lab02todoornot;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.widget.SimpleCursorAdapter;

public class CursorAdapter extends SimpleCursorAdapter
{
    static final String TAG = "CursorAdapter";
    static final String[] FROM = {DBManager.C_CONTENT, DBManager.C_DATE, DBManager.C_COMPLETED_FLAG};
    static final int[] TO = {R.id.cursor_content, R.id.cursor_date_created, R.id.cursor_completed_flag};

    public CursorAdapter(Context context, Cursor cursor)
    {
        super(context, R.layout.listview_todo_item_row, cursor, FROM, TO);
    }

    @Override
    public void bindView(View row, Context context, Cursor cursor)
    {
        String label = cursor.getString(cursor.getColumnIndex(DBManager.C_COMPLETED_FLAG));
        if (label.equals("1")){
            int color = Color.parseColor("#D3D3D3");
            row.setBackgroundColor(color);
        }
        super.bindView(row, context, cursor);
    }
}
