package com.ghao.developer.offline;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ghao.developer.offline.dao.OutDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OutFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OutFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final Logger LOG = LoggerFactory.getLogger(OutFragment.class);

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Context context;
    private OnFragmentInteractionListener mListener;
    private View view;

    public OutFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OutFragment newInstance(String param1, String param2) {
        OutFragment fragment = new OutFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        context = getActivity();
    }

    private View.OnClickListener QrOpenListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity activity = (MainActivity)getActivity();
            LOG.info("打开出库扫码");
            activity.Scanner("out");
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_out, container, false);
        Button QrOpen =  (Button) view.findViewById(R.id.scan_out);
        QrOpen.setOnClickListener(QrOpenListener);

        Button sureButton = view.findViewById(R.id.sure_out);
        sureButton.setOnClickListener(sureButtonClickListener);
        return view;
    }
    private Button.OnClickListener sureButtonClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText ckdbhText =(EditText) view.findViewById (R.id.ckdbhText);
            String ckdbh = ckdbhText.getText().toString();
            OutDao outDao = new OutDao(context);
            ContentValues contentValues = new ContentValues();
            contentValues.put("ckdbh",ckdbh);
            contentValues.put("htbh","htbh001");
            contentValues.put("pcbh","pcbh001");
            contentValues.put("rksj",new Date().getTime());
            contentValues.put("czr","ghao");
            if("".equals(ckdbh)){
                LOG.info("出库单编号为空");
                Toast.makeText(context, "出库单编号为空", Toast.LENGTH_SHORT).show();
            }else{
                long resultNumber = outDao.actionOut(contentValues);
                if(resultNumber>0){
                    LOG.info("出库成功,内容:"+contentValues.toString());
                    Toast.makeText(context, "出库成功", Toast.LENGTH_SHORT).show();
                    ckdbhText.setText(null);
                }
            }
        }
    };
    public void setEditTextValue(String scanResult){
        EditText rkdbhText =(EditText) view.findViewById (R.id.ckdbhText);
        rkdbhText.setText(scanResult);
    }


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
