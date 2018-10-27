package com.cyfer.jazzmax;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOError;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AccountSettings extends AppCompatActivity {
    TextView emailt;
    EditText passold;
    EditText passnew;
    EditText passnewc;
    Button btnchange;
    ConnectionClass connectionClass;
    private ProgressDialog mProgress;


    ////////////////////////////////////

    String agentemail;

    public static final String PREFS_NAME_AGENT = "MyPrefsAgent";

    private static final String PREF_AGENTEMAIL = "EMAIL";

    ///////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        emailt=findViewById(R.id.txt_Settings_email);
        passold=findViewById(R.id.txt_OldP);
        passnew=findViewById(R.id.txt_NewP);
        passnewc=findViewById(R.id.txt_NewP_C);
        btnchange=findViewById(R.id.btn_Change_password);
        SharedPreferences prefagent = getSharedPreferences(PREFS_NAME_AGENT, Context.MODE_PRIVATE);
        agentemail=prefagent.getString(PREF_AGENTEMAIL, null);
        emailt.setText("Email: "+agentemail);

        connectionClass=new ConnectionClass();
        mProgress=new ProgressDialog(this);

    }
    public void beginchange(View view)
    {
        confirmold();

    }

    public void confirmold()
    {
        mProgress.setTitle("Verifying");
        mProgress.setMessage("Please wait...");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();
        confP downloadList = new confP();
        downloadList.execute("");


    }

    public String hashP(String password)
    {
        String hash="";
        encryptSHA1 en=new encryptSHA1();
        try {
            hash=  en.SHA1(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return hash;

    }

    public void changep()
    {

            String newp=passnew.getText().toString();
            String newpc=passnewc.getText().toString();
            if(newp.equals(newpc) && !newp.isEmpty() && !newpc.isEmpty())
            {
                mProgress.setTitle("Making Changes");
                mProgress.setMessage("Please wait...");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();
                ChangeP downloadList = new ChangeP();
                downloadList.execute("");


            }else
            {
                if(newp.isEmpty() || newpc.isEmpty())
                {
                    Toast.makeText(this , "Password Cannot be blank" , Toast.LENGTH_SHORT).show();
                }else
                {
                    Toast.makeText(this , "Passwords Dont Match" , Toast.LENGTH_SHORT).show();
                }

            }

    }


    public class confP extends AsyncTask<String,String,String>
    {
        @Override
        protected void onPostExecute(String r)
        {


            Log.e("Upload", r);
            if(r.equals("Found"))
            {
                changep();
                mProgress.dismiss();

            }else
            {
                mProgress.hide();
                Toast.makeText(AccountSettings.this , "Old Password is Incorrect." , Toast.LENGTH_SHORT).show();
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
                String oldp=passold.getText().toString();

                Log.e("File", "1"+oldp);
                Log.e("File", "1"+agentemail);
                String holdp=hashP(oldp);
                String commands="Select * from AspNetUsers where Email = '"+agentemail+"' and PasswordHash='"+holdp+"'";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(commands);


                if(rs.next())
                {

                    msg = "Found";

                }
                else
                {
                    msg = "Not Found";

                }

            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println(msg);
            return msg;

        }
    }


    public class ChangeP extends AsyncTask<String,String,String>
    {
        @Override
        protected void onPostExecute(String r)
        {
            // After successful insertion of image

            Log.e("Upload", r);
            if(r.equals("Inserted Successfully"))
            {
                mProgress.dismiss();
                Toast.makeText(AccountSettings.this , "Password Changed" , Toast.LENGTH_SHORT).show();


            }else
            {
                mProgress.hide();
                Toast.makeText(AccountSettings.this , "There was an Error Changing the Password." , Toast.LENGTH_SHORT).show();
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


                String newp=passnew.getText().toString();
                String hnewp=hashP(newp);

                String commands="UPDATE AspNetUsers" +
                        " SET PasswordHash = '" + hnewp+"'" +
                        " WHERE Email = '"+agentemail+"'";
                PreparedStatement preStmt = con.prepareStatement(commands);
                preStmt.executeUpdate();
                msg = "Inserted Successfully";
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
