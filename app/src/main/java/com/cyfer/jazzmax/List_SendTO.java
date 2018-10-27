package com.cyfer.jazzmax;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOError;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class List_SendTO extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ImageButton btnsrch;
    private EditText SendTO;
    ProgressBar progressBar;
    List<String> download_array_agents = new ArrayList<String>();
    ArrayList<String> send=new ArrayList<>();
    ListView lv;
    ConnectionClass connectionClass;
    public static final String PREFS_NAME_AGENT = "MyPrefsAgent";

    //////////////////////////////////
    String userid;

    String agentemail;

    String branchid;

    private static final String PREF_USERID = "USERID";

    ///////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list__send_to);
        SendTO = findViewById(R.id.txt_Search_Recipient);
        connectionClass=new ConnectionClass();
        lv=findViewById(R.id.list_Recipient);
        lv.setOnItemClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_Search);
        progressBar.setVisibility(View.GONE);
        getFileList();
        btnsrch= findViewById(R.id.btn_Search);
        btnsrch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFileList();
            }
        });
    }

    public void setAdapter()
    {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, download_array_agents);
        lv.setAdapter(arrayAdapter);
    }








    public void getFileList()
    {
        download_array_agents = new ArrayList<String>();

        DownloadList downloadList = new DownloadList();
        downloadList.execute("");
        progressBar.setVisibility(View.VISIBLE);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e("SendTO",send.get(position));

        getSharedPreferences("SEND",MODE_PRIVATE)
                .edit()
                .putString("EMAIL", send.get(position))
                .commit();
        this.finish();

    }
    String sp;

    public class DownloadList extends AsyncTask<String,String,String>
    {
        @Override
        protected void onPostExecute(String r)
        {
            progressBar.setVisibility(View.GONE);

            Log.e("Upload", r);
            if(r.equals("Success"))
            {
               setAdapter();

            }else
            {

            }


            // End After successful insertion of image
        }
        @Override
        protected String doInBackground(String... params)
        {
            // Inserting in the database
            Connection con = connectionClass.CONN();

            String msg = "unknown";

            try
            {
                SharedPreferences prefagent = getSharedPreferences(PREFS_NAME_AGENT, Context.MODE_PRIVATE);
                userid=prefagent.getString(PREF_USERID, null);

                Log.e("File", "1 "+agentemail);
                 sp= SendTO.getText().toString();
                Log.e("SP",sp);
                if(sp.equals(null))
                {
                    sp="";
                }
                String commands="Select FirstName, EmailAddress from CoreUser where EmailAddress Like  '%"+sp+"%' or FirstName Like '%"+sp+"%'";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(commands);
                send.add("Document Directory");
                download_array_agents.add("Document Directory");

                while (rs.next())
                {

                    String Item="Agent: "+rs.getString("FirstName")+"\nEmail: "+rs.getString("EmailAddress");
                    Log.e("File", Item);

                    download_array_agents.add(Item);
                    send.add(rs.getString("EmailAddress"));



                }
                msg = "Success";

            }
            catch (SQLException ex)
            {
                msg = ex.getMessage().toString();
                Log.d("Error no 1:", msg);
            }
            catch (IOError ex)
            {
                msg = ex.getMessage().toString();
                Log.d("Error no 2:", msg);
            }
            catch (AndroidRuntimeException ex)
            {
                msg = ex.getMessage().toString();
                Log.d("Error no 3:", msg);
            }
            catch (NullPointerException ex)
            {
                msg = ex.getMessage().toString();
                Log.d("Error no 4:", msg);
            }
            catch (Exception ex)
            {
                msg = ex.getMessage().toString();
                Log.d("Error no 5:", msg);
            }
            System.out.println(msg);
            return msg;
            //End Inserting in the database
        }
    }



}
