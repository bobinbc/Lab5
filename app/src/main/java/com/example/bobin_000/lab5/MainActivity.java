package com.example.bobin_000.lab5;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends Activity {

    //Global variables
    //Database Handler reference
    DBAdapter dbHelper;
    //CursorAdapter to set CustomListView
    SimpleCursorAdapter myCursorAdapter;
    //Inflater to show pop up window
    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Initializing inflater
        inflater = MainActivity.this.getLayoutInflater();
        //Instantiating Database
        openDB();
        //Populate List with all records in database
        populateListView();
        ListView myList = (ListView) findViewById(R.id.listView_contact);
        //Display name on short single click of node
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Getting row details based on ID into cursor
                Cursor sampCursor = dbHelper.getRow(l);

                //Storing the details of the row into local variables
                String firstName = String.valueOf(sampCursor.getString(1));
                String lastName = String.valueOf(sampCursor.getString(2));

                //Display Text as Toast
                Toast.makeText(MainActivity.this, firstName + " " + lastName, Toast.LENGTH_SHORT).show();
            }
        });
        //Delete node on long click
        myList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor sampCursor = dbHelper.getRow(l);
                boolean result = dbHelper.deleteRow(l);

                String firstName = String.valueOf(sampCursor.getString(1));
                String lastName = String.valueOf(sampCursor.getString(2));

                //Display Text as Toast
                Toast.makeText(MainActivity.this, firstName + " " + lastName + " deleted", Toast.LENGTH_SHORT).show();

                populateListView();
                return result;
            }
        });
    }

    //Initializing database handler reference
    private void openDB() {
        dbHelper = new DBAdapter(this);
        dbHelper.open();
    }

    private void populateListView() {
        //Getting all student details saving into cursor
        Cursor cursor = dbHelper.getAllRows();
        //Storing Student table column names
        String[] fromFieldNames = new String[]{DBAdapter.KEY_FIRSTNAME, DBAdapter.KEY_LASTNAME};
        //Mapping data to fields in custom_list.xml
        int[] toViewIDs = new int[]{R.id.textViewFirstName, R.id.textViewLastName};

        //Initializing Cursor Adapter
        myCursorAdapter = new SimpleCursorAdapter(getBaseContext(), R.layout.custom_list, cursor, fromFieldNames, toViewIDs, 0);

        //Setting Adapter with ListView
        ListView myList = (ListView) findViewById(R.id.listView_contact);
        myList.setAdapter(myCursorAdapter);
    }

    public void onClick_AddPopUp(View view){
        //Initializing an Alert Builder for popup window
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        //Inflating custom view for the popup window
        view = inflater.inflate(R.layout.modify_screen, null);
        //Setting the view for the popup window
        builder.setView(view);
        //Setting title for popup window
        builder.setTitle("Add Student");
        //Creating a reference copy of view for the onClick Listener
        final View view2 = view;

        //Click Listener of Positive Button (Add) - Add to database
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                inflater.inflate(R.layout.modify_screen, null);

                //Getting references of edit texts in popup window
                final EditText etFirstName = (EditText) view2.findViewById(R.id.editFirstName);
                final EditText etLastName = (EditText) view2.findViewById(R.id.editLastName);

                //Insert details into database from edittext fields - First Name, Last Name and Marks
                boolean result = dbHelper.insertRow(etFirstName.getText().toString(), etLastName.getText().toString());
                //Set toast message based on result
                if (result)
                    Toast.makeText(getApplicationContext(), "Student:"+etLastName.getText().toString()+", "+etFirstName.getText().toString()+"  added successfully", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), "Student:"+etLastName.getText().toString()+", "+etFirstName.getText().toString()+"  adding failed", Toast.LENGTH_SHORT).show();

                //Reload list with latest data
                populateListView();
            }
        })
                //Click Listener of Negative Button (Cancel) - Do nothing and close window
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        //Show popup window
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
