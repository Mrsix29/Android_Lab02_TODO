package ca.nait.zli.lab02todoornot;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener,AdapterView.OnItemSelectedListener,SharedPreferences.OnSharedPreferenceChangeListener
{

    static final String TAG = "MainActivity";
    ArrayList<TodoName> aTodoNames;
    ArrayList<TodoItem> aTodoItems;

    Spinner spinner;
    SpinnerAdapter adapter;
    ListView listView;
    private Button updateItemButton, deleteItemButton, archiveItemButton;
    private EditText etContent;
    private CheckBox checkBoxCompleted;
    static SQLiteDatabase database;
    static DBManager manager;
    static int currentItemIndex = 0;
    static int currentListIndex = 0;
    String currentTitle;

    EditText etAddTodoName;
    EditText etAddTodoItem;


    TextView header, flagExplain, content, date, flag;
    SharedPreferences prefs;
    View mainView;
    


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       /* prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);*/

        mainView = findViewById(R.id.layout_main_activity);



        //for spinner
        spinner = (Spinner) findViewById(R.id.spinner_cursor);
        spinner.setOnItemSelectedListener(new SpinnerListener(this));

         manager = new DBManager(this);

        Button saveTodoNameButton = (Button)findViewById(R.id.button_save_todo_name);
        saveTodoNameButton.setOnClickListener(this);
        Button saveItemButton = (Button)findViewById(R.id.button_save_item);
        saveItemButton.setOnClickListener(this);


        etAddTodoName = findViewById(R.id.edittext_add_todo_name);
        etAddTodoItem = findViewById(R.id.edittext_add_item);

        listView = (ListView) findViewById(R.id.listview_item);
        listView.setOnItemClickListener(this);


        manager = new DBManager(this);

        refreshSpinner();
        refreshTodoItemList();

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }



        Log.d(TAG, "onCreate() called");
    }



    private void refreshSpinner()
    {
        aTodoNames = new ArrayList<TodoName>();
        database = manager.getReadableDatabase();
        Cursor cursor = database.query(DBManager.TABLE_TODO_NAME, null,null,null,null,null,null);
        startManagingCursor(cursor);
        String tempTitle, output;
        int id;

        while (cursor.moveToNext())
        {
            id = cursor.getInt(cursor.getColumnIndex(DBManager.C_ID));
            tempTitle = cursor.getString(cursor.getColumnIndex(DBManager.C_TODO_NAME));
            TodoName temp = new TodoName(id, tempTitle);
            aTodoNames.add(temp);
        }

        adapter = new SpinAdapter(this, R.layout.spinner_todo_name_row, aTodoNames);
        spinner.setAdapter(adapter);
    }


    @Override
    public void onClick(View button)
    {
        switch (button.getId())
        {
            case R.id.button_save_todo_name:
            {
                String todoName = etAddTodoName.getText().toString();
                etAddTodoName.setText("");
                ContentValues values = new ContentValues();
                values.put(DBManager.C_TODO_NAME, todoName);
                try
                {
                    database = manager.getWritableDatabase();
                    database.insertOrThrow(DBManager.TABLE_TODO_NAME, null, values);
                    Toast.makeText(this, "You saved: " + todoName, Toast.LENGTH_LONG).show();
                    database.close();
                    refreshSpinner();
                }
                catch (Exception e)
                {
                    Log.d(TAG, "Error" + e);
                    Toast.makeText(this, "Error: " + e, Toast.LENGTH_LONG).show();
                }
                break;
            }
            case R.id.button_save_item:
            {
                String content = etAddTodoItem.getText().toString();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                String completeFlag = "Incomplete";

                etAddTodoItem.setText("");
                ContentValues values = new ContentValues();
                values.put(DBManager.C_TODO_ID, aTodoNames.get(currentListIndex).getId());
                values.put(DBManager.C_CONTENT, content);
                values.put(DBManager.C_DATE, df.format(date));
                values.put(DBManager.C_COMPLETED_FLAG, completeFlag);

                try
                {
                    database = manager.getWritableDatabase();
                    database.insertOrThrow(DBManager.TABLE_TODO_ITEM, null, values);
                    Toast.makeText(this, "You saved: " + content, Toast.LENGTH_LONG).show();
                    database.close();
                    refreshTodoItemList();
                }
                catch (Exception e)
                {
                    Log.d(TAG, "Error" + e);
                    Toast.makeText(this, "Error: " + e, Toast.LENGTH_LONG).show();
                }
                break;
            }

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        currentTitle = aTodoNames.get(position).getTodoName();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        currentListIndex = position;
        String content = aTodoItems.get(position).getContent();
        Cursor data = manager.getItemID(content);
        String title = aTodoNames.get(position).getTodoName();

        int contentId = -1;
        while (data.moveToNext()) {
            contentId = data.getInt(0);
        }
        if (contentId > -1) {
            Intent editContentIntent = new Intent(this, EditContentActivity.class);
            editContentIntent.putExtra("id", contentId);
            editContentIntent.putExtra("content", content);
            editContentIntent.putExtra("title", title);
            startActivity(editContentIntent);
        } else {
            Toast.makeText(this, "No ID Found " , Toast.LENGTH_LONG).show();
        }

    }



    private void refreshTodoItemList()
    {
        aTodoItems = new ArrayList<TodoItem>();
        database = manager.getReadableDatabase();
        if (aTodoNames.size() > 0)
        {
            String whereClause = DBManager.C_TODO_ID + "=" + (aTodoNames.get(currentListIndex).getId());
            Cursor cursor = database.query(DBManager.TABLE_TODO_ITEM, null, whereClause, null, null, null, null);
            startManagingCursor(cursor);
            String tempTodoItemContent, tempTodoItemDate, tempTodoItemCompleteFlag, output;
            int id, tempTodoNameId;

            while (cursor.moveToNext())
            {
                id = cursor.getInt(cursor.getColumnIndex(DBManager.C_ID));

                tempTodoNameId = cursor.getInt(cursor.getColumnIndex(DBManager.C_TODO_ID));
                tempTodoItemContent = cursor.getString(cursor.getColumnIndex(DBManager.C_CONTENT));
                tempTodoItemDate = cursor.getString(cursor.getColumnIndex(DBManager.C_DATE));
                tempTodoItemCompleteFlag = cursor.getString(cursor.getColumnIndex(DBManager.C_COMPLETED_FLAG));
                TodoItem todoItem = new TodoItem(id, tempTodoNameId, tempTodoItemContent, tempTodoItemDate, tempTodoItemCompleteFlag);
                aTodoItems.add(todoItem);
            }
            CursorAdapter adapter = new CursorAdapter(this, cursor);
            listView.setAdapter(adapter);



        }
    }



    private class SpinnerListener implements AdapterView.OnItemSelectedListener
    {
        MainActivity activity;

        public SpinnerListener(MainActivity context)
        {
            activity = (MainActivity)context;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int index, long id)
        {
            TodoName groupName = (TodoName) parent.getAdapter().getItem(index);
            currentListIndex = index;
            refreshTodoItemList();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {

        }
    }




    public void changeFontSize(float textSize){
        content  = (TextView) findViewById(R.id.text_view_list_name);


        if (date != null ){
            content.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);

        }

    }

    //preferene
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String bgColor = prefs.getString("main_bg_color", "#1c2c3c");
        mainView.setBackgroundColor(Color.parseColor(bgColor));
        float textSize = Float.parseFloat(prefs.getString("text_size", null));
        changeFontSize(textSize);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            ActionBar bar = this.getSupportActionBar();
            bar.setBackgroundDrawable((new ColorDrawable(Color.parseColor(bgColor))));

            bar.setDisplayHomeAsUpEnabled(true);

            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#009999"));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_prefs: {
                Intent intent = new Intent(this, PrefsActivity.class);
                this.startActivity(intent);
                break;
            }
            case R.id.menu_item_todo_main: {
                Intent intent = new Intent(this, MainActivity.class);
                this.startActivity(intent);
                break;
            }
            case R.id.menu_item_view_archived: {
                Intent intent = new Intent(this, ArchivedViewActivity.class);
                this.startActivity(intent);
                break;
            }
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

}
