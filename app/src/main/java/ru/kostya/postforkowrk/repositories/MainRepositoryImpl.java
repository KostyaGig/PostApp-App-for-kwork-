package ru.kostya.postforkowrk.repositories;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ru.kostya.postforkowrk.constans.Firebase;
import ru.kostya.postforkowrk.view.interfaces.MainI;

public class MainRepositoryImpl implements MainI {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    @Override
    public DatabaseReference getCurrentUserReference() {
        DatabaseReference currentUserReference = FirebaseDatabase.getInstance().getReference(Firebase.USER_REF);

        return currentUserReference;
    }

}
