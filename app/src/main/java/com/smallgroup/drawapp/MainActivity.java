package com.smallgroup.drawapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.azeesoft.lib.colorpicker.ColorPickerDialog;

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
    private ImageView drawBtn, eraseBtn, newBtn, saveBtn, palitra, undo;

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
        palitra = findViewById(R.id.palitra);

        undo = findViewById(R.id.undo);
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
                //TODO("избавиться от хардокада")
                switch (materialIntroViewId) {
                    case "ID":
                        showIntro("PALITRA", "В палитре есть базовые цвета", paintLayout);
                        break;
                    case "PALITRA":
                        showIntro("ID2", "И еще тут есть много разных вариантов. Они сохраняются на время создания твоего шедевра вместо выбранного ранее цвета", palitra);
                        break;
                    case "ID2":
                        showIntro("ID3", "Стрелочкой можно отменить одно последнее действие", undo);
                        break;
                    case "ID3":
                        showIntro("ID4", "Кисточкой ты будешь рисовать", drawBtn);
                        break;
                    case "ID4":
                        showIntro("ID5", "Это просто ластик", eraseBtn);
                        break;
                    case "ID5":
                        showIntro("ID6", "Тут можно очистить холс", newBtn);
                        break;
                    case "ID6":
                        showIntro("ID7", "А здесь сохранить готовую работу", saveBtn);
                        break;
                }
            }
        };

        showIntro("ID", "Вот смотри. Это твой холст. Здеь ты можешь риовать своим пальчиком", drawView);

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


    public void showIntro(String id, String text, View view) {
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

    public void chooseColor() {
        ColorPickerDialog colorPickerDialog= ColorPickerDialog.createColorPickerDialog(this);
        colorPickerDialog.setNegativeActionText("Закрыть");
        colorPickerDialog.setPositiveActionText("Выбрать");
        colorPickerDialog.setOnColorPickedListener((color, hexVal) -> {
            drawView.setColor(color);
            currPaint.setTag(hexVal);
            currPaint.setBackgroundColor(color);
        });
        colorPickerDialog.show();
    }

    void selectItem(View view) {
        //TODO
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.brush_btn) {
            drawView.setErase(false);
        }
        else if (view.getId()==R.id.undo) {
            drawView.removeLastPath();
        }
        else if (view.getId()==R.id.palitra) {
            chooseColor();
        }
        else if(view.getId()==R.id.erase_btn) {
            drawView.setErase(true);
        }
        else if(view.getId()==R.id.new_btn) {
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("Новое изображение");
            newDialog.setMessage("Очистить экран?");
            newDialog.setPositiveButton("Да", (dialog, which) -> {
                drawView.startNew();
                dialog.dismiss();
            });
            newDialog.setNegativeButton("Закрыть", (dialog, which) -> dialog.cancel());
            newDialog.show();
        }
        else if(view.getId()==R.id.save_btn) {
            saveImg();
        }
    }

    public void saveImg() {
        AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
        saveDialog.setTitle("Сохранение изоражения");
        saveDialog.setMessage("Хотите сохранить изображение в галереи?");
        saveDialog.setPositiveButton("Да", (dialog, which) -> {
            drawView.setDrawingCacheEnabled(true);
            String imgSaved = MediaStore.Images.Media.insertImage(
                    getContentResolver(), drawView.getDrawingCache(),
                    UUID.randomUUID().toString()+".png", "drawing");
            if(imgSaved!=null){
                showMsg("Изображение сохранено в галереи");
            }
            else{
                showMsg("Упс. Изображение не сохранилось");
            }
            drawView.destroyDrawingCache();
        });
        saveDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        saveDialog.show();
    }
}