package com.example.guest.congress.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.guest.congress.R;
import com.example.guest.congress.models.Representative;

import java.util.ArrayList;
import java.util.Locale;
import java.util.zip.DeflaterOutputStream;

/**
 * Created by Guest on 11/9/15.
 */
public class RepAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Representative> mRepresentatives;

    public RepAdapter(Context context, ArrayList<Representative> representatives) {
        mContext = context;
        mRepresentatives = representatives;
    }

    @Override
    public int getCount() {
        return mRepresentatives.size();
    }

    @Override
    public Object getItem(int position) {
        return mRepresentatives.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.rep_info, null);
            holder = new ViewHolder();
            holder.mRepName = (TextView) convertView.findViewById(R.id.repName);
            holder.mRepLayout = (RelativeLayout) convertView.findViewById(R.id.repLayout);
            holder.mRepParty = (TextView) convertView.findViewById(R.id.repParty);
            holder.mRepGender = (TextView) convertView.findViewById(R.id.repGender);
            holder.mRepBirthday = (TextView) convertView.findViewById(R.id.repBirthday);
            holder.mRepPhone = (TextView) convertView.findViewById(R.id.repPhone);
            holder.mRepOffice = (TextView) convertView.findViewById(R.id.repOffice);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Representative rep = mRepresentatives.get(position);

        holder.mRepName.setText(rep.getName());
        holder.mRepParty.setText(rep.getParty());
        holder.mRepGender.setText(rep.getGender());
        holder.mRepBirthday.setText(rep.getBirthday());
        holder.mRepPhone.setText(rep.getPhone());
        holder.mRepOffice.setText(rep.getOffice());

        holder.mRepOffice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = String.format(Locale.ENGLISH, "geo:0,0?q=" + rep.getOffice());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }


    private class ViewHolder {
        RelativeLayout mRepLayout;
        TextView mRepName;
        TextView mRepBirthday;
        TextView mRepGender;
        TextView mRepParty;
        TextView mRepPhone;
        TextView mRepOffice;

    }
}
