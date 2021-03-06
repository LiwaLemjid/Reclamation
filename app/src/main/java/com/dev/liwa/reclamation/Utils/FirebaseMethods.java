package com.dev.liwa.reclamation.Utils;

import android.content.Context;
import android.provider.ContactsContract;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dev.liwa.reclamation.Home.MainActivity;
import com.dev.liwa.reclamation.Models.Image;
import com.dev.liwa.reclamation.Models.Photo;
import com.dev.liwa.reclamation.Models.User;
import com.dev.liwa.reclamation.Models.UserAccountSettings;
import com.dev.liwa.reclamation.Models.UserSettings;
import com.dev.liwa.reclamation.Profile.AccountSettingActivity;
import com.dev.liwa.reclamation.R;
import com.dev.liwa.reclamation.Share.NextActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseMethods {

    private static final String TAG = "FirebaseMethods";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String userID;
    private StorageReference mStorageReference;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;


    //vars
    private double mPhotoUploadProgress = 0;


    private Context mContext;

    public FirebaseMethods(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mContext = context;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }



    public void uploadNewPhoto(String photoType, final String caption,final int count, final String imgUrl,
                               Bitmap bm){
        Log.d(TAG, "uploadNewPhoto: attempting to uplaod new photo.");

        FilePaths filePaths = new FilePaths();
        //case1) new photo
        if(photoType.equals(mContext.getString(R.string.new_photo))){
            Log.d(TAG, "uploadNewPhoto: uploading NEW photo.");

            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo" + (count + 1));

            //convert image url to bitmap
            if(bm == null){
                bm = ImageManager.getBitmap(imgUrl);
            }

            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> firebaseUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl();


                    firebaseUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String photoLink = uri.toString();
                            //System.out.println("photoLink........"+photoLink);
                            addPhotoToDatabase(caption, photoLink);
                            Image i = new Image();
                            i.setPath(photoLink);




                                RequestQueue MyRequestQueue = Volley.newRequestQueue(mContext);
                                String URL_ADD_POST="http://192.168.43.32:8888/rec/web/app_dev.php/s/photos/new";
                                StringRequest MyStringRequest = new StringRequest(Request.Method.POST, URL_ADD_POST, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        //This code is executed if the server responds, whether or not the response contains data.
                                        //The String 'response' contains the server's response.
                                        System.out.println(response);

                                        Toast.makeText(mContext, "Attempting to upload new photo", Toast.LENGTH_SHORT).show();
                                    }
                                }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                        //This code is executed if there is an error.
                                    }
                                }) {


                                    protected Map<String, String> getParams() {

                                        Map<String, String> MyData = new HashMap<String, String>();
                                        MyData.put("path", photoLink); //Add the data you'd like to send to the server.

                                        return MyData;
                                    }
                                };
                                MyRequestQueue.add(MyStringRequest);





                        }
                    });

                    //System.out.println("download url"+firebaseUrl.toString());
                    Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();

                    mContext.startActivity(new Intent(mContext, MainActivity.class));

                    //add the new photo to 'photos' node and 'user_photos' node

                    //navigate to the main feed so the user can see their photo

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Photo upload failed.");
                    Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    if(progress - 15 > mPhotoUploadProgress){
                        Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }

                    Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                }
            });

        }
        //case new profile photo
        else if(photoType.equals(mContext.getString(R.string.profile_photo))){
            Log.d(TAG, "uploadNewPhoto: uploading new PROFILE photo");




            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/profile_photo");

            //convert image url to bitmap
            if(bm == null){
                bm = ImageManager.getBitmap(imgUrl);
            }
            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> firebaseUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl();


                    firebaseUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String photoLink = uri.toString();
                            setProfilePhoto(photoLink);


                            ((AccountSettingActivity)mContext).setViewPager(
                                    ((AccountSettingActivity)mContext).pagerAdapter
                                            .getFragmentNumber(mContext.getString(R.string.edit_profile_fragment))
                            );
                        }
                    });



                    Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();

                    //insert into 'user_account_settings' node



                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Photo upload failed.");
                    Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    if(progress - 15 > mPhotoUploadProgress){
                        Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }

                    Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                }
            });



        }

    }



    private void setProfilePhoto(String url){
        Log.d(TAG, "setProfilePhoto: setting new profile image: " + url);

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mContext.getString(R.string.profile_photo))
                .setValue(url);
    }


    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.FRANCE);
        sdf.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        return sdf.format(new Date());
    }

    private void addPhotoToDatabase(String caption, String url){
        Log.d(TAG, "addPhotoToDatabase: adding photo to database.");

        String tags = StringManipulation.getTags(caption);
        String newPhotoKey = myRef.child(mContext.getString(R.string.dbname_photos)).push().getKey();
        Photo photo = new Photo();
        photo.setCaption(caption);
        photo.setDate_created(getTimestamp());
        photo.setImage_path(url);
        photo.setTags(tags);
        photo.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        photo.setPhoto_id(newPhotoKey);

        //insert into database
        myRef.child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser()
                        .getUid()).child(newPhotoKey).setValue(photo);
        myRef.child(mContext.getString(R.string.dbname_photos)).child(newPhotoKey).setValue(photo);

    }

    public int getImageCount(DataSnapshot dataSnapshot){
        int count = 0;
        for(DataSnapshot ds: dataSnapshot
                .child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .getChildren()){
            count++;
        }
        return count;
    }

    /**
     * Register a new email and password to Firebase Authentication
     * @param email
     * @param password
     * @param username
     */
    public void registerNewEmail(final String email, String password, final String username){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(mContext, "Failed to register",
                                    Toast.LENGTH_SHORT).show();

                        }
                        else if(task.isSuccessful()){
                            sendVerificationEmail();
                            userID = mAuth.getCurrentUser().getUid();
                            Log.d(TAG, "onComplete: Authstate changed: " + userID);
                        }

                    }
                });
    }


    public void sendVerificationEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                            }else{
                                Toast.makeText(mContext, "couldn't send verification email.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


    public boolean checkIfUsernameExists(String username, DataSnapshot datasnapshot){
        Log.d(TAG, "checkIfUsernameExists: checking if " + username + " already exists.");

        User user = new User();

        for (DataSnapshot ds: datasnapshot.child(userID).getChildren()){
            Log.d(TAG, "checkIfUsernameExists: datasnapshot: " + ds);

            user.setUsername(ds.getValue(User.class).getUsername());
            System.out.println("user.usernameee:"+user.getUsername());
            Log.d(TAG, "checkIfUsernameExists: username: " + user.getUsername());

            if(user.getUsername().equals(username)){
                Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH: " + user.getUsername());
                return true;
            }
        }
        return false;
    }


    public void addNewUser(String email, String username, String password, String description, String profile_photo){

        User user = new User( userID,   email, password, username);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .setValue(user);


        UserAccountSettings settings = new UserAccountSettings(
                description,
                username,
                0,
                0,
                0,
                profile_photo,
                username,
                userID

        );

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .setValue(settings);

    }

    /**
     * Retireves the account settings for the user currently logged in
     * Database : user_account_settings node
     *
     * @param dataSnapshot
     * @return
     */
    public UserSettings getUserSettings(DataSnapshot dataSnapshot) {
        Log.d(TAG, "getUserAccountSettings:retrieving user account settings");


        UserAccountSettings settings = new UserAccountSettings();
        User user = new User();

        for (DataSnapshot ds : dataSnapshot.getChildren()) {

            //user_account_settings node
            if (ds.getKey().equals(mContext.getString(R.string.dbname_user_account_settings))) {
                Log.d(TAG, "getUserAccountSettings: " + ds.child(userID).getValue(UserAccountSettings.class));

                try {


                    settings.setDisplay_name(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getDisplay_name()
                    );
                    settings.setUsername(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getUsername()
                    );
                    settings.setDescription(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getDescription()
                    );
                    settings.setProfile_photo(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getProfile_photo()
                    );
                    settings.setFollowers(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getFollowers()
                    );
                    settings.setFollowing(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getFollowing()
                    );
                    settings.setPosts(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getPosts()
                    );
                } catch (NullPointerException e) {
                    Log.e(TAG, "NullPointerException" + e.getMessage());
                }
            }

            if (ds.getKey().equals(mContext.getString(R.string.dbname_users))) {
                HashMap<String,String> ld = (HashMap<String, String>) ds.child(userID).getValue();
                for (Map.Entry<String,String>a:ld.entrySet()) {

                    Log.d(TAG, "getUser: " + a.getKey());
                    Log.d(TAG, "getUser: " + a.getValue());

                }
                user.setUsername(
                        ds.child(userID)
                                .getValue(User.class)
                                .getUsername()
                );
                user.setEmail(
                        ds.child(userID)
                                .getValue(User.class)
                                .getEmail()
                );
                user.setUser_id(
                        ds.child(userID)
                                .getValue(User.class)
                                .getUser_id()
                );
                user.setPassword(
                        ds.child(userID)
                                .getValue(User.class)
                                .getPassword()
                );

            }
        }
        return new UserSettings(user, settings);

    }

    /**
     * update the email in the 'user's' node and 'usersetting node
     * @param username
     */
    public void updateUsername (String username ){
        Log.d(TAG,"Updating username to "+username);
        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);
    }

    /**
     * update the email in the 'user's' node
     * @param email
     */
    public void updateEmail(String email){
        Log.d(TAG, "updateEmail: upadting email to: " + email);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_email))
                .setValue(email);

    }
}
