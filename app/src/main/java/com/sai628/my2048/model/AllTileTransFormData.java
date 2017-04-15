package com.sai628.my2048.model;

/**
 * @author
 * @ClassName: AllTileInfoData
 * @Description: 二维数据变换过程需要的信息结构类
 * @date Mar 18, 2017 4:57:55 PM
 */
public class AllTileTransFormData
{
    public int[][] newData; // 变换后得到的数据
    public TranslateInfo[][] translateInfos; // 变换过程需要的位置偏移信息,每一个元素对应一个数据元素变换过程的偏移量
    public int addScore; // 变换后新增的分数


    public AllTileTransFormData()
    {
    }


    public AllTileTransFormData(int[][] newData, TranslateInfo[][] translateInfos, int addScore)
    {
        this.newData = newData;
        this.translateInfos = translateInfos;
        this.addScore = addScore;
    }
}
