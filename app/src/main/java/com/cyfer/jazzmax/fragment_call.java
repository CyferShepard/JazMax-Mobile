package com.cyfer.jazzmax;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class fragment_call extends Fragment implements AdapterView.OnItemClickListener {
    ConnectionClass connectionClass;

    ListView lv;
    private OnFragmentInteractionListener mListener;

    List<String> download_array_clients = new ArrayList<String>();
    ArrayList<String> nums=new ArrayList<>();
    ArrayList<String> cid=new ArrayList<>();
    int arrayid;
    ProgressBar progressBar;


    public static final String PREFS_NAME_AGENT = "MyPrefsAgent";

    //////////////////////////////////
    String userid;

    String agentemail;

    String branchid;

    private static final String PREF_USERID = "USERID";

    private static final String PREF_AGENTEMAIL = "EMAIL";

    private static final String PREF_BRANCHID = "BID";

    ///////////////////////////////////

    public Boolean firstVisit;

    public fragment_call() {
        // Required empty public constructor
    }



    public static fragment_call newInstance() {
        fragment_call fragment = new fragment_call();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectionClass = new ConnectionClass();

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.READ_CALL_LOG}, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_call, container, false);
        lv=view.findViewById(R.id.ClientList);
        lv.setOnItemClickListener(this);
        firstVisit=true;
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar_clientList);
        progressBar.setVisibility(View.GONE);
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

        callnumber(position);
        arrayid=position;

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
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, download_array_clients);
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
                agentemail=prefagent.getString(PREF_AGENTEMAIL, null);
                branchid=prefagent.getString(PREF_BRANCHID, null);
                userid=prefagent.getString(PREF_USERID, null);
                Log.e("File", "1 "+agentemail);
                String commands="Select a.LeadID, a.FullName, a.ContactNumber"
                        +" from LeadProspect a"
                        +" left JOIN LeadAgents b on a.LeadID = b.LeadID"
                        +" left JOIN Lead c on a.LeadID = c.LeadID"
                        +" where b.AgentID = "+userid+" and c.IsCompleted = 'False' and c.CoreBranchId="+branchid;
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(commands);

                while (rs.next())
                {

                    String Item="Lead ID: "+rs.getString("LeadID")+"\nName: "+rs.getString("FullName")+"\nNumber: "+rs.getString("ContactNumber");
                    Log.e("File", Item);
                    download_array_clients.add(Item);
                    cid.add(rs.getString("LeadID"));
                    nums.add(rs.getString("ContactNumber"));
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

    public class LogCallTask extends AsyncTask<String,String,String>
    {
        @Override
        protected void onPostExecute(String r)
        {

            progressBar.setVisibility(View.GONE);
            Log.e("Upload", r);
            if(r.equals("Success"))
            {
                Toast.makeText(getActivity() , "Call Logged" , Toast.LENGTH_SHORT).show();

            }else
            {
                Toast.makeText(getActivity() , "Error Fetching Files" , Toast.LENGTH_SHORT).show();
            }


            // End After successful insertion of image
        }
        @Override
        protected String doInBackground(String... params)
        {
            // Inserting in the database
            Connection con = connectionClass.CONN();

            String msg = "unknown";
            Log.e("LeadC", arrayid+"");
            Log.e("LeadC", cid.get(arrayid));

            try
            {
                SharedPreferences prefagent = getActivity().getSharedPreferences(PREFS_NAME_AGENT,Context.MODE_PRIVATE);
                agentemail=prefagent.getString(PREF_AGENTEMAIL, null);
                branchid=prefagent.getString(PREF_BRANCHID, null);
                userid=prefagent.getString(PREF_USERID, null);
                String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());

                String commands="Insert into LoggedCalls (LeadProspectID, CallDuration, DateLogged, AgentID) Values ("+cid.get(arrayid)+",'"+LastCallDur()+"','"+timeStamp+"',"+userid+")";
                PreparedStatement preStmt = con.prepareStatement(commands);
                preStmt.executeUpdate();
                msg="Success";



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

    ////////////////////////////////////////////


    public void logcall()
    {
        if( callplaced==true)
        {
            LogCallTask logCallTask=new LogCallTask();
            logCallTask.execute("");
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    String numcalled="";
    Boolean callplaced=false;
    public void checkifcallplaced(String lastnum)
    {

        //Num called
        String subcode=numcalled.substring(0,3);
        String numcalledCorrect=numcalled;
        Log.e("Called Number", subcode+"");
        if(!subcode.equals("+27"))
        {
            numcalledCorrect="+27"+numcalled.substring(1);
        }

        //Number Dialed
        String subcodeD=lastnum.substring(0,4);
        String numlastCorrect=lastnum;
        Log.e("Called Number", subcodeD+"");
        if(!subcodeD.equals("+27"))
        {
            numlastCorrect="+27"+lastnum.substring(2);
        }

        //check match
        if(numcalledCorrect.equals(numlastCorrect))
        {
            callplaced=true;
        }
        Log.e("Called Number", numcalledCorrect+"");
        Log.e("Called Number", numlastCorrect+"");

    }

    public String LastCallDur() {
        StringBuffer sb = new StringBuffer();
        @SuppressLint("MissingPermission") Cursor cur = getActivity().getContentResolver().query( CallLog.Calls.CONTENT_URI,null, null,null, android.provider.CallLog.Calls.DATE + " DESC");

        int duration = cur.getColumnIndex( CallLog.Calls.DURATION);
        int toc = cur.getColumnIndex( CallLog.Calls.DATE);
        Log.e("Time", toc+"");
        String callDuration="";
        while ( cur.moveToNext() ) {
            callDuration = cur.getString( duration );
            sb.append( "\n"+callDuration);
            break;
        }
        cur.close();
        String str=sb.toString();
        String cd=secToTime(Integer.parseInt(callDuration));
        Log.e("LastCall",cd);

        return cd;
    }

    public String LastCall() {
        StringBuffer sb = new StringBuffer();
        String str="Not Called";
        @SuppressLint("MissingPermission") Cursor cur = getActivity().getContentResolver().query( CallLog.Calls.CONTENT_URI,null, null,null, android.provider.CallLog.Calls.DATE + " DESC");
        int number = cur.getColumnIndex( CallLog.Calls.NUMBER );

            while ( cur.moveToNext() ) {
                String phNumber = cur.getString( number );
                sb.append("\n"+phNumber);
                break;
            }
            cur.close();
            str=sb.toString();
            Log.e("LastCall",str);

        return str;
    }

    String secToTime(int sec) {
        int seconds = sec % 60;
        int minutes = sec / 60;
        if (minutes >= 60) {
            int hours = minutes / 60;
            minutes %= 60;
            if( hours >= 24) {
                int days = hours / 24;
                return String.format("%d days %02d:%02d:%02d", days,hours%24, minutes, seconds);
            }
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format("00:%02d:%02d", minutes, seconds);
    }


    /////////////////////////////////////////////////
    public void callnumber(int client)
    {
        String numtocall=nums.get(client);
        numcalled=numtocall;
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:"+numtocall));
        startActivity(callIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(firstVisit){
            firstVisit=false;
        }else {
            checkifcallplaced(LastCall());
            logcall();
            download_array_clients
                    =new ArrayList<>();
            getFileList();
        }

    }

    /////////////////////////////////////////////////////
    //ToDO: Fix logging already Existing Calls



}
