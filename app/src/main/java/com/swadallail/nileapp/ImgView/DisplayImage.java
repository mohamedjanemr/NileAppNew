package com.swadallail.nileapp.ImgView;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.swadallail.nileapp.R;

public class DisplayImage extends AppCompatActivity {
    ImageView view ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        view = findViewById(R.id.dis_img);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent getImage = getIntent();
        String url = getImage.getStringExtra("img");

        if(!url.equals(null) && !url.isEmpty()){
            Picasso.get().load(url).into(view);
        }else{
            Toast.makeText(DisplayImage.this, "هناك خطأ بالصورة", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}