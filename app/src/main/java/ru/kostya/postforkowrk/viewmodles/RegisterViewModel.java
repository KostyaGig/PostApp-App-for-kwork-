package ru.kostya.postforkowrk.viewmodles;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import ru.kostya.postforkowrk.constans.Firebase;
import ru.kostya.postforkowrk.models.User;
import ru.kostya.postforkowrk.repositories.RegisterRepositoryImpl;
import ru.kostya.postforkowrk.view.interfaces.RegisterI;

public class RegisterViewModel extends ViewModel {

    private RegisterRepositoryImpl repository;
    private MutableLiveData<String> resultData = new MutableLiveData<>();
    private MutableLiveData<User> regUser = new MutableLiveData<>();

    //для проверки юзера в realtimedb
    private MutableLiveData<String> existingUserInDatabase = new MutableLiveData<>();

    public RegisterViewModel(){
        repository = new RegisterRepositoryImpl();
    }

    public void createAccountWithEmailPassword(final String name, final String email, final String password){
        FirebaseAuth mAuth = repository.createAccountWithEmailPassword();

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    User user = new User(name, email, password,"null");
                    resultData.setValue(Firebase.SUCCESS_REGISTER_USER);
                    regUser.setValue(user);
                } else {
                    resultData.setValue(Firebase.ERROR_CREATE_ACCOUNT);
                }
            }
        });

    }

    public MutableLiveData<String> getResultData() {
        return resultData;
    }
    public MutableLiveData<User> getRegUser() {
        return regUser;
    }

    public void verifyUserInRealtimeDatabase(String uId) {
       DatabaseReference userRef = repository.getUserReference().child(uId);

       userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    existingUserInDatabase.setValue(Firebase.USER_EXISTING);
                } else {
                    existingUserInDatabase.setValue(Firebase.USER_NOT_EXISTING);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public MutableLiveData<String> getExistingUserInDatabase() {
        return existingUserInDatabase;
    }
}
