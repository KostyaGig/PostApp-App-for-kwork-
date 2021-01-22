package ru.kostya.postforkowrk.repositories;

import android.provider.ContactsContract;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ru.kostya.postforkowrk.constans.Firebase;
import ru.kostya.postforkowrk.view.interfaces.RegisterI;

public class RegisterRepositoryImpl implements RegisterI {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child(Firebase.USER_REF);

    @Override
    public FirebaseAuth createAccountWithEmailPassword() {
        return mAuth;
    }

    @Override
    public DatabaseReference getUserReference() {
        return userRef;
    }


}
