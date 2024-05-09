package com.novaardiansyah.SqlLite3;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.novaardiansyah.R;


public class CustomCursorSql extends CursorAdapter {

    private LayoutInflater ly;
    private SparseBooleanArray mSelectedItems;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public CustomCursorSql(Context context, Cursor c, int flags) {
        super(context, c, flags);
        ly = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSelectedItems = new SparseBooleanArray();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View v = ly.inflate(R.layout.listview_bills, viewGroup, false);
        MyHolder holder = new MyHolder();

        holder.ListID = (TextView)v.findViewById(R.id.listID);
        holder.ListTitle = (TextView)v.findViewById(R.id.listTitle);
        holder.listAmount = (TextView)v.findViewById(R.id.listAmount);
        holder.listStartDate = (TextView)v.findViewById(R.id.listStartDate);
        holder.ListStatus = (TextView)v.findViewById(R.id.listStatus);

        v.setTag(holder);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        MyHolder holder = (MyHolder)view.getTag();

        holder.ListID.setText(cursor.getString(cursor.getColumnIndex(DBHelper.row_id)));
        holder.ListTitle.setText("Rp. " + cursor.getString(cursor.getColumnIndex(DBHelper.row_amount)));
        holder.listAmount.setText(cursor.getString(cursor.getColumnIndex(DBHelper.row_title)));
        holder.listStartDate.setText(cursor.getString(cursor.getColumnIndex(DBHelper.row_date)) + " s/d " + cursor.getString(cursor.getColumnIndex(DBHelper.row_due_date)));
        holder.ListStatus.setText(cursor.getString(cursor.getColumnIndex(DBHelper.row_status)));
    }

    class MyHolder{
        TextView ListID;
        TextView ListTitle;
        TextView listAmount;
        TextView listStartDate;
        TextView ListStatus;
    }
}
