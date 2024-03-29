package com.ghao.developer.offline;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ghao.developer.offline.dao.InDao;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import androidx.fragment.app.Fragment;

import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final Logger LOG = LoggerFactory.getLogger(InFragment.class);

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Context context;
    private OnFragmentInteractionListener mListener;
    private View view;

    public InFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InFragment newInstance(String param1, String param2) {
        InFragment fragment = new InFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private View.OnClickListener QrOpenListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity activity = (MainActivity)getActivity();
            LOG.info("打开入库扫码");
            activity.Scanner("in");
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_in, container, false);
        Button QrOpen =  (Button) view.findViewById(R.id.scan_in);
        QrOpen.setOnClickListener(QrOpenListener);

        Button sureButton = view.findViewById(R.id.sure_In);
        sureButton.setOnClickListener(sureButtonClickListener);
        return view;
    }
    private Button.OnClickListener sureButtonClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText rkdbhText =(EditText) view.findViewById (R.id.rkdbhText);
            String rkdbh = rkdbhText.getText().toString();
            InDao inDao = new InDao(context);
            ContentValues contentValues = new ContentValues();
            contentValues.put("rkdbh",rkdbh);
            contentValues.put("htbh","htbh001");
            contentValues.put("pcbh","pcbh001");
            contentValues.put("rksj",new Date().getTime());
            contentValues.put("czr","ghao");
            if("".equals(rkdbh)){
                LOG.info("入库单编号为空");
                Toast.makeText(context, "入库单编号为空", Toast.LENGTH_SHORT).show();
            }else{
                long resultNumber = inDao.actionIn(contentValues);
                if(resultNumber>0){
                    LOG.info("入库成功,内容:"+contentValues.toString());
                    Toast.makeText(context, "入库成功", Toast.LENGTH_SHORT).show();
                    rkdbhText.setText(null);
                }
            }
        }
    };
    public void setEditTextValue(String scanResult){
        EditText rkdbhText =(EditText) view.findViewById (R.id.rkdbhText);
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this.getActivity(), "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this.getActivity(), "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
