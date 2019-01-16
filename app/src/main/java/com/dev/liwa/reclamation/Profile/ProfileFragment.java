package com.dev.liwa.reclamation.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dev.liwa.reclamation.Constants;
import com.dev.liwa.reclamation.Models.Comment;
import com.dev.liwa.reclamation.Models.Like;
import com.dev.liwa.reclamation.Models.Photo;
import com.dev.liwa.reclamation.Models.User;
import com.dev.liwa.reclamation.Models.UserAccountSettings;
import com.dev.liwa.reclamation.Models.UserSettings;
import com.dev.liwa.reclamation.MyModels.Comments;
import com.dev.liwa.reclamation.MyModels.Likes;
import com.dev.liwa.reclamation.MyModels.Post;
import com.dev.liwa.reclamation.R;
import com.dev.liwa.reclamation.Utils.ButtomNavigationHelper;
import com.dev.liwa.reclamation.Utils.FirebaseMethods;
import com.dev.liwa.reclamation.Utils.GridImageAdapter;
import com.dev.liwa.reclamation.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private static final int ACTIVITY_NUM = 4;
    private static final int NUM_GRID_COLUMNS = 3;


    public interface OnGridImageSelectedListener{
        void onGridImageSelected(Photo photo, int activityNumber);
    }
    OnGridImageSelectedListener mOnGridImageSelectedListener;

        //firebase
        private FirebaseAuth mAuth;
        private FirebaseAuth.AuthStateListener mAuthListener;
        private FirebaseDatabase mFirebaseDatabase;
        private DatabaseReference myRef ;

    private TextView mPosts, mFollowers, mFollowing, mDsiplayName, mUserName, mWebSite, mDescription;
    private ProgressBar progressBar;
    private CircleImageView mProfilePhoto;
    private GridView gridView;
    private Toolbar toolbar;
    private ImageView profileMenu;
    private BottomNavigationView bottomNavigationView;
    private Context mcontext;
    private FirebaseMethods mFirebaseMethods ;

    private ArrayList<Photo> mPhotos;
    private  ArrayList<Comments> mComments;
    private  ArrayList<Likes> mLikes;
    Context context;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mDsiplayName = (TextView) view.findViewById(R.id.display_name);
        mUserName = (TextView) view.findViewById(R.id.username);
        mWebSite = (TextView) view.findViewById(R.id.website);
        mDescription = (TextView) view.findViewById(R.id.description);
        mProfilePhoto = (CircleImageView) view.findViewById(R.id.profile_photo);
        mPosts = (TextView) view.findViewById(R.id.tvPosts);
        mFollowers = (TextView) view.findViewById(R.id.tvFollowers);
        mFollowing = (TextView) view.findViewById(R.id.tvFollowing);
        progressBar = (ProgressBar) view.findViewById(R.id.profileProgressBar);
        gridView = (GridView) view.findViewById(R.id.gridView);
        toolbar = (Toolbar) view.findViewById(R.id.profileToolBar);
        profileMenu = (ImageView) view.findViewById(R.id.profileImage);
        bottomNavigationView = (BottomNavigationView) view.findViewById(R.id.bottomNavViewBar);
        mcontext = getActivity();
        context = getActivity().getApplicationContext();
        mFirebaseMethods = new FirebaseMethods(getActivity());
        TextView editProfile = (TextView) view.findViewById(R.id.textEditProfile);



        /**
         * setup buttom navigation view
         */


        ButtomNavigationHelper.enableNavigation(mcontext,getActivity(), bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);



       setupToolbar();
        setupFirebaseAuth();

        getAllPosts();



        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"on click navigating to:");
                Intent intent = new Intent(getActivity(),AccountSettingActivity.class);
                intent.putExtra(getString(R.string.calling_activity),getString(R.string.profile_activity));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        Log.d(TAG, "onCreateView : started ");
        return view;
    }

    @Override
    public void onAttach(Context context) {
        try{
            mOnGridImageSelectedListener = (OnGridImageSelectedListener) getActivity();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );
        }
        super.onAttach(context);
    }


    private void getAllPosts(){
        final ArrayList<Post> posts = new ArrayList<>();
        final ArrayList<Photo> photos = new ArrayList<>();
        final ArrayList<Comment> commentArrayList = new ArrayList<>();
        final ArrayList<Like> likeArrayList = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                Constants.URL_PUBLICATIONS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Post post = new Post();
                                post.setDescription(((JSONObject)jsonArray.get(i)).get("description").toString());
                                post.setTag(((JSONObject)jsonArray.get(i)).get("tag").toString());
                                post.setTitre(((JSONObject)jsonArray.get(i)).get("titre").toString());
                                post.setUserid(Integer.parseInt(((JSONObject)jsonArray.get(i)).get("userid").toString()));
                                post.setIdpub(Integer.parseInt(((JSONObject)jsonArray.get(i)).get("idpub").toString()));
                                post.setCreated_at(((JSONObject)jsonArray.get(i)).get("createdAt").toString());


                                Photo photo = new Photo();
                                photo.setCaption(((JSONObject)jsonArray.get(i)).get("description").toString());
                                photo.setDate_created(((JSONObject)jsonArray.get(i)).get("createdAt").toString());
                                photo.setTags(((JSONObject)jsonArray.get(i)).get("tag").toString());
                                photo.setUser_id(((JSONObject)jsonArray.get(i)).get("userid").toString());
                                photo.setPhoto_id(((JSONObject)jsonArray.get(i)).get("idpub").toString());
                                photo.setImage_path(((JSONObject)jsonArray.get(i)).get("path").toString());












                                //getting all the comments
                                mComments = new ArrayList<Comments>();
                                StringRequest stringRequest = new StringRequest(
                                        Request.Method.GET,
                                        Constants.URL_COMMENTS,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    JSONArray jsonArray = new JSONArray(response);
                                                    for (int i = 0; i < jsonArray.length(); i++) {
                                                        Comments comments = new Comments();


                                                        comments.setTextcomment(((JSONObject)jsonArray.get(i)).get("textcomment").toString());
                                                        comments.setCreated_at(((JSONObject)jsonArray.get(i)).get("created_at").toString());
                                                        mComments.add(comments);

                                                        //photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());





                                                    }

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                            }
                                        }
                                );

                                RequestQueue requestQueue = Volley.newRequestQueue(context);
                                requestQueue.add(stringRequest);











                                for(Comments comments : mComments){
                                    if(comments.getIdpost().equals(photo.getPhoto_id())){
                                        Comment c = new Comment();
                                        c.setComment(comments.getTextcomment());
                                        c.setDate_created(comments.getCreated_at());
                                        c.setUser_id(""+comments.getUserid());
                                        commentArrayList.add(c);
                                    }
                                }
                                photo.setComments(commentArrayList);




                                //getting all the likes
                                mLikes = new ArrayList<>();
                                StringRequest stringRequest2 = new StringRequest(
                                        Request.Method.GET,
                                        Constants.URL_COMMENTS,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    JSONArray jsonArray = new JSONArray(response);
                                                    for (int i = 0; i < jsonArray.length(); i++) {
                                                        Likes likes = new Likes();

                                                        likes.setIdpost(((JSONObject)jsonArray.get(i)).get("idpost").toString());
                                                        likes.setId(Integer.parseInt(((JSONObject)jsonArray.get(i)).get("id").toString()));
                                                        likes.setUserid(Integer.parseInt(((JSONObject)jsonArray.get(i)).get("userid").toString()));
                                                        mLikes.add(likes);

                                                    }

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                            }
                                        }
                                );

                                RequestQueue requestQueue1 = Volley.newRequestQueue(context);
                                requestQueue1.add(stringRequest2);





                                for(Likes likes : mLikes){
                                    if(likes.getIdpost().equals(photo.getPhoto_id())){
                                        Like l = new Like();
                                        l.setUser_id(""+likes.getUserid());

                                        likeArrayList.add(l);
                                    }
                                }
                                photo.setLikes(likeArrayList);





                                photos.add(photo);









                                posts.add(post);
                                int gridWidth = getResources().getDisplayMetrics().widthPixels;
                                int imageWidth = gridWidth/NUM_GRID_COLUMNS;
                                gridView.setColumnWidth(imageWidth);

                                ArrayList<String> imgUrls = new ArrayList<String>();
                                for(int j = 0; j < photos.size(); j++){
                                    imgUrls.add(photos.get(i).getImage_path());
                                }
                                GridImageAdapter adapter = new GridImageAdapter(getActivity(),R.layout.layout_grid_imageview,
                                        "", imgUrls);
                                gridView.setAdapter(adapter);

                                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        mOnGridImageSelectedListener.onGridImageSelected(photos.get(position), ACTIVITY_NUM);
                                    }
                                });

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            System.out.println("erreur");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }














    private void setupGridView(){
        Log.d(TAG, "setupGridView: Setting up image grid.");

        final ArrayList<Photo> photos = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    Photo photo = new Photo();
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                    photo.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                    photo.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                    photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                    photo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                    photo.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                    photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());


                    ArrayList<Comment> comments = new ArrayList<Comment>();
                    for (DataSnapshot dSnapshot : singleSnapshot
                            .child(getString(R.string.field_comments)).getChildren()){
                        Comment comment = new Comment();
                        comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                        comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                        comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                        comments.add(comment);
                    }

                    photo.setComments(comments);

                    List<Like> likesList = new ArrayList<Like>();
                    for (DataSnapshot dSnapshot : singleSnapshot
                            .child(getString(R.string.field_likes)).getChildren()){
                        Like like = new Like();
                        like.setUser_id(dSnapshot.getValue(Like.class).getUser_id());
                        likesList.add(like);
                    }
                    photo.setLikes(likesList);
                    photos.add(photo);
                }
                //setup our image grid
                int gridWidth = getResources().getDisplayMetrics().widthPixels;
                int imageWidth = gridWidth/NUM_GRID_COLUMNS;
                gridView.setColumnWidth(imageWidth);

                ArrayList<String> imgUrls = new ArrayList<String>();
                for(int i = 0; i < photos.size(); i++){
                    imgUrls.add(photos.get(i).getImage_path());
                }
                GridImageAdapter adapter = new GridImageAdapter(getActivity(),R.layout.layout_grid_imageview,
                        "", imgUrls);
                gridView.setAdapter(adapter);

                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mOnGridImageSelectedListener.onGridImageSelected(photos.get(position), ACTIVITY_NUM);
                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });
    }



    private void SetProfileWidget (UserSettings userSettings){
        Log.d(TAG,"SetProfileWidget: setting with data retrievieng from firebase database"+userSettings.toString());
        Log.d(TAG,"SetProfileWidget: setting with data retrievieng from firebase database"+userSettings.getSettings().toString());
        //User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();

        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mcontext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());


        System.out.println("Photo     :"+settings.getProfile_photo());
        UniversalImageLoader.setImage(settings.getProfile_photo(),mProfilePhoto,null,"");
        mDsiplayName.setText(settings.getDisplay_name());
        mUserName.setText(settings.getUsername());
        mDescription.setText(settings.getDescription());
        mPosts.setText(String.valueOf(settings.getPosts()));
        mFollowing.setText(String.valueOf(settings.getFollowing()));
        mFollowers.setText(String.valueOf(settings.getFollowers()));
        progressBar.setVisibility(View.GONE);
    }

 private void setupToolbar() {

        ((ProfileActivity)getActivity()).setSupportActionBar(toolbar);

       profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcontext, AccountSettingActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

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
                //retrieve user information from database

                SetProfileWidget (mFirebaseMethods.getUserSettings(dataSnapshot));

                //retrieve images for the user in question
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