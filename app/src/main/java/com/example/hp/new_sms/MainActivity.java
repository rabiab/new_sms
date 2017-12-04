package com.example.hp.new_sms;


import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class MainActivity extends AppCompatActivity {

    TextView tvShowMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvShowMessages= (TextView) findViewById(R.id.tvShow);
    }

    public void getAll(View v)
    {
        requestPermission();
    }

    public void getMessages()
    {
        ArrayList<String> numbers;
        ArrayList<String> messages;
        String msgData=null;
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");

        if (indexBody < 0 || !smsInboxCursor.moveToFirst())
        {
            tvShowMessages.setText("No Messages Found");
        }
        else
        {

            msgData=new String();
            numbers=new ArrayList<String>();
            messages=new ArrayList<String>();

            do {

                numbers.add(smsInboxCursor.getString(indexAddress));
                messages.add(smsInboxCursor.getString(indexBody));


            } while (smsInboxCursor.moveToNext());

            if(msgData!=null)
            {
                taskSorting obj=new taskSorting();
                obj.setData(numbers,messages);
                obj.execute();

            }
            else
            {
                tvShowMessages.setText("No Message Found! After Loop");
            }
        }
    }

    public void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("Your Permission Required");
                dialog.setMessage("Some decoding needs respective of your Messages");
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS}, 1);
                    }
                });
                dialog.setNegativeButton("Do not Want", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this,"User is King!",Toast.LENGTH_LONG).show();
                    }
                });
                dialog.show();


            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 1);
            }
        }
        else
        {
            getMessages();
        }
    }

    public void checkForPermission()
    {
        int permissionCheck=ContextCompat.checkSelfPermission(this, android.Manifest.permission_group.SMS);

        if(permissionCheck!=PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission_group.SMS))
            {
                AlertDialog.Builder builder=new AlertDialog.Builder(this).setTitle("We Need to read your messages")
                        .setMessage("Are you OK to go with").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MainActivity.this,new String[]{android.Manifest.permission_group.SMS},111);
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                AlertDialog dialog=builder.create();
                dialog.show();
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_SMS},111);
            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==1)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                getMessages();
            }
            else if(grantResults[0]==PackageManager.PERMISSION_DENIED)
            {
                tvShowMessages.setText("Permission Index 0");
            }
        }

    }

    class taskSorting extends AsyncTask<Void,Void,Void>
    {
        ArrayList<String> numbers;
        ArrayList<String> messages;

        ArrayList<String> sortedNumbers;//to contain just numbers that are appeared with you while chatting

        ArrayList<ArrayList<String>> sortedMessages;// contain messages each index contain array, and each array contains messages recieved corresponding to number stored at same index on numbers array
        public void setData(ArrayList<String> numbers,ArrayList<String> messages)
        {
            this.numbers=numbers;
            this.messages=messages;
        }
        @Override
        protected Void doInBackground(Void... voids) {


            //to remove duplications from numbers array

            LinkedHashSet<String> tempNumbers=new LinkedHashSet<>();
            tempNumbers.addAll(numbers);
            sortedNumbers.addAll(tempNumbers);

            //to get messages from same number
            ArrayList<String> messagesUnderSameNumber=new ArrayList<>();
            for(int i=0;i<sortedNumbers.size();i++)
            {
                String currentNumber=sortedNumbers.get(i);
                messagesUnderSameNumber=new ArrayList<String>();
                for(int j=0;j<messages.size();j++)
                {
                    if(numbers.get(j)==currentNumber)
                    {
                        messagesUnderSameNumber.add(messages.get(j));
                    }
                }
                sortedMessages.add(messagesUnderSameNumber);

            }

            return null;
        }
    }
}