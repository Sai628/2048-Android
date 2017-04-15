package com.sai628.my2048.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.sai628.my2048.R;
import com.sai628.my2048.model.AllTileTransFormData;
import com.sai628.my2048.model.Position;
import com.sai628.my2048.model.TileStyle;
import com.sai628.my2048.util.TransformDataUtil;

import java.util.ArrayList;


public class GameActivity extends Activity
{
    private static final int MESSAGE_TYPE_INIT = 0;
    private static final int MESSAGE_TYPE_NEWSTEP = 1;
    private static final int MESSAGE_TYPE_TO_LEFT = 2;
    private static final int MESSAGE_TYPE_TO_RIGHT = 3;
    private static final int MESSAGE_TYPE_TO_UP = 4;
    private static final int MESSAGE_TYPE_TO_DOWN = 5;
    private static final int MESSAGE_TYPE_WIN = 6;
    private static final int MESSAGE_TYPE_REFRESH = 7;

    private static final int MOVE_TYPE_LEFT = 0;
    private static final int MOVE_TYPE_RIGHT = 1;
    private static final int MOVE_TYPE_UP = 2;
    private static final int MOVE_TYPE_DOWN = 3;

    private static final int ROW_NUM = 4;
    private static final int COLUMN_NUM = 4;

    private static final int STAGE_NUM = 256;
    private static final int TARGE_NUM = 2048;

    private TextView[][] tileTextViewArray;
    private int[][] tilePositionViewIDS;
    private int[][] newDataArray;
    private int[] bgColorResourceArray;
    private int[] textColorResourceArray;
    private int[] valueArray;
    private Position newPosition;
    private AllTileTransFormData allTileInfoData;
    private int currentScore = 0;

    private GestureDetector mGestureDetector;
    private Animation scaleOutAnimation;
    private Animation addScoreAnimation;

    private TextView scoreTv; // 当前得分
    private TextView addScoreTv; // 增加得分提示
    private TextView bestTv; // 最高得分

    private boolean isFirst; // 首次打开软件
    private boolean isBusy; // 是否在执行移动
    private boolean isStage2; // 是否进入第二阶段

    private MediaPlayer mediaPlayer;
    private SoundPool soundPool;
    private int soundID;

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MESSAGE_TYPE_INIT:
                    showTwoNewTileView();
                    break;

                case MESSAGE_TYPE_NEWSTEP:
                    if (doNextStep())
                    {
                        TextView textView = tileTextViewArray[newPosition.x][newPosition.y];
                        showNewTileView(textView, 2, scaleOutAnimation);
                    }
                    else  // 结束游戏
                    {
                        releaseMediaPlayer();
                        isBusy = false;
                        showResultDialog(false);
                    }
                    break;

                case MESSAGE_TYPE_TO_LEFT:
                    doMove(MOVE_TYPE_LEFT);
                    break;

                case MESSAGE_TYPE_TO_RIGHT:
                    doMove(MOVE_TYPE_RIGHT);
                    break;

                case MESSAGE_TYPE_TO_UP:
                    doMove(MOVE_TYPE_UP);
                    break;

                case MESSAGE_TYPE_TO_DOWN:
                    doMove(MOVE_TYPE_DOWN);
                    break;

                case MESSAGE_TYPE_WIN:
                    releaseMediaPlayer();
                    isBusy = false;
                    showResultDialog(true);
                    break;

                case MESSAGE_TYPE_REFRESH:
                    refreseAllTileView();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        initData();
        initView();
        initMusic();
        initSoundPool();
    }


    @Override
    protected void onStart()
    {
        super.onStart();
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        if (mediaPlayer != null)
        {
            mediaPlayer.start();
        }

        if (isFirst)
        {
            isFirst = false;
            new InitThread().start();
        }
    }


    @Override
    protected void onPause()
    {
        super.onPause();
        if (mediaPlayer != null)
        {
            mediaPlayer.pause();
        }
    }


    @Override
    protected void onRestart()
    {
        super.onRestart();
        if (mediaPlayer != null)
        {
            if (isStage2)
            {
                mediaPlayer = MediaPlayer.create(this, R.raw.rich);
            }
            else
            {
                mediaPlayer = MediaPlayer.create(this, R.raw.baga);
            }

            mediaPlayer.start();
        }
    }


    @Override
    protected void onStop()
    {
        super.onStop();
        SharedPreferences sharedPreferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        int oldBest = sharedPreferences.getInt("best", 0);
        if (currentScore > oldBest) // 刷新最高分
        {
            sharedPreferences.edit().putInt("best", currentScore).commit();
        }
    }


    @Override
    protected void onDestroy()
    {
        releaseMediaPlayer();
        super.onDestroy();
    }


    private void initData()
    {
        tilePositionViewIDS = new int[][]{
            {
                R.id.tile_layout_positon_00, R.id.tile_layout_positon_01, R.id.tile_layout_positon_02,
                R.id.tile_layout_positon_03
            },
            {
                R.id.tile_layout_positon_10, R.id.tile_layout_positon_11, R.id.tile_layout_positon_12,
                R.id.tile_layout_positon_13
            },
            {
                R.id.tile_layout_positon_20, R.id.tile_layout_positon_21, R.id.tile_layout_positon_22,
                R.id.tile_layout_positon_23
            },
            {
                R.id.tile_layout_positon_30, R.id.tile_layout_positon_31, R.id.tile_layout_positon_32,
                R.id.tile_layout_positon_33
            }
        };

        bgColorResourceArray = new int[]
            {
                R.drawable.num_2_bg, R.drawable.num_4_bg, R.drawable.num_8_bg, R.drawable.num_16_bg, R.drawable.num_32_bg,
                R.drawable.num_64_bg, R.drawable.num_128_bg, R.drawable.num_256_bg_anim, R.drawable.num_512_bg_anim,
                R.drawable.num_1024_bg_anim, R.drawable.num_2048_bg_anim
            };

        textColorResourceArray = new int[]
            {
                R.color.number_2_text_color, R.color.number_4_text_color, R.color.number_8_text_color,
                R.color.number_16_text_color, R.color.number_32_text_color, R.color.number_64_text_color,
                R.color.number_128_text_color, R.color.number_256_text_color, R.color.number_512_text_color,
                R.color.number_1024_text_color, R.color.number_2048_text_color
            };

        valueArray = new int[]{2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048};

        newDataArray = getNewDataArray();
        mGestureDetector = new GestureDetector(this, new MyGestureListener());

        scaleOutAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_out);
        scaleOutAnimation.setAnimationListener(scaleAnimationListener);
        scaleOutAnimation.setAnimationListener(scaleAnimationListener);

        addScoreAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        addScoreAnimation.setDuration(550);
        addScoreAnimation.setInterpolator(new AccelerateInterpolator());
        addScoreAnimation.setAnimationListener(addScoreAnimationListener);

        isFirst = true;
    }


    private void initView()
    {
        scoreTv = (TextView) findViewById(R.id.main_score_value_textview);
        scoreTv.setText("0");
        addScoreTv = (TextView) findViewById(R.id.main_add_score_prompt_textview);
        bestTv = (TextView) findViewById(R.id.main_best_value_textview);

        SharedPreferences sharedPreferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        int oldBest = sharedPreferences.getInt("best", 0);
        bestTv.setText(String.valueOf(oldBest));

        addScoreTv.setVisibility(View.GONE);

        tileTextViewArray = new TextView[tilePositionViewIDS.length][];
        for (int i = 0, rowLength = tilePositionViewIDS.length; i < rowLength; i++)
        {
            tileTextViewArray[i] = new TextView[tilePositionViewIDS[i].length];
            for (int j = 0, columnLength = tilePositionViewIDS[i].length; j < columnLength; j++)
            {
                tileTextViewArray[i][j] = (TextView) findViewById(tilePositionViewIDS[i][j]);
            }
        }
    }


    private void initMusic()
    {
        mediaPlayer = MediaPlayer.create(this, R.raw.baga);
        mediaPlayer.setOnPreparedListener(new OnPreparedListener()
        {
            public void onPrepared(MediaPlayer mp)
            {
                mediaPlayer.setLooping(true);
                mediaPlayer.setVolume(1.5f, 1.5f);
                mediaPlayer.start();
            }
        });
    }


    private void initSoundPool()
    {
        soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        soundID = soundPool.load(this, R.raw.onestep, 1);
    }


    private void playSoundPool()
    {
        soundPool.play(soundID, 0.5f, 0.5f, 0, 0, 1.0f);
    }


    private void releaseMediaPlayer()
    {
        if (mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    private int[][] getNewDataArray()
    {
        int[][] newDataArray = new int[ROW_NUM][COLUMN_NUM];
        for (int i = 0; i < ROW_NUM; i++)
        {
            for (int j = 0; j < COLUMN_NUM; j++)
            {
                newDataArray[i][j] = 0;
            }
        }

        return newDataArray;
    }


    private void initTwoRandomTile()
    {
        int x1 = -1;
        int y1 = -1;
        int x2 = -1;
        int y2 = -1;

        do
        {
            x1 = (int) (Math.random() * ROW_NUM);
            y1 = (int) (Math.random() * COLUMN_NUM);
            x2 = (int) (Math.random() * ROW_NUM);
            y2 = (int) (Math.random() * COLUMN_NUM);
        }
        while (x1 == x2 && y1 == y2);

        newDataArray[x1][y1] = Math.random() > 0.5 ? 4 : 2;
        newDataArray[x2][y2] = Math.random() > 0.5 ? 4 : 2;
    }


    // 随机执行下一步，返回false表示游戏失败结束，无法再走
    private boolean doNextStep()
    {
        ArrayList<Position> emptyPointList = new ArrayList<Position>();
        for (int i = 0; i < ROW_NUM; i++)
        {
            for (int j = 0; j < COLUMN_NUM; j++)
            {
                if (newDataArray[i][j] == 0) // 为空白格子
                {
                    Position point = new Position(i, j);
                    emptyPointList.add(point);
                }
            }
        }

        int size = emptyPointList.size();
        if (size > 0) // 还有空白格子
        {
            int nextPosition = (int) (Math.random() * size);
            newPosition = emptyPointList.get(nextPosition);
            newDataArray[newPosition.x][newPosition.y] = 2;

            return true;
        }
        else
        {
            return false; // 结束游戏
        }
    }


    private void doMove(int moveType)
    {
        switch (moveType)
        {
            case MOVE_TYPE_LEFT:
                allTileInfoData = TransformDataUtil.copy(TransformDataUtil.moveToLeft(newDataArray));
                break;

            case MOVE_TYPE_RIGHT:
                allTileInfoData = TransformDataUtil.copy(TransformDataUtil.moveToRight(newDataArray));
                break;

            case MOVE_TYPE_UP:
                allTileInfoData = TransformDataUtil.copy(TransformDataUtil.moveToUp(newDataArray));
                break;

            case MOVE_TYPE_DOWN:
                allTileInfoData = TransformDataUtil.copy(TransformDataUtil.moveToDown(newDataArray));
                break;
        }

        newDataArray = TransformDataUtil.copy(allTileInfoData.newData);
        currentScore += allTileInfoData.addScore;

        if (allTileInfoData.addScore != 0)
        {
            addScoreTv.setText("+" + String.valueOf(allTileInfoData.addScore));
            addScoreTv.setVisibility(View.VISIBLE);
            addScoreTv.startAnimation(addScoreAnimation);

            playSoundPool();
        }

        scoreTv.setText(String.valueOf(currentScore));
        handler.sendEmptyMessageDelayed(MESSAGE_TYPE_REFRESH, 100);

        if (isWin())
        {
            handler.sendEmptyMessageDelayed(MESSAGE_TYPE_WIN, 10);
        }
        else
        {
            if (!isStage2 && getMaxNum() >= STAGE_NUM)
            {
                isStage2 = true;

                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                mediaPlayer = MediaPlayer.create(GameActivity.this, R.raw.rich);
                mediaPlayer.setOnPreparedListener(new OnPreparedListener()
                {
                    public void onPrepared(MediaPlayer mp)
                    {
                        mediaPlayer.setLooping(true);
                        mediaPlayer.setVolume(1.5f, 1.5f);
                        mediaPlayer.start();
                    }
                });
            }

            handler.sendEmptyMessageDelayed(MESSAGE_TYPE_NEWSTEP, 80);
        }
    }


    private TileStyle getTileStyle(int value)
    {
        Drawable bgDrawable = null;
        int textColor = 0;
        float textSize = 0f;

        int index = -1;
        for (int i = 0, length = valueArray.length; i < length; i++)
        {
            if (valueArray[i] == value)
            {
                index = i;
                break;
            }
        }

        if (index != -1)
        {
            bgDrawable = getResources().getDrawable(bgColorResourceArray[index]);
            textColor = getResources().getColor(textColorResourceArray[index]);

            if (value < 16)
            {
                textSize = getResources().getDimension(R.dimen.text_size_number_1);
            }
            if (value < 128)
            {
                textSize = getResources().getDimension(R.dimen.text_size_number_2);
            }
            else if (value >= 128 && value < 1024)
            {
                textSize = getResources().getDimension(R.dimen.text_size_number_3);
            }
            else
            {
                textSize = getResources().getDimension(R.dimen.text_size_number_4);
            }

            return new TileStyle(bgDrawable, textColor, textSize);
        }

        return null;
    }


    private void showTwoNewTileView()
    {
        int num = 0;
        TextView textView1 = null;
        TextView textView2 = null;
        int value1 = 0;
        int value2 = 0;

        for (int i = 0; i < ROW_NUM; i++)
        {
            for (int j = 0; j < COLUMN_NUM; j++)
            {
                if (newDataArray[i][j] != 0)
                {
                    if (textView1 == null)
                    {
                        textView1 = tileTextViewArray[i][j];
                        value1 = newDataArray[i][j];
                    }
                    else
                    {
                        textView2 = tileTextViewArray[i][j];
                        value2 = newDataArray[i][j];
                    }

                    num++;
                }

                if (num == 2)
                {
                    break;
                }
            }
        }

        showNewTileView(textView1, value1, scaleOutAnimation);
        showNewTileView(textView2, value2, scaleOutAnimation);
    }


    private void refreseAllTileView()
    {
        for (int i = 0, rowLength = tilePositionViewIDS.length; i < rowLength; i++)
        {
            for (int j = 0, columnLength = tilePositionViewIDS[i].length; j < columnLength; j++)
            {
                refreshOneTileView(tileTextViewArray[i][j], newDataArray[i][j], null);
            }
        }
    }


    private void refreshOneTileView(TextView view, int tileValue, Animation animation)
    {
        if (tileValue == 0)
        {
            view.setText(null);
            view.setVisibility(View.GONE);
            return;
        }

        TileStyle style = getTileStyle(tileValue);
        view.setBackgroundDrawable(style.bgDrawable);
        view.setTextColor(style.textColor);
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, style.textSize);
        view.setText(String.valueOf(tileValue));
        view.setVisibility(View.VISIBLE);

        if (style.bgDrawable instanceof AnimationDrawable)
        {
            ((AnimationDrawable) style.bgDrawable).start();
        }

        if (animation != null)
        {
            view.startAnimation(animation);
        }
    }


    private void showNewTileView(TextView view, int tileValue, Animation animation)
    {
        TileStyle style = getTileStyle(tileValue);

        view.setBackgroundDrawable(style.bgDrawable);
        view.setTextColor(style.textColor);
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, style.textSize);
        view.setText(String.valueOf(tileValue));
        view.setVisibility(View.VISIBLE);

        if (style.bgDrawable instanceof AnimationDrawable)
        {
            ((AnimationDrawable) style.bgDrawable).start();
        }

        view.startAnimation(animation);
    }


    private boolean isWin()
    {
        for (int i = 0, rowLength = newDataArray.length; i < rowLength; i++)
        {
            for (int j = 0, columnLength = newDataArray[i].length; j < columnLength; j++)
            {
                if (newDataArray[i][j] == TARGE_NUM)
                {
                    return true;
                }
            }
        }

        return false;
    }


    private int getMaxNum()
    {
        int maxNum = 0;

        for (int i = 0, rowLength = newDataArray.length; i < rowLength; i++)
        {
            for (int j = 0, columnLength = newDataArray[i].length; j < columnLength; j++)
            {
                if (newDataArray[i][j] > maxNum)
                {
                    maxNum = newDataArray[i][j];
                }
            }
        }

        return maxNum;
    }


    private void showResultDialog(final boolean isWin)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(isWin ? R.string.win_prompt : R.string.lost_prompt);
        builder.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                finish();
            }
        });
        builder.setPositiveButton(isWin ? R.string.restart : R.string.retry, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                releaseMediaPlayer();

                SharedPreferences sharedPreferences = getSharedPreferences("config", Context.MODE_PRIVATE);
                int oldBest = sharedPreferences.getInt("best", 0);
                if (currentScore > oldBest) // 刷新最高分
                {
                    sharedPreferences.edit().putInt("best", currentScore).commit();
                }

                initData();
                initView();
                initMusic();
                initSoundPool();

                handler.sendEmptyMessageDelayed(MESSAGE_TYPE_REFRESH, 0);
                new InitThread().start();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }


    private AnimationListener scaleAnimationListener = new AnimationListener()
    {

        @Override
        public void onAnimationStart(Animation animation)
        {
            isBusy = true;
        }


        @Override
        public void onAnimationRepeat(Animation animation)
        {
        }


        @Override
        public void onAnimationEnd(Animation animation)
        {
            isBusy = false;
        }
    };


    private AnimationListener addScoreAnimationListener = new AnimationListener()
    {

        @Override
        public void onAnimationStart(Animation animation)
        {
        }


        @Override
        public void onAnimationRepeat(Animation animation)
        {
        }


        @Override
        public void onAnimationEnd(Animation animation)
        {
            addScoreTv.setVisibility(View.GONE);
        }
    };


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return mGestureDetector.onTouchEvent(event);
    }


    @Override
    public void onBackPressed()
    {
        Intent ntent = new Intent(Intent.ACTION_MAIN);
        ntent.addCategory(Intent.CATEGORY_HOME);
        ntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(ntent);
    }


    class MyGestureListener extends GestureDetector.SimpleOnGestureListener
    {
        public boolean onSingleTapUp(MotionEvent ev)
        {
            return true;
        }


        public void onLongPress(MotionEvent ev)
        {
        }


        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {
            return true;
        }


        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            if (isBusy)
            {
                return true;
            }

            isBusy = true;

            int deltaX = (int) e2.getX() - (int) e1.getX();
            int deltaY = (int) e2.getY() - (int) e1.getY();
            int distanceX = Math.abs(deltaX);
            int distanceY = Math.abs(deltaY);

            if (distanceX > distanceY)
            {
                if (deltaX < 0)  // 向左滑动
                {
                    handler.sendEmptyMessageDelayed(MESSAGE_TYPE_TO_LEFT, 50);
                }
                else  // 向右滑动
                {
                    handler.sendEmptyMessageDelayed(MESSAGE_TYPE_TO_RIGHT, 50);
                }
            }
            else
            {
                if (deltaY < 0)  // 向上滑动
                {
                    handler.sendEmptyMessageDelayed(MESSAGE_TYPE_TO_UP, 50);
                }
                else  // 向下滑动
                {
                    handler.sendEmptyMessageDelayed(MESSAGE_TYPE_TO_DOWN, 50);
                }
            }

            return true;
        }


        public boolean onDown(MotionEvent ev)
        {
            return true;
        }
    }


    class InitThread extends Thread
    {
        @Override
        public void run()
        {
            try
            {
                Thread.sleep(500);

                initTwoRandomTile();
                handler.sendEmptyMessage(MESSAGE_TYPE_INIT);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
