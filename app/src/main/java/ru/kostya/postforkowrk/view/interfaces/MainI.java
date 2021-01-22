package ru.kostya.postforkowrk.view.interfaces;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public interface MainI {

    FirebaseUser getCurrentUser();

    DatabaseReference getCurrentUserReference();
}
