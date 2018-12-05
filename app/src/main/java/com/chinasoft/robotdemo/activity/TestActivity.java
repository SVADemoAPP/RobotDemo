package com.chinasoft.robotdemo.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.chinasoft.robotdemo.R;
import com.chinasoft.robotdemo.db.dbflow.DirectionData;
import com.chinasoft.robotdemo.util.DBUtils;
import com.google.gson.Gson;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import net.yoojia.imagemap.ImageMap1;
import net.yoojia.imagemap.TouchImageView1;

import java.util.List;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_coordinate_layout);
        ImageMap1 map1 = findViewById(R.id.coordinate_map);
        final TextView textView = findViewById(R.id.coordinate_data);
        Bitmap bitmap = BitmapFactory.decodeFile("/storage/emulated/0/robotdemo/maps/chinasoft_1f.png");
        map1.setMapBitmap(bitmap);
        map1.setOnCenerPointListener(new TouchImageView1.OnCenterPointListener() {
            @Override
            public void onCenter(PointF pointF) {
                textView.setText("" + pointF.x + "," + pointF.y);
            }
        });
        test();
    }

    public void test() {
        DirectionData directionData = new DirectionData("U9", "X1", "1222");
        directionData.setId(1);
        DBUtils.delete(directionData);
        List<DirectionData> query = DBUtils.query("U9", "X1");

    }
}
