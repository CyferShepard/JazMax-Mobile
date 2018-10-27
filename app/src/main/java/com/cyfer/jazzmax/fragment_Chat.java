package com.cyfer.jazzmax;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class fragment_Chat extends Fragment {
    ConnectionClass connectionClass;

    private OnFragmentInteractionListener mListener;

    public fragment_Chat() {
        // Required empty public constructor
    }


    public static fragment_Chat newInstance(String param1, String param2) {
        fragment_Chat fragment = new fragment_Chat();

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment__chat, container, false);
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


    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }





    public class getChatsTask extends AsyncTask<String, String, String> {

        private final String mEmail;
        Boolean isSuccess = false;
        String message="";

        getChatsTask(String email) {
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

                    Statement stmt = con.createStatement();

                    ResultSet rs = stmt.executeQuery(query);



                    if(rs.next())
                    {

                        String rresult=rs.getString("Message");



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
