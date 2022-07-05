package mazzoubi.ldjobs.com.alpharide.ViewModel.Users.Cars;

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
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mazzoubi.ldjobs.com.alpharide.ApplicationLifecycleHandler;
import mazzoubi.ldjobs.com.alpharide.Data.Users.UserInfo_sharedPreference;
import mazzoubi.ldjobs.com.alpharide.R;
import mazzoubi.ldjobs.com.alpharide.ViewModel.Users.UserViewModel;

public class ChangeImagesActivity extends AppCompatActivity {
    CheckBox checkBoxDriverL , checkBoxCarL , checkBoxFrontCar ;
    Button btnDriverL , btnCarL , btnFrontCar;
    String imaPath = System.currentTimeMillis()+"";

    String from = "";
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_change_images);
        init();

        ApplicationLifecycleHandler handler = new ApplicationLifecycleHandler();
        registerActivityLifecycleCallbacks(handler);
        registerComponentCallbacks(handler);

        from = getIntent().getStringExtra("from");

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
        
    }


    void init (){
        checkBoxDriverL = findViewById(R.id.checkBoxDriverL);
        checkBoxCarL = findViewById(R.id.checkBoxCarL);
        checkBoxFrontCar = findViewById(R.id.checkBoxFrontCar);
        btnDriverL = findViewById(R.id.btnDriverL);
        btnCarL = findViewById(R.id.btnCarL);
        btnFrontCar = findViewById(R.id.btnFrontCar);

    }
    
    void onDriverL(){
        new AlertDialog.Builder(ChangeImagesActivity.this)
                .setMessage("يرجى إختيار أسلوب الإدخال..")
                .setPositiveButton("الأستوديو", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 4);
                    }
                }).setNegativeButton("الكاميرا", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                int camera = ContextCompat.checkSelfPermission(ChangeImagesActivity.this, Manifest.permission.CAMERA);
                List<String> listPermissionsNeeded = new ArrayList<>();

                if (camera != PackageManager.PERMISSION_GRANTED)
                    listPermissionsNeeded.add(Manifest.permission.CAMERA);

                if (!listPermissionsNeeded.isEmpty()){
                    ActivityCompat.requestPermissions(ChangeImagesActivity.this,listPermissionsNeeded.toArray
                            (new String[listPermissionsNeeded.size()]), 1); }
                else{
                    CaptureImage(1);

                }

            }
        }).create().show();
    }
    void onCarL(){
        new AlertDialog.Builder(ChangeImagesActivity.this)
                .setMessage("يرجى إختيار أسلوب الإدخال..")
                .setPositiveButton("الأستوديو", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 5);
                    }
                }).setNegativeButton("الكاميرا", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                int camera = ContextCompat.checkSelfPermission(ChangeImagesActivity.this, Manifest.permission.CAMERA);
                List<String> listPermissionsNeeded = new ArrayList<>();

                if (camera != PackageManager.PERMISSION_GRANTED)
                    listPermissionsNeeded.add(Manifest.permission.CAMERA);

                if (!listPermissionsNeeded.isEmpty()){
                    ActivityCompat.requestPermissions(ChangeImagesActivity.this,listPermissionsNeeded.toArray
                            (new String[listPermissionsNeeded.size()]), 1); }
                else{
                    CaptureImage(2);

                }

            }
        }).create().show();
    }
    void onFrontCar(){
        new AlertDialog.Builder(ChangeImagesActivity.this)
                .setMessage("يرجى إختيار أسلوب الإدخال..")
                .setPositiveButton("الأستوديو", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 6);
                    }
                }).setNegativeButton("الكاميرا", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                int camera = ContextCompat.checkSelfPermission(ChangeImagesActivity.this, Manifest.permission.CAMERA);
                List<String> listPermissionsNeeded = new ArrayList<>();

                if (camera != PackageManager.PERMISSION_GRANTED)
                    listPermissionsNeeded.add(Manifest.permission.CAMERA);

                if (!listPermissionsNeeded.isEmpty()){
                    ActivityCompat.requestPermissions(ChangeImagesActivity.this,listPermissionsNeeded.toArray
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

        int camera = ContextCompat.checkSelfPermission(ChangeImagesActivity.this, Manifest.permission.CAMERA);
        int store1 = ContextCompat.checkSelfPermission(ChangeImagesActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        //     int store2 = ContextCompat.checkSelfPermission(ChangeImagesActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        ArrayList<String> listPermissionsNeeded = new ArrayList<>();
        if (camera != PackageManager.PERMISSION_GRANTED){
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
            ActivityCompat.requestPermissions(ChangeImagesActivity.this,listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), 1); }

        if (store1 != PackageManager.PERMISSION_GRANTED){
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            ActivityCompat.requestPermissions(ChangeImagesActivity.this,listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), 2); }

        startActivityForResult(cameraIntent, requestCode);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){

            if(requestCode == 1){

                Toast.makeText(ChangeImagesActivity.this, "جاري تحميل الصورة...", Toast.LENGTH_SHORT).show();

                File imgFile = new File(pictureImagePath);
                final Bitmap imageBitmap = decodeFile(imgFile);

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(ChangeImagesActivity.this.getContentResolver(), imageBitmap, "Title", null);

                Uri uri = Uri.parse(path);

                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                final StorageReference ImageName =  storageReference.child("imageRequestDriver")
                        .child(System.currentTimeMillis()+"").child("driverLicense");


                checkBoxDriverL.setChecked(false);
                checkBoxDriverL.setText("الرجاء الإنتظار...");
                ImageName.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                DownloadUrl = uri.toString();
                                checkBoxDriverL.setText("تم الرفع");
                                checkBoxDriverL.setChecked(true);
                                Map<String,Object> map = new HashMap<>();
                                map.put("driverLicense",uri.toString());
                                if (from.equals("1")){
                                    FirebaseFirestore.getInstance().collection("Users")
                                            .document(UserInfo_sharedPreference.getUser(ChangeImagesActivity.this).uid)
                                            .update(map);
                                    MyCarsActivity.user.driverLicense = uri.toString();
                                    UserInfo_sharedPreference.setInfo(ChangeImagesActivity.this, MyCarsActivity.user);
                                    FirebaseFirestore.getInstance().collection("DriverRequestsAccount")
                                            .whereEqualTo("idUser",UserInfo_sharedPreference.getUser(ChangeImagesActivity.this).uid)
                                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            queryDocumentSnapshots.getDocuments().get(0).getReference()
                                                    .update(map);
                                        }
                                    });
                                }else {
                                    FirebaseFirestore.getInstance().collection("OtherCars")
                                            .document(MyCarsActivity.requestAccountModel._id)
                                            .update(map);
                                }
                                Toast.makeText(ChangeImagesActivity.this, "تم رفع الصورة بنجاح", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });

            }
            else if (requestCode == 2){
                Toast.makeText(ChangeImagesActivity.this, "جاري تحميل الصورة...", Toast.LENGTH_SHORT).show();

                File imgFile = new File(pictureImagePath);
                final Bitmap imageBitmap = decodeFile(imgFile);

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(ChangeImagesActivity.this.getContentResolver(), imageBitmap, "Title", null);

                Uri uri = Uri.parse(path);

                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                final StorageReference ImageName =  storageReference.child("imageRequestDriver")
                        .child(imaPath).child("drivingLicense");

                checkBoxCarL.setChecked(false);
                checkBoxCarL.setText("الرجاء الإنتظار...");
                ImageName.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                DownloadUrl = uri.toString();
                                checkBoxCarL.setText("تم الرفع");
                                checkBoxCarL.setChecked(true);
                                Map<String,Object> map = new HashMap<>();
                                map.put("drivingLicense",uri.toString());
                                if (from.equals("1")){
                                    FirebaseFirestore.getInstance().collection("Users")
                                            .document(UserInfo_sharedPreference.getUser(ChangeImagesActivity.this).uid)
                                            .update(map);
                                    MyCarsActivity.user.drivingLicense = uri.toString();
                                    UserInfo_sharedPreference.setInfo(ChangeImagesActivity.this, MyCarsActivity.user);
                                    FirebaseFirestore.getInstance().collection("DriverRequestsAccount")
                                            .whereEqualTo("idUser",UserInfo_sharedPreference.getUser(ChangeImagesActivity.this).uid)
                                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            queryDocumentSnapshots.getDocuments().get(0).getReference()
                                                    .update(map);
                                        }
                                    });
                                }else {
                                    FirebaseFirestore.getInstance().collection("OtherCars")
                                            .document(MyCarsActivity.requestAccountModel._id)
                                            .update(map);
                                }
                                Toast.makeText(ChangeImagesActivity.this, "تم رفع الصورة بنجاح", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });

            }
            else if (requestCode == 3){
                Toast.makeText(ChangeImagesActivity.this, "جاري تحميل الصورة...", Toast.LENGTH_SHORT).show();

                File imgFile = new File(pictureImagePath);
                final Bitmap imageBitmap = decodeFile(imgFile);

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(ChangeImagesActivity.this.getContentResolver(), imageBitmap, "Title", null);

                Uri uri = Uri.parse(path);

                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                final StorageReference ImageName =  storageReference.child("imageRequestDriver")
                        .child(imaPath).child("frontCar");

                checkBoxFrontCar.setChecked(false);
                checkBoxFrontCar.setText("الرجاء الإنتظار...");
                ImageName.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                DownloadUrl = uri.toString();
                                checkBoxFrontCar.setText("تم الرفع");
                                checkBoxFrontCar.setChecked(true);
                                Map<String,Object> map = new HashMap<>();
                                map.put("frontCar",uri.toString());
                                if (from.equals("1")){
                                    FirebaseFirestore.getInstance().collection("Users")
                                            .document(UserInfo_sharedPreference.getUser(ChangeImagesActivity.this).uid)
                                            .update(map);
                                    MyCarsActivity.user.frontCar = uri.toString();
                                    UserInfo_sharedPreference.setInfo(ChangeImagesActivity.this, MyCarsActivity.user);
                                    FirebaseFirestore.getInstance().collection("DriverRequestsAccount")
                                            .whereEqualTo("idUser",UserInfo_sharedPreference.getUser(ChangeImagesActivity.this).uid)
                                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            queryDocumentSnapshots.getDocuments().get(0).getReference()
                                                    .update(map);
                                        }
                                    });
                                }else {
                                    FirebaseFirestore.getInstance().collection("OtherCars")
                                            .document(MyCarsActivity.requestAccountModel._id)
                                            .update(map);
                                }
                                Toast.makeText(ChangeImagesActivity.this, "تم رفع الصورة بنجاح", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });

            }
            else if (requestCode==4){
                Toast.makeText(ChangeImagesActivity.this, "جاري تحميل الصورة...", Toast.LENGTH_SHORT).show();
                Uri uri = data.getData();

                try { bitmap = MediaStore.Images.Media.getBitmap(ChangeImagesActivity.this.getContentResolver(), uri); }
                catch (IOException e) {}
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 5, baos);

                String path = MediaStore.Images.Media.insertImage(ChangeImagesActivity.this.getContentResolver(), bitmap, "image"+uri.getLastPathSegment(), null);
                uri = Uri.parse(path);

                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                final StorageReference ImageName =  storageReference.child("imageRequestDriver")
                        .child(System.currentTimeMillis()+"").child("driverLicense");


                checkBoxDriverL.setChecked(false);
                checkBoxDriverL.setText("الرجاء الإنتظار...");
                ImageName.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                DownloadUrl = uri.toString();
                                checkBoxDriverL.setText("تم الرفع");
                                checkBoxDriverL.setChecked(true);
                                Map<String,Object> map = new HashMap<>();
                                map.put("driverLicense",uri.toString());
                                if (from.equals("1")){
                                    FirebaseFirestore.getInstance().collection("Users")
                                            .document(UserInfo_sharedPreference.getUser(ChangeImagesActivity.this).uid)
                                            .update(map);
                                    MyCarsActivity.user.driverLicense = uri.toString();
                                    UserInfo_sharedPreference.setInfo(ChangeImagesActivity.this, MyCarsActivity.user);
                                    FirebaseFirestore.getInstance().collection("DriverRequestsAccount")
                                            .whereEqualTo("idUser",UserInfo_sharedPreference.getUser(ChangeImagesActivity.this).uid)
                                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            queryDocumentSnapshots.getDocuments().get(0).getReference()
                                                    .update(map);
                                        }
                                    });
                                }else {
                                    FirebaseFirestore.getInstance().collection("OtherCars")
                                            .document(MyCarsActivity.requestAccountModel._id)
                                            .update(map);
                                }
                                Toast.makeText(ChangeImagesActivity.this, "تم رفع الصورة بنجاح", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });

            }
            else if (requestCode==5){
                Toast.makeText(ChangeImagesActivity.this, "جاري تحميل الصورة...", Toast.LENGTH_SHORT).show();
                Uri uri = data.getData();

                try { bitmap = MediaStore.Images.Media.getBitmap(ChangeImagesActivity.this.getContentResolver(), uri); }
                catch (IOException e) {}
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 5, baos);

                String path = MediaStore.Images.Media.insertImage(ChangeImagesActivity.this.getContentResolver(), bitmap, "image"+uri.getLastPathSegment(), null);
                uri = Uri.parse(path);

                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                final StorageReference ImageName =  storageReference.child("imageRequestDriver")
                        .child(imaPath).child("drivingLicense");

                checkBoxCarL.setChecked(false);
                checkBoxCarL.setText("الرجاء الإنتظار...");
                ImageName.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                DownloadUrl = uri.toString();
                                checkBoxCarL.setText("تم الرفع");
                                checkBoxCarL.setChecked(true);
                                Map<String,Object> map = new HashMap<>();
                                map.put("drivingLicense",uri.toString());
                                if (from.equals("1")){
                                    FirebaseFirestore.getInstance().collection("Users")
                                            .document(UserInfo_sharedPreference.getUser(ChangeImagesActivity.this).uid)
                                            .update(map);
                                    MyCarsActivity.user.drivingLicense = uri.toString();
                                    UserInfo_sharedPreference.setInfo(ChangeImagesActivity.this, MyCarsActivity.user);
                                    FirebaseFirestore.getInstance().collection("DriverRequestsAccount")
                                            .whereEqualTo("idUser",UserInfo_sharedPreference.getUser(ChangeImagesActivity.this).uid)
                                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            queryDocumentSnapshots.getDocuments().get(0).getReference()
                                                    .update(map);
                                        }
                                    });
                                }else {
                                    FirebaseFirestore.getInstance().collection("OtherCars")
                                            .document(MyCarsActivity.requestAccountModel._id)
                                            .update(map);
                                }
                                Toast.makeText(ChangeImagesActivity.this, "تم رفع الصورة بنجاح", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });
            }
            else if (requestCode==6){
                Toast.makeText(ChangeImagesActivity.this, "جاري تحميل الصورة...", Toast.LENGTH_SHORT).show();
                Uri uri = data.getData();

                try { bitmap = MediaStore.Images.Media.getBitmap(ChangeImagesActivity.this.getContentResolver(), uri); }
                catch (IOException e) {}
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 5, baos);

                String path = MediaStore.Images.Media.insertImage(ChangeImagesActivity.this.getContentResolver(), bitmap, "image"+uri.getLastPathSegment(), null);
                uri = Uri.parse(path);

                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                final StorageReference ImageName =  storageReference.child("imageRequestDriver")
                        .child(imaPath).child("frontCar");

                checkBoxFrontCar.setChecked(false);
                checkBoxFrontCar.setText("الرجاء الإنتظار...");
                ImageName.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                DownloadUrl = uri.toString();
                                checkBoxFrontCar.setText("تم الرفع");
                                checkBoxFrontCar.setChecked(true);
                                Map<String,Object> map = new HashMap<>();
                                map.put("frontCar",uri.toString());
                                if (from.equals("1")){
                                    FirebaseFirestore.getInstance().collection("Users")
                                            .document(UserInfo_sharedPreference.getUser(ChangeImagesActivity.this).uid)
                                            .update(map);
                                    MyCarsActivity.user.frontCar = uri.toString();
                                    UserInfo_sharedPreference.setInfo(ChangeImagesActivity.this, MyCarsActivity.user);
                                    FirebaseFirestore.getInstance().collection("DriverRequestsAccount")
                                            .whereEqualTo("idUser",UserInfo_sharedPreference.getUser(ChangeImagesActivity.this).uid)
                                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            queryDocumentSnapshots.getDocuments().get(0).getReference()
                                                    .update(map);
                                        }
                                    });
                                }else {
                                    FirebaseFirestore.getInstance().collection("OtherCars")
                                            .document(MyCarsActivity.requestAccountModel._id)
                                            .update(map);
                                }
                                Toast.makeText(ChangeImagesActivity.this, "تم رفع الصورة بنجاح", Toast.LENGTH_SHORT).show();

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

    public void close(View view) {

        ChangeImagesActivity.this.finish();

    }

//    public void onClickNext(View view) {
//        if (!checkBoxDriverL.isChecked()){
//            Toast.makeText(ChangeImagesActivity.this, "الرجاء تحميل صورة رخصة القيادة!", Toast.LENGTH_SHORT).show();
//        }else if (!checkBoxCarL.isChecked()){
//            Toast.makeText(ChangeImagesActivity.this, "الرجاء تحميل صورة رخصة المركبة!", Toast.LENGTH_SHORT).show();
//        }else if (!checkBoxFrontCar.isChecked()){
//            Toast.makeText(ChangeImagesActivity.this, "الرجاء تحميل صورة امامية للمركبة!", Toast.LENGTH_SHORT).show();
//        }else {
//            finish();
//        }
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (!checkBoxDriverL.isChecked()){
//            Toast.makeText(ChangeImagesActivity.this, "الرجاء تحميل صورة رخصة القيادة!", Toast.LENGTH_SHORT).show();
//        }else if (!checkBoxCarL.isChecked()){
//            Toast.makeText(ChangeImagesActivity.this, "الرجاء تحميل صورة رخصة المركبة!", Toast.LENGTH_SHORT).show();
//        }else if (!checkBoxFrontCar.isChecked()){
//            Toast.makeText(ChangeImagesActivity.this, "الرجاء تحميل صورة امامية للمركبة!", Toast.LENGTH_SHORT).show();
//        }else {
//            finish();
//        }
//    }
}