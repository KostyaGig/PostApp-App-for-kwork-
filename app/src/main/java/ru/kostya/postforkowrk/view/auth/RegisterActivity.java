package ru.kostya.postforkowrk.view.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.wang.avi.AVLoadingIndicatorView;

import ru.kostya.postforkowrk.view.main.MainActivity;
import ru.kostya.postforkowrk.R;
import ru.kostya.postforkowrk.constans.Firebase;
import ru.kostya.postforkowrk.models.User;
import ru.kostya.postforkowrk.viewmodles.RegisterViewModel;

public class RegisterActivity extends AppCompatActivity {

    private RegisterViewModel viewModel;
    private Observer<String> observer,existingUserObserver;
    private Observer<User> userObserver;


    private EditText fieldName,fieldEmail,fieldPassword;
    private Button signInBtn;
    private TextView loginAccountTv;

    //loadingView
    private AVLoadingIndicatorView loadingView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();
        loadingView.show();

        if (getIntent() == null){
            Log.d("CurrentName", "onCreate:null ");
        }

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingView.show();
                createAccount();
            }
        });
        loginAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });

        observer = new Observer<String>() {
            @Override
            public void onChanged(String result) {

                switch (result){
                    case Firebase.SUCCESS_REGISTER_USER:
                        loadingView.hide();
                        Log.d("RegisterUser","Success register");
                        break;
                    case Firebase.ERROR_CREATE_ACCOUNT:
                        loadingView.hide();
                        Toast.makeText(RegisterActivity.this, "Проверьте соединение с интернетом!", Toast.LENGTH_SHORT).show();
                        break;
                    case Firebase.ERROR_ADD_USER:
                        loadingView.hide();
                        Log.d("RegisterUser","Error add user");
                        break;
                }

            }
        };

        userObserver = new Observer<User>() {
            @Override
            public void onChanged(User user) {
                loadingView.hide();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class)
                .putExtra(Firebase.NAME_USER,user.getName()).putExtra(Firebase.EMAIL_USER,user.getEmail()).putExtra(Firebase.PASSWORD_USER,user.getPassword()).putExtra(Firebase.IMAGE_URL_USER,"null")
                );
                Toast.makeText(RegisterActivity.this, "Регистрация прошла успешно,войдите в свой аккаунт", Toast.LENGTH_SHORT).show();
            }
        };

        existingUserObserver = new Observer<String>() {
            @Override
            public void onChanged(String res) {

                switch (res){
                    case Firebase.USER_EXISTING:
                        loadingView.hide();
                        Log.d("CurrentUser","user exist register activity");
                        startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                        break;
                    case Firebase.USER_NOT_EXISTING:
                        Log.d("CurrentUser","not user exist register activity");
                        loadingView.hide();
                        Toast.makeText(RegisterActivity.this, "Войдите в аккаунт!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                        break;
                }
            }
        };

    }

    private void createAccount() {

        String name = fieldName.getText().toString().trim();
        String email = fieldEmail.getText().toString().trim();
        String password = fieldPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) ||TextUtils.isEmpty(password)){
            loadingView.hide();
            Toast.makeText(this, "Заполните пустые поля!", Toast.LENGTH_SHORT).show();
        } else {
            loadingView.show();
            viewModel.createAccountWithEmailPassword(name,email,password);
        }

    }

    private void init() {
        loadingView = findViewById(R.id.loading_view);

        fieldName = findViewById(R.id.field_name);
        fieldEmail = findViewById(R.id.field_email);
        fieldPassword = findViewById(R.id.field_password);

        viewModel = ViewModelProviders.of(this).get(RegisterViewModel.class);

        signInBtn = findViewById(R.id.sign_in_btn);
        loginAccountTv = findViewById(R.id.login_account_tv);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            Toast.makeText(this, "Зарегистрируйте аккаунт!", Toast.LENGTH_SHORT).show();
            loadingView.hide();
        } else {
            Log.d("CurrentUser","RegisterAcitivty UID -> " + FirebaseAuth.getInstance().getCurrentUser().getUid());
            viewModel.verifyUserInRealtimeDatabase(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }

        viewModel.getResultData().observe(this,observer);
        viewModel.getRegUser().observe(this,userObserver);
        viewModel.getExistingUserInDatabase().observe(this,existingUserObserver);
    }

}