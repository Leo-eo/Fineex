package com.lucky.game2048.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.lucky.game2048.R;
import com.lucky.game2048.config.Config;
import com.lucky.game2048.view.GameView;

import ljun.testlibrary.T;

/**
 * 游戏界面
 */
public class GameActivity extends AppCompatActivity implements OnClickListener {

    // Activity的引用
    private static GameActivity mGame;
    // 记录分数
    private TextView mTvScore;
    // 历史记录分数
    private TextView mTvHighScore;
    private int mHighScore;
    // 目标分数
    private TextView mTvGoal;
    private int mGoal;
    // 重新开始按钮
    private Button mBtnRestart;
    // 撤销按钮
    private Button mBtnRevert;
    // 选项按钮
    private Button mBtnOptions;
    // 游戏面板
    private GameView mGameView;
    private long mExitTime;
    private MediaPlayer mediaplayer;

    public GameActivity() {
        mGame = this;
    }

    /**
     * 获取当前Activity的引用
     *
     * @return Activity.this
     */
    public static GameActivity getGameActivity() {
        return mGame;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 初始化View
        initView();
        mGameView = (GameView) findViewById(R.id.game_view);
        mediaplayer = MediaPlayer.create(this, R.raw.error);
        mediaplayer.setLooping(true);
        mediaplayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaplayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaplayer.start();
    }

    /**
     * 初始化View
     */
    private void initView() {
        mTvScore = (TextView) findViewById(R.id.scroe);
        mTvGoal = (TextView) findViewById(R.id.tv_Goal);
        mTvHighScore = (TextView) findViewById(R.id.record);
        mBtnRestart = (Button) findViewById(R.id.btn_restart);
        mBtnRevert = (Button) findViewById(R.id.btn_revert);
        mBtnOptions = (Button) findViewById(R.id.btn_option);
        mBtnRestart.setOnClickListener(this);
        mBtnRevert.setOnClickListener(this);
        mBtnOptions.setOnClickListener(this);
        mHighScore = Config.mSp.getInt(Config.KEY_HIGH_SCROE, 0);
        mGoal = Config.mSp.getInt(Config.KEY_GAME_GOAL, 2048);
        mTvHighScore.setText(String.valueOf(mHighScore));
        mTvGoal.setText(String.valueOf(mGoal));
        mTvScore.setText("0");
        setScore(0, 0);
    }

    public void setGoal(int num) {
        mTvGoal.setText(String.valueOf(num));
    }

    /**
     * 修改得分
     *
     * @param score score
     * @param flag  0 : score 1 : high score
     */
    public void setScore(int score, int flag) {
        switch (flag) {
            case 0:
                mTvScore.setText(String.valueOf(score));
                break;
            case 1:
                mTvHighScore.setText(String.valueOf(score));
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_restart:
                mGameView.startGame();
                setScore(0, 0);
                break;
            case R.id.btn_revert:
                mGameView.revertGame();
                break;
            case R.id.btn_option:
                Intent intent = new Intent(this, ConfigPreferenceActivity.class);
                startActivityForResult(intent, 0);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mGoal = Config.mSp.getInt(Config.KEY_GAME_GOAL, 2048);
            mTvGoal.setText(String.valueOf(mGoal));
            getHighScore();
            mGameView.startGame();
        }
    }

    /**
     * 获取最高记录
     */
    private void getHighScore() {
        int score = Config.mSp.getInt(Config.KEY_HIGH_SCROE, 0);
        setScore(score, 1);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                T.s(this, "再按一次退出游戏");
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);

            }
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}
