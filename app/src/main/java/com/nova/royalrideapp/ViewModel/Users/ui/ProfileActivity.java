package com.nova.royalrideapp.ViewModel.Users.ui;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import com.nova.royalrideapp.Data.Users.UserInfo_sharedPreference;
import com.nova.royalrideapp.Data.Users.UserModel;
import com.nova.royalrideapp.R;
import com.nova.royalrideapp.ViewModel.Users.UserViewModel;

public class ProfileActivity extends AppCompatActivity {

    EditText edtName , edtPhone , edtEmail , edtPassword ;
    Button btnSave ;
    CircleImageView cim;
    String pictureImagePath = "" ,DownloadUrl = "" ;
    Bitmap bitmap = null;
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile);
        init();

//        ApplicationLifecycleHandler handler = new ApplicationLifecycleHandler();
//        registerActivityLifecycleCallbacks(handler);
//        registerComponentCallbacks(handler);

    }



    void init(){
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnSave = findViewById(R.id.btnSave);
        cim = findViewById(R.id.profile);

        edtName.setText(UserInfo_sharedPreference.getUser(ProfileActivity.this).fullName);
        edtPhone.setText(UserInfo_sharedPreference.getUser(ProfileActivity.this).phoneNumber);
        edtEmail.setText(UserInfo_sharedPreference.getUser(ProfileActivity.this).email);
        edtPassword.setText(UserInfo_sharedPreference.getUser(ProfileActivity.this).password);
        try{Picasso.get().load(UserInfo_sharedPreference.getUser(ProfileActivity.this).imageProfile).into(cim);}
        catch (Exception ex){}

        edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!edtName.getText().toString().equals(UserInfo_sharedPreference
                        .getUser(ProfileActivity.this).fullName)){
                    btnSave.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!edtEmail.getText().toString().equals(UserInfo_sharedPreference
                        .getUser(ProfileActivity.this).email)){
                    btnSave.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!edtPassword.getText().toString().equals(UserInfo_sharedPreference
                        .getUser(ProfileActivity.this).password)){
                    btnSave.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    public void onClickSave(View view) {
        if (edtName.getText().toString().isEmpty()){
            edtName.setError("الاسم مطلوب!");
        }else if (edtEmail.getText().toString().isEmpty()){
            edtEmail.setError("الاميل مطلوب!");
        }else if (edtPassword.getText().toString().isEmpty()){
            edtPassword.setError("كلمة المرور مطلوبه!");
        }else {
            UserModel userModel = UserInfo_sharedPreference.getUser(ProfileActivity.this);
            userModel.fullName = edtName.getText().toString();
            userModel.email = edtEmail.getText().toString();
            userModel.password = edtPassword.getText().toString();
            userModel.imageProfile = DownloadUrl;
            UserViewModel vm = ViewModelProviders.of(this).get(UserViewModel.class);
            vm.UpdateUser(ProfileActivity.this,userModel);
        }
    }

    public void ProfileImage(View view) {

        new AlertDialog.Builder(ProfileActivity.this)
                .setMessage("يرجى إختيار أسلوب الإدخال..")
                .setPositiveButton("الأستوديو", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                    }
                }).setNegativeButton("الكاميرا", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                int camera = ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA);
                List<String> listPermissionsNeeded = new ArrayList<>();

                if (camera != PackageManager.PERMISSION_GRANTED)
                    listPermissionsNeeded.add(Manifest.permission.CAMERA);

                if (!listPermissionsNeeded.isEmpty()){
                    ActivityCompat.requestPermissions(ProfileActivity.this,listPermissionsNeeded.toArray
                            (new String[listPermissionsNeeded.size()]), 1); }
                else{
                    CaptureImage(2);
                }

            }
        }).create().show();

    }

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

        int camera = ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA);
        int store1 = ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        //     int store2 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        ArrayList<String> listPermissionsNeeded = new ArrayList<>();
        if (camera != PackageManager.PERMISSION_GRANTED){
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
            ActivityCompat.requestPermissions(ProfileActivity.this,listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), 1); }

        if (store1 != PackageManager.PERMISSION_GRANTED){
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            ActivityCompat.requestPermissions(ProfileActivity.this,listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), 2); }

        startActivityForResult(cameraIntent, requestCode);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){

            if(requestCode == 1){

                Toast.makeText(ProfileActivity.this, "جاري تحميل الصورة...", Toast.LENGTH_SHORT).show();
                Uri uri = data.getData();

                try { bitmap = MediaStore.Images.Media.getBitmap(ProfileActivity.this.getContentResolver(), uri); }
                catch (IOException e) {}
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 5, baos);

                String path = MediaStore.Images.Media.insertImage(ProfileActivity.this.getContentResolver(), bitmap, "image"+uri.getLastPathSegment(), null);
                uri = Uri.parse(path);

                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                final StorageReference ImageName =  storageReference.child("UsersImage")
                        .child(System.currentTimeMillis()+"");

                ImageName.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                DownloadUrl = uri.toString();
                                Toast.makeText(ProfileActivity.this, "تم رفع الصورة بنجاح", Toast.LENGTH_LONG).show();
                                Picasso.get().load(DownloadUrl).into(cim);

                                UserModel userModel = UserInfo_sharedPreference.getUser(ProfileActivity.this);
                                userModel.fullName = edtName.getText().toString();
                                userModel.email = edtEmail.getText().toString();
                                userModel.password = edtPassword.getText().toString();
                                userModel.imageProfile = DownloadUrl;
                                UserViewModel vm = ViewModelProviders.of(ProfileActivity.this).get(UserViewModel.class);
                                vm.UpdateUser(ProfileActivity.this,userModel);

                            }
                        });
                    }
                });

            }
            else if (requestCode == 2){
                Toast.makeText(ProfileActivity.this, "جاري تحميل الصورة...", Toast.LENGTH_SHORT).show();

                File imgFile = new File(pictureImagePath);
                final Bitmap imageBitmap = decodeFile(imgFile);

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(ProfileActivity.this.getContentResolver(), imageBitmap, "Title", null);

                Uri uri = Uri.parse(path);

                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                final StorageReference ImageName =  storageReference.child("UsersImage")
                        .child(System.currentTimeMillis()+"");

                ImageName.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                DownloadUrl = uri.toString();
                                Toast.makeText(ProfileActivity.this, "تم رفع الصورة بنجاح", Toast.LENGTH_LONG).show();
                                Picasso.get().load(DownloadUrl).into(cim);

                                UserModel userModel = UserInfo_sharedPreference.getUser(ProfileActivity.this);
                                userModel.fullName = edtName.getText().toString();
                                userModel.email = edtEmail.getText().toString();
                                userModel.password = edtPassword.getText().toString();
                                userModel.imageProfile = DownloadUrl;
                                UserViewModel vm = ViewModelProviders.of(ProfileActivity.this).get(UserViewModel.class);
                                vm.UpdateUser(ProfileActivity.this,userModel);

                            }
                        });
                    }
                });

            }

        }
    }

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