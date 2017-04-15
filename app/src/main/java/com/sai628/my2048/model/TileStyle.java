package com.sai628.my2048.model;

import android.graphics.drawable.Drawable;


/**
 * @author
 * @ClassName: TileStyle
 * @Description: 方格样式信息结构类
 * @date Mar 18, 2017 4:57:34 PM
 */
public class TileStyle
{
    public Drawable bgDrawable; // 背景资源
    public int textColor; // 文字颜色
    public float textSize; // 文字大小


    public TileStyle(Drawable bgDrawable, int textColor, float textSize)
    {
        this.bgDrawable = bgDrawable;
        this.textColor = textColor;
        this.textSize = textSize;
    }
}
