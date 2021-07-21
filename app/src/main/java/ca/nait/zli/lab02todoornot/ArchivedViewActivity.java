package ca.nait.zli.lab02todoornot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

public class ArchivedViewActivity extends AppCompatActivity
{

    ListView listView;
    SharedPreferences prefs;

    ArrayList<HashMap<String, String>> archivedList = new ArrayList<HashMap<String, String>>();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archived_view);
        listView = findViewById(R.id.listview_custom);
        displayArchive();

    }


    private void displayArchive()
    {

        String[] keys = new String[]{"LIST_TITLE","CONTENT","COMPLETED_FLAG","CREATED_DATE"};
        int[] ids = new int[]{R.id.custom_row_date,R.id.custom_row_title,R.id.custom_row_content,R.id.custom_row_flag};
        SimpleAdapter adapter = new SimpleAdapter(this,archivedList,R.layout.custom_row_archive,keys,ids);
        populateList();
        listView.setAdapter(adapter);
    }


    private void populateList() {
            BufferedReader in = null;
            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI("http://www.youcode.ca/Lab02Get.jsp?ALIAS=" + "Li" + "&PASSWORD=" + "123"));
                HttpResponse response = client.execute(request);
                in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String field = "";
                while ((field = in.readLine()) != null) {
                    HashMap<String, String> temp = new HashMap<String, String>();
                    temp.put("LIST_TITLE", field);
                    field = in.readLine();
                    temp.put("CONTENT", field);
                    field = in.readLine();
                    temp.put("COMPLETED_FLAG", field);
                    field = in.readLine();
                    temp.put("CREATED_DATE", field);

                    archivedList.add(temp);

                }

                in.close();
            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e, Toast.LENGTH_LONG).show();
            }
        }

    }
