package ru.kostya.postforkowrk.repositories;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ru.kostya.postforkowrk.constans.Firebase;
import ru.kostya.postforkowrk.view.interfaces.CommentI;

public class CommentRepositoryImpl implements CommentI {


    @Override
    public DatabaseReference getPostReference() {
        return FirebaseDatabase.getInstance().getReference(Firebase.POST_REF);
    }


    @Override
    public DatabaseReference getUserReference() {
        return FirebaseDatabase.getInstance().getReference(Firebase.USER_REF);
    }

    @Override
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

}
