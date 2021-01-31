package ru.kostya.postforkowrk.viewmodles;

import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import ru.kostya.postforkowrk.constans.Firebase;
import ru.kostya.postforkowrk.models.Post;
import ru.kostya.postforkowrk.models.User;
import ru.kostya.postforkowrk.repositories.MainRepositoryImpl;

public class MainViewModel extends ViewModel {

    private MainRepositoryImpl repository;

    public MainViewModel(){
        repository = new MainRepositoryImpl();
    }

    //for get signedUser
    private MutableLiveData<User> liveDataUser = new MutableLiveData<>();

    //Для того,чтобы знать успешно ли добавился наш пост или нет,method addPost в данном классе,ищи
    private MutableLiveData<String> pushPostResult = new MutableLiveData<>();

    //for update user profile
    private MutableLiveData<String> updateProfileData = new MutableLiveData<>();

//    for add post
    private MutableLiveData<String> addPost = new MutableLiveData<>();

    public FirebaseUser getCurrentUser(){
        return repository.getCurrentUser();
    }

    //Получить юзера,который зашел
    public void getSignedUser(String uId){
        DatabaseReference currentUserReference = repository.getCurrentUserReference().child(uId);

        Log.d("CurrentUser","uid = " + uId);
        currentUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("CurrentUser",dataSnapshot.getChildrenCount() + "");
                User currentUser = dataSnapshot.getValue(User.class);
                if (currentUser != null) {
                    Log.d("CurrentUser", currentUser.getEmail());
                    liveDataUser.setValue(currentUser);
                } else {
                    liveDataUser.setValue(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                liveDataUser.setValue(null);
            }
        });

    }

    public MutableLiveData<User> getLiveDataUser() {
        return liveDataUser;
    }


    public void updateDataUser(final String uId, final String name, final String email, final String password, Uri imageUri ,String extensionImageUri) {

        Log.d("CurrentUser","updatedATAUSER VIEWMODEL MAIN");

        Log.d("CurrentUser","extension mainviewmodel ->" + extensionImageUri);
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("ImagesProfiles/" + System.currentTimeMillis() + "." + extensionImageUri);

        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Log.d("CurrentUser","success download image");

                        // Get a URL to the uploaded content
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                if (uri != null){
                                    Log.d("CurrentUser","image url task -> " + uri.toString());

                                    DatabaseReference reference = repository.getCurrentUserReference().child(uId);

                            HashMap<String, Object> userMap = new HashMap<>();
                            userMap.put("name",name);
                            userMap.put("email",email);
                            userMap.put("password",password);
                            userMap.put("imageUrl", uri.toString());

                            reference.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        updateProfileData.setValue(Firebase.SUCCESS_UPDATE_USER_PROFILE);
                                    } else {
                                        updateProfileData.setValue(Firebase.FAILURE_UPDATE_USER_PROFILE);
                                        Log.d("CurrentUser","update error " + task.getException().getMessage());
                                    }
                                }
                            });

                                } else {
                                    Log.d("CurrentUser","image url task == null");
                                }
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        updateProfileData.setValue(Firebase.FAILURE_UPDATE_USER_PROFILE);
                        Log.d("CurrentUser","failed download image " + exception.getMessage());
                    }
                });

    }

    public DatabaseReference getPostReference(){
        return repository.getPostsReference();
    }

    public void addPost(final String titlePost, final String textPost, Uri imageUrlPost, final String publisherImageUrl, final String publisherName, String extensionImageUrlPost) {


        final DatabaseReference postRef = repository.getPostsReference();

        final StorageReference reference = FirebaseStorage.getInstance().getReference().child("PostImages/" + System.currentTimeMillis() + "." + extensionImageUrlPost);

        reference.putFile(imageUrlPost).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Log.d("CurrentPost","URL POST IMAGE -> " + uri.toString());

                        assert uri != null;
                        Post currentPost = new Post(titlePost,textPost,uri.toString(),publisherImageUrl,publisherName);

                        String key = postRef.push().getKey();

                        postRef.child(key).setValue(currentPost).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Log.d("CurrentPost","Success post push!");
                                    addPost.setValue(Firebase.SUCCESS_ADD_POST);
                                } else {
                                    Log.d("CurrentPost","Error push post  " + task.getException());
                                    addPost.setValue(Firebase.ERROR_ADD_POST);
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("CurrentPost","ONFAILURE Error push post :" + e.getMessage());
                                addPost.setValue(Firebase.ERROR_ADD_POST);
                            }
                        });
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("CurrentPost","ONFAILURE Error Upload image add POST:" + e.getMessage());
                addPost.setValue(Firebase.ERROR_ADD_POST);
            }
        });


    }

    public MutableLiveData<String> getUpdateProfileData() {
        return updateProfileData;
    }

    public MutableLiveData<String> getAddPost() {
        return addPost;
    }

    public void updateNullDataUser(FirebaseUser currentUser, String name, String email, String password,String imageUrl) {

        DatabaseReference reference = repository.getCurrentUserReference().child(currentUser.getUid());

                                    HashMap<String, Object> userMap = new HashMap<>();
                                    userMap.put("name",name);
                                    userMap.put("email",email);
                                    userMap.put("password",password);
                                    userMap.put("imageUrl", imageUrl);

                                    reference.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                updateProfileData.setValue(Firebase.SUCCESS_UPDATE_USER_PROFILE);
                                            } else {
                                                updateProfileData.setValue(Firebase.FAILURE_UPDATE_USER_PROFILE);
                                                Log.d("CurrentUser","update error " + task.getException().getMessage());
                                            }
                                        }
                                    })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        updateProfileData.setValue(Firebase.FAILURE_UPDATE_USER_PROFILE);
                        Log.d("CurrentUser","failed download image " + exception.getMessage());
                    }
                });
    }


}
