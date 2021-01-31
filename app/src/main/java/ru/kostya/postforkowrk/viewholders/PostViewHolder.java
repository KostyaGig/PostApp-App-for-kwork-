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
import ru.kostya.postforkowrk.models.Post;

public class PostViewHolder extends RecyclerView.ViewHolder {

   public ImageView userProfileImage,postImage,likeImage,disLikeImage,commentImage;
   public TextView userName,postTitle,postText,countLikes,countDisLikes;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);

        userProfileImage = itemView.findViewById(R.id.profileImage);
        postImage = itemView.findViewById(R.id.postImage);
        likeImage = itemView.findViewById(R.id.likeImage);
        disLikeImage = itemView.findViewById(R.id.disLikeImage);
        commentImage = itemView.findViewById(R.id.commentImage);

        userName = itemView.findViewById(R.id.publisherNameTv);
        postTitle = itemView.findViewById(R.id.titleTv);
        postText = itemView.findViewById(R.id.textTv);
        countLikes = itemView.findViewById(R.id.countLikeTv);
        countDisLikes = itemView.findViewById(R.id.countDisLikeTv);
    }

   public void bind(Post post){
       userName.setText(post.getPublisherName());
       postTitle.setText(post.getTitle());
       postText.setText(post.getText());

       Glide.with(itemView.getContext()).load(post.getPostImageUrl()).placeholder(R.drawable.ic_image_grey).into(postImage);
       Glide.with(itemView.getContext()).load(post.getPublisherImageUrl()).placeholder(R.drawable.ic_profile).into(userProfileImage);

    }

    public void setLikesButtonStatus(final String postKey) {
        final DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference(Firebase.LIKE_REF);
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        likesRef.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.child(postKey).hasChild(userId)){
                countLikes.setText((int)dataSnapshot.child(postKey).getChildrenCount() + "");
                likeImage.setImageResource(R.drawable.ic_plus_green);
            } else {
                countLikes.setText((int)dataSnapshot.child(postKey).getChildrenCount() + "");
                likeImage.setImageResource(R.drawable.ic_plus_grey);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
}

    public void setDisLikesButtonStatus(final String postKey) {
        final DatabaseReference disLikesRef = FirebaseDatabase.getInstance().getReference(Firebase.DISLIKE_REF);
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        disLikesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postKey).hasChild(userId)){
                    countDisLikes.setText((int)dataSnapshot.child(postKey).getChildrenCount() + "");
                    disLikeImage.setImageResource(R.drawable.ic_minus_red);
                } else {
                    countDisLikes.setText((int)dataSnapshot.child(postKey).getChildrenCount() + "");
                    disLikeImage.setImageResource(R.drawable.ic_minus_red);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
