package com.testxml.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;


import com.testxml.R;
import com.testxml.adapters.NotificationsAdapters;
import com.testxml.models.Notifications;
import com.testxml.services.Services;

import java.util.ArrayList;
import java.util.List;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by Raoelson on 06/11/2017.
 */

public class NotificationActivity extends AppCompatActivity {

    List<Notifications> notificationsList;
    Services services;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notifications);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Notification");
        recyclerView = (RecyclerView) findViewById(R.id.main_recycler1);
        notificationsList = new ArrayList<>();
        services = new Services(this);
        chargementNotification();
    }


    public void chargementNotification() {
        notificationsList = services.getAllNotification();
        NotificationsAdapters adapters = new NotificationsAdapters(NotificationActivity.this, notificationsList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapters);
        adapters.notifyDataSetChanged();
        UpdateNotification();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                onBackPressed();
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void UpdateNotification() {
        services.updateAllNotifications();
        ShortcutBadger.removeCount(NotificationActivity.this);
    }
}
