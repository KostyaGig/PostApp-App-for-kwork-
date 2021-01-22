package ru.kostya.postforkowrk.viewmodles;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ru.kostya.postforkowrk.models.User;
import ru.kostya.postforkowrk.repositories.MainRepositoryImpl;

public class MainViewModel extends ViewModel {

    private MainRepositoryImpl repository;

    private MutableLiveData<User> liveDataUser = new MutableLiveData<>();

    public MainViewModel(){
        repository = new MainRepositoryImpl();
    }

    public FirebaseUser getCurrentUser(){
        return repository.getCurrentUser();
    }

    //Получить юзера,который зашел
    public void getSignedUser(String uId){
        DatabaseReference currentUserReference = repository.getCurrentUserReference().child(uId);

        currentUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                liveDataUser.setValue(currentUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public MutableLiveData<User> getLiveDataUser() {
        return liveDataUser;
    }
}
