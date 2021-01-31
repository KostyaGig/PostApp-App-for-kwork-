package ru.kostya.postforkowrk.constans;

public class Firebase {

    //for path users
    public static final String USER_REF = "Users";

    //for path posts
    public static final String POST_REF = "Posts";

    //for path likes
    public static final String LIKE_REF = "Likes";

    //for path dislikes
    public static final String DISLIKE_REF = "Dislikes";

    //for path like comment
    public static final String COMMENT_LIKE_REF = "CommentLikes";

    //for path dislikes comment
    public static final String COMMENT_DISLIKE_REF = "CommentDislikes";

    //for comment
    public static final String COMMENT_REF = "Comments";

    //For login user
    public static final String ERROR_CREATE_ACCOUNT= "ErrorRegisterUser";
    public static final String SUCCESS_REGISTER_USER = "Success";

    //For sign in user
    public static final String SUCCESS_SIGN_IN_USER = "Success";
    public static final String ERROR_SIGN_IN_USER = "ErrorSignInUser";
    public static final String ERROR_ADD_USER = "ErrorAddUser";

    //Если мы вышли из приложения и нажимааем войти,вводим данные но current user == null,нам приходит на помощь данная
    //Костанта,мы отправляем юзера на registerActivity
    public static final String ERROR_SIGN_IN_EXIST_USER = "ErrorSignInExistUser";

    //for manactivity
    public static final String EXTRA_USER_EMAIL = "UserEmail";

    //for user
    public static final String NAME_USER = "NameUser";
    public static final String EMAIL_USER = "EmailUser";
    public static final String PASSWORD_USER = "PasswordUser";

    public static final String IMAGE_URL_USER = "ImageUrlUser";
    public static final String EXTENSION_IMAGE_URL_USER = "ExtenstionImageUrlUser";

    //for поверки на обновление профиля юзера
    public static final String SUCCESS_UPDATE_USER_PROFILE = "SuccessUpdateUserProfile";
    public static final String FAILURE_UPDATE_USER_PROFILE = "FailureUpdateUserProfile";

    //Для проверки юзера в realtimedb
    public static final String USER_EXISTING = "UserExist";
    public static final String USER_NOT_EXISTING = "UserNotExisting";

    //for startactivityresult post,имя publisher'a and his name
    public static final String PUBLISHER_IMAGE_URL = "PublisherImageUrl";
    public static final String PUBLISHER_NAME = "PublisherName";

    //После добавления поста мы попадаем в onactivity result in mainactivty , эти константы нужны для получения названия поста ,его текста ,фотки и так далее

    public static final String TITLE_POST = "TitlePost";
    public static final String TEXT_POST = "TextPost";
    public static final String IMAGE_URL_POST = "ImageUrlPost";
    public static final String EXTENSION_IMAGE_URL_POST = "ExtensionImageUrlPost";

    //Для получения publisher image url,фотки пользователя который публикует данный пост и его имени воспользуемся 2 - мя константами выше


    //For postkey при нажатии на пост в onBindViewHolder onstart mainactivity
    public static final String POST_KEY = "PostKey";

    //for post comment key
    public static final String RECEIVER_UID = "uId";
    public static final String COMMENT_TEXT = "commentText";
    public static final String RECEIVER_NAME = "userName";
    public static final String RECEIVER_PROFILE_IMAGE_URL = "userProfileImageUrl";


    //for отслеживание добавился ли пост или нет
    public static final String ERROR_ADD_POST = "ErrorAddPost";
    public static final String SUCCESS_ADD_POST = "SuccessAddPost";

    //Для получения фотки поста на который мы нажали по нажатии на коментарий нужно для commentviewmodel
    public static final String CURRENT_POST_IMAGE_URL = "postImageUrl";
    public static final String CURRENT_POST_TITLE = "title";

    //Если не получилось получить фото и название поста на который мы нажали по нажатию на коментарий нужно для commenViewModel
    public static final String ERROR_GET_POST_IMAGE_URL = "ErrorGetPostImageUrl";
    public static final String ERROR_GET_POST_TITLE = "ErrorGetPostTitle";

    //Отслеживание добавления коментраия(успешное или нет)
    public static final String SUCCESS_ADD_COMMENT = "SuccessAddComment";
    public static final String ERROR_ADD_COMMENT = "ErrorAddComment";

}
