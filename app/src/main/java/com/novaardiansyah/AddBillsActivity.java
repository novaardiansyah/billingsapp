package com.novaardiansyah;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.novaardiansyah.SqlLite3.DBHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddBillsActivity extends AppCompatActivity {

    DBHelper dbHelper;
    TextView TvStatus;
    Button btnProccess;
    EditText TxID, TxTitle, TxAmount, TxStartDate, TxDueDate, TxStatus;
    long id;
    DatePickerDialog datePickerDialog;
    SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bills);

        dbHelper = new DBHelper(this);

        id = getIntent().getLongExtra(DBHelper.row_id, 0);

        TxID = (EditText)findViewById(R.id.txID);
        TxTitle = (EditText)findViewById(R.id.txTitle);
        TxAmount = (EditText)findViewById(R.id.txAmount);
        TxStartDate = (EditText)findViewById(R.id.txStartDate);
        TxDueDate = (EditText)findViewById(R.id.txDueDate);
        TxStatus = (EditText)findViewById(R.id.txStatus);

        TvStatus = (TextView)findViewById(R.id.tVStatus);
        btnProccess = (Button)findViewById(R.id.btnProses);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        getData();

        TxStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog("start");
            }
        });
        TxDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog("due");
            }
        });

        btnProccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proccessFinish();
            }
        });

        ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setDisplayHomeAsUpEnabled(true);
    }

    private void proccessFinish() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(AddBillsActivity.this);
        builder.setMessage("Has this bill been paid?");
        builder.setCancelable(true);
        builder.setPositiveButton("Already Paid", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String idtask = TxID.getText().toString().trim();
                String paid = "Paid";

                ContentValues values = new ContentValues();

                values.put(DBHelper.row_status, paid);
                dbHelper.updateData(values, id);
                Toast.makeText(AddBillsActivity.this, "The bill has been successfully paid!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDateDialog(String dateType) {
        Calendar calendar = Calendar.getInstance();

        if (dateType == "start") {
            datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, month, dayOfMonth);
                    TxStartDate.setText(dateFormatter.format(newDate.getTime()));
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        } else {
            datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, month, dayOfMonth);
                    TxDueDate.setText(dateFormatter.format(newDate.getTime()));
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        }
        datePickerDialog.show();
    }

    private void getData() {
        Calendar c1 = Calendar.getInstance();
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        Cursor cur = dbHelper.oneData(id);
        if(cur.moveToFirst()){
            String idbill = cur.getString(cur.getColumnIndex(DBHelper.row_id));
            String title = cur.getString(cur.getColumnIndex(DBHelper.row_title));
            String amount = cur.getString(cur.getColumnIndex(DBHelper.row_amount));
            String startDate = cur.getString(cur.getColumnIndex(DBHelper.row_date));
            String dueDate = cur.getString(cur.getColumnIndex(DBHelper.row_due_date));
            String status = cur.getString(cur.getColumnIndex(DBHelper.row_status));

            TxID.setText(idbill);
            TxTitle.setText(title);
            TxAmount.setText(amount);
            TxStartDate.setText(startDate);
            TxDueDate.setText(dueDate);
            TxStatus.setText(status);

            if (TxID.equals("")){
                TvStatus.setVisibility(View.GONE);
                TxStatus.setVisibility(View.GONE);
                btnProccess.setVisibility(View.GONE);
            }else{
                TvStatus.setVisibility(View.VISIBLE);
                TxStatus.setVisibility(View.VISIBLE);
                btnProccess.setVisibility(View.VISIBLE);
            }

            if(status.equals("Pending")){
                btnProccess.setVisibility(View.VISIBLE);
            }else {
                btnProccess.setVisibility(View.GONE);
                TxTitle.setEnabled(false);
                TxAmount.setEnabled(false);
                TxStartDate.setEnabled(false);
                TxStartDate.setEnabled(false);
                TxStatus.setEnabled(false);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.billing_menu, menu);
        String idbill = TxID.getText().toString().trim();
        String status = TxStatus.getText().toString().trim();

        MenuItem itemDelete = menu.findItem(R.id.action_delete);
        MenuItem itemClear = menu.findItem(R.id.action_clear);
        MenuItem itemSave = menu.findItem(R.id.action_save);

        if (idbill.equals("")){
            itemDelete.setVisible(false);
            itemClear.setVisible(true);
        }else {
            itemDelete.setVisible(true);
            itemClear.setVisible(false);
        }

        if(status.equals("Paid")){
            itemSave.setVisible(false);
            itemDelete.setVisible(true);
            itemClear.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:
                insertAndUpdate();
        }
        switch (item.getItemId()){
            case R.id.action_clear:
                TxTitle.setText("");
                TxAmount.setText("");
                TxStartDate.setText("");
                TxDueDate.setText("");
        }
        switch (item.getItemId()){
            case R.id.action_delete:
                final AlertDialog.Builder builder = new AlertDialog.Builder(AddBillsActivity.this);
                builder.setMessage("Are you sure?");
                builder.setCancelable(true);
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.deleteData(id);
                        Toast.makeText(AddBillsActivity.this, "The bill has been successfully deleted!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void insertAndUpdate(){
        String idbill = TxID.getText().toString().trim();
        String title = TxTitle.getText().toString().trim();
        String amount = TxAmount.getText().toString().trim();
        String startDate = TxStartDate.getText().toString().trim();
        String dueDate = TxDueDate.getText().toString().trim();
        String status = "Pending";

        ContentValues values = new ContentValues();

        values.put(DBHelper.row_title, title);
        values.put(DBHelper.row_amount, amount);
        values.put(DBHelper.row_date, startDate);
        values.put(DBHelper.row_due_date, dueDate);
        values.put(DBHelper.row_status, status);

        if (title.equals("") || amount.equals("") || startDate.equals("") || dueDate.equals("")){
            Toast.makeText(AddBillsActivity.this, "The form is not valid, please try again!", Toast.LENGTH_SHORT).show();
        }else {
            if(idbill.equals("")){
                dbHelper.insertData(values);
            }else {
                dbHelper.updateData(values, id);
            }

            Toast.makeText(AddBillsActivity.this, "The bill has been successfully added!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
