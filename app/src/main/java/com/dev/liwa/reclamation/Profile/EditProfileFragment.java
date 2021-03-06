package com.dev.liwa.reclamation.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView;

import com.dev.liwa.reclamation.Dialog.ConfirmPasswordDialog;
import com.dev.liwa.reclamation.Models.User;
import com.dev.liwa.reclamation.Models.UserAccountSettings;
import com.dev.liwa.reclamation.Models.UserSettings;
import com.dev.liwa.reclamation.R;
import com.dev.liwa.reclamation.Utils.FirebaseMethods;
import com.dev.liwa.reclamation.Share.AddActivity;
import com.dev.liwa.reclamation.Utils.UniversalImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileFragment extends Fragment implements
        ConfirmPasswordDialog.OnConfirmPasswordListener {

    @Override
    public void onConfirmPassword(String password) {
        Log.d(TAG, "onConfirmPassword: got the password: " + password);

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider
                .getCredential(mAuth.getCurrentUser().getEmail(), password);

        ///////////////////// Prompt the user to re-provide their sign-in credentials
        mAuth.getCurrentUser().reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "User re-authenticated.");

                            ///////////////////////check to see if the email is not already present in the database
                            mAuth.fetchProvidersForEmail(mEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                                    if(task.isSuccessful()){
                                        try{
                                            if(task.getResult().getProviders().size() == 1){
                                                Log.d(TAG, "onComplete: that email is already in use.");
                                                Toast.makeText(getActivity(), "That email is already in use", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                Log.d(TAG, "onComplete: That email is available.");

                                                //////////////////////the email is available so update it
                                                mAuth.getCurrentUser().updateEmail(mEmail.getText().toString())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Log.d(TAG, "User email address updated.");
                                                                    Toast.makeText(getActivity(), "email updated", Toast.LENGTH_SHORT).show();
                                                                    mFirebaseMethods.updateEmail(mEmail.getText().toString());
                                                                }
                                                            }
                                                        });
                                            }
                                        }catch (NullPointerException e){
                                            Log.e(TAG, "onComplete: NullPointerException: "  +e.getMessage() );
                                        }
                                    }
                                }
                            });





                        }else{
                            Log.d(TAG, "onComplete: re-authentication failed.");
                        }

                    }
                });
    }

    private static final String TAG = "EditProfileFragment";
    private TextView mChangeProfilePhoto;
    private CircleImageView mProfilePhoto;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef ;
    private Context mContext ;
    private FirebaseMethods mFirebaseMethods ;
    //Edit profile fragment widgets

    private EditText mDisplayName,mUsername ,mEmail,mPassword, mRepeatPassword ;
    private String userID;

    //variable
    private UserSettings mUserSettings;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editprofile, container, false);

        mContext =getActivity();
        mProfilePhoto = (CircleImageView) view.findViewById(R.id.profile_photo);
        mDisplayName = (EditText) view.findViewById(R.id.display_name);
        mUsername = (EditText) view.findViewById(R.id.username);
        mEmail = (EditText) view.findViewById(R.id.email);
        mChangeProfilePhoto = (TextView) view.findViewById(R.id.changeProfilePhoto);
        mFirebaseMethods = new FirebaseMethods(getActivity());



        /// ça manque password

        initImageLoader();


        ImageView backArrow = (ImageView) view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to 'ProfileActivity'");
                getActivity().finish();
            }
        });



        mChangeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"OnClick : Changing profile photo");
                Intent intent = new Intent(getActivity(), AddActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });


        setupFirebaseAuth();
        setProfileImage();

        ImageView checkmark = (ImageView) view.findViewById(R.id.saveChanges);
        checkmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Attempting to save changes");
                saveProfileSettings();
            }
        });
        return view;
    }

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getActivity());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private void setProfileImage(){
        Log.d(TAG, "setProfileImage: setting profile image.");
        String imgURL = "www.androidcentral.com/sites/androidcentral.com/files/styles/xlarge/public/article_images/2016/08/ac-lloyd.jpg?itok=bb72IeLf";
        UniversalImageLoader.setImage(imgURL, mProfilePhoto, null, "https://");
    }

    private void saveProfileSettings () {
        final String display_name = mDisplayName.getText().toString();
        final String username = mUsername.getText().toString();
        final String email = mEmail.getText().toString();


            //case 1 : if the user made a change to their username

            if(!mUserSettings.getUser().getUsername().equals(username)) {

                 checkIfUsernameExists(username);

            }
            //case 2 : if the user made a change to their email
            if (!mUserSettings.getUser().getEmail().equals(email)){

                //step1)  reauthenticate
                //     Confirm the password and the email

                ConfirmPasswordDialog dialog = new ConfirmPasswordDialog();
                dialog.show(getFragmentManager(),getString(R.string.confirm_password_dialog));
                dialog.setTargetFragment(EditProfileFragment.this, 1);
                //step2) check if the email exists
                //     fetchProvidersForEmail(String email)
                //step3) change the email
                //     submit the new email to the database and authentication


            }


            //User Did not change their username





    }

    /**
     * chekch is param username exists in database
     * @param username
     */
    private void checkIfUsernameExists(final String username) {
        Log.d(TAG,"checkIfUsernameExists :chekcing if "+username+" already exists");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_username))
                .equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    //add the username
                mFirebaseMethods.updateUsername(username);
                }
                for (DataSnapshot singleSnapshot :dataSnapshot.getChildren() ){
                    if(singleSnapshot.exists()){
                        Log.d(TAG,"CheckifUsernameExists: Found Match "+singleSnapshot.getValue(User.class).getUsername());

                        Toast.makeText(getActivity(),"That username already exists",Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void SetProfileWidget (UserSettings userSettings){
//        Log.d(TAG,"SetProfileWidget: setting with data retrievieng from firebase database"+userSettings.toString());
      Log.d(TAG,"SetProfileWidget: setting with data retrievieng from firebase database"+userSettings.getUser().getEmail());

        mUserSettings = userSettings ;
        User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();



        UniversalImageLoader.setImage(settings.getProfile_photo(),mProfilePhoto,null,"");
        mDisplayName.setText(settings.getDisplay_name());
        mUsername.setText(settings.getUsername());
        mEmail.setText((user.getEmail()));


    }


         /*
    ------------------------------------ Firebase ---------------------------------------------
     */


    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        userID =mAuth.getCurrentUser().getUid();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SetProfileWidget (mFirebaseMethods.getUserSettings(dataSnapshot));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


}