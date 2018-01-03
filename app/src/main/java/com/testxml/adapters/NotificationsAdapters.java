package com.testxml.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

import com.testxml.R;
import com.testxml.activities.WebActivity;
import com.testxml.models.Notifications;
import com.testxml.outils.TimeAgo;
import com.testxml.services.Services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by Raoelson on 12/09/2017.
 */

public class NotificationsAdapters extends RecyclerView.Adapter<NotificationsAdapters.ViewHolder> {

    // region Member Variables
    private Context mContext;
    List<Notifications> notificationsList;
    TimeAgo timeAgo;
    View v;
    Services services;


    public NotificationsAdapters(Context context, List<Notifications> notificationsList) {
        mContext = context;
        this.notificationsList = notificationsList;
        // endregion
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notifications, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Notifications notifications = notificationsList.get(position);
        holder.txtTitre.setText(notifications.getTitle());
        holder.txtMessage.setText(notifications.getMessage());
        timeAgo = new TimeAgo(mContext);
        holder.txtTimer.setText(timeAgo.timeAgo(DateNotif(notifications.getDateUP())));
        if(!notifications.getImage().equalsIgnoreCase("")){
            Glide.with(mContext)
                    .load(getUrlWithHeaders(notifications.getImage())).into(holder.image);
        }
        holder.deletePanier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePanier(position);
            }
        });
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //UpdateNotification();
                Intent intent = new Intent(mContext, WebActivity.class);
                intent.putExtra("url", notificationsList.get(position).getUrl());
                mContext.startActivity(intent);
            }
        });
    }

    public void UpdateNotification(){
        services.updateAllNotifications();
        ShortcutBadger.removeCount(mContext);
    }

    public void deletePanier(final int position) {
        this.services = new Services(mContext);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                mContext);

        alertDialogBuilder.setIcon(R.drawable.ic_delete_forever_black_24dp);
        alertDialogBuilder.setTitle("Message de confirmation");
        alertDialogBuilder.setMessage("Voulez-vous vraiment supprimer cet notification ?");
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OUI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        int reponses = services.deleteNotification("" + notificationsList.get(position).getId());
                        if (reponses == 1) {
                            notificationsList.remove(position);
                            notifyDataSetChanged();
                        }
                    }
                })
                .setNegativeButton("NON", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }



    public static Date DateNotif(String d){
        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            date = dateFormat.parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    GlideUrl getUrlWithHeaders(String url) {
        String credentials = "VG1GA74ZSPV861V76R79F6IERER129GE";
        credentials = credentials + ":";
        String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        return new GlideUrl(url, new LazyHeaders.Builder()
                .addHeader("Authorization", "Basic " + base64EncodedCredentials)
                .build());
    }

    @Override
    public int getItemCount() {
        return notificationsList.size();
    }

    public Notifications get(Integer id){
        return  notificationsList.get(id);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitre, txtMessage, txtTimer;
        ImageView deletePanier, image;

        public ViewHolder(View view) {
            super(view);
            txtTitre = (TextView) view.findViewById(R.id.txtTitre);
            txtMessage = (TextView) view.findViewById(R.id.txtMessage);
            txtTimer = (TextView) view.findViewById(R.id.txtTimer);
            deletePanier = (ImageView) view.findViewById(R.id.deleteNotification);
            image = (ImageView) view.findViewById(R.id.image);
        }

    }
}
