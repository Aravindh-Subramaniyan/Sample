package com.example.steganography;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ShowImageActivity extends AppCompatActivity {

    EditText nameofImageET;
    ImageView downloadIV;
    FirebaseFirestore objectFirebaseFireStore;
    DocumentReference objectDocumentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image2);

        try{
             nameofImageET = findViewById(R.id.downloadImageNameET);
             downloadIV=findViewById(R.id.downloadImage);
             objectFirebaseFireStore= FirebaseFirestore.getInstance();
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    public void downloadImage(View v){
        try{
            if(!nameofImageET.getText().toString().isEmpty())
            {
               objectDocumentReference=objectFirebaseFireStore.collection("Links").document(nameofImageET.getText().toString());
               objectDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                   @Override
                   public void onSuccess(DocumentSnapshot documentSnapshot) {
                      String linkOfImage=documentSnapshot.getString("url");
                       Glide.with(ShowImageActivity.this)
                               .load(linkOfImage)
                               .into(downloadIV);
                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Toast.makeText(ShowImageActivity.this,"Fails to get image",Toast.LENGTH_SHORT).show();
                   }
               });
            }
            else{
                Toast.makeText(this,"Please enter an image",Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

    }
}