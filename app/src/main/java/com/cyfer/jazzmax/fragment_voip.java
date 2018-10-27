package com.cyfer.jazzmax;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;

import java.io.IOError;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class fragment_voip extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    ConnectionClass connectionClass;
    ListView lv;
    ImageButton btnsrc;
    EditText txtsrc;
    List<String> download_array_Users = new ArrayList<String>();
    ArrayList<String> emails=new ArrayList<>();
    ProgressBar progressBar;
    ////////////////////////////////////

    String agentemail;

    public static final String PREFS_NAME_AGENT = "MyPrefsAgent";

    private static final String PREF_AGENTEMAIL = "EMAIL";

    ///////////////////////////////////

    public fragment_voip() {
        // Required empty public constructor
    }


    public static fragment_voip newInstance() {
        fragment_voip fragment = new fragment_voip();


        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectionClass=new ConnectionClass();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_voip, container, false);
        lv=view.findViewById(R.id.listDownload_Voip);
        lv.setOnItemClickListener(this);
        btnsrc=view.findViewById(R.id.btn_Search_Agent);
        btnsrc.setOnClickListener(this);
        txtsrc=view.findViewById(R.id.txt_Search_Call);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar_VoipList);
        progressBar.setVisibility(View.GONE);
        getFileList();



        return view;
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void makecall()
    {

    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), voip_call_screen.class);
        intent.putExtra("callerId", agentemail);
        intent.putExtra("recipientId",emails.get(position));
        startActivity(intent);

    }

    @Override
    public void onClick(View v) {
        getFileList();
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }

    public void getFileList()
    {
        download_array_Users = new ArrayList<String>();
        emails = new ArrayList<String>();

        DownloadList downloadList = new DownloadList();
        downloadList.execute("");
        progressBar.setVisibility(View.VISIBLE);

    }

    String sp="";

    public class DownloadList extends AsyncTask<String,String,String>
    {
        @Override
        protected void onPostExecute(String r)
        {

            progressBar.setVisibility(View.GONE);
            Log.e("Upload", r);
            if(r.equals("Success"))
            {
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_expandable_list_item_1, download_array_Users);
                lv.setAdapter(arrayAdapter);


            }else
            {
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_expandable_list_item_1, download_array_Users);
                lv.setAdapter(arrayAdapter);
                Toast.makeText(getActivity() , "No Results Found" , Toast.LENGTH_LONG).show();
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
                SharedPreferences prefagent = getActivity().getSharedPreferences(PREFS_NAME_AGENT,Context.MODE_PRIVATE);
                agentemail=prefagent.getString(PREF_AGENTEMAIL, null);
                Log.e("File", "1 "+agentemail);
                sp=txtsrc.getText().toString();
                String commands="Select FirstName, LastName, EmailAddress from CoreUser where EmailAddress != '"+agentemail+"' and EmailAddress Like  '%"+sp+"%' or EmailAddress != '"+agentemail+"' and FirstName Like '%"+sp+"%'";

                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(commands);

                while (rs.next())
                {

                    String Item="Name: "+rs.getString("FirstName")+" "+rs.getString("LastName")+"\n"+rs.getString("EmailAddress");
                    Log.e("File", Item);
                    download_array_Users.add(Item);
                    emails.add(rs.getString("EmailAddress"));
                    msg = "Success";

                }

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



