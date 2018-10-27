package com.cyfer.jazzmax;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.AndroidRuntimeException;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;

import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
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


public class fragment_download extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    ConnectionClass connectionClass;
    ListView lv;
    String filenameD;
    private Uri downloadimg;
    List<String> download_array_File = new ArrayList<String>();
    private NotificationManagerCompat notificationManagerCompat;
    ProgressBar progressBar;

    ////////////////////////////////////

    String agentemail;

    public static final String PREFS_NAME_AGENT = "MyPrefsAgent";

    private static final String PREF_AGENTEMAIL = "EMAIL";

    ///////////////////////////////////

    private OnFragmentInteractionListener mListener;

    public fragment_download() {
        // Required empty public constructor
    }

    public static fragment_download newInstance() {
        fragment_download fragment = new fragment_download();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectionClass = new ConnectionClass();

        notificationManagerCompat=NotificationManagerCompat.from(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_download, container, false);
        lv=view.findViewById(R.id.listDownload);
        lv.setOnItemClickListener(this);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar_DownloadList);
        progressBar.setVisibility(View.GONE);
        getFileList();
        checknewfiles();



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
    public void onClick(View v) {


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
       String selecteditem= (String) parent.getItemAtPosition(position);
        getfilename(selecteditem);
        AlertDialog.Builder opt=new AlertDialog.Builder(getActivity());
        opt.setMessage(filenameD);
        opt.setPositiveButton("Download", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(fileformat().equals(".jpg"))
                {
                    download();
                }
                else
                {
                    downloadPDF();
                }

            }
        });
        opt.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DeleteFile DeleteFile=new DeleteFile();
                DeleteFile.execute("");
            }
        });
        opt.create().show();

    }


    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }



    public void getFileList()
    {
        download_array_File = new ArrayList<String>();

           DownloadList downloadList = new DownloadList();
            downloadList.execute("");
        progressBar.setVisibility(View.VISIBLE);

    }
    public String fileformat()
    {
        String formmat=filenameD.substring(filenameD.length()-4,filenameD.length());
        return formmat;
    }

    public void checknewfiles()
    {
        DownloadListN downloadListn = new DownloadListN();
        downloadListn.execute("");
    }
    public void getfilename(String d)
    {
        int line=d.indexOf("\n");
        String item=d.substring(0,line);
        Log.e("Name",item);
        int space=item.indexOf(":");
        item=item.substring(space+1);
        Log.e("Name",item);
        filenameD=item.substring(1);
        fileformat();

    }

    public void download()
    {


        DownloadImage downloadImage=new DownloadImage();
        downloadImage.execute("");
    }
    public void downloadPDF()
    {


        DownloadPDF DownloadPDF=new DownloadPDF();
        DownloadPDF.execute("");
    }
    public void notificationFiles(int nid, String f, String sender)
    {
        Notification notification = new NotificationCompat.Builder(getActivity(), NotificationChannels.CHANNEL_1_ID)
                .setSmallIcon(R.drawable.jazzmax_ico)
                .setContentTitle("New File from "+sender)
                .setContentText(f)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();
        notificationManagerCompat.notify(nid, notification);

    }


    private static File getOutputMediaFile(String fn){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "Jazzmax Downloads");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                Log.d("Jazzmax", "failed to create directory");
                return null;
            }
        }


        return new File(mediaStorageDir.getPath() + File.separator +fn);
    }



//TODO: Download JPEG/PDF and store it


    public class DownloadList extends AsyncTask<String,String,String>
    {
        @Override
        protected void onPostExecute(String r)
        {

            progressBar.setVisibility(View.GONE);
            Log.e("Upload", r);
            if(r.equals("Success"))
            {
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, download_array_File);
                lv.setAdapter(arrayAdapter);

            }else
            {
                Toast.makeText(getActivity() , "No files found" , Toast.LENGTH_LONG).show();
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
                String commands="Select FileNames, FileDescription, SentFrom from CoreFileUploads where SentFrom = '"+agentemail+"' or SentTo='"+agentemail+"'";

                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(commands);

                while (rs.next())
                {

                    String Item="File: "+rs.getString("FileNames")+"\nDescription: "+rs.getString("FileDescription")+"\nFrom: "+rs.getString("SentFrom");
                    Log.e("File", Item);
                    download_array_File.add(Item);
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




    public class DownloadListN extends AsyncTask<String,String,String>
    {
        @Override
        protected void onPostExecute(String r)
        {

            Log.e("Upload", r);
            if(r.equals("Success"))
            {


            }else
            {
                Toast.makeText(getActivity() , "No new files" , Toast.LENGTH_SHORT).show();
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
                String commands="Select FileNames, isRecieved, SentFrom from CoreFileUploads where SentTo = '"+agentemail+"' and isRecieved=0 or SentTO = 'Document Directory' and isRecieved=0" ;

                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(commands);
                int nid=1;

                while (rs.next())
                {

                    String f=rs.getString("FileNames");
                    String Sender =rs.getString("SentFrom");

                    notificationFiles(nid,f, Sender);

                    msg = "Success";
                    nid++;

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


    private class DownloadImage extends AsyncTask<String, Void, String>
    {
        String image="";
        String msg =  "";
        ResultSet rs;

        @Override
        protected void onPreExecute()
        {
//            errorMsg.setText("Downloading Please Wait...");
//            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params)
        {
            String msg =  "";
            try
            {
                //Connecting
                Connection con = connectionClass.CONN();
                Log.e("BitMap","1");

                // Lets suppose the image which existed in the database has an ID=1 and we want to retrieve that image.
			            /*
                      	   Here column picture is that column which contains the picture
                     	*/
                String commands = "SELECT FileContent From CoreFileUploads WHERE FileNames = '"+filenameD+"' and FileContent != 'NULL'";
                Log.e("BitMap","2");
                Log.e("BitMap","!"+filenameD+"");
                Statement stmt = con.createStatement();
                Log.e("BitMap","3");
                rs = stmt.executeQuery(commands);
                Log.e("BitMap","4");
                if(rs.next())
                {
                    image = rs.getString("FileContent");
                    msg = "Retrieved Successfully";
                    Log.e("BitMap","5next");
                }
                else
                {
                    msg = "Image not Found in the Database";
                    Log.e("BitMap","5skp");
                }
            }
            // Catching all the exceptions
            catch (SQLException ex)
            {
                msg = ex.getMessage().toString();
               
            }
            catch (IOError ex)
            {

                msg = ex.getMessage().toString();
               
            }
            catch (AndroidRuntimeException ex)
            {
                msg = ex.getMessage().toString();
               
            }
            catch (NullPointerException ex)
            {
                msg = ex.getMessage().toString();
               
            }
            catch (Exception ex)
            {
                msg = ex.getMessage().toString();
               
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String resultSet)
        {

            if(resultSet.equals("Retrieved Successfully"))
            {
                image="0x"+image;
                Log.e("BitMap",image+" Decode");
                byte[] decodeString = Base64.decode(image, Base64.DEFAULT);
                Bitmap decodebitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
                Log.e("BitMap","Decode");

                saveToInternalStorage(decodebitmap);
            }
            else
            {
                Toast.makeText(getActivity() , "Unable to Download File" , Toast.LENGTH_LONG).show();

            }
        }
    }

    private class DownloadPDF extends AsyncTask<String, Void, String>
    {
        String image="";
        String msg =  "";
        ResultSet rs;

        @Override
        protected void onPreExecute()
        {
//            errorMsg.setText("Downloading Please Wait...");
//            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params)
        {
            String msg =  "";
            try
            {
                //Connecting
                Connection con = connectionClass.CONN();
                Log.e("BitMap","1");

                // Lets suppose the image which existed in the database has an ID=1 and we want to retrieve that image.
			            /*
                      	   Here column picture is that column which contains the picture
                     	*/
                String commands = "SELECT FileContent From CoreFileUploads WHERE FileNames = '"+filenameD+"' and FileContent != 'NULL'";

                Statement stmt = con.createStatement();

                rs = stmt.executeQuery(commands);

                if(rs.next())
                {

                    msg = "Retrieved Successfully";

                }
                else
                {
                    msg = "File not Found in the Database";

                }
            }
            // Catching all the exceptions
            catch (SQLException ex)
            {
                msg = ex.getMessage().toString();
               
            }
            catch (IOError ex)
            {

                msg = ex.getMessage().toString();
               
            }
            catch (AndroidRuntimeException ex)
            {
                msg = ex.getMessage().toString();
               
            }
            catch (NullPointerException ex)
            {
                msg = ex.getMessage().toString();
               
            }
            catch (Exception ex)
            {
                msg = ex.getMessage().toString();
               
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String resultSet)
        {

            if(resultSet.equals("Retrieved Successfully"))
            {
                image="0x"+image;
                Log.e("BitMap",image+" Decode");
                byte[] decodeString = Base64.decode(image, Base64.DEFAULT);
                Bitmap decodebitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
                Log.e("BitMap","Decode");

                saveToInternalStorage(decodebitmap);
            }
            else
            {
                Toast.makeText(getActivity() , "Unable to Download File" , Toast.LENGTH_LONG).show();

            }
        }
    }

    private class DeleteFile extends AsyncTask<String, Void, String>
    {
        String image="";
        String msg =  "";
        ResultSet rs;

        @Override
        protected void onPreExecute()
        {
//            errorMsg.setText("Downloading Please Wait...");
//            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params)
        {
            String msg =  "";
            try
            {
                //Connecting
                Connection con = connectionClass.CONN();
                Log.e("BitMap","1");

                // Lets suppose the image which existed in the database has an ID=1 and we want to retrieve that image.
			            /*
                      	   Here column picture is that column which contains the picture
                     	*/
                String commands = "Delete From CoreFileUploads WHERE FileNames = '"+filenameD+"'";

                Statement stmt = con.createStatement();

                rs = stmt.executeQuery(commands);

                if(rs.next())
                {

                    msg = "Deleted successfully";

                }
                else
                {
                    msg = "Unable to Delete";

                }
            }
            // Catching all the exceptions
            catch (SQLException ex)
            {
                msg = ex.getMessage().toString();
               
            }
            catch (IOError ex)
            {

                msg = ex.getMessage().toString();
               
            }
            catch (AndroidRuntimeException ex)
            {
                msg = ex.getMessage().toString();
               
            }
            catch (NullPointerException ex)
            {
                msg = ex.getMessage().toString();
               
            }
            catch (Exception ex)
            {
                msg = ex.getMessage().toString();
               
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String resultSet)
        {

            if(resultSet.matches(""))
            {
                Toast.makeText(getActivity() , "Unable To Delete File" , Toast.LENGTH_LONG).show();

            }
            else
            {
                getFileList();

                Toast.makeText(getActivity() , "File Deleted Successfully" , Toast.LENGTH_LONG).show();
            }
        }
    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getActivity());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "Jazzmax");
        if (!directory.exists()){
            if (!directory.mkdirs()){
                Log.d("Jazzmax", "failed to create directory");
                return null;
            }
        }



        // Create imageDir
        Log.e("BitMap",directory+"Decode");
        File mypath=new File(directory,filenameD);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
                Log.e("BitMap","Done");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    //convert base64 to bitmap

    private Bitmap base64ToBitmap(String b64) {
        byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }


}
