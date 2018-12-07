package com.chinasoft.robotdemo.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.chinasoft.robotdemo.R;
import com.chinasoft.robotdemo.db.dbflow.DirectionData;
import com.chinasoft.robotdemo.framwork.sharef.SharedPrefHelper;
import com.chinasoft.robotdemo.util.Constant;
import com.chinasoft.robotdemo.util.DBUtils;
import com.google.gson.Gson;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import net.yoojia.imagemap.ImageMap1;
import net.yoojia.imagemap.TouchImageView1;
import net.yoojia.imagemap.core.CircleShape;
import net.yoojia.imagemap.core.Shape;

import java.util.List;

public class TestActivity extends AppCompatActivity {

    private ImageMap1 map1;
    private int mapHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_coordinate_layout);
        map1 = findViewById(R.id.coordinate_map);
        Constant.mapScale=100;
        final TextView textView = findViewById(R.id.coordinate_data);
        Bitmap bitmap = BitmapFactory.decodeFile("/storage/emulated/0/robotdemo/maps/chinasoft_1f.png");
        mapHeight =bitmap.getHeight();
        map1.setMapBitmap(bitmap);
        map1.setOnCenerPointListener(new TouchImageView1.OnCenterPointListener() {
            @Override
            public void onCenter(PointF pointF) {
                textView.setText("" + pointF.x + "," + pointF.y);
            }
        });

        View viewById = findViewById(R.id.pop_coordinate_confirm);
        viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map1.getCenterByImagePoint();
                float defaultX=100f; //获取设置页面设置坐标X
                float defaultY=100f; //获取设置页面设置坐标Y
                float[] floats = mapToReal(defaultX, defaultY);
                map1.moveToCenter(floats[0],floats[1]); //移动默认设置点到地图中心
            }
        });
    }

    public void test() {
        Shape shape=new CircleShape("1", Color.RED);
        shape.setValues(100f,100f);
        map1.addShape(shape,true);
//        DirectionData directionData = new DirectionData("U9", "X1", "1222");
//        directionData.setId(1);
//        DBUtils.delete(directionData);
//        List<DirectionData> query = DBUtils.query("U9", "X1");

    }
    private float[] mapToReal(float mx, float my) {
        return new float[]{mx / Constant.mapScale, (mapHeight - my) / Constant.mapScale};
    }

    private float[] realToMap(float rx, float ry) {
        return new float[]{rx * Constant.mapScale, mapHeight - ry * Constant.mapScale};
    }

}
