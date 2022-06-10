package mazzoubi.ldjobs.com.alpharide.ViewModel.Users.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.hbb20.CountryCodePicker;

import mazzoubi.ldjobs.com.alpharide.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserInfoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserInfoFragment newInstance(String param1, String param2) {
        UserInfoFragment fragment = new UserInfoFragment();
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

    CountryCodePicker countryCodePicker ;
    EditText edtFullName , edtPassword , edtPhone ;
    Button btnNext ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_user_info, container, false);
        init(view);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickNext();
            }
        });

        return view;
    }

    void init (View v){
        edtFullName = v.findViewById(R.id.edtFullName);
        edtPassword = v.findViewById(R.id.edtPassword);
        edtPhone = v.findViewById(R.id.edtPhone);
        countryCodePicker = v.findViewById(R.id.countryCode_picker);
        btnNext = v.findViewById(R.id.btnNext);
    }


    void onClickNext(){
        if (edtFullName.getText().toString().isEmpty()){
            edtFullName.setError("الاسم مطلوب!");
        }else if (edtPassword.getText().toString().isEmpty()){
            edtPassword.setError("كلمة المرور مطلوبة!");
        }else if (edtPhone.getText().toString().isEmpty()){
            edtPhone.setError("رقم الهاتف مطلوب!");
        }else {
            RegisterActivity.userModel.fullName = edtFullName.getText().toString();
            RegisterActivity.userModel.password = edtPassword.getText().toString();
            RegisterActivity.userModel.phoneNumber =
                    countryCodePicker.getSelectedCountryCodeWithPlus()+edtPhone.getText().toString();
            getFragmentManager().beginTransaction().replace(R.id.frameLayout,new CarInfoFragment())
                    .commit();
        }
    }


}