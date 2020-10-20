package com.example.savebundlearraylist;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String heading = "heading";
    private static final String subtitle = "subtitle";
    private SharedPreferences saveSting;
    private ArrayList<Integer> deletePosition = new ArrayList<>();
    private final List<Map<String, String>> data = new ArrayList<>();
    private SimpleAdapter sAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sAdapter = new SimpleAdapter(this, data, R.layout.twin_item,
                new String[]{heading, subtitle}, new int[]{R.id.heading, R.id.subtitle});
        saveSting = getSharedPreferences("LargeText", MODE_PRIVATE);
        SharedPreferences.Editor editor = saveSting.edit();
        if (!saveSting.contains(getString(R.string.large_text))) {
            editor.putString("LargeText", getString(R.string.large_text));
            editor.apply();
        }
        String largeText = saveSting.getString("LargeText", "");
        final ListView list = findViewById(R.id.list);
        final String[] values = largeText.split("\n\n");

        DataAdd(values, data);

        list.setAdapter(sAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                data.remove(position);
                sAdapter.notifyDataSetChanged();
                deletePosition.add(position);
            }
        });

        final androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeLayout = findViewById(R.id.swiperefresh);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String largeText2 = saveSting.getString("LargeText", "");
                final String[] values2 = largeText2.split("\n\n");
                final List<Map<String, String>> data2 = new ArrayList<>();
                DataAdd(values2, data2);
                final SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, data2, R.layout.twin_item,
                        new String[]{heading, subtitle}, new int[]{R.id.heading, R.id.subtitle});
                list.setAdapter(adapter);
                swipeLayout.setRefreshing(false);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        data2.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
                deletePosition.clear();
            }
        });

    }

    public static void DataAdd(String[] values, List<Map<String, String>> data) {
        for (String s : values) {
            Map<String, String> str = new HashMap<>();
            str.put(heading, s);
            str.put(subtitle, Integer.toString(s.length()));
            data.add(str);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList("Position", deletePosition);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        deletePosition = savedInstanceState.getIntegerArrayList("Position");
        for (int i : deletePosition) {
            data.remove(i);
        }
        sAdapter.notifyDataSetChanged();

    }
}
