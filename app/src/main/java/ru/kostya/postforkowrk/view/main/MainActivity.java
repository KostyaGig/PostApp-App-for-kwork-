package ru.kostya.postforkowrk.view.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Objects;

import ru.kostya.postforkowrk.PostActivity;
import ru.kostya.postforkowrk.R;
import ru.kostya.postforkowrk.view.comments.CommentActivity;
import ru.kostya.postforkowrk.constans.Firebase;
import ru.kostya.postforkowrk.models.Post;
import ru.kostya.postforkowrk.models.User;
import ru.kostya.postforkowrk.view.auth.RegisterActivity;
import ru.kostya.postforkowrk.view.profile.ProfileActivity;
import ru.kostya.postforkowrk.viewholders.PostViewHolder;
import ru.kostya.postforkowrk.viewmodles.MainViewModel;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_EDIT_PROFILE = 1;
    public static final int REQUEST_CODE_ADD_POST = 2;

    private Toolbar toolbar;

    private MainViewModel viewModel;

    private Observer<User> currentUserObserver;
    private Observer<String> updateUserObserver;
    private Observer<String> addPostObserver;

    private User currentUser;
    private BottomNavigationView bottomBar;

    private RecyclerView postRecView;
    private FirebaseRecyclerAdapter<Post,PostViewHolder> adapter;
    private FirebaseRecyclerOptions<Post> options;
    private boolean checkerLike = false;

    //loadingView
    private AVLoadingIndicatorView loadingView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        setUpRecyclerView();

        if (getIntent().hasExtra(Firebase.EXTRA_USER_EMAIL)){
            Toast.makeText(this, getIntent().getStringExtra(Firebase.EXTRA_USER_EMAIL), Toast.LENGTH_SHORT).show();
        }

        currentUserObserver = new Observer<User>() {
            @Override
            public void onChanged(User user) {

                if (user != null) {
                    //Уалось получить текущего юзера
                    currentUser = user;
                    Log.d("CurrentUser", "mainactivity " + currentUser.getEmail());
                } else {
                    Toast.makeText(MainActivity.this, "Ошибка при получении пользователя!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        updateUserObserver = new Observer<String>() {
            @Override
            public void onChanged(String result) {
                switch (result){

                    case Firebase.SUCCESS_UPDATE_USER_PROFILE:

                        loadingView.hide();
                        Toast.makeText(MainActivity.this, "Данные о пользователе были обновлены", Toast.LENGTH_SHORT).show();
                        break;

                    case Firebase.FAILURE_UPDATE_USER_PROFILE:
                        loadingView.hide();
                        Toast.makeText(MainActivity.this, "Ошибка!Проверьте соединение с интернетом", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        addPostObserver = new Observer<String>() {
            @Override
            public void onChanged(String result) {
                switch (result){

                    case Firebase.SUCCESS_ADD_POST:

                        loadingView.hide();
                        Toast.makeText(MainActivity.this, "Запись была добавлена", Toast.LENGTH_SHORT).show();
                        break;

                    case Firebase.ERROR_ADD_POST:
                        loadingView.hide();
                        Toast.makeText(MainActivity.this, "Ошибка!Проверьте соединение с интернетом", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.home_item:

                        return false;
                    case R.id.profile_item:
                      Intent profileActivity = new Intent(MainActivity.this, ProfileActivity.class).putExtra(Firebase.NAME_USER,currentUser.getName()).putExtra(Firebase.EMAIL_USER,currentUser.getEmail()).putExtra(Firebase.IMAGE_URL_USER,currentUser.getImageUrl()
                        );
                        startActivityForResult(profileActivity,REQUEST_EDIT_PROFILE);

                        return true;
                }

                return true;
            }
        });

    }

    private void setUpRecyclerView() {
        postRecView = findViewById(R.id.post_recview);
        postRecView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void init() {
        loadingView = findViewById(R.id.loading_view);

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Лента");
        bottomBar = findViewById(R.id.bottom_bar);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d("CurrentPost","onstart mainactivity");
        if (viewModel.getCurrentUser() == null) {
            Toast.makeText(this, "Войдите в аккаунт", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        } else {
            //Получить юзера,который вошел
            viewModel.getSignedUser(viewModel.getCurrentUser().getUid());

            //Сюда придет текущий юзер
            viewModel.getLiveDataUser().observe(this,currentUserObserver);

            //Подписка на обновление юзера
            viewModel.getUpdateProfileData().observe(this,updateUserObserver);

            //Подписка на добавление поста
            viewModel.getAddPost().observe(this,addPostObserver);

            //делаем запрос и отображаем наши посты
            final DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference(Firebase.LIKE_REF);
            final DatabaseReference disLikeRef = FirebaseDatabase.getInstance().getReference(Firebase.DISLIKE_REF);
            final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            Query query = viewModel.getPostReference();
            options = new FirebaseRecyclerOptions.Builder<Post>()
                    .setQuery(query,Post.class)
                    .build();

            adapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull PostViewHolder postViewHolder, int i, @NonNull final Post post) {
                    postViewHolder.bind(post);

                    final String postKey = getRef(i).getKey();
                    postViewHolder.setLikesButtonStatus(postKey);

                    postViewHolder.setDisLikesButtonStatus(postKey);

                    postViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });

                    postViewHolder.commentImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //Открываем активити с добавлением комментария + отправляем postkey записи на которую нажали
                            Intent commentActivity = new Intent(MainActivity.this, CommentActivity.class);
                            commentActivity.putExtra(Firebase.POST_KEY,postKey);
                            startActivity(commentActivity);
                        }
                    });

                    postViewHolder.likeImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            checkerLike = true;

                            likeRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (postKey != null) {
                                        if (checkerLike) {
                                            if (dataSnapshot.child(postKey).hasChild(userId)) {
                                                likeRef.child(postKey).child(userId).removeValue();
                                                checkerLike = false;
                                            } else {
                                                likeRef.child(postKey).child(userId).setValue(true);
                                                checkerLike = false;
                                            }
                                        }
                                    }
                                    else {
                                        Toast.makeText(MainActivity.this, "PostKey == null", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });

                    postViewHolder.disLikeImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkerLike = true;

                            disLikeRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (postKey != null) {
                                        if (checkerLike) {
                                            if (dataSnapshot.child(postKey).hasChild(userId)) {
                                                disLikeRef.child(postKey).child(userId).removeValue();
                                                checkerLike = false;
                                            } else {
                                                disLikeRef.child(postKey).child(userId).setValue(true);
                                                checkerLike = false;
                                            }
                                        }
                                    }
                                    else {
                                        Toast.makeText(MainActivity.this, "PostKey == null", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }

                @NonNull
                @Override
                public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    return new PostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item,parent,false));
                }
            };
            postRecView.setAdapter(adapter);
            adapter.startListening();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_EDIT_PROFILE && resultCode == RESULT_OK){
            loadingView.show();

            Log.d("CurrentUser","onactresult mAINACTIVITY");
            String name = data.getStringExtra(Firebase.NAME_USER);
            Uri imageUri = data.getParcelableExtra(Firebase.IMAGE_URL_USER);


            String extensionUriUser = data.getStringExtra(Firebase.EXTENSION_IMAGE_URL_USER);

            if (imageUri != null && name != null){
                viewModel.updateDataUser(viewModel.getCurrentUser().getUid(),name,currentUser.getEmail(),currentUser.getPassword(),imageUri,extensionUriUser);
            } else {
                //Если что - то пустое,то обновляем так:
                viewModel.updateNullDataUser(viewModel.getCurrentUser(),"Введите свое имя",currentUser.getEmail(),currentUser.getPassword(),"null");
            }


        } else if (requestCode == REQUEST_CODE_ADD_POST && resultCode == RESULT_OK){
            loadingView.show();
            Log.d("CurrentPost","onactivity result mainactivity POST");

            String titlePost = data.getStringExtra(Firebase.TITLE_POST);
            String textPost = data.getStringExtra(Firebase.TEXT_POST);
            Uri imageUrlPost = data.getParcelableExtra(Firebase.IMAGE_URL_POST);
            String publisherImageUrl = data.getStringExtra(Firebase.PUBLISHER_IMAGE_URL);
            String publisherName = data.getStringExtra(Firebase.PUBLISHER_NAME);
            String extensionImageUrlPost = data.getStringExtra(Firebase.EXTENSION_IMAGE_URL_POST);

            Log.d("CurrentPost","MainActivity onactivtyRESULT all data post, publisher imageUrl -> " + publisherImageUrl + " publisher-> " + publisherName + " URI POST IMAGE --:> " + imageUrlPost.toString() + " publisher name -> " + publisherName);

            viewModel.addPost(titlePost,textPost,imageUrlPost,publisherImageUrl,publisherName,extensionImageUrlPost);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.add_post_item){

            Intent intent = new Intent(MainActivity.this, PostActivity.class);
            intent.putExtra(Firebase.PUBLISHER_IMAGE_URL,currentUser.getImageUrl());
            intent.putExtra(Firebase.PUBLISHER_NAME,currentUser.getName());

            startActivityForResult(intent, REQUEST_CODE_ADD_POST);
        }

        return true;
    }

}