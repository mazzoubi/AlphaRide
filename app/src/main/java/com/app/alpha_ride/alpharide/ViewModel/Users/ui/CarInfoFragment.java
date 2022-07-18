package com.app.alpha_ride.alpharide.ViewModel.Users.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.app.alpha_ride.alpharide.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CarInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CarInfoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CarInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CarInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CarInfoFragment newInstance(String param1, String param2) {
        CarInfoFragment fragment = new CarInfoFragment();
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
    }

    EditText edtCarType , edtCarColor , edtCarModel , edtCarNumber;
    Button btnNext;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_car_info, container, false);
        init(view);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickNext();
            }
        });
        return view;
    }

    void init(View v){
        edtCarType = v.findViewById(R.id.edtCarType);
        edtCarColor = v.findViewById(R.id.edtCarColor);
        edtCarModel = v.findViewById(R.id.edtCarModel);
        edtCarNumber = v.findViewById(R.id.edtCarNumber);
        btnNext = v.findViewById(R.id.btnNext);
    }

    void onClickNext(){
        String carType = edtCarType.getText().toString();
        String carColor = edtCarColor.getText().toString();
        String carModel = edtCarModel.getText().toString();
        String carNumber = edtCarNumber.getText().toString();

        if (carType.isEmpty()){
            edtCarType.setError("نوع المركبة والفئة مطلوب!");
        }else if (carColor.isEmpty()){
            edtCarType.setError("لون المركبة مطلوب!");
        }else if (carModel.isEmpty()){
            edtCarType.setError("سنة صنع المركبة مطلوب!");
        }else if (carNumber.isEmpty()){
            edtCarType.setError("رقم المركبة مطلوب!");
        }else {
            RegisterActivity.userModel.carModel = carModel ;
            RegisterActivity.userModel.carType = carType ;
            RegisterActivity.userModel.carColor = carColor ;
            RegisterActivity.userModel.numberCar = carNumber ;
            getFragmentManager().beginTransaction().replace(R.id.frameLayout,new DocumentFragment())
                    .commit();
        }
    }
}