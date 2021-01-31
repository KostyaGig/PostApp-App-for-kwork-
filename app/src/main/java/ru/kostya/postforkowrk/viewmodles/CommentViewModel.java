package ru.kostya.postforkowrk.viewmodles;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import ru.kostya.postforkowrk.R;
import ru.kostya.postforkowrk.constans.Firebase;
import ru.kostya.postforkowrk.models.User;
import ru.kostya.postforkowrk.repositories.CommentRepositoryImpl;
import ru.kostya.postforkowrk.view.comments.CommentActivity;

public class CommentViewModel extends ViewModel {

    private CommentRepositoryImpl repository;

    public CommentViewModel() {
        repository = new CommentRepositoryImpl();
    }

    //Для получения текущего юзера
    private MutableLiveData<User> user = new MutableLiveData<>();

    //Для получения фото поста на который нажали
    private MutableLiveData<String> photoCurrentPost =  new MutableLiveData<>();

    //Для получения названия поста на который нажали
    private MutableLiveData<String> titleCurrentPost = new MutableLiveData<>();

    //Для отслеживания добавления коментария под постом
    private MutableLiveData<String> commentResult = new MutableLiveData<>();

    public void getUser(String uId){
        DatabaseReference currentUserReference = repository.getUserReference().child(uId);

        currentUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);

                if (currentUser != null){
                    user.setValue(currentUser);
                } else {
                    //Если не удалось получиь юзера отпраляем null
                    user.setValue(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Если не удалось получиь юзера отпраляем null
                user.setValue(null);
            }
        });

    }

    public void getPhotoCurrentPost(String postKey){
        DatabaseReference postPhotoReference = repository.getPostReference().child(postKey).child(Firebase.CURRENT_POST_IMAGE_URL);

        postPhotoReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String imageUrl = dataSnapshot.getValue(String.class);

                if (imageUrl != null){
                    photoCurrentPost.setValue(imageUrl);
                } else {
                    photoCurrentPost.setValue(Firebase.ERROR_GET_POST_IMAGE_URL);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                photoCurrentPost.setValue(Firebase.ERROR_GET_POST_IMAGE_URL);
            }
        });

    }

    public void getTitleCurrentPost(String postKey){

        DatabaseReference postPhotoReference = repository.getPostReference().child(postKey).child(Firebase.CURRENT_POST_TITLE);

        postPhotoReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String titlePost = dataSnapshot.getValue(String.class);

                if (titlePost != null){
                    titleCurrentPost.setValue(titlePost);
                } else {
                    titleCurrentPost.setValue(Firebase.ERROR_GET_POST_IMAGE_URL);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                titleCurrentPost.setValue(Firebase.ERROR_GET_POST_IMAGE_URL);
            }
        });
    }

    public void postComment(String postKey,String commentText,String receiverName,String receiverImageUrl){

        final String randomKey = repository.getCurrentUser().getUid() + System.currentTimeMillis();
        final DatabaseReference commentReference = repository.getPostReference().child(postKey).child(Firebase.COMMENT_REF).child(randomKey);

        HashMap<String ,Object> map = new HashMap<>();
        map.put(Firebase.RECEIVER_UID,repository.getCurrentUser().getUid());
        map.put(Firebase.COMMENT_TEXT,commentText);
        map.put(Firebase.RECEIVER_NAME,receiverName);
        map.put(Firebase.RECEIVER_PROFILE_IMAGE_URL,receiverImageUrl);

        Log.d("CurrentPost", "user image url ->  " + receiverImageUrl);

        commentReference.updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    commentResult.setValue(Firebase.SUCCESS_ADD_COMMENT);
                } else {
                    commentResult.setValue(Firebase.ERROR_ADD_COMMENT);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                commentReference.setValue(Firebase.ERROR_ADD_COMMENT);
            }
        });

    }

    public MutableLiveData<User> getUserData() {
        return user;
    }

    public MutableLiveData<String> getPhotoCurrentPost() {
        return photoCurrentPost;
    }

    public MutableLiveData<String> getTitleCurrentPost() {
        return titleCurrentPost;
    }

    public MutableLiveData<String> getCommentResult() {
        return commentResult;
    }



    //TODO Доделать корректно экран входа
    //TODO доделать дизайн
}
