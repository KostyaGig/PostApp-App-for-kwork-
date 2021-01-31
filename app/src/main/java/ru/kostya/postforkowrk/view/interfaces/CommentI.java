package ru.kostya.postforkowrk.view.interfaces;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public interface CommentI {

    DatabaseReference getPostReference();


    DatabaseReference getUserReference();

    FirebaseUser getCurrentUser();

}
