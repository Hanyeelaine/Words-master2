package com.example.words;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.words.wordcontract.Words;

import java.util.ArrayList;
import java.util.Map;



public class WordItemFragment extends ListFragment {
    private static final String TAG = "myTag";

    //当前是否为横屏
    private boolean currentIsLand;

    /*
    Fragment关联的Activity 实现 OnFragmentInteractionListener接口
    两个Fragment交互, 必通过他们之间关联的Activity. 两个Fragment之间的直接的信息传递是行不通的.
      实现Fragment和与之关联的Activity之间的信息传递,
    在Fragment中定义一个接口, 然后在Activity中实现这个接口.
    这个Fragment会在onAttach()方法中捕捉到Activity中实现的这个接口,
    然后通过调用接口中的方法实现与Activity的交流
     */

    private OnFragmentInteractionListener mListener;

    // TODO: Rename and change types of parameters
    public static WordItemFragment newInstance() {
        WordItemFragment fragment = new WordItemFragment();
        Bundle args = new Bundle();
        ;
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 用于片段管理器的强制空构造函数实例化片段（例如在屏幕方向更改时）
     */
    public WordItemFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        //为列表注册上下文菜单
        ListView mListView = (ListView) view.findViewById(android.R.id.list);
        registerForContextMenu(mListView);
        return view;
    }

    /*
    Fragment的onAttach
    一个 Activity 管理多个 Fragment 进行，需要两者相互传递数据，
    给 Fragment 添加回调接口，让 Activity 继承并实现。
    回调接口一般都写在 Fragment 的onAttach()方法中
    */

    @Override
    public void onAttach(Context context) {
        Log.v(TAG, "WordItemFragment::onAttach");
        super.onAttach(context);
    }

    //更新单词列表，从数据库中找到所有单词，然后在列表中显示出来
    public void refreshWordsList() {
        WordsDB wordsDB=WordsDB.getWordsDB();
        if (wordsDB != null) {
            ArrayList<Map<String, String>> items = wordsDB.getAllWords();

            // SimpleAdapter 适配器。将静态数据映射到XML文件中定义好的视图。
            SimpleAdapter adapter = new SimpleAdapter(getActivity(), items, R.layout.item,
                    new String[]{Words.Word._ID, Words.Word.COLUMN_NAME_WORD},
                    new int[]{R.id.textId, R.id.textViewWord});

            setListAdapter(adapter);
        }
    }

    //更新单词列表，从数据库中找到同strWord匹配的单词，然后在列表中显示出来
    public void refreshWordsList(String strWord) {
        WordsDB wordsDB=WordsDB.getWordsDB();
        if (wordsDB != null) {
            ArrayList<Map<String, String>> items = wordsDB.SearchUseSql(strWord);
            if(items.size()>0){
                SimpleAdapter adapter = new SimpleAdapter(getActivity(), items, R.layout.item,
                        new String[]{Words.Word._ID, Words.Word.COLUMN_NAME_WORD},
                        new int[]{R.id.textId, R.id.textViewWord});

                setListAdapter(adapter);
            }else{
                Toast.makeText(getActivity(),"Not found",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListener = (OnFragmentInteractionListener) getActivity();
        //刷新单词列表
        refreshWordsList();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        TextView textId = null;
        //TextView textWord = null;
        //TextView textMeaning = null;
        //TextView textSample = null;

        AdapterView.AdapterContextMenuInfo info = null;
        View itemView = null;

        switch (item.getItemId()) {
            case R.id.action_delete:
                //删除单词
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                itemView = info.targetView;
                textId = (TextView) itemView.findViewById(R.id.textId);
                if (textId != null) {
                    String strId = textId.getText().toString();
                    mListener.onDeleteDialog(strId);
                }
                break;
            case R.id.action_update:
                //修改单词
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                itemView = info.targetView;
                textId = (TextView) itemView.findViewById(R.id.textId);

                if (textId != null) {
                    String strId = textId.getText().toString();
                    mListener.onUpdateDialog(strId);
                }
                break;
        }
        return true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        Log.v(TAG, "WordItemFragment::onCreateContextMenu()");
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.contextmenu_wordslistview, menu);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            //通知Fragment所在的Activity，用户单击了列表的position项
            TextView textView = (TextView) v.findViewById(R.id.textId);
            if (textView != null) {
                //将单词ID传过去
                mListener.onWordItemClick(textView.getText().toString());
            }
        }
    }

    /**
     * Fragment所在的Activity必须实现该接口，通过该接口Fragment和Activity可以进行通信
     */
    public interface OnFragmentInteractionListener {
        public void onWordItemClick(String id);

        public void onDeleteDialog(String strId);

        public void onUpdateDialog(String strId);
    }

}