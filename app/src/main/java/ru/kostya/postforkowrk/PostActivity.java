package ru.kostya.postforkowrk;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileReader;

import ru.kostya.postforkowrk.constans.Firebase;
import ru.kostya.postforkowrk.viewmodles.MainViewModel;

public class PostActivity extends AppCompatActivity implements View.OnClickListener{

    public static final int REQUEST_IMAGE = 1;

    // for get nameUser and imageUser
    private String nameUser;
    private String imageUrlUser;

    private Toolbar toolbar;

    private FloatingActionButton fabSave,fabSelectImage;
    private EditText fieldTitle,fieldText;
    private ImageView postImage;
    private Uri postImageUri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        init();

        if (getIntent().hasExtra(Firebase.PUBLISHER_NAME)){
            nameUser = getIntent().getStringExtra(Firebase.PUBLISHER_NAME);
            imageUrlUser = getIntent().getStringExtra(Firebase.PUBLISHER_IMAGE_URL);

            Log.d("CurrentPost","PostActivity getIntent nameUser --> " + nameUser + " image ---> " + imageUrlUser);
        }


        fabSave.setOnClickListener(this);
        fabSelectImage.setOnClickListener(this);
    }

    private void init() {

        //setUpToolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Добавить запись");

        fabSave = findViewById(R.id.fab_save);
        fabSelectImage = findViewById(R.id.fab_select_image);

        postImage = findViewById(R.id.postImage);
        fieldTitle = findViewById(R.id.field_title);
        fieldText = findViewById(R.id.field_text);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.fab_save:

                //Проверка на пустые поля и фотографию
                verifyField();
                break;

            case R.id.fab_select_image:

                Intent selectImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
                selectImageIntent.setType("image/*");
                startActivityForResult(selectImageIntent, REQUEST_IMAGE);
                break;
        }

    }

    private void verifyField() {

        String title = fieldTitle.getText().toString().trim();
        String text = fieldText.getText().toString().trim();


        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(text)){
            Toast.makeText(this, "Введите название и текст записи!", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(postImageUri == null){
            Toast.makeText(this, "Выберите фотографию для записи!", Toast.LENGTH_SHORT).show();
            return;
        }

        //for mainactivity придет в onactivity result in mainactivity
        Intent responseIntent = new Intent();

        responseIntent.putExtra(Firebase.PUBLISHER_NAME,nameUser);
        responseIntent.putExtra(Firebase.PUBLISHER_IMAGE_URL,imageUrlUser);
        responseIntent.putExtra(Firebase.TITLE_POST,title);
        responseIntent.putExtra(Firebase.TEXT_POST,text);
        responseIntent.putExtra(Firebase.IMAGE_URL_POST,postImageUri);
        responseIntent.putExtra(Firebase.EXTENSION_IMAGE_URL_POST,getFileExtension(postImageUri));


        setResult(RESULT_OK,responseIntent);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK &&  data.getData() != null){
            postImageUri = data.getData();

            Log.d("CurrentPost","image uri onactresult postactivity --> " + postImageUri.toString());
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),postImageUri);
                postImage.setImageBitmap(bitmap);
            }catch (Exception e){
                Log.d("CurrentUser","failed set bitmap profile image in POSTACTIVITY");
                e.printStackTrace();
            }

        } else {
            Toast.makeText(this, "Выбор фотографии отменен", Toast.LENGTH_SHORT).show();
        }

    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}