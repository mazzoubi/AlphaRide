package com.nova.royalrideapp.ViewModel.Users.Cars.ui;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.nova.royalrideapp.R;
import com.nova.royalrideapp.ViewModel.Users.Cars.AddNewCarActivity;
import com.nova.royalrideapp.ViewModel.Users.UserViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewDocumentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewDocumentFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NewDocumentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DocumentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewDocumentFragment newInstance(String param1, String param2) {
        NewDocumentFragment fragment = new NewDocumentFragment();
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

    CheckBox checkBoxDriverL , checkBoxCarL , checkBoxFrontCar ;
    Button btnDriverL , btnCarL , btnFrontCar,btnNext;
    String imaPath = System.currentTimeMillis()+"";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_new_document, container, false);
        init(view);
        btnDriverL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDriverL();
            }
        });
        btnCarL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCarL();
            }
        });
        btnFrontCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFrontCar();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickNext();
            }
        });
        return view;
    }

    void init (View v){
        checkBoxDriverL = v.findViewById(R.id.checkBoxDriverL);
        checkBoxCarL = v.findViewById(R.id.checkBoxCarL);
        checkBoxFrontCar = v.findViewById(R.id.checkBoxFrontCar);
        btnDriverL = v.findViewById(R.id.btnDriverL);
        btnCarL = v.findViewById(R.id.btnCarL);
        btnFrontCar = v.findViewById(R.id.btnFrontCar);
        btnNext = v.findViewById(R.id.btnNext);
    }

    void onClickNext(){
        if (AddNewCarActivity.driverRequestAccountModel.driverLicense.isEmpty()){
            Toast.makeText(getActivity(), "???????????? ?????????? ???????? ???????? ??????????????!", Toast.LENGTH_SHORT).show();
        }else if (AddNewCarActivity.driverRequestAccountModel.drivingLicense.isEmpty()){
            Toast.makeText(getActivity(), "???????????? ?????????? ???????? ???????? ??????????????!", Toast.LENGTH_SHORT).show();
        }else if (AddNewCarActivity.driverRequestAccountModel.frontCar.isEmpty()){
            Toast.makeText(getActivity(), "???????????? ?????????? ???????? ???????????? ??????????????!", Toast.LENGTH_SHORT).show();
        }else {
//            startActivity(new Intent(getActivity(), VerifyPhoneActivity.class)); 555555555
            UserViewModel vm = ViewModelProviders.of(getActivity()).get(UserViewModel.class);
            vm.addNewCar(getActivity(),AddNewCarActivity.userModel,AddNewCarActivity.driverRequestAccountModel);
        }
    }
    void onDriverL(){
        new AlertDialog.Builder(getActivity())
                .setMessage("???????? ???????????? ?????????? ??????????????..")
                .setPositiveButton("??????????????????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 4);
                    }
                }).setNegativeButton("????????????????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                int camera = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
                List<String> listPermissionsNeeded = new ArrayList<>();

                if (camera != PackageManager.PERMISSION_GRANTED)
                    listPermissionsNeeded.add(Manifest.permission.CAMERA);

                if (!listPermissionsNeeded.isEmpty()){
                    ActivityCompat.requestPermissions(getActivity(),listPermissionsNeeded.toArray
                            (new String[listPermissionsNeeded.size()]), 1); }
                else{
                    CaptureImage(1);

                }

            }
        }).create().show();
    }
    void onCarL(){
        new AlertDialog.Builder(getActivity())
                .setMessage("???????? ???????????? ?????????? ??????????????..")
                .setPositiveButton("??????????????????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 5);
                    }
                }).setNegativeButton("????????????????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                int camera = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
                List<String> listPermissionsNeeded = new ArrayList<>();

                if (camera != PackageManager.PERMISSION_GRANTED)
                    listPermissionsNeeded.add(Manifest.permission.CAMERA);

                if (!listPermissionsNeeded.isEmpty()){
                    ActivityCompat.requestPermissions(getActivity(),listPermissionsNeeded.toArray
                            (new String[listPermissionsNeeded.size()]), 1); }
                else{
                    CaptureImage(2);

                }

            }
        }).create().show();
    }
    void onFrontCar(){
        new AlertDialog.Builder(getActivity())
                .setMessage("???????? ???????????? ?????????? ??????????????..")
                .setPositiveButton("??????????????????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 6);
                    }
                }).setNegativeButton("????????????????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                int camera = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
                List<String> listPermissionsNeeded = new ArrayList<>();

                if (camera != PackageManager.PERMISSION_GRANTED)
                    listPermissionsNeeded.add(Manifest.permission.CAMERA);

                if (!listPermissionsNeeded.isEmpty()){
                    ActivityCompat.requestPermissions(getActivity(),listPermissionsNeeded.toArray
                            (new String[listPermissionsNeeded.size()]), 1); }
                else{
                    CaptureImage(3);

                }

            }
        }).create().show();
    }

    String pictureImagePath = "" ,DownloadUrl = "" ;
    Bitmap bitmap = null;

    private void CaptureImage(int requestCode) {

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        pictureImagePath = "";
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        pictureImagePath = storageDir.getAbsolutePath() + "/" + imageFileName;
        File file = new File(pictureImagePath);
        Uri outputFileUri = Uri.fromFile(file);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        int camera = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
        int store1 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
   //     int store2 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        ArrayList<String> listPermissionsNeeded = new ArrayList<>();
        if (camera != PackageManager.PERMISSION_GRANTED){
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
            ActivityCompat.requestPermissions(getActivity(),listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), 1); }

        if (store1 != PackageManager.PERMISSION_GRANTED){
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            ActivityCompat.requestPermissions(getActivity(),listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), 2); }

            startActivityForResult(cameraIntent, requestCode);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){

            if(requestCode == 1){

                Toast.makeText(getActivity(), "???????? ?????????? ????????????...", Toast.LENGTH_SHORT).show();

                File imgFile = new File(pictureImagePath);
                final Bitmap imageBitmap = decodeFile(imgFile);

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), imageBitmap, "Title", null);

                Uri uri = Uri.parse(path);

                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                final StorageReference ImageName =  storageReference.child("imageRequestDriver")
                        .child(System.currentTimeMillis()+"").child("driverLicense");


                checkBoxDriverL.setChecked(false);
                checkBoxDriverL.setText("???????????? ????????????????...");
                ImageName.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                DownloadUrl = uri.toString();
                                checkBoxDriverL.setText("???? ??????????");
                                checkBoxDriverL.setChecked(true);
                                AddNewCarActivity.driverRequestAccountModel.driverLicense = uri.toString();
                                Toast.makeText(getContext(), "???? ?????? ???????????? ??????????", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });

            }
            else if (requestCode == 2){
                Toast.makeText(getActivity(), "???????? ?????????? ????????????...", Toast.LENGTH_SHORT).show();

                File imgFile = new File(pictureImagePath);
                final Bitmap imageBitmap = decodeFile(imgFile);

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), imageBitmap, "Title", null);

                Uri uri = Uri.parse(path);

                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                final StorageReference ImageName =  storageReference.child("imageRequestDriver")
                        .child(imaPath).child("drivingLicense");

                checkBoxCarL.setChecked(false);
                checkBoxCarL.setText("???????????? ????????????????...");
                ImageName.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                DownloadUrl = uri.toString();
                                checkBoxCarL.setText("???? ??????????");
                                checkBoxCarL.setChecked(true);
                                AddNewCarActivity.driverRequestAccountModel.drivingLicense = uri.toString();
                                Toast.makeText(getContext(), "???? ?????? ???????????? ??????????", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });

            }
            else if (requestCode == 3){
                Toast.makeText(getActivity(), "???????? ?????????? ????????????...", Toast.LENGTH_SHORT).show();

                File imgFile = new File(pictureImagePath);
                final Bitmap imageBitmap = decodeFile(imgFile);

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), imageBitmap, "Title", null);

                Uri uri = Uri.parse(path);

                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                final StorageReference ImageName =  storageReference.child("imageRequestDriver")
                        .child(imaPath).child("frontCar");

                checkBoxFrontCar.setChecked(false);
                checkBoxFrontCar.setText("???????????? ????????????????...");
                ImageName.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                DownloadUrl = uri.toString();
                                checkBoxFrontCar.setText("???? ??????????");
                                checkBoxFrontCar.setChecked(true);
                                AddNewCarActivity.driverRequestAccountModel.frontCar = uri.toString();
                                Toast.makeText(getContext(), "???? ?????? ???????????? ??????????", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });

            }
            else if (requestCode==4){
                Toast.makeText(getActivity(), "???????? ?????????? ????????????...", Toast.LENGTH_SHORT).show();
                Uri uri = data.getData();

                try { bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri); }
                catch (IOException e) {}
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 5, baos);

                String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bitmap, "image"+uri.getLastPathSegment(), null);
                uri = Uri.parse(path);

                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                final StorageReference ImageName =  storageReference.child("imageRequestDriver")
                        .child(System.currentTimeMillis()+"").child("driverLicense");


                checkBoxDriverL.setChecked(false);
                checkBoxDriverL.setText("???????????? ????????????????...");
                ImageName.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                DownloadUrl = uri.toString();
                                checkBoxDriverL.setText("???? ??????????");
                                checkBoxDriverL.setChecked(true);
                                AddNewCarActivity.driverRequestAccountModel.driverLicense = uri.toString();
                                Toast.makeText(getContext(), "???? ?????? ???????????? ??????????", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });

            }
            else if (requestCode==5){
                Toast.makeText(getActivity(), "???????? ?????????? ????????????...", Toast.LENGTH_SHORT).show();
                Uri uri = data.getData();

                try { bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri); }
                catch (IOException e) {}
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 5, baos);

                String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bitmap, "image"+uri.getLastPathSegment(), null);
                uri = Uri.parse(path);

                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                final StorageReference ImageName =  storageReference.child("imageRequestDriver")
                        .child(imaPath).child("drivingLicense");

                checkBoxCarL.setChecked(false);
                checkBoxCarL.setText("???????????? ????????????????...");
                ImageName.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                DownloadUrl = uri.toString();
                                checkBoxCarL.setText("???? ??????????");
                                checkBoxCarL.setChecked(true);
                                AddNewCarActivity.driverRequestAccountModel.drivingLicense = uri.toString();
                                Toast.makeText(getContext(), "???? ?????? ???????????? ??????????", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });
            }
            else if (requestCode==6){
                Toast.makeText(getActivity(), "???????? ?????????? ????????????...", Toast.LENGTH_SHORT).show();
                Uri uri = data.getData();

                try { bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri); }
                catch (IOException e) {}
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 5, baos);

                String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bitmap, "image"+uri.getLastPathSegment(), null);
                uri = Uri.parse(path);

                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                final StorageReference ImageName =  storageReference.child("imageRequestDriver")
                        .child(imaPath).child("frontCar");

                checkBoxFrontCar.setChecked(false);
                checkBoxFrontCar.setText("???????????? ????????????????...");
                ImageName.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                DownloadUrl = uri.toString();
                                checkBoxFrontCar.setText("???? ??????????");
                                checkBoxFrontCar.setChecked(true);
                                AddNewCarActivity.driverRequestAccountModel.frontCar = uri.toString();
                                Toast.makeText(getContext(), "???? ?????? ???????????? ??????????", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });

            }

        }
    }

    void uploadDriverLicense(){}
    private Bitmap decodeFile(File f){
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE=70;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale++;
            }

            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;

    }

}