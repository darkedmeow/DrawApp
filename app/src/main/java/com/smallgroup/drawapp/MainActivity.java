package com.smallgroup.drawapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.UUID;

import co.mobiwise.materialintro.MaterialIntroConfiguration;
import co.mobiwise.materialintro.animation.MaterialIntroListener;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.ShapeType;
import co.mobiwise.materialintro.view.MaterialIntroView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawingView drawView;
    private ImageButton currPaint;
    private ImageView drawBtn, eraseBtn, newBtn, saveBtn;

    private float smallBrush, mediumBrush, largeBrush;

    private MaterialIntroConfiguration config;
    private MaterialIntroListener materialIntroListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawView = (DrawingView)findViewById(R.id.drawing);

        LinearLayout paintLayout = findViewById(R.id.paint_colors);
        currPaint = (ImageButton) paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getDrawable(R.drawable.paint_pressed));

        drawBtn = findViewById(R.id.brush_btn);
        drawBtn.setOnClickListener(this);
        eraseBtn = findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(this);
        newBtn = findViewById(R.id.new_btn);
        newBtn.setOnClickListener(this);
        saveBtn = findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);

        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);

        configurationMaterialIntro();

        materialIntroListener = new MaterialIntroListener() {
            @Override
            public void onUserClicked(String materialIntroViewId) {
                switch (materialIntroViewId) {
                    case "ID":
                        startIntro("ID2", "Палитра", paintLayout);
                        break;
                    case "ID2":
                        startIntro("ID3", "Кисть", drawBtn);
                        break;
                    case "ID3":
                        startIntro("ID4", "Ластик", eraseBtn);
                        break;
                    case "ID4":
                        startIntro("ID5", "Очистить экран", newBtn);
                        break;
                    case "ID5":
                        startIntro("ID6", "Сохранить рисунок", saveBtn);
                        break;
                }
            }
        };

        startIntro("ID", "Рабочая область", drawView);

    }

    public void showMsg(String txt) {
        Toast.makeText(this, txt, Toast.LENGTH_LONG).show();
    }

    private void configurationMaterialIntro() {
        config = new MaterialIntroConfiguration();
        config.setDotViewEnabled(true);
        config.setFocusGravity(FocusGravity.CENTER);
        config.setFocusType(Focus.MINIMUM);
        config.setDelayMillis(500);
        config.setFadeAnimationEnabled(true);
    }


    public void startIntro(String id, String text, View view) {
        new MaterialIntroView.Builder(this)
                .setConfiguration(config)
                .enableIcon(false)
                .performClick(false)
                .setInfoText(text)
                .setShape(ShapeType.CIRCLE)
                .setTarget(view)
                .setUsageId(id)
                .setListener(materialIntroListener)
                .show();
    }


    public void paintClicked(View view) {
        if (view != currPaint){
            ImageButton imgView = (ImageButton)view;
            String color = view.getTag().toString();
            drawView.setColor(color);

            imgView.setImageDrawable(getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getDrawable(R.drawable.paint));
            currPaint=(ImageButton)view;
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.brush_btn) {
            drawView.setErase(false);
        }
        else if(view.getId()==R.id.erase_btn) {
            drawView.setErase(true);
        }
        else if(view.getId()==R.id.new_btn) {
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("Новое изображение");
            newDialog.setMessage("Оистить экран?");
            newDialog.setPositiveButton("Да", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    drawView.startNew();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("Закрыть", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            newDialog.show();
        }
        else if(view.getId()==R.id.save_btn) {
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Сохранение изоражения");
            saveDialog.setMessage("Хотите сохранить изображение в галерею?");
            saveDialog.setPositiveButton("Да", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    drawView.setDrawingCacheEnabled(true);
                    String imgSaved = MediaStore.Images.Media.insertImage(
                            getContentResolver(), drawView.getDrawingCache(),
                            UUID.randomUUID().toString()+".png", "drawing");
                    if(imgSaved!=null){
                        Toast savedToast = Toast.makeText(getApplicationContext(),
                                "Изображение сохранено в галерею", Toast.LENGTH_SHORT);
                        savedToast.show();
                    }
                    else{
                        Toast unsavedToast = Toast.makeText(getApplicationContext(),
                                "Упс. Изоюражение не сохранилось", Toast.LENGTH_SHORT);
                        unsavedToast.show();
                    }
                    drawView.destroyDrawingCache();
                }
            });
            saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            saveDialog.show();
        }
    }
}