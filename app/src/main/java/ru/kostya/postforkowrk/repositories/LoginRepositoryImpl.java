package ru.kostya.postforkowrk.repositories;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ru.kostya.postforkowrk.constans.Firebase;
import ru.kostya.postforkowrk.view.interfaces.LoginI;

public class LoginRepositoryImpl implements LoginI {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();

    @Override
    public FirebaseAuth signInAccountWithEmailPassword() {
        return mAuth;
    }

    @Override
    public FirebaseUser getUsers() {
        return currentUser;
    }
}
