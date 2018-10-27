package com.cyfer.jazzmax;

import android.content.Context;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOError;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class fragment_call_history extends Fragment implements AdapterView.OnItemClickListener {

    private OnFragmentInteractionListener mListener;
    List<String> download_array_calls = new ArrayList<String>();
    ConnectionClass connectionClass;
    ProgressBar progressBar;
    public static final String PREFS_NAME_AGENT = "MyPrefsAgent";
    public Boolean firstVisit;

    //////////////////////////////////
    String userid;

    String agentemail;

    String branchid;

    private static final String PREF_USERID = "USERID";

    ///////////////////////////////////

    ListView lv;

    public fragment_call_history() {
        // Required empty public constructor
    }


    @Override
    public void onResume() {
        super.onResume();
        if(firstVisit){
            firstVisit=false;
        }else {
            download_array_calls=new ArrayList<>();

            getFileList();
        }
    }




    public static fragment_call_history newInstance(String param1, String param2) {
        fragment_call_history fragment = new fragment_call_history();

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
        View view= inflater.inflate(R.layout.fragment_call_history, container, false);
        lv=view.findViewById(R.id.HistoryList);
        lv.setOnItemClickListener(this);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar_callHistory);
        progressBar.setVisibility(View.GONE);
        firstVisit=true;
        getFileList();
        return view;
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }





    public void getFileList()
    {


        DownloadList downloadList = new DownloadList();
        downloadList.execute("");
        progressBar.setVisibility(View.VISIBLE);

    }

    public class DownloadList extends AsyncTask<String,String,String>
    {
        @Override
        protected void onPostExecute(String r)
        {
            progressBar.setVisibility(View.GONE);

            Log.e("Upload", r);
            if(r.equals("Success"))
            {
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, download_array_calls);
                lv.setAdapter(arrayAdapter);

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
                SharedPreferences prefagent = getActivity().getSharedPreferences(PREFS_NAME_AGENT,Context.MODE_PRIVATE);
                userid=prefagent.getString(PREF_USERID, null);
                Log.e("File", "1 "+agentemail);
                String commands="Select a.LeadID, a.FullName, a.ContactNumber, b.CallDuration"
                        +" from LeadProspect a"
                        +" left JOIN LoggedCalls b on a.LeadID = b.LeadProspectID"
                        +" where b.AgentID = "+userid;
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(commands);

                while (rs.next())
                {

                    String Item="Lead ID: "+rs.getString("LeadID")+"\nName: "+rs.getString("FullName")+"\nNumber: "+rs.getString("ContactNumber")+"\nDuration: "+rs.getString("CallDuration");
                    Log.e("File", Item);
                    download_array_calls.add(Item);

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
