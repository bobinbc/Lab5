package com.example.bobin_000.lab5;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    //Global variables
    //Database Handler reference
    DBAdapter dbHelper;
    //CursorAdapter to set CustomListView
    SimpleCursorAdapter myCursorAdapter;
    //Inflator to show pop up window
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
    }

    //Initilizing database handler reference
    private void openDB() {
        dbHelper = new DBAdapter(this);
        dbHelper.open();
    }

    private void populateListView(){
        //Getting all student details saving into cursor
        Cursor cursor = dbHelper.getAllRows();
        //Storing Student table column names
        String[] fromFieldNames = new String[]{DBAdapter.KEY_FIRSTNAME,DBAdapter.KEY_LASTNAME, DBAdapter.KEY_MARKS};
        //Mapping data to fields in custom_list.xml
        int[] toViewIDs = new int[]{R.id.textViewFirstName, R.id.textViewLastName, R.id.textViewMarks};

        //Initializing Cursor Adapter
        myCursorAdapter = new SimpleCursorAdapter(getBaseContext(), R.layout.custom_list, cursor, fromFieldNames, toViewIDs,0);

        //Setting Adapter with ListView
        ListView myList = (ListView) findViewById(R.id.listView_student);
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

        //Removing ID field as it is not required for Add Screen
        final TextView tvID = (TextView)view2.findViewById(R.id.textViewID);
        tvID.setText("");

        //Click Listener of Positive Button (Add) - Add to database
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                inflater.inflate(R.layout.modify_screen, null);

                //Getting references of edit texts in popup window
                final EditText etFirstName = (EditText) view2.findViewById(R.id.editFirstName);
                final EditText etLastName = (EditText) view2.findViewById(R.id.editLastName);
                final EditText etMarks = (EditText) view2.findViewById(R.id.editMarks);

                //Insert details into database from edittext fields - First Name, Last Name and Marks
                boolean result = dbHelper.insertRow(etFirstName.getText().toString(),etLastName.getText().toString(),Integer.parseInt(etMarks.getText().toString()));
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

    //On click of Edit button, retrieves row details and populates the modify window
    public void onClick_EditButton(View view)
    {
        //Getting List reference
        ListView myList = (ListView) findViewById(R.id.listView_student);
        //Getting position from the list
        int position = myList.getPositionForView((View)view.getParent());
        //Getting itemID based on retrieved position
        long valueID = myCursorAdapter.getItemId(position);
        //Getting row details based on ID into cursor
        Cursor sampCursor = dbHelper.getRow(valueID);

        //Storing the details of the row into local variables
        String editID = String.valueOf(sampCursor.getLong(0));
        String editFName = sampCursor.getString(1).toString();
        String editLName = sampCursor.getString(2).toString();
        String editMarks = String.valueOf(sampCursor.getInt(3));

        //Setting up popup window using AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        view = inflater.inflate(R.layout.modify_screen, null);
        builder.setView(view);
        builder.setTitle("Edit Student");
        final View view2 = view;

        //Getting references of popup window Text and Edit fields
        final TextView tvID = (TextView) view2.findViewById(R.id.viewID);
        final EditText etFirstName = (EditText) view2.findViewById(R.id.editFirstName);
        final EditText etLastName = (EditText) view2.findViewById(R.id.editLastName);
        final EditText etMarks = (EditText) view2.findViewById(R.id.editMarks);

        //Setting text fields based on retrieved values
        tvID.setText(editID);
        etFirstName.setText(editFName);
        etLastName.setText(editLName);
        etMarks.setText(editMarks);

        //Click Listener of Positive Button (Edit) - Update changes to database
        builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                inflater.inflate(R.layout.modify_screen, null);

                //Getting references of edit texts in popup window
                final TextView tvID = (TextView) view2.findViewById(R.id.viewID);
                final EditText etFirstName = (EditText) view2.findViewById(R.id.editFirstName);
                final EditText etLastName = (EditText) view2.findViewById(R.id.editLastName);
                final EditText etMarks = (EditText) view2.findViewById(R.id.editMarks);

                //Updating database with changed values
                boolean result = dbHelper.updateRow( Long.parseLong(tvID.getText().toString()), etFirstName.getText().toString(),etLastName.getText().toString(),Integer.parseInt(etMarks.getText().toString()));

                //Set toast message based on result
                if (result)
                    Toast.makeText(getApplicationContext(), "Student:"+etLastName.getText().toString()+", "+etFirstName.getText().toString()+"  updated successfully", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), "Student:"+etLastName.getText().toString()+", "+etFirstName.getText().toString()+"  updating failed", Toast.LENGTH_SHORT).show();

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

    public void onClick_DeleteButton(View view)
    {
        //Getting List reference
        ListView myList = (ListView) findViewById(R.id.listView_student);
        //Getting position from the list
        final int position = myList.getPositionForView((View) view.getParent());
        //Getting itemID based on retrieved position
        long valueID = myCursorAdapter.getItemId(position);
        //Getting row details based on ID into cursor
        Cursor sampCursor = dbHelper.getRow(valueID);

        //Storing the details of the row into local variables
        String editFName = sampCursor.getString(1).toString();
        String editLName = sampCursor.getString(2).toString();

        //Deleting row from database based on ID
        boolean result = dbHelper.deleteRow(valueID);

        //Set toast message based on result
        if(result)
            Toast.makeText(getApplicationContext(),"Student:"+editLName+", "+editFName+"  deleted",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getApplicationContext(),"Student:"+editLName+", "+editFName+"  failed to delete",Toast.LENGTH_SHORT).show();

        //Reload list with latest data
        populateListView();
    }
}
