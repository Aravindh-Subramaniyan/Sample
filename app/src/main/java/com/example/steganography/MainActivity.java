package com.example.steganography;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;


public class MainActivity extends AppCompatActivity {

    ImageView uploadPicIV;
    EditText uploadPicET;
    final int Image_Req=71;
    Uri ImageLocationPath;

    StorageReference objectStorageReference;
    FirebaseFirestore objectFirebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            uploadPicIV=findViewById(R.id.imageId);
            uploadPicET=findViewById(R.id.imageNameET);
            objectStorageReference= FirebaseStorage.getInstance().getReference("ImageFolder");
            objectFirebaseFirestore=FirebaseFirestore.getInstance();
        }
        catch (Exception e) {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

    }

    public void selectImage(View view){
        try{
            Intent objectIntent= new Intent();
            objectIntent.setType("image/*");
            objectIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(objectIntent,Image_Req);
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if(requestCode == Image_Req && resultCode == RESULT_OK && data != null && data.getData()!=null)
            {
                ImageLocationPath=data.getData();
                Bitmap ObjectBitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),ImageLocationPath);
                uploadPicIV.setImageBitmap(ObjectBitmap);
            }
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadImage(View view){
        try{
            if(!uploadPicET.getText().toString().isEmpty() && ImageLocationPath!=null){
                 String nameOfImage=uploadPicET.getText().toString()+"."+getExtension(ImageLocationPath);
                 StorageReference imageRef=objectStorageReference.child(nameOfImage);
                 UploadTask objectUploadTask= imageRef.putFile(ImageLocationPath);
                 objectUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                     @Override
                     public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                         if(!task.isSuccessful())
                         {
                             throw task.getException();
                         }
                         return imageRef.getDownloadUrl();
                     }
                 }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                     @Override
                     public void onComplete(@NonNull Task<Uri> task) {
                         if(task.isSuccessful()){
                             Map<String,String> objectMap=new HashMap<>();
                             objectMap.put("url",task.getResult().toString());
                             objectFirebaseFirestore.collection("Links").document(uploadPicET.getText().toString()).set(objectMap)
                                     .addOnSuccessListener(new OnSuccessListener<Void>() {
                                         @Override
                                         public void onSuccess(Void aVoid) {
                                             Toast.makeText(MainActivity.this,"Image is uploaded",Toast.LENGTH_SHORT).show();
                                         }
                                     })
                                     .addOnFailureListener(new OnFailureListener() {
                                         @Override
                                         public void onFailure(@NonNull Exception e) {
                                             Toast.makeText(MainActivity.this,"Failed to upload image",Toast.LENGTH_SHORT).show();
                                         }
                                     });
                             }
                         else{
                             Toast.makeText(MainActivity.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                         }
                     }
                 });
            }
            else{
                Toast.makeText(this,"Please provide name for the image ",Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    private String getExtension(Uri uri){
        try{
            ContentResolver objectContenResolver=getContentResolver();
            MimeTypeMap objectMimeTypeMap=MimeTypeMap.getSingleton();
            return objectMimeTypeMap.getExtensionFromMimeType(objectContenResolver.getType(uri));
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        return null;
    }
    

    public void movetoSecActivity(View v){
        try{
            startActivity(new Intent(this,ShowImageActivity.class));
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

    }
}
