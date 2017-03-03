package com.lucky.game2048.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.lucky.game2048.R;
import com.lucky.game2048.config.Config;

import java.text.MessageFormat;

/**
 * 游戏配置
 */
public class ConfigPreferenceActivity extends AppCompatActivity implements OnClickListener {

    private Button mBtnGameLines;

    private Button mBtnGoal;

    private Button mBtnBack;

    private Button mBtnDone;

    private String[] mGameLinesList;

    private String[] mGameGoalList;

    private AlertDialog.Builder mBuilder;

    private TextView contact_me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_preference);
        initView();
    }

    private void initView() {
        contact_me = (TextView) findViewById(R.id.tv_contact);
        mBtnGameLines = (Button) findViewById(R.id.btn_gamelines);
        mBtnGoal = (Button) findViewById(R.id.btn_goal);
        mBtnBack = (Button) findViewById(R.id.btn_back);
        mBtnDone = (Button) findViewById(R.id.btn_done);
        mBtnGameLines.setText(MessageFormat.format("{0}", Config.mSp.getInt(Config.KEY_GAME_LINES, 4)));
        mBtnGoal.setText(String.valueOf(Config.mSp.getInt(Config.KEY_GAME_GOAL, 2048)));
        mBtnGameLines.setOnClickListener(this);
        mBtnGoal.setOnClickListener(this);
        mBtnBack.setOnClickListener(this);
        mBtnDone.setOnClickListener(this);
        contact_me.setOnClickListener(this);
        mGameLinesList = new String[]{"4", "5", "6"};
        mGameGoalList = new String[]{"1024", "2048", "4096", "8192", "16384"};
    }

    private void saveConfig() {
        Editor editor = Config.mSp.edit();
        editor.putInt(Config.KEY_GAME_LINES, Integer.parseInt(mBtnGameLines.getText().toString()));
        editor.putInt(Config.KEY_GAME_GOAL, Integer.parseInt(mBtnGoal.getText().toString()));
        editor.apply();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_gamelines:
                mBuilder = new AlertDialog.Builder(this);
                mBuilder.setTitle("选择游戏的难度");
                mBuilder.setItems(mGameLinesList, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mBtnGameLines.setText(mGameLinesList[which]);
                    }
                });
                mBuilder.create().show();
                break;
            case R.id.btn_goal:
                mBuilder = new AlertDialog.Builder(this);
                mBuilder.setTitle("选择游戏的目标");
                mBuilder.setItems(mGameGoalList, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mBtnGoal.setText(mGameGoalList[which]);
                    }
                });
                mBuilder.create().show();
                break;
            case R.id.btn_back:
                this.finish();
                break;
            case R.id.btn_done:
                saveConfig();
                setResult(RESULT_OK);
                this.finish();
                break;
            case R.id.tv_contact:
                try {
                    String urlQQ = "mqqwpa://im/chat?chat_type=wpa&uin=2206404530&version=1";
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlQQ)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
}
