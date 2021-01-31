package ru.kostya.postforkowrk.view.comments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.HashMap;

import ru.kostya.postforkowrk.R;
import ru.kostya.postforkowrk.constans.Firebase;
import ru.kostya.postforkowrk.models.Comment;
import ru.kostya.postforkowrk.models.User;
import ru.kostya.postforkowrk.view.main.MainActivity;
import ru.kostya.postforkowrk.viewholders.CommentViewHolder;
import ru.kostya.postforkowrk.viewmodles.CommentViewModel;

public class CommentActivity extends AppCompatActivity {

    private static final String TAG = "CurrentPost";

    private String postKey;

    private CommentViewModel viewModel;

    private Observer<User> currentUserObserver;
    private Observer<String> photoCurrentPostObserver;
    private Observer<String> titleCurrentPostObserver;
    private Observer<String> postCommentObserver;

    private Toolbar toolbar;
    private TextView toolbarPostTitle;
    private ImageView toolbarPostImage;

    private EditText fieldComment;
    private Button sendCommentBtn;

    private RecyclerView commentsRecView;
    private FirebaseRecyclerAdapter<Comment, CommentViewHolder> adapter;
    private FirebaseRecyclerOptions<Comment> options;

    private DatabaseReference postReference,userReference;

    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    private User currentUser;

    private boolean checkerLike = false;

    //loadingView
    private AVLoadingIndicatorView loadingView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (getIntent().hasExtra(Firebase.POST_KEY)){
            //getPostKey
            postKey = getIntent().getStringExtra(Firebase.POST_KEY);
            Log.d(TAG, "Post key -> " + postKey);
        }


        init();
        initToolbar();
        loadingView.show();

        viewModel.getUser(firebaseUser.getUid());

        viewModel.getPhotoCurrentPost(postKey);


        currentUserObserver = new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    Log.d(TAG, "Success getUser " + user.getName());
                    currentUser = user;
                } else {
                    Toast.makeText(CommentActivity.this, "При получении пользователя произошла ошибка", Toast.LENGTH_SHORT).show();
                }
            }
        };

        photoCurrentPostObserver = new Observer<String>() {
            @Override
            public void onChanged(String result) {
                //Если не ошибка делаем запрос на получения названия поста и загружаем фото поста через Glide в toolbar
                if (!result.equals(Firebase.ERROR_GET_POST_IMAGE_URL)){
                    Glide.with(CommentActivity.this).load(result).placeholder(R.mipmap.ic_launcher).into(toolbarPostImage);

                    //Делаем запрос на получение названия (title post) поста
                    viewModel.getTitleCurrentPost(postKey);
                } else {
                    Toast.makeText(CommentActivity.this, "При получении изображения записи произошла ошибка", Toast.LENGTH_SHORT).show();
                }
            }
        };

        titleCurrentPostObserver = new Observer<String>() {
            @Override
            public void onChanged(String result) {

                //Если не ошибка ,то отображаем название поста и выключаем загрузку
                if (!result.equals(Firebase.ERROR_GET_POST_TITLE)){
                    toolbar.setTitle(null);
                    toolbarPostTitle.setText(result);
                    loadingView.hide();
                } else {
                    Toast.makeText(CommentActivity.this, "При отображении названия поста произошла ошибка", Toast.LENGTH_SHORT).show();
                }
            }
        };

        postCommentObserver = new Observer<String>() {
            @Override
            public void onChanged(String result) {

                switch (result){
                    case Firebase.SUCCESS_ADD_COMMENT:
                        Log.d(TAG, "Succes addComment ");
                        break;

                    case Firebase.ERROR_ADD_COMMENT:
                        Log.d(TAG, "Error add comment ");
                        break;
                }

                loadingView.hide();
            }
        };

        sendCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });

    }

    private void postComment() {

        loadingView.show();
        if (TextUtils.isEmpty(fieldComment.getText().toString().trim())){
            Toast.makeText(this, "Напишите коментарий!", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.postComment(postKey,fieldComment.getText().toString().trim(),currentUser.getName(),currentUser.getImageUrl());
    }

    private void init() {
        loadingView = findViewById(R.id.loading_view);

        viewModel = ViewModelProviders.of(this).get(CommentViewModel.class);

        toolbarPostTitle = findViewById(R.id.toolbar_post_title);
        toolbarPostImage = findViewById(R.id.toolbar_post_image);

        fieldComment = findViewById(R.id.field_comment);
        sendCommentBtn = findViewById(R.id.send_comment_btn);

        commentsRecView = findViewById(R.id.comments_recview);
        commentsRecView.setLayoutManager(new LinearLayoutManager(this));

        postReference = FirebaseDatabase.getInstance().getReference(Firebase.POST_REF);
        userReference = FirebaseDatabase.getInstance().getReference(Firebase.USER_REF);
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //subscribe livedata
        viewModel.getUserData().observe(this,currentUserObserver);
        viewModel.getPhotoCurrentPost().observe(this,photoCurrentPostObserver);
        viewModel.getTitleCurrentPost().observe(this,titleCurrentPostObserver);
        viewModel.getCommentResult().observe(this,postCommentObserver);

        //load comment
        final String userId = firebaseUser.getUid();

        final DatabaseReference commentLikeRef = FirebaseDatabase.getInstance().getReference(Firebase.COMMENT_LIKE_REF);
        final DatabaseReference commentDislikeRef = FirebaseDatabase.getInstance().getReference(Firebase.COMMENT_DISLIKE_REF);

        Query query = postReference.child(postKey).child(Firebase.COMMENT_REF);
        options = new FirebaseRecyclerOptions.Builder<Comment>()
                .setQuery(query,Comment.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Comment, CommentViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CommentViewHolder commentViewHolder, int pos, @NonNull final Comment comment) {
                commentViewHolder.bind(comment);

                final String commentKey = getRef(pos).getKey();

                commentViewHolder.setLikesButtonStatus(commentKey);

                commentViewHolder.setDisLikesButtonStatus(commentKey);

                commentViewHolder.likeImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        checkerLike = true;

                        commentLikeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (postKey != null && commentKey != null) {
                                    if (checkerLike) {
                                        if (dataSnapshot.child(commentKey).hasChild(userId)) {
                                            commentLikeRef.child(commentKey).child(userId).removeValue();
                                            checkerLike = false;
                                        } else {
                                            commentLikeRef.child(commentKey).child(userId).setValue(true);
                                            checkerLike = false;
                                        }
                                    }
                                }
                                else {
                                    Toast.makeText(CommentActivity.this, "PostKey == null", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });

                commentViewHolder.disLikeImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkerLike = true;

                        commentDislikeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (postKey != null) {
                                    if (checkerLike) {
                                        if (dataSnapshot.child(commentKey).hasChild(userId)) {
                                            commentDislikeRef.child(commentKey).child(userId).removeValue();
                                            checkerLike = false;
                                        } else {
                                            commentDislikeRef.child(commentKey).child(userId).setValue(true);
                                            checkerLike = false;
                                        }
                                    }
                                }
                                else {
                                    Toast.makeText(CommentActivity.this, "PostKey == null", Toast.LENGTH_SHORT).show();
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
            public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new CommentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item,parent,false));
            }
        };

        commentsRecView.setAdapter(adapter);
        adapter.startListening();
    }

}