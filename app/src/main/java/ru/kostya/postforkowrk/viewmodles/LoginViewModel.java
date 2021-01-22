package ru.kostya.postforkowrk.viewmodles;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ru.kostya.postforkowrk.constans.Firebase;
import ru.kostya.postforkowrk.models.User;
import ru.kostya.postforkowrk.repositories.LoginRepositoryImpl;
import ru.kostya.postforkowrk.repositories.RegisterRepositoryImpl;

public class LoginViewModel extends ViewModel {

    private LoginRepositoryImpl repository;
    private MutableLiveData<String> resultData = new MutableLiveData<>();

    public LoginViewModel(){
        repository = new LoginRepositoryImpl();
    }

    public void signInAccountWithEmailPassword(String email,String password){
        FirebaseAuth mAuth = repository.signInAccountWithEmailPassword();

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    resultData.setValue(Firebase.SUCCESS_SIGN_IN_USER);
                } else {
                    resultData.setValue(Firebase.ERROR_SIGN_IN_USER);
                }
            }
        });

    }

    public MutableLiveData<String> getResultData() {
        return resultData;
    }

    public void sendUserToDatabase(User user){
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(Firebase.USER_REF);
        final String uId = repository.getUsers().getUid();

        userRef.child(uId).setValue(user);
    }

}
