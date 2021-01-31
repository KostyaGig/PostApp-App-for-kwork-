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

import ru.kostya.postforkowrk.view.main.MainActivity;
import ru.kostya.postforkowrk.R;
import ru.kostya.postforkowrk.constans.Firebase;
import ru.kostya.postforkowrk.models.User;
import ru.kostya.postforkowrk.viewmodles.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel viewModel;
    private Observer<String> observer;

    private EditText fieldEmail,fieldPassword;
    private Button signInBtn;

    private String email;
    private String password;

    private TextView registerAccountTv;
    private User currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInAccount();
            }
        });
        registerAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });

        if (getIntent().hasExtra(Firebase.NAME_USER)){

            String name = getIntent().getStringExtra(Firebase.NAME_USER);
            String email = getIntent().getStringExtra(Firebase.EMAIL_USER);
            String password = getIntent().getStringExtra(Firebase.PASSWORD_USER);
            String imageUrl = getIntent().getStringExtra(Firebase.IMAGE_URL_USER);

            Log.d("CurrentUser","name -> " + name);
            currentUser = new User(name,email,password,imageUrl);
        }

                observer = new Observer<String>() {
            @Override
            public void onChanged(String result) {

                switch (result){
                    case Firebase.SUCCESS_SIGN_IN_USER:

                        if (getIntent().hasExtra(Firebase.NAME_USER)) {
                            //Когда с регистрации попадаем на экран входа и входим
                            //Мы через putextra jтправляем данные которые ввел в поля пр  регистрации юзер,а именно имя,email,password
                            //Когда мы сами нажимаем на войти у нас будет getIntent == null и просто будем брать email и password из полей при входе,
                            // а имя будем ставить по умолчанию его сможет изменить пользователь при обновлении профиля
                            viewModel.sendUserToDatabase(currentUser);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            Toast.makeText(LoginActivity.this, "Вход в аккаунт прошел успешно!", Toast.LENGTH_SHORT).show();
                        } else {
                            //Если getIntent == null и мы не можем получить имя с registeractivity email и password  возьмем с полей field email и fieldPassword данной активности,имя поставим по умолчанию,а фото и так по умлоанию null
                            User signedUser = new User("Введите свое имя",fieldEmail.getText().toString().trim(),fieldPassword.getText().toString().trim(),"null");
                            viewModel.sendUserToDatabase(signedUser);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            Toast.makeText(LoginActivity.this, "Вход в аккаунт прошел успешно!", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case Firebase.ERROR_SIGN_IN_USER:
                        Toast.makeText(LoginActivity.this, "Проверьте соединение с интернетом и пароль!", Toast.LENGTH_SHORT).show();
                        break;
                    case Firebase.ERROR_SIGN_IN_EXIST_USER:
                        startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                        break;
                }

            }
        };
    }

    private void signInAccount() {

        email = fieldEmail.getText().toString().trim();
        password = fieldPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) ||TextUtils.isEmpty(password)){
            Toast.makeText(this, "Заполните пустые поля!", Toast.LENGTH_SHORT).show();
        } else {
            viewModel.signInAccountWithEmailPassword(email,password);
        }

    }

    private void init() {
        fieldEmail = findViewById(R.id.field_email);
        fieldPassword = findViewById(R.id.field_password);

        signInBtn = findViewById(R.id.sign_in_btn);
        registerAccountTv = findViewById(R.id.reg_account_tv);

        viewModel = ViewModelProviders.of(this).get(LoginViewModel.class);

    }

    @Override
    protected void onStart() {
        super.onStart();

        viewModel.getResultData().observe(this,observer);
    }
}