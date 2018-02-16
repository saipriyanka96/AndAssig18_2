package com.example.layout.assig18_2;
//Package objects contain version information about the implementation and specification of a Java package
import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    //public keyword is used in the declaration of a class,method or field;public classes,method and fields can be accessed by the members of any class.
//extends is for extending a class. implements is for implementing an interface
//AppCompatActivity is a class from e v7 appcompat library. This is a compatibility library that back ports some features of recent versions of
// Android to older devices.
    // implements means you are using the elements of a Java Interface in your class.
//Interface definition for a callback to be invoked when an item in this AdapterView has been clicked.
    //Declaration
    private static MainActivity inst;
    //arraylist will have list of array we created
    ArrayList<String> smsMessagesList = new ArrayList<String>();
    ListView smsListView;
    //listview will show the list
    //Array adapter is a view
    ArrayAdapter arrayAdapter;

    //returns the object to the activity
    public static MainActivity instance() {
        return inst;
    }

    @Override
    //Called after onCreate(Bundle) — or after onRestart() when the activity had been stopped, but is now again being displayed to the user. It will be followed by onResume().
    public void onStart() {
        super.onStart();
        // super keyword in java is a reference variable which is used to refer immediate parent class object.
        inst = this;//refers to the current object
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Variables, methods, and constructors, which are declared protected in a superclass can be accessed only by the subclasses
        // in other package or any class within the package of the protected members class.
        //void is a Java keyword.  Used at method declaration and definition to specify that the method does not return any type,
        // the method returns void.
        //onCreate Called when the activity is first created. This is where you should do all of your normal static set up: create views,
        // bind data to lists, etc. This method also provides you with a Bundle containing the activity's previously frozen state,
        // if there was one.Always followed by onStart().
        //Bundle is most often used for passing data through various Activities.
// This callback is called only when there is a saved instance previously saved using onSaveInstanceState().
// We restore some state in onCreate() while we can optionally restore other state here, possibly usable after onStart() has
// completed.The savedInstanceState Bundle is same as the one used in onCreate().

        super.onCreate(savedInstanceState);
// call the super class onCreate to complete the creation of activity like the view hierarchy
        setContentView(R.layout.activity_main);
        //R means Resource
        //layout means design
        //  main is the xml you have created under res->layout->main.xml
        //  Whenever you want to change your current Look of an Activity or when you move from one Activity to another .
        // The other Activity must have a design to show . So we call this method in onCreate and this is the second statement to set
        // the design
        ///findViewById:A user interface element that displays text to the user.
        //Setting up UI Component
        smsListView = (ListView) findViewById(R.id.SMSList);
        //array adapter will take the list and maintains it in a layout
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, smsMessagesList);
        smsListView.setAdapter(arrayAdapter);
        //array adapter is to be set
        smsListView.setOnItemClickListener(this);
        //item listener will take the sms list
        // Condition for Checking permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
           //it will debug the data and sees if we gave the permission to read the data and requests for permission
            Log.d("SmsReceiver", "Permission is not granted, requesting");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 123);
            smsListView.setEnabled(false);//
        } else {
            Log.d("SmsReceiver", "Permission is granted");
        }
    }

    //  onRequestPermissionsResult() Method
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("SmsReceiver", "Permission has been granted");
                refreshSmsInbox();
                smsListView.setEnabled(true);
                //Set the enabled state of this view with the boolean value
            } else {
                Log.d("SmsReceiver", "Permission has been denied or request cancelled");
                //other wise it is denied
            }
        }
    }

    // Method refreshSmsInbox()
    public void refreshSmsInbox() {
        // object of contentResolver
        //This class provides applications access to the content model.
        ContentResolver contentResolver = getContentResolver();
        // query
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        /**Parameters
         uri	Uri: The URI, using the content:// scheme, for the content to retrieve.
         This value must never be null.

         projection	String: A list of which columns to return. Passing null will return all columns, which is inefficient.
         selection	String: A filter declaring which rows to return, formatted as an SQL WHERE clause (excluding the WHERE itself). Passing null will return all rows for the given URI.
         selectionArgs	String: You may include ?s in selection, which will be replaced by the values from selectionArgs, in the order that they appear in the selection. The values will be bound as Strings.
         This value may be null.

         sortOrder	String: How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort order, which may be unordered.
         cancellationSignal	CancellationSignal: A signal to cancel the operation in progress, or null if none. If the operation is canceled, then OperationCanceledException will be thrown when the query is executed.
         Returns
         Cursor	A Cursor object, which is positioned before the first entry, or null**/
        int indexBody = smsInboxCursor.getColumnIndex("body");
        //body and address of the sma
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        // Condition
        if (indexBody < 0 || !smsInboxCursor.moveToFirst())
            return;
        //array adapter will clear once the cusor moves to first
        arrayAdapter.clear();
        do {
            //string will have the sender's number with messafe address and the body and which will be added to the array adapter
            String str = "senderNum:  " + smsInboxCursor.getString(indexAddress) +
                    "\n" + smsInboxCursor.getString(indexBody) + "\n";
            arrayAdapter.add(str);
        } while (smsInboxCursor.moveToNext());
    }

    // Method updateList
    public void updateList(final String smsMessage) {
        //inserting in arrayAdapter
        //once a new msg is sent it need to update messgae box
        arrayAdapter.insert(smsMessage, 0);
        arrayAdapter.notifyDataSetChanged();
    }

    // Method onItemClick
    /**Callback method to be invoked when an item in this AdapterView has been clicked.

     Implementers can call getItemAtPosition(position) if they need to access the data associated with the selected item.

     Parameters
     parent	AdapterView: The AdapterView where the click happened.
     view	View: The view within the AdapterView that was clicked (this will be a view provided by the adapter)
     position	int: The position of the view in the adapter.
     id	long: The row id of the item that was clicked.**/
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        // try and catch
        try {
            //Taking String
            //split() method breaks a given string around matches of the given regular expression.
            String[] smsMessages = smsMessagesList.get(pos).split("\n");
            String address = smsMessages[0];
            //index of the mesg will start from 0
            String smsMessage = "";
            //initializing Sms
            //length  of the sms
            for (int i = 1; i < smsMessages.length; ++i) {
                smsMessage += smsMessages[i];
                //sms upgrades and it is equal to index
            }
            String smsMessageStr = address + "\n";
            //msg string is equal to address and that string is equal to msg
            smsMessageStr += smsMessage;
            //toast will shows the msg
            Toast.makeText(this, smsMessageStr, Toast.LENGTH_LONG).show();//Showing Toast
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
