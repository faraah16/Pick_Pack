package com.example.pick_pack.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pick_pack.Adapter.CubeAdapter;
import com.example.pick_pack.CartActivity;
import com.example.pick_pack.Model.CubeItem;
import com.example.pick_pack.R;

import java.util.ArrayList;
import java.util.List;

public class ClientProductsListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_products);

        RecyclerView rv = findViewById(R.id.rvProducts);



        rv.setLayoutManager(new LinearLayoutManager(this));

        List<CubeItem> cubes = new ArrayList<>();

        cubes.add(new CubeItem(
                "Red Cube",
                "Strong cube for storage",
                R.drawable.red_cube,
                20,
                false
        ));

        cubes.add(new CubeItem(
                "Green Cube",
                "Eco friendly cube",
                R.drawable.green_cube,
                25,
                false
        ));

        cubes.add(new CubeItem(
                "Blue Cube",
                "Heavy duty cube",
                R.drawable.bleu_cube,
                30,
                false
        ));

        rv.setAdapter(new CubeAdapter(cubes));

        ImageButton btnCart = findViewById(R.id.btnCart);
        btnCart.setOnClickListener(v ->
                startActivity(new Intent(this, CartActivity.class))
        );
    }
}
