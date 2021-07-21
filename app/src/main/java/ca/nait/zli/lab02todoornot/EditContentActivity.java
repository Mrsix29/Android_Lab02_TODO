package ca.nait.zli.lab02todoornot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditContentActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    static final String TAG = "EditContentActivity";
    private Button updateItemButton, deleteItemButton, archiveItemButton;
    private EditText etContent;
    private CheckBox checkBoxCompleted;
    DBManager dbManager;
    MainActivity mainActivity;
    private String selectedContent;
    private int selectedId;
    private String selectedTitle;
    SharedPreferences prefs;
    View mainView;
    String password;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_content);
         updateItemButton = (Button)findViewById(R.id.button_update_item);
         deleteItemButton = (Button)findViewById(R.id.button_delete_item);
         archiveItemButton = (Button)findViewById(R.id.button_archive_item);
         checkBoxCompleted = (CheckBox) findViewById(R.id.checkbox_complete_item);
         etContent = (EditText) findViewById(R.id.edit_text_content);
        dbManager = new DBManager(this);

        Intent receivedIntent = getIntent();

        selectedId = receivedIntent.getIntExtra("id", -1);
        selectedContent = receivedIntent.getStringExtra("content");
        selectedTitle = receivedIntent.getStringExtra("title");
        etContent.setText(selectedContent);


        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);



        mainView = findViewById(R.id.layout_edit_content);
        String bgColor = prefs.getString("main_bg_color", "#ccdd3c");

        mainView.setBackgroundColor(Color.parseColor(bgColor));

        updateItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String editedContent = etContent.getText().toString();
                if (!editedContent.equals("")) {
                    dbManager.updateContent(editedContent, selectedId, selectedContent);
                    Toast.makeText(getApplicationContext(), "Updated successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Content cannot be empty", Toast.LENGTH_SHORT).show();
                }
                //check completed
                if (checkBoxCompleted.isChecked()){
                    dbManager.updateStatus("1",selectedId);
                }else{
                    dbManager.updateStatus("0",selectedId);
                }

            }
        });

        deleteItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbManager.deleteContent(selectedId);
                etContent.setText("");
                Toast.makeText(getApplicationContext(), "delete successful", Toast.LENGTH_SHORT).show();
            }
        });

        archiveItemButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                postToCloud(selectedContent, selectedTitle);
                etContent.setText("");
                Toast.makeText(getApplicationContext(), "archived successful", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String checkCheckBox() {
        String flagData="";
        Cursor cursor = dbManager.getFlag(selectedId);
        if (cursor.moveToFirst()){
            do{
                flagData = cursor.getString(cursor.getColumnIndex(DBManager.C_COMPLETED_FLAG));
            }while(cursor.moveToNext());
        }
        cursor.close();
        return flagData;
    }

    private void postToCloud(String content, String title) {
        String date = Calendar.getInstance().getTime().toString();
        String userKey = getResources().getString(R.string.preference_login_name_key);
        String user = prefs.getString(userKey, "Li");
        String passwordKey = getResources().getString(R.string.preference_password_key);
        String password = prefs.getString(passwordKey,"123" );
        //String flag = checkCheckBox();
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://www.youcode.ca/Lab02Post.jsp");
            List<NameValuePair> postParameters = new ArrayList<>();
            postParameters.add(new BasicNameValuePair("LIST_TITLE", title));
            postParameters.add(new BasicNameValuePair("CONTENT", content));
            postParameters.add(new BasicNameValuePair("COMPLETED_FLAG", "completed"));
            postParameters.add(new BasicNameValuePair("ALIAS", user));
            postParameters.add(new BasicNameValuePair("PASSWORD", password));
            postParameters.add(new BasicNameValuePair("CREATED_DATE", date));
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters);
            post.setEntity(formEntity);
            client.execute(post);

        } catch (Exception e) {
            Toast.makeText(this, "ERROR: " + e, Toast.LENGTH_LONG).show();
        }
    }



    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        int bgColor = prefs.getInt("ambil_warna_key",0xff6699cc);
        mainView.setBackgroundColor(bgColor);
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
