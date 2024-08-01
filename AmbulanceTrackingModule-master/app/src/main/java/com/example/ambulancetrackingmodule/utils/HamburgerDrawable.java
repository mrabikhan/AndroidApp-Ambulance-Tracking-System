package com.example.ambulancetrackingmodule.utils;

import android.content.Context;
import android.graphics.Canvas;

import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;

import com.example.ambulancetrackingmodule.R;

public class HamburgerDrawable extends DrawerArrowDrawable {
    public HamburgerDrawable(Context context){
        super(context);
        setColor(context.getResources().getColor(R.color.white));
    }

    @Override
    public void draw(Canvas canvas){
        super.draw(canvas);

        setBarLength(60.0f);
        setBarThickness(8.0f);
        setGapSize(10.0f);

    }
}
