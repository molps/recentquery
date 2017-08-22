package com.molps.recentquery;


import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.molps.recentquery.RecentQueryContract.RecentQueryEntry;

public class CustomRecAdapter extends RecyclerView.Adapter<CustomRecAdapter.MyViewHolder> {
    private Cursor cursor;

    public CustomRecAdapter(Cursor cursor) {
        this.cursor = cursor;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_query_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) {
            return;
        }
        String query = cursor.getString(cursor.getColumnIndex(RecentQueryEntry.COLUMN_QUERY));
        holder.queryTextView.setText(query);
    }

    @Override
    public int getItemCount() {
        if (cursor == null) {
            return 0;
        } else {
            return cursor.getCount();
        }
    }

    public void swapCursor(Cursor newCursor) {
        cursor = newCursor;
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView queryTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            queryTextView = (TextView) itemView.findViewById(R.id.rec_query_textView);
        }

    }
}
