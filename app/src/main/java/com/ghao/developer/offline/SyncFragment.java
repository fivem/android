package com.ghao.developer.offline;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ghao.developer.offline.dao.DBHelper;
import com.ghao.developer.offline.dao.SyncDao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SyncFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SyncFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SyncFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Context context;
    private ListView listView;
    private OnFragmentInteractionListener mListener;
    private SyncDao syncDao;
    private static final int COMPLETED = 0;

    public SyncFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SyncFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SyncFragment newInstance(String param1, String param2) {
        SyncFragment fragment = new SyncFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
        syncDao = new SyncDao(context);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sync, container, false);
        //入库记录按钮点击事件
        Button recordsIn = (Button) view.findViewById(R.id.records_in);
        recordsIn.setOnClickListener(recordsInButtonClickListener);
        //出库记录按钮点击事件
        Button recordsOut = (Button) view.findViewById(R.id.records_out);
        recordsOut.setOnClickListener(recordsOutButtonClickListener);
        //同步按钮点击事件
        Button sycnBtn = (Button) view.findViewById(R.id.sync_btn);
        sycnBtn.setOnClickListener(syncBtnButtonClickListener);


        listView = (ListView)view.findViewById(R.id.sync_listview);
        List<Map<String, Object>> list=getInData();
        listView.setAdapter(new ListViewAdapter(getActivity(), list));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = view.findViewById(R.id.listview_textview);
                Toast.makeText(context, "点击:"+textView.getText(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("title",textView.getText());
                startActivity(intent);
            }
        });

        return view;
    }

    private Button.OnClickListener recordsInButtonClickListener = new Button.OnClickListener(){
        @SuppressLint("ResourceAsColor")
        @Override
        public void onClick(View view) {
            List<Map<String, Object>> list = getInData();
            listView.setAdapter(new ListViewAdapter(getActivity(), list));
        }
    };
    private final Button.OnClickListener recordsOutButtonClickListener = new Button.OnClickListener() {
        @SuppressLint("ResourceAsColor")
        @Override
        public void onClick(View view) {
            List<Map<String, Object>> list = getOutData();
            listView.setAdapter(new ListViewAdapter(getActivity(), list));
        }
    };
    private final Button.OnClickListener syncBtnButtonClickListener = new Button.OnClickListener(){

        @Override
        public void onClick(View view) {
            //检查网络连接状态
          MainActivity mainActivity = (MainActivity ) getActivity();
          if(mainActivity.isNetworkConnected(SyncFragment.super.getContext())){
              showDialog("提示","是否执行同步?");
          }else{
              Toast.makeText(SyncFragment.super.getContext(), "网络未连接,请检查网络连接状态", Toast.LENGTH_SHORT).show();
          };
        }
    };
    private void execSync(){
        final ProgressBar progressBar = getActivity().findViewById(R.id.progressbar);
        progressBar.setMax(200);
        progressBar.setProgress(20);
        progressBar.setVisibility(View.VISIBLE); //To show ProgressBar
        getActivity().findViewById(R.id.progressMusk).setVisibility(View.VISIBLE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        new Thread(){
            int index = 20;
            @Override
            public void run(){
                while (index <= 200){
                    progressBar.setProgress(index);
                    index+=10;
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //  progressBar.setVisibility(View.GONE);
                Message message = new Message();
                message.what = COMPLETED;
                mHandler.sendMessage(message);
            }
        }.start();
    }
    public List<Map<String, Object>> getInData(){
        Cursor cursor = syncDao.getInData();
        return getData(cursor);
    }
    public List<Map<String, Object>> getOutData(){
        Cursor cursor = syncDao.getOutData();
        return getData(cursor);
    }

    public List<Map<String, Object>> getData(Cursor cursor){
        List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
        while(cursor.moveToNext()){
            Map<String, Object> map=new HashMap<String, Object>();
            String rkdbh  = cursor.getString(1);
            map.put("title",rkdbh);
            String czr = cursor.getString(6);
            long millisecond = cursor.getLong(5);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
            // time为转换格式后的字符串
            String time = dateFormat.format(new Date(millisecond)) ;
            map.put("time",time);
            map.put("image", R.drawable.ic_home_black_24dp);
            list.add(map);
        }
        return list;
    }

    private void showDialog(String title,String msg){
        boolean result = false;
        AlertDialog ad = new AlertDialog.Builder(this.context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("确定",new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        execSync();
                    }
                })
                .setNegativeButton("取消",new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                })
                .create();
        ad.show();
    }

    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == COMPLETED) {
                getActivity().findViewById(R.id.progressMusk).setVisibility(View.INVISIBLE);
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }
    };

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
