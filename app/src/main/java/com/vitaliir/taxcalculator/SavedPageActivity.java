package com.vitaliir.taxcalculator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.vitaliir.taxcalculator.model.MainPage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import lombok.SneakyThrows;

public class SavedPageActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);
        ActionBar actionBar = getSupportActionBar();

        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        listView = findViewById(R.id.listSavedData);
        showAllSavedItems();
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showAllSavedItems() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);

        Map<String, ?> all = sharedPreferences.getAll();
        List<String> keys = new ArrayList<>();
        all.forEach((key, value) -> keys.add(key));
        Collections.sort(keys);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, keys);
        listView.setAdapter(arrayAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        String item = adapterView.getItemAtPosition(position).toString();
        MainPage mainPage = loadData(item);

        Intent intent = new Intent(this, MainPageActivity.class);
        intent.putExtra("txtIncome", mainPage.getIncome());
        intent.putExtra("txtEsv", mainPage.getEsv());
        intent.putExtra("txtEp", mainPage.getEp());
        intent.putExtra("txtTransBankCommission", mainPage.getBankCommission());
        intent.putExtra("txtOtherCommissions", mainPage.getOtherCommission());
        intent.putExtra("txtAllTaxes", mainPage.getAllTaxes());
        intent.putExtra("txtTransBankCommissionResult", mainPage.getTransBankCommission());
        intent.putExtra("txtAllBankCommissions", mainPage.getAllBankCommission());
        intent.putExtra("txtCleanIncome", mainPage.getCleanIncome());

        startActivity(intent);
    }

    @SneakyThrows
    private MainPage loadData(String item) {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        String json = sharedPreferences.getString(item, null);
        return new Gson().fromJson(json, MainPage.class);
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        String item = adapterView.getItemAtPosition(position).toString();
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Delete?");
        alert.setMessage("Are you sure you want to delete " + item);
        alert.setNegativeButton("Cancel", null);
        alert.setPositiveButton("Ok", (dialog, which) -> {
            arrayAdapter.remove(item);
            arrayAdapter.notifyDataSetChanged();
            deleteFromSharedPreferences(item);
        });
        alert.show();
        return true;
    }

    private void deleteFromSharedPreferences(String item) {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        sharedPreferences.edit().remove(item).apply();
    }
}