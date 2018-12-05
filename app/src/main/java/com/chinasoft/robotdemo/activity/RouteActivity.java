package com.chinasoft.robotdemo.activity;

import android.content.Context;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinasoft.robotdemo.R;
import com.chinasoft.robotdemo.framwork.activity.BaseActivity;
import com.chinasoft.robotdemo.framwork.utils.NumUtil;
import com.chinasoft.robotdemo.util.Constant;

import net.yoojia.imagemap.ImageMap1;
import net.yoojia.imagemap.TouchImageView1;
import net.yoojia.imagemap.core.CollectPointShape;
import net.yoojia.imagemap.core.CustomShape;
import net.yoojia.imagemap.core.LineShape;
import net.yoojia.imagemap.core.MoniPointShape;
import net.yoojia.imagemap.core.PushMessageShape;
import net.yoojia.imagemap.core.ShapeExtension;
import net.yoojia.imagemap.core.SpecialShape;

import java.util.ArrayList;
import java.util.List;

public class RouteActivity extends BaseActivity  {
    private ImageMap1 map;
    private int mapHeight;
    private ImageView mIvSave;
    private TextView mTvClear,mTvPrev,mRouteBack;
    private List<PointF> mPointList=new ArrayList<>();
    private LineShape routeShape;
    private Path routePath;
    private int pointSize;
    private Context mContext;


    @Override
    public void setContentLayout() {
        setContentView(R.layout.activity_route);
    }


    @Override
    public void dealLogicBeforeInitView() {
        mContext=RouteActivity.this;
        routePath=new Path();
        routeShape = new LineShape("route", R.color.green, 2, "#FF4081");
    }

    @Override
    public void initView() {
        map = findViewById(R.id.imagemap);
        mIvSave=findViewById(R.id.iv_save);
        mTvClear=findViewById(R.id.tv_clear);
        mTvPrev=findViewById(R.id.tv_prev);
        mRouteBack=findViewById(R.id.tv_route_back);
        mIvSave.setOnClickListener(this);
        mTvClear.setOnClickListener(this);
        mTvPrev.setOnClickListener(this);
        mRouteBack.setOnClickListener(this);
    }


    @Override
    public void dealLogicAfterInitView() {
        map.setMapBitmap(Constant.mapBitmap);
        mapHeight = Constant.mapBitmap.getHeight();
        map.setOnShapeClickListener(new ShapeExtension.OnShapeActionListener() {
            @Override
            public void onSpecialShapeClick(SpecialShape shape, float xOnImage, float yOnImage) {
            }

            @Override
            public void onPushMessageShapeClick(PushMessageShape shape, float xOnImage, float yOnImage) {
            }

            @Override
            public void onCollectShapeClick(CollectPointShape shape, float xOnImage, float yOnImage) {
            }

            @Override
            public void onMoniShapeClick(MoniPointShape shape, float xOnImage, float yOnImage) {
            }

            @Override
            public void outShapeClick(float xOnImage, float yOnImage) {
                mPointList.add(new PointF(xOnImage,yOnImage));
                pointSize=mPointList.size();
                if(pointSize==1){
                    routePath.moveTo(xOnImage,yOnImage);
                }else {
                    routePath.lineTo(xOnImage,yOnImage);
                    routeShape.setPath(routePath);
                    map.addShape(routeShape,false);
                }
                CustomShape shape = new CustomShape("point" + pointSize, R.color.blue, mContext, "dwf", R.mipmap.destination_point);
                shape.setValues(xOnImage, yOnImage);
                map.addShape(shape, false);

            }
        });
    }

    @Override
    public void onClickEvent(View view) {
        switch (view.getId()){
            case R.id.iv_save:
                if(mPointList.size()<2){
                    showToast("至少选择两个点");
                    return;
                }
                float[] tempF;
                for(PointF p:mPointList){
                    tempF=mapToReal(p.x,p.y);
                    p.x=tempF[0];
                    p.y=tempF[1];
                }
                break;
            case R.id.tv_clear:
                for(int i=0,len=mPointList.size();i<len;i++){
                    map.removeShape("point"+(i+1));
                }
                map.removeShape("route");
                routePath.reset();
                mPointList.clear();
                break;
            case R.id.tv_prev:
                pointSize=mPointList.size();
                if(pointSize>0){
                    map.removeShape("point"+pointSize);
                    routePath.reset();
                    routePath.moveTo(mPointList.get(0).x,mPointList.get(0).y);
                    mPointList.remove(pointSize-1);
                    if(pointSize>2){
                        for(int i=1;i<pointSize-1;i++){
                            routePath.lineTo(mPointList.get(i).x,mPointList.get(i).y);
                        }
                    }
                    routeShape.setPath(routePath);
                    map.addShape(routeShape,false);
                }else {
                    showToast("已经撤空了");
                }
                break;
            case R.id.tv_route_back:
                finish();
                break;
            default:
                break;
        }

    }


    private float[] mapToReal(float mx, float my) {
        return new float[]{NumUtil.saveTwoFloat(mx / Constant.mapScale), NumUtil.saveTwoFloat((mapHeight - my) / Constant.mapScale)};
    }

}
