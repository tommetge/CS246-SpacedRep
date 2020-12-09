package com.cs246.team1.spacedrepetition;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ReminderAdapter extends ArrayAdapter<Reminder> {
    private Context _context;
    private List<Reminder> _reminders = new ArrayList<Reminder>();

    public ReminderAdapter(Context context, List<Reminder> reminders) {
        super(context, 0, reminders);
        _context = context;
        _reminders = reminders;
    }

    @Override
    public View getView(int position, View listItem, ViewGroup parent) {
        if (listItem == null) {
            listItem = LayoutInflater.from(_context).inflate(
                    R.layout.list_item_reminder, parent, false);
        }

        Reminder reminder = _reminders.get(position);

        ImageView image = (ImageView)listItem.findViewById(R.id.iconView);
        image.setImageResource(R.drawable.ic_stat_name);

        TextView summary = (TextView)listItem.findViewById(R.id.summaryText);
        summary.setText(reminder.getSummary());

        return listItem;
    }
}
