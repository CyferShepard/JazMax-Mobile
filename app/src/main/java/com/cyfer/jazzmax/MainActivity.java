package com.cyfer.jazzmax;




import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.AsyncTask;
import android.os.Bundle;

import android.support.annotation.NonNull;

import android.support.v4.app.FragmentTransaction;

import android.support.v7.app.ActionBar;
import android.util.Log;

import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.TextView;


import com.firebase.ui.auth.ui.phone.CountryListSpinner;

import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String PREFS_NAME_AGENT = "MyPrefsAgent";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";
    private static final String PREF_LAUNCH_STATE = "state";
    ConnectionClass connectionClass;
    String emailpref=null;
    private TextView navName;
    private TextView navEmail;
    private AgentDetailTask getDTask = null;
   public View headerView;
    private Toolbar toolbar;
    private DrawerLayout drawer;


    //////////////////////////////////
    String userid;
    String agentfname;
    String agentmname;
    String agentlname;
    String agentemail;
    String agentusertype;
    String branchid;
    String provinceid;

    private static final String PREF_USERID = "USERID";
    private static final String PREF_AGENTFNAME = "FNAME";
    private static final String PREF_AGENTMNAME = "MNAME";
    private static final String PREF_AGENTLNAME = "LNAME";
    private static final String PREF_AGENTEMAIL = "EMAIL";
    private static final String PREF_USERTYPE = "USERTYPE";
    private static final String PREF_BRANCHID = "BID";
    private static final String PREF_PROV_ID = "PID";
    ///////////////////////////////////




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences pref = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);

        connectionClass = new ConnectionClass();

        emailpref = pref.getString(PREF_USERNAME, null);

         drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerView = navigationView.getHeaderView(0);
        navEmail=(TextView) headerView.findViewById(R.id.txtnavagentemail);
        navEmail.setText(emailpref);
        Log.e("Det", "1");
        getDTask= new AgentDetailTask(emailpref);
        Log.e("Det", "2");
        getDTask.execute("");
        toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Test");


        final ActionBar ab = getSupportActionBar();
       ab.setDisplayHomeAsUpEnabled(true);
       ab.setHomeAsUpIndicator(R.drawable.ic_round_menu_24px);
        fragChat( );


    }

    public void logout()
    {

        getSharedPreferences(PREFS_NAME,MODE_PRIVATE)
                .edit()
                .putString(PREF_USERNAME, emailpref)
                .putString(PREF_PASSWORD, "")
                .putString(PREF_LAUNCH_STATE, "LoggedOUT")
                .commit();

        Intent i = new Intent(this, LoginActivity.class);
        this.finish();
        startActivity(i);
    }




    public void fragcall( )
    {
        Call_Leads fragCall = new Call_Leads();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameContainerMain, fragCall);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    public void fragcamera( )
    {
        fragment_camera fragCam = new fragment_camera();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameContainerMain, fragCam);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void fragdownload( )
    {
        fragment_download fragd = new fragment_download();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameContainerMain, fragd);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    public void fragChat( )
    {
        fragment_voip fragment_voip = new fragment_voip();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameContainerMain, fragment_voip);
        transaction.addToBackStack(null);
        transaction.commit();
    }






    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
//    public void getd(String email)
//    {
//
//    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_account) {
            Intent i= new Intent(this, AccountSettings.class);
            startActivity(i);


        } else
        if (id == R.id.nav_chat) {
            fragChat();

        } else


            if (id == R.id.nav_call) {
            fragcall();

        }else if (id == R.id.nav_scan) { ;
            fragcamera();

        }
        else if (id == R.id.nav_download) { ;
            fragdownload();

        }
        else if (id == R.id.nav_logout) {

            logout();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


    public class AgentDetailTask extends AsyncTask<String, String, String> {

        private final String mEmail;
        Boolean isSuccess = false;
        String message="";

        AgentDetailTask(String email) {
            mEmail = email;
        }

        @Override
        protected String doInBackground(String... params) {
            Log.e("Det", "3");
            message = "Error in connection with SQL server";
            try {
                Log.e("Det", "4");
                String msg = "unknown";

                Connection con = connectionClass.CONN();
                if (con == null) {
                    message = "Error in connection with SQL server";
                } else {
                    Log.e("Det", "5");
//                    String query = "select FirstName, MiddleName, LastName from CoreUser where EmailAddress='" + mEmail + "'";
                    String query = "Select u.CoreUserID, u.FirstName, u.MiddleName, u.LastName, u.EmailAddress, t.CoreUserTypeId, a.CoreBranchId, b.ProvinceId"
                    +" from CoreUser u"
                    +" left JOIN CoreAgent a on a.CoreUserId = u.CoreUserId"
                    +" left JOIN CoreBranch b on b.BranchId= a.CoreBranchId"
                    +" left JOIN CoreUserInType t on t.CoreUserInTypeId= u.CoreUserId"
                    +" where u.EmailAddress = '"+mEmail+"'";
                    Log.e("Det", "6");
                    Statement stmt = con.createStatement();
                    Log.e("Det", "7");
                    ResultSet rs = stmt.executeQuery(query);
                    Log.e("Det", "8");


                    if(rs.next())
                    {


                      navName=(TextView) headerView.findViewById(R.id.txtnavName);
//                      String namedb=rs.getString("FirstName");
//                      String mnamedb=rs.getString("MiddleName");
//                      String lnamedb=rs.getString("LastName");




                      userid=rs.getString("CoreUserID");
                      agentfname=rs.getString("FirstName");
                      agentmname=rs.getString("MiddleName");
                      agentlname=rs.getString("LastName");
                      agentemail=rs.getString("EmailAddress");
                      agentusertype=rs.getString("CoreUserTypeId");
                      branchid=rs.getString("CoreBranchId");
                      provinceid=rs.getString("ProvinceId");

                      String output="";
                      if(agentfname!=null)
                      {
                          output+=agentfname;
                      }
                        if(agentmname!=null)
                        {
                            output+=" "+agentmname;
                        }
                        if(agentlname!=null)
                        {
                            output+=" "+agentlname;
                        }
                      navName.setText(output);
                        Log.e("AgentInfo",output);
                        String logm="\n=================================================================================================\n"
                                +"UserID | FName | MName | LName | Email         | UserType | BranchId | ProvinceID\n"
                                +"=================================================================================================\n"
                                +userid+" | "+agentfname+" | "+agentmname+" | "+agentlname+" | "+agentemail+" | "+agentusertype+" | "+branchid+" | "+provinceid;
                        Log.e("AgentInfo",logm);


                        getSharedPreferences(PREFS_NAME_AGENT,MODE_PRIVATE)
                                .edit()
                                .putString(PREF_USERID, userid)
                                .putString(PREF_AGENTFNAME, agentfname)
                                .putString(PREF_AGENTMNAME, agentmname)
                                .putString(PREF_AGENTLNAME, agentlname)
                                .putString(PREF_AGENTEMAIL, agentemail)
                                .putString(PREF_USERTYPE, agentusertype)
                                .putString(PREF_BRANCHID, branchid)
                                .putString(PREF_PROV_ID, provinceid)
                                .commit();
                    }
                    else
                    {
                        message = "Invalid Username/Password";
                        isSuccess = false;
                    }

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return message;
        }

        @Override
        protected void onPostExecute(final String m) {


            if (isSuccess) {

                finish();
            } else {
                if(m.equals("Error in connection with SQL server"))
                {

                }else
                {

                }


            }
        }

        @Override
        protected void onCancelled() {

        }
    }



}
