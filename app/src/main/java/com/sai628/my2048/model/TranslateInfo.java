package com.sai628.my2048.model;


public class TranslateInfo
{
    public int deltaX; // X方向的相对偏移量,当为上下移动时,其值为0
    public int deltaY; // Y方向的相对偏移量,当为左右移动时,其值为0


    public TranslateInfo(int deltaX, int deltaY)
    {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }
}
