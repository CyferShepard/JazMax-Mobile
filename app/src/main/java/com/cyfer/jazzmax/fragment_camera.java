package com.cyfer.jazzmax;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.util.AndroidRuntimeException;
import android.util.Base64;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOError;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.nio.file.*;


public class fragment_camera extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private OnFragmentInteractionListener mListener;
    private ImageView imgTakenPic;
    private TextView SendTO;
    private Button fileUP;
    private  FloatingActionButton scan;
    FloatingActionButton up;
    private FloatingActionMenu m;
    private EditText desc;
    private static String filename="";
    private Uri file;

    FileInputStream filestream;
    String encodedImage;
    String fileloc;
    File docfile;
    byte[] byteArray;
    byte[] encodeimageb;
    public static final String PREFS_NAME = "MyPrefsIMGFile";
    private static final String PREF_IMGNAME = "img";
    ProgressBar progressBar;
    ConnectionClass connectionClass;
    private static int ACTIVITY_CHOOSE_FILE= 21;

    ////////////////////////////////////
    String userid;
    String agentfname;
    String agentmname;
    String agentlname;
    String agentemail;
    String agentusertype;
    String branchid;
    String provinceid;

    public static final String PREFS_NAME_AGENT = "MyPrefsAgent";
    private static final String PREF_USERID = "USERID";
    private static final String PREF_AGENTFNAME = "FNAME";
    private static final String PREF_AGENTMNAME = "MNAME";
    private static final String PREF_AGENTLNAME = "LNAME";
    private static final String PREF_AGENTEMAIL = "EMAIL";
    private static final String PREF_USERTYPE = "USERTYPE";
    private static final String PREF_BRANCHID = "BID";
    private static final String PREF_PROV_ID = "PID";
    Boolean cameramode=true;
    ///////////////////////////////////

    private FloatingActionButton fabcam;

    public fragment_camera() {
        // Required empty public constructor
    }



    public static fragment_camera newInstance() {
        fragment_camera fragment = new fragment_camera();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectionClass = new ConnectionClass();

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            imgTakenPic.setEnabled(false);
            ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_camera, container, false);
        imgTakenPic = (ImageView) view.findViewById(R.id.imageView);
        SendTO = view.findViewById(R.id.txt_Recipient);
        SendTO.setOnClickListener(this);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        desc = view.findViewById(R.id.txtDescription);
        Spinner dropdown = view.findViewById(R.id.spinner);
        String[] items = new String[] {"Take A Picture", "Upload a File"};
        ArrayAdapter<String> adapter =new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item,items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);
        fileUP= view.findViewById(R.id.btn_SelectFile);
        fileUP.setOnClickListener(this);

        scan= view.findViewById(R.id.fab_rescan);
        up= view.findViewById(R.id.fab_upload);
        m=view.findViewById(R.id.fabmenu);
        m.setOnClickListener(this);
        scan.setOnClickListener(this);
        up.setOnClickListener(this);

        //scan.performClick();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        recievedfromsend();
    }

    public void recievedfromsend()
    {
        SharedPreferences prefagent = getActivity().getSharedPreferences("SEND",Context.MODE_PRIVATE);
        String sendtoemail=prefagent.getString("EMAIL", "Send To...");

            SendTO.setHint(sendtoemail);


    }
    public void resetdir()
    {
        getActivity().getSharedPreferences("SEND",getActivity().MODE_PRIVATE)
                .edit()
                .putString("EMAIL","Send to...")
                .commit();
        SendTO.setHint("Send to...");
    }




    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.fab_upload:
                if(cameramode){
                    upload();
                }else
                {
                    uploadfile();
                }

                break;

            case R.id.fab_rescan:

                takePicture();
                break;
            case R.id.btn_SelectFile:

                onBrowse();
                break;

            case R.id.txt_Recipient:
                sendto();
                break;

        }
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position)
        {
            case 0: //Scan
                imgTakenPic.setVisibility(View.VISIBLE);
                if(scan.getVisibility()==View.GONE)
                {
                    scan.setVisibility(View.VISIBLE);

                }

                fileUP.setVisibility(View.GONE);
                cameramode=true;
                break;
            case 1: //File
                scan.setVisibility(View.GONE);
                fileUP.setVisibility(View.VISIBLE);
                imgTakenPic.setVisibility(View.GONE);
                cameramode=false;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }

//    public void notificationm()
//    {
//        Notification n=new Notification();
//        NotificationManagerCompat nm=new NotificationManagerCompat();
//
//    }
    //Todo: Upload PDFs



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                imgTakenPic.setEnabled(true);
            }
        }
    }

    public void takePicture() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = Uri.fromFile(getOutputMediaFile());
        Log.e("FileE",file+"");
        Log.e("FileE2",filename+"");

        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);

        startActivityForResult(intent, 100);
    }

    FileInputStream fileInputStream;
    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Jazzmax");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                Log.d("Jazzmax", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String fn="JM_"+ timeStamp + ".jpg";
        filename=fn;


        return new File(mediaStorageDir.getPath() + File.separator +fn);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {
                imgTakenPic.setImageURI(file);
                m.close(true);

            }
        }
        if(requestCode==21)
        {
            if(resultCode != Activity.RESULT_OK) return;
            String path ="";
            if(requestCode==ACTIVITY_CHOOSE_FILE)
            {
                Uri urif = data.getData();
                fileloc=urif.toString();
                File file = new File(urif.toString());
                try {
                    fileInputStream = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                Log.e("Path",urif+"");
            }
        }

    }

    public void sendto()

    {
        Intent i= new Intent(getActivity(), List_SendTO.class);
        startActivity(i);
    }


int len;


    public boolean checkDesc()
    {
        Boolean isempty=false;
        String text =desc.getText().toString();
        Log.e("Desc",text);
        if(text.equals(""))
        {
            isempty=true;
        }
        Log.e("Desc",isempty+"");
        return isempty;
    }

    public void upload()
    {
        if(!checkDesc() && !SendTO.getHint().equals("Send to...")){


        try
        {
            Bitmap image = ((BitmapDrawable) imgTakenPic.getDrawable()).getBitmap();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byteArray = byteArrayOutputStream.toByteArray();
            Log.e("Byte", byteArray+"");
            encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

            // Calling the background process so that application wont slow down
            m.close(true);
            UploadImage uploadImage = new UploadImage();
            uploadImage.execute("");
            progressBar.setVisibility(View.VISIBLE);
            //End Calling the background process so that application wont slow down
        }
        catch (Exception e)
        {
            Log.w("OOooooooooo","exception");
            Toast.makeText(getActivity() , "No Scan Taken" , Toast.LENGTH_LONG).show();
        }
        }else
        {
            Toast.makeText(getActivity() , "Description cannot be Blank." , Toast.LENGTH_LONG).show();
        }

    }



    public class UploadImage extends AsyncTask<String,String,String>
    {
        @Override
        protected void onPostExecute(String r)
        {
            // After successful insertion of image
            progressBar.setVisibility(View.GONE);
            Log.e("Upload", r);
            if(r.equals("Inserted Successfully"))
            {

                resetdir();
                Toast.makeText(getActivity() , "Upload Complete" , Toast.LENGTH_LONG).show();
                imgTakenPic.setImageURI(null);
                desc.setText("");

            }else
            {
                Toast.makeText(getActivity() , "There was an Error Uploading this Image." , Toast.LENGTH_SHORT).show();
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
                agentfname=prefagent.getString(PREF_AGENTFNAME, null);
                agentmname=prefagent.getString(PREF_AGENTMNAME, null);
                agentlname=prefagent.getString(PREF_AGENTLNAME, null);
                agentemail=prefagent.getString(PREF_AGENTEMAIL, null);
                agentusertype=prefagent.getString(PREF_USERTYPE, null);
                branchid=prefagent.getString(PREF_BRANCHID, null);
                provinceid=prefagent.getString(PREF_PROV_ID, null);
                String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
                Date nd=new Date();
                String desccript =desc.getText().toString();
                Log.e("Encode",encodedImage+"");

//TODO: Fix Encoding of Images Then Work On downloading




                byte[] b = encodedImage.getBytes(StandardCharsets.UTF_8);
                InputStream is = new ByteArrayInputStream(encodedImage.getBytes());

                String commands="INSERT INTO CoreFileUploads (FILENAMES,COREUSERID,COREUSERTYPEID,DATECREATED,DELETEDBY,DELETEDDATE,BRANCHID,PROVINCEID,COREFILETYPEID,FILEDESCRIPTION,COREFILECATEGORYID,LASTUPDATED,ISACTIVE,ISSENT,ISRECIEVED,FILECONTENT,SENTFROM,SENTTO)" +
                        " VALUES ('"+filename+"','" + userid + "','" + agentusertype + "','" + timeStamp + "',Null,'" + timeStamp + "','" + branchid + "','" + provinceid + "',1,'" + desccript + "',1,'" + timeStamp + "',1,1,0,"+b+",'" + agentemail + "','"+SendTO.getHint()+"')";
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

    ////////////////////////////////////////////////////
    public void onBrowse()
    {
        Intent choosefile;
        Intent intent;
        choosefile=new Intent(Intent.ACTION_GET_CONTENT);
        choosefile.addCategory(Intent.CATEGORY_OPENABLE);
        choosefile.setType("application/pdf");
        intent = Intent.createChooser(choosefile,"Choose a file");
        startActivityForResult(intent, ACTIVITY_CHOOSE_FILE);
    }


    public void uploadfile()
    {

            UploadFile uploadFile = new UploadFile();
            uploadFile.execute("");
            progressBar.setVisibility(View.VISIBLE);


    }

    public class UploadFile extends AsyncTask<String,String,String>
    {
        @Override
        protected void onPostExecute(String r)
        {
            // After successful insertion of image
            progressBar.setVisibility(View.GONE);
            Log.e("Upload", r);
            if(r.equals("Inserted Successfully"))
            {
                Toast.makeText(getActivity() , "Upload Complete" , Toast.LENGTH_LONG).show();
                m.close(true);
            }else
            {
                Toast.makeText(getActivity() , "There was an Error Uploading this Image." , Toast.LENGTH_SHORT).show();
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
                agentfname=prefagent.getString(PREF_AGENTFNAME, null);
                agentmname=prefagent.getString(PREF_AGENTMNAME, null);
                agentlname=prefagent.getString(PREF_AGENTLNAME, null);
                agentemail=prefagent.getString(PREF_AGENTEMAIL, null);
                agentusertype=prefagent.getString(PREF_USERTYPE, null);
                branchid=prefagent.getString(PREF_BRANCHID, null);
                provinceid=prefagent.getString(PREF_PROV_ID, null);
                String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
                Date nd=new Date();
                String desccript =desc.getText().toString();
                Log.e("Encode",byteArray+"");





                String commands="INSERT INTO CoreFileUploads (FILENAMES,COREUSERID,COREUSERTYPEID,DATECREATED,DELETEDBY,DELETEDDATE,BRANCHID,PROVINCEID,COREFILETYPEID,FILEDESCRIPTION,COREFILECATEGORYID,LASTUPDATED,ISACTIVE,ISSENT,ISRECIEVED,FILECONTENT,SENTFROM,SENTTO)" +
                        " VALUES ('"+fileloc+"','" + userid + "','" + agentusertype + "','" + timeStamp + "','None','" + timeStamp + "','" + branchid + "','" + provinceid + "',1,'" + desccript + "',1,'" + timeStamp + "',1,1,0,"+ fileInputStream +",'" + agentemail + "','"+SendTO.getHint()+"')";
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
////////////////////////////////////////////////////////////////
}
