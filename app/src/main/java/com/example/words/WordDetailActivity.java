package com.example.words;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;



public class WordDetailActivity extends AppCompatActivity implements WordDetailFragment.OnFragmentInteractionListener{
    // 某个类后使用 implements，并指定相应接口，则下面需要 实现相应接口的方法。
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   //saveInsanceState也就是保存Activity的状态的
        //如果是横屏的话直接退出
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 读取系统资源函数。获取设备配置信息。方向
            finish();
            return;
        }
        // 只有在savedInstanceState==null时，才进行创建Fragment实例：
        // 现在无论进行多次旋转都只会有一个Fragment实例在Activity中。
        if (savedInstanceState == null) {
            WordDetailFragment detailFragment = new WordDetailFragment();
            detailFragment.setArguments(getIntent().getExtras());
            //通过fragment的setArguments()传入值。
            //Bundle在Activity之间传值  getExtras()取出Intent所携带的数据

            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, detailFragment)
                    .commit();
        }
    }
    @Override
    public void onWordDetailClick(Uri uri) {
    }
}

