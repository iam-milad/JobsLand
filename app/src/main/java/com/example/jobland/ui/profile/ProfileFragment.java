package com.example.jobland.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.jobland.HomeActivity;
import com.example.jobland.R;
import com.example.jobland.RegisterUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private ImageView myProfileImage;
    private String photoURL;

    private TextInputLayout fullName;
    private TextInputLayout phoneNum;
    private TextInputLayout street;
    private TextInputLayout city;
    private TextInputLayout postCode;
    private TextInputLayout country;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userId;
    private StorageReference storageReference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
//        final TextView textView = root.findViewById(R.id.text_gallery);
//        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        myProfileImage = root.findViewById(R.id.profileImage2);
        fullName = root.findViewById(R.id.profileFullName);
        phoneNum = root.findViewById(R.id.profilePhoneNumber);
        street = root.findViewById(R.id.profileStreet);
        city = root.findViewById(R.id.profileCity);
        postCode = root.findViewById(R.id.profilePostCode);
        country = root.findViewById(R.id.profileCountry);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();

//        StorageReference profileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
//        profileRef.getDownloadUrl().addOnSuccessListener(uri -> {
//            Glide.with(this).load(uri).into(myProfileImage);
//        });

//        DocumentReference documentReference = fStore.collection("users").document(userId);
//        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
//                fullName.getEditText().setText(documentSnapshot.getString("uName"));
//                phoneNum.getEditText().setText(documentSnapshot.getString("uEmail"));
//                photoURL = documentSnapshot.getString("uPhoto");
//                setProfileImage(photoURL);
//            }
//        });

        final DocumentReference docRef = fStore.collection("users").document(userId);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    System.out.println("Listen Failed");
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    fullName.getEditText().setText(snapshot.getString("uName"));
                    phoneNum.getEditText().setText(snapshot.getString("uPhoneNum"));
                    street.getEditText().setText(snapshot.getString("uStreet"));
                    city.getEditText().setText(snapshot.getString("uCity"));
                    postCode.getEditText().setText(snapshot.getString("uPostalCode"));
                    country.getEditText().setText(snapshot.getString("uCountry"));


                    photoURL = snapshot.getString("uPhoto");
                    setProfileImage(photoURL);
                } else {
                    System.out.println("Current data: null");
                }
            }
        });

        myProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

        TextView updateButton = (Button) root.findViewById(R.id.updateBtn);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(v);
            }
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();

                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri){
        Map<String, Object> note = new HashMap<>();
        final StorageReference fileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    note.put("uPhoto", uri.toString());
                    fStore.collection("users").document(userId).set(note, SetOptions.merge());
                    setProfileImage(uri.toString());
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(),"Failed", Toast.LENGTH_SHORT).show();
        });
    }

    private void setProfileImage(String imageURL){
        if(isAdded()){
            Glide.with(this).load(imageURL).into(myProfileImage);
        }
    }

    private void updateProfile(View v){
        String userFullName = fullName.getEditText().getText().toString().trim();
        String userPhoneNum = phoneNum.getEditText().getText().toString().trim();
        String userStreet = street.getEditText().getText().toString().trim();
        String userCity = city.getEditText().getText().toString().trim();
        String userPostCode = postCode.getEditText().getText().toString().trim();
        String userCountry = country.getEditText().getText().toString().trim();

        if(userFullName.isEmpty()){
            fullName.setError("Full name is required");
            fullName.requestFocus();
            return;
        }else {
            fullName.setErrorEnabled(false);
            fullName.clearFocus();
        }


        if(userPhoneNum.isEmpty()){
            phoneNum.setError("Phone Number is required");
            phoneNum.requestFocus();
            return;
        }else {
            phoneNum.setErrorEnabled(false);
            phoneNum.clearFocus();
        }

        if(userStreet.isEmpty()){
            street.setError("Street is required");
            street.requestFocus();
            return;
        }else {
            street.setErrorEnabled(false);
            street.clearFocus();
        }

        if(userCity.isEmpty()){
            city.setError("City is required");
            city.requestFocus();
            return;
        } else {
            city.setErrorEnabled(false);
            city.clearFocus();
        }

        if(userPostCode.isEmpty()){
            postCode.setError("Postal code is required");
            postCode.requestFocus();
            return;
        } else {
            postCode.setErrorEnabled(false);
            postCode.clearFocus();
        }

        if(userCountry.isEmpty()){
            country.setError("Country is required");
            country.requestFocus();
            return;
        } else {
            country.setErrorEnabled(false);
            country.clearFocus();
        }

        Map<String, Object> profileData = new HashMap<>();
        profileData.put("uName", userFullName);
        profileData.put("uPhoneNum", userPhoneNum);
        profileData.put("uStreet", userStreet);
        profileData.put("uPostalCode", userPostCode);
        profileData.put("uCity", userCity);
        profileData.put("uCountry", userCountry);

        fStore.collection("users").document(userId).set(profileData, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Snackbar snackbar = Snackbar.make(v,"Profile Details Updated", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    Snackbar snackbar = Snackbar.make(v,"Failed to Update Profile Details", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });

    }
}