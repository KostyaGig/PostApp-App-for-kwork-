package ru.kostya.postforkowrk.viewholders;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ru.kostya.postforkowrk.R;
import ru.kostya.postforkowrk.constans.Firebase;
import ru.kostya.postforkowrk.models.Comment;
import ru.kostya.postforkowrk.models.Post;
import ru.kostya.postforkowrk.models.User;

public class CommentViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "CurrentPost";

   public ImageView userProfileImage,likeImage,disLikeImage;
   public TextView userName,textComment,countLikes,countDisLikes;

    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);

        userProfileImage = itemView.findViewById(R.id.profile_image);
        likeImage = itemView.findViewById(R.id.likeImage);
        disLikeImage = itemView.findViewById(R.id.disLikeImage);
        userName = itemView.findViewById(R.id.user_name_tv);
        textComment = itemView.findViewById(R.id.text_comment_tv);
        countLikes = itemView.findViewById(R.id.countLikeTv);
        countDisLikes = itemView.findViewById(R.id.countDisLikeTv);
    }

   public void bind(Comment comment){
        Glide.with(itemView.getContext()).load(comment.getUserProfileImageUrl()).placeholder(R.drawable.ic_profile).into(userProfileImage);
        userName.setText(comment.getUserName());
        textComment.setText(comment.getCommentText());
    }

    public void setLikesButtonStatus(final String commentKey) {
        final DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference(Firebase.COMMENT_LIKE_REF);
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(commentKey).hasChild(userId)){
                    countLikes.setText((int)dataSnapshot.child(commentKey).getChildrenCount() + "");
                    likeImage.setImageResource(R.drawable.ic_plus_green);
                } else {
                    countLikes.setText((int)dataSnapshot.child(commentKey).getChildrenCount() + "");
                    likeImage.setImageResource(R.drawable.ic_plus_grey);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setDisLikesButtonStatus(final String commentKey) {
        final DatabaseReference disLikesRef = FirebaseDatabase.getInstance().getReference(Firebase.COMMENT_DISLIKE_REF);
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        disLikesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(commentKey).hasChild(userId)){
                    countDisLikes.setText((int)dataSnapshot.child(commentKey).getChildrenCount() + "");
                    disLikeImage.setImageResource(R.drawable.ic_minus_red);
                } else {
                    countDisLikes.setText((int)dataSnapshot.child(commentKey).getChildrenCount() + "");
                    disLikeImage.setImageResource(R.drawable.ic_minus_red);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
