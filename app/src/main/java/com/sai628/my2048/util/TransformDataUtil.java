package com.sai628.my2048.util;

import com.sai628.my2048.model.AllTileTransFormData;
import com.sai628.my2048.model.EachRowTileTransFormData;
import com.sai628.my2048.model.TranslateInfo;


public class TransformDataUtil
{
    public static AllTileTransFormData moveToLeft(int[][] data)
    {
        int[][] newData = copy(data);
        TranslateInfo[][] translateInfos = new TranslateInfo[data.length][];
        int addScore = 0;

        for (int i = 0, rowLength = data.length; i < rowLength; i++)
        {
            EachRowTileTransFormData eachRowTileInfoData = toLeft(data[i]);
            newData[i] = eachRowTileInfoData.newData;
            translateInfos[i] = eachRowTileInfoData.translateInfos;
            addScore += eachRowTileInfoData.addScore;
        }

        return new AllTileTransFormData(newData, translateInfos, addScore);
    }


    public static AllTileTransFormData moveToRight(int[][] data)
    {
        int[][] newData = copy(data);
        TranslateInfo[][] translateInfos = new TranslateInfo[data.length][];
        int addScore = 0;

        for (int i = 0, rowLength = data.length; i < rowLength; i++)
        {
            EachRowTileTransFormData eachRowTileInfoData = toRigth(data[i]);
            newData[i] = eachRowTileInfoData.newData;
            translateInfos[i] = eachRowTileInfoData.translateInfos;
            addScore += eachRowTileInfoData.addScore;
        }

        return new AllTileTransFormData(newData, translateInfos, addScore);
    }


    public static AllTileTransFormData moveToUp(int[][] data)
    {
        int rowLength = data.length;
        int columnLength = rowLength;
        int[][] result = new int[rowLength][columnLength];
        int[] tempRow = new int[rowLength];
        TranslateInfo[][] translateInfos = new TranslateInfo[rowLength][columnLength];
        int addScore = 0;

        for (int i = 0; i < columnLength; i++)
        {
            for (int j = 0; j < rowLength; j++)
            {
                tempRow[j] = data[j][i];
            }

            EachRowTileTransFormData eachRowTileInfoData = toUp(tempRow);
            addScore += eachRowTileInfoData.addScore;

            for (int k = 0; k < rowLength; k++)
            {
                result[k][i] = eachRowTileInfoData.newData[k];
                translateInfos[k][i] = eachRowTileInfoData.translateInfos[k];
            }
        }

        return new AllTileTransFormData(result, translateInfos, addScore);
    }


    public static AllTileTransFormData moveToDown(int[][] data)
    {
        int rowLength = data.length;
        int columnLength = rowLength;
        int[][] result = new int[rowLength][columnLength];
        int[] tempRow = new int[rowLength];
        TranslateInfo[][] translateInfos = new TranslateInfo[rowLength][columnLength];
        int addScore = 0;

        for (int i = 0; i < columnLength; i++)
        {
            for (int j = 0; j < rowLength; j++)
            {
                tempRow[j] = data[j][i];
            }

            EachRowTileTransFormData eachRowTileInfoData = toDown(tempRow);
            addScore += eachRowTileInfoData.addScore;

            for (int k = 0; k < rowLength; k++)
            {
                result[k][i] = eachRowTileInfoData.newData[k];
                translateInfos[k][i] = eachRowTileInfoData.translateInfos[k];
            }
        }

        return new AllTileTransFormData(result, translateInfos, addScore);
    }


    private static EachRowTileTransFormData toLeft(int[] data)
    {
        EachRowTileTransFormData eachRowTileInfoDataA = removeLeftEmpty(data, 0);
        TranslateInfo[] translateInfosA = eachRowTileInfoDataA.translateInfos;

        EachRowTileTransFormData eachRowTileInfoDataB = mergeToLeft(eachRowTileInfoDataA.newData);
        TranslateInfo[] translateInfosB = eachRowTileInfoDataB.translateInfos;

        EachRowTileTransFormData eachRowTileInfoDataC = removeLeftEmpty(eachRowTileInfoDataB.newData,
                eachRowTileInfoDataB.addScore);
        TranslateInfo[] translateInfosC = eachRowTileInfoDataC.translateInfos;

        TranslateInfo[] temp = mergeLeftTranslateInfo(translateInfosA, translateInfosB);
        TranslateInfo[] resultTranslateInfos = mergeLeftTranslateInfo(temp, translateInfosC);

        return new EachRowTileTransFormData(eachRowTileInfoDataC.newData, resultTranslateInfos, eachRowTileInfoDataC.addScore);
    }


    private static EachRowTileTransFormData toUp(int[] data)
    {
        EachRowTileTransFormData eachRowTileInfoDataA = removeUpEmpty(data, 0);
        TranslateInfo[] translateInfosA = eachRowTileInfoDataA.translateInfos;

        EachRowTileTransFormData eachRowTileInfoDataB = mergeToUp(eachRowTileInfoDataA.newData);
        TranslateInfo[] translateInfosB = eachRowTileInfoDataB.translateInfos;

        EachRowTileTransFormData eachRowTileInfoDataC = removeUpEmpty(eachRowTileInfoDataB.newData, eachRowTileInfoDataB.addScore);
        TranslateInfo[] translateInfosC = eachRowTileInfoDataC.translateInfos;

        TranslateInfo[] temp = mergeUpTranslateInfo(translateInfosA, translateInfosB);
        TranslateInfo[] resultTranslateInfos = mergeUpTranslateInfo(temp, translateInfosC);

        return new EachRowTileTransFormData(eachRowTileInfoDataC.newData, resultTranslateInfos, eachRowTileInfoDataC.addScore);
    }


    private static EachRowTileTransFormData toRigth(int[] data)
    {
        EachRowTileTransFormData eachRowTileInfoDataA = removeRightEmpty(data, 0);
        TranslateInfo[] translateInfosA = eachRowTileInfoDataA.translateInfos;

        EachRowTileTransFormData eachRowTileInfoDataB = mergeToRight(eachRowTileInfoDataA.newData);
        TranslateInfo[] translateInfosB = eachRowTileInfoDataB.translateInfos;

        EachRowTileTransFormData eachRowTileInfoDataC = removeRightEmpty(eachRowTileInfoDataB.newData,
                eachRowTileInfoDataB.addScore);
        TranslateInfo[] translateInfosC = eachRowTileInfoDataC.translateInfos;

        TranslateInfo[] temp = mergeRightTranslateInfo(translateInfosA, translateInfosB);
        TranslateInfo[] resultTranslateInfos = mergeRightTranslateInfo(temp, translateInfosC);

        return new EachRowTileTransFormData(eachRowTileInfoDataC.newData, resultTranslateInfos, eachRowTileInfoDataC.addScore);
    }


    private static EachRowTileTransFormData toDown(int[] data)
    {
        EachRowTileTransFormData eachRowTileInfoDataA = removeDownEmpty(data, 0);
        TranslateInfo[] translateInfosA = eachRowTileInfoDataA.translateInfos;

        EachRowTileTransFormData eachRowTileInfoDataB = mergeToDown(eachRowTileInfoDataA.newData);
        TranslateInfo[] translateInfosB = eachRowTileInfoDataB.translateInfos;

        EachRowTileTransFormData eachRowTileInfoDataC = removeDownEmpty(eachRowTileInfoDataB.newData,
                eachRowTileInfoDataB.addScore);
        TranslateInfo[] translateInfosC = eachRowTileInfoDataC.translateInfos;

        TranslateInfo[] temp = mergeDownTranslateInfo(translateInfosA, translateInfosB);
        TranslateInfo[] resultTranslateInfos = mergeDownTranslateInfo(temp, translateInfosC);

        return new EachRowTileTransFormData(eachRowTileInfoDataC.newData, resultTranslateInfos, eachRowTileInfoDataC.addScore);
    }


    private static EachRowTileTransFormData removeLeftEmpty(int[] data, int addScore)
    {
        TranslateInfo[] newTranslateInfo = new TranslateInfo[data.length];

        int length = data.length;
        int[] result = new int[length];
        int j = 0;

        for (int i = 0; i < length; i++)
        {
            if (data[i] != 0)
            {
                result[j] = data[i];
                newTranslateInfo[i] = new TranslateInfo(j - i, 0);

                j++;
            }
            else
            {
                newTranslateInfo[i] = new TranslateInfo(0, 0);
            }
        }

        for (; j < length; j++)
        {
            result[j] = 0;
        }

        return new EachRowTileTransFormData(result, newTranslateInfo, addScore);
    }


    private static EachRowTileTransFormData removeUpEmpty(int[] data, int addScore)
    {
        TranslateInfo[] newTranslateInfo = new TranslateInfo[data.length];

        int length = data.length;
        int[] result = new int[length];
        int j = 0;

        for (int i = 0; i < length; i++)
        {
            if (data[i] != 0)
            {
                result[j] = data[i];
                newTranslateInfo[i] = new TranslateInfo(0, j - i);

                j++;
            }
            else
            {
                newTranslateInfo[i] = new TranslateInfo(0, 0);
            }
        }

        for (; j < length; j++)
        {
            result[j] = 0;
        }

        return new EachRowTileTransFormData(result, newTranslateInfo, addScore);
    }


    private static EachRowTileTransFormData removeRightEmpty(int[] data, int addScore)
    {
        TranslateInfo[] newTranslateInfo = new TranslateInfo[data.length];

        int length = data.length;
        int[] result = new int[length];
        int j = length - 1;

        for (int i = length - 1; i >= 0; i--)
        {
            if (data[i] != 0)
            {
                result[j] = data[i];
                newTranslateInfo[i] = new TranslateInfo(j - i, 0);

                j--;
            }
            else
            {
                newTranslateInfo[i] = new TranslateInfo(0, 0);
            }
        }

        for (; j >= 0; j--)
        {
            result[j] = 0;
        }

        return new EachRowTileTransFormData(result, newTranslateInfo, addScore);
    }


    private static EachRowTileTransFormData removeDownEmpty(int[] data, int addScore)
    {
        TranslateInfo[] newTranslateInfo = new TranslateInfo[data.length];

        int length = data.length;
        int[] result = new int[length];
        int j = length - 1;

        for (int i = length - 1; i >= 0; i--)
        {
            if (data[i] != 0)
            {
                result[j] = data[i];
                newTranslateInfo[i] = new TranslateInfo(0, j - i);

                j--;
            }
            else
            {
                newTranslateInfo[i] = new TranslateInfo(0, 0);
            }
        }

        for (; j >= 0; j--)
        {
            result[j] = 0;
        }

        return new EachRowTileTransFormData(result, newTranslateInfo, addScore);
    }


    private static EachRowTileTransFormData mergeToLeft(int[] data)
    {
        TranslateInfo[] newTranslateInfo = new TranslateInfo[data.length];
        for (int i = 0, length = newTranslateInfo.length; i < length; i++)
        {
            newTranslateInfo[i] = new TranslateInfo(0, 0);
        }

        int length = data.length;
        int[] result = new int[length];
        int addScore = 0;

        for (int i = 0; i < length; i++)
        {
            result[i] = data[i];
        }

        for (int i = 0, j = 1; j < length; i++, j++)
        {
            if (result[i] == result[j] && result[i] != 0 && result[j] != 0)
            {
                result[i] = 2 * result[i];
                result[j] = 0;

                newTranslateInfo[j].deltaX = i - j;
                newTranslateInfo[j].deltaY = 0;

                addScore += result[i];
            }
        }

        return new EachRowTileTransFormData(result, newTranslateInfo, addScore);
    }


    private static EachRowTileTransFormData mergeToUp(int[] data)
    {
        TranslateInfo[] newTranslateInfo = new TranslateInfo[data.length];
        for (int i = 0, length = newTranslateInfo.length; i < length; i++)
        {
            newTranslateInfo[i] = new TranslateInfo(0, 0);
        }

        int length = data.length;
        int[] result = new int[length];
        int addScore = 0;

        for (int i = 0; i < length; i++)
        {
            result[i] = data[i];
        }

        for (int i = 0, j = 1; j < length; i++, j++)
        {
            if (result[i] == result[j] && result[i] != 0 && result[j] != 0)
            {
                result[i] = 2 * result[i];
                result[j] = 0;

                newTranslateInfo[j].deltaX = 0;
                newTranslateInfo[j].deltaY = i - j;

                addScore += result[i];
            }
        }

        return new EachRowTileTransFormData(result, newTranslateInfo, addScore);
    }


    private static EachRowTileTransFormData mergeToRight(int[] data)
    {
        TranslateInfo[] newTranslateInfo = new TranslateInfo[data.length];
        for (int i = 0, length = newTranslateInfo.length; i < length; i++)
        {
            newTranslateInfo[i] = new TranslateInfo(0, 0);
        }

        int length = data.length;
        int[] result = new int[length];
        int addScore = 0;

        for (int i = 0; i < length; i++)
        {
            result[i] = data[i];
        }

        for (int i = length - 1, j = length - 2; j >= 0; i--, j--)
        {
            if (result[i] == result[j] && result[i] != 0 && result[j] != 0)
            {
                result[i] = 2 * result[i];
                result[j] = 0;

                newTranslateInfo[j].deltaX = i - j;
                newTranslateInfo[j].deltaY = 0;

                addScore += result[i];
            }
        }

        return new EachRowTileTransFormData(result, newTranslateInfo, addScore);
    }


    private static EachRowTileTransFormData mergeToDown(int[] data)
    {
        TranslateInfo[] newTranslateInfo = new TranslateInfo[data.length];
        for (int i = 0, length = newTranslateInfo.length; i < length; i++)
        {
            newTranslateInfo[i] = new TranslateInfo(0, 0);
        }

        int length = data.length;
        int[] result = new int[length];
        int addScore = 0;

        for (int i = 0; i < length; i++)
        {
            result[i] = data[i];
        }

        for (int i = length - 1, j = length - 2; j >= 0; i--, j--)
        {
            if (result[i] == result[j] && result[i] != 0 && result[j] != 0)
            {
                result[i] = 2 * result[i];
                result[j] = 0;

                newTranslateInfo[j].deltaX = 0;
                newTranslateInfo[j].deltaY = i - j;

                addScore += result[i];
            }
        }

        return new EachRowTileTransFormData(result, newTranslateInfo, addScore);
    }


    private static TranslateInfo[] mergeLeftTranslateInfo(TranslateInfo[] translateInfosA, TranslateInfo[] translateInfosB)
    {
        TranslateInfo[] newTranslateInfo = new TranslateInfo[translateInfosA.length];

        for (int i = translateInfosA.length - 1; i >= 0; i--)
        {
            int deltaX = translateInfosA[i].deltaX;

            if (i + deltaX >= 0)
            {
                newTranslateInfo[i] = new TranslateInfo(deltaX + translateInfosB[i + deltaX].deltaX, 0);
            }
            else
            {
                newTranslateInfo[i] = new TranslateInfo(deltaX, 0);
            }
        }

        return newTranslateInfo;
    }


    private static TranslateInfo[] mergeUpTranslateInfo(TranslateInfo[] translateInfosA, TranslateInfo[] translateInfosB)
    {
        TranslateInfo[] newTranslateInfo = new TranslateInfo[translateInfosA.length];

        for (int i = translateInfosA.length - 1; i >= 0; i--)
        {
            int deltaY = translateInfosA[i].deltaY;

            if (i + deltaY >= 0)
            {
                newTranslateInfo[i] = new TranslateInfo(0, deltaY + translateInfosB[i + deltaY].deltaY);
            }
            else
            {
                newTranslateInfo[i] = new TranslateInfo(0, deltaY);
            }
        }

        return newTranslateInfo;
    }


    private static TranslateInfo[] mergeRightTranslateInfo(TranslateInfo[] translateInfosA, TranslateInfo[] translateInfosB)
    {
        TranslateInfo[] newTranslateInfo = new TranslateInfo[translateInfosA.length];

        for (int i = 0; i < translateInfosA.length; i++)
        {
            int deltaX = translateInfosA[i].deltaX;

            if (i + deltaX < translateInfosA.length)
            {
                newTranslateInfo[i] = new TranslateInfo(deltaX + translateInfosB[i + deltaX].deltaX, 0);
            }
            else
            {
                newTranslateInfo[i] = new TranslateInfo(deltaX, 0);
            }
        }

        return newTranslateInfo;
    }


    private static TranslateInfo[] mergeDownTranslateInfo(TranslateInfo[] translateInfosA, TranslateInfo[] translateInfosB)
    {
        TranslateInfo[] newTranslateInfo = new TranslateInfo[translateInfosA.length];

        for (int i = 0; i < translateInfosA.length; i++)
        {
            int deltaY = translateInfosA[i].deltaY;

            if (i + deltaY < translateInfosA.length)
            {
                newTranslateInfo[i] = new TranslateInfo(0, deltaY + translateInfosB[i + deltaY].deltaY);
            }
            else
            {
                newTranslateInfo[i] = new TranslateInfo(0, deltaY);
            }
        }

        return newTranslateInfo;
    }


    public static int[][] copy(int[][] data)
    {
        int[][] newData = new int[data.length][];
        for (int i = 0, rowLength = data.length; i < rowLength; i++)
        {
            newData[i] = new int[data[i].length];
            System.arraycopy(data[i], 0, newData[i], 0, data[i].length);
        }

        return newData;
    }


    private static TranslateInfo[][] copy(TranslateInfo[][] translateInfos)
    {
        if (translateInfos == null)
        {
            return null;
        }

        TranslateInfo[][] newTranslateInfos = new TranslateInfo[translateInfos.length][];
        for (int i = 0, rowLength = translateInfos.length; i < rowLength; i++)
        {
            newTranslateInfos[i] = new TranslateInfo[translateInfos[i].length];
            for (int j = 0, columnLength = translateInfos[i].length; j < columnLength; j++)
            {
                newTranslateInfos[i][j] = new TranslateInfo(translateInfos[i][j].deltaX, translateInfos[i][j].deltaY);
            }
        }

        return newTranslateInfos;
    }


    public static AllTileTransFormData copy(AllTileTransFormData allTileTransFormData)
    {
        AllTileTransFormData newAllTileTransFormData = new AllTileTransFormData();
        newAllTileTransFormData.addScore = allTileTransFormData.addScore;
        newAllTileTransFormData.newData = copy(allTileTransFormData.newData);
        newAllTileTransFormData.translateInfos = copy(allTileTransFormData.translateInfos);

        return newAllTileTransFormData;
    }
}
