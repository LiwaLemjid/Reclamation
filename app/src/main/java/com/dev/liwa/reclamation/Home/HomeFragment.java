package com.dev.liwa.reclamation.Home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dev.liwa.reclamation.Constants;
import com.dev.liwa.reclamation.Models.Comment;
import com.dev.liwa.reclamation.MyModels.Comments;
import com.dev.liwa.reclamation.Models.Like;
import com.dev.liwa.reclamation.Models.Photo;
import com.dev.liwa.reclamation.MyModels.Likes;
import com.dev.liwa.reclamation.MyModels.Post;
import com.dev.liwa.reclamation.R;
import com.dev.liwa.reclamation.Utils.MainfeedListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {
    private static final String TAG="HomeFragment";
    public static final String URL_PUBS = "http://192.168.1.7:8888/rec/web/app_dev.php/s/posts/";


    //vars
    private ArrayList<Photo> mPhotos;
    private ArrayList<String> mFollowing;
    private ListView mListView;
    private MainfeedListAdapter mAdapter;
    private ArrayList<Photo> mPaginatedPhotos;
    private int mResults;
    private  ArrayList<Comments> mComments;
    private  ArrayList<Likes> mLikes;
    Context context;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment,container,false);

        mListView = (ListView) view.findViewById(R.id.listView);
        mFollowing = new ArrayList<>();
        mPhotos = new ArrayList<>();
        context = getActivity().getApplicationContext();






        getAllPosts();

        //getFollowing();
        //getAllPhotos();


        return view;
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




                                mPhotos.add(photo);
                                photos.add(photo);









                                posts.add(post);
                                displayPhotos();

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

    //getting the following
    private void getFollowing(){
        Log.d(TAG, "getFollowing: searching for following");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found user: " +
                            singleSnapshot.child(getString(R.string.field_user_id)).getValue());

                    mFollowing.add(singleSnapshot.child(getString(R.string.field_user_id)).getValue().toString());
                }
                mFollowing.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                //get the photos
                getPhotos();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void displayAllPosts(){
        mPaginatedPhotos = new ArrayList<>();
        if(mPhotos != null){
            try{
                Collections.sort(mPhotos, new Comparator<Photo>() {
                    @Override
                    public int compare(Photo o1, Photo o2) {
                        return o2.getDate_created().compareTo(o1.getDate_created());
                    }
                });

                int iterations = mPhotos.size();

                if(iterations > 10){
                    iterations = 10;
                }

                mResults = 10;
                for(int i = 0; i < iterations; i++){
                    mPaginatedPhotos.add(mPhotos.get(i));
                }

                mAdapter = new MainfeedListAdapter(getActivity(), R.layout.layout_mainfeed_listitem, mPaginatedPhotos);
                mListView.setAdapter(mAdapter);

            }catch (NullPointerException e){
                Log.e(TAG, "displayPhotos: NullPointerException: " + e.getMessage() );
            }catch (IndexOutOfBoundsException e){
                Log.e(TAG, "displayPhotos: IndexOutOfBoundsException: " + e.getMessage() );
            }
        }
    }



    private void getPhotos(){
        Log.d(TAG, "getPhotos: getting photos");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        for(int i = 0; i < mFollowing.size(); i++){
            final int count = i;
            Query query = reference
                    .child(getString(R.string.dbname_user_photos))
                    .child(mFollowing.get(i))
                    .orderByChild(getString(R.string.field_user_id))
                    .equalTo(mFollowing.get(i));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

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

                        mPhotos.add(photo);
                    }
                    if(count >= mFollowing.size() -1){
                        //display our photos
                        displayPhotos();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    private void displayPhotos(){
        mPaginatedPhotos = new ArrayList<>();
        System.out.println("photoooos"+mPhotos);
        if(mPhotos != null){
            try{
                Collections.sort(mPhotos, new Comparator<Photo>() {
                    @Override
                    public int compare(Photo o1, Photo o2) {
                        return o2.getDate_created().compareTo(o1.getDate_created());
                    }
                });

                int iterations = mPhotos.size();

                if(iterations > 10){
                    iterations = 10;
                }

                mResults = 10;
                for(int i = 0; i < iterations; i++){
                    mPaginatedPhotos.add(mPhotos.get(i));
                }

                mAdapter = new MainfeedListAdapter(getActivity(), R.layout.layout_mainfeed_listitem, mPaginatedPhotos);
                mListView.setAdapter(mAdapter);

            }catch (NullPointerException e){
                Log.e(TAG, "displayPhotos: NullPointerException: " + e.getMessage() );
            }catch (IndexOutOfBoundsException e){
                Log.e(TAG, "displayPhotos: IndexOutOfBoundsException: " + e.getMessage() );
            }
        }
    }

    public void displayMorePhotos(){
        Log.d(TAG, "displayMorePhotos: displaying more photos");

        try{

            if(mPhotos.size() > mResults && mPhotos.size() > 0){

                int iterations;
                if(mPhotos.size() > (mResults + 10)){
                    Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");
                    iterations = 10;
                }else{
                    Log.d(TAG, "displayMorePhotos: there is less than 10 more photos");
                    iterations = mPhotos.size() - mResults;
                }

                //add the new photos to the paginated results
                for(int i = mResults; i < mResults + iterations; i++){
                    mPaginatedPhotos.add(mPhotos.get(i));
                }
                mResults = mResults + iterations;
                mAdapter.notifyDataSetChanged();
            }
        }catch (NullPointerException e){
            Log.e(TAG, "displayPhotos: NullPointerException: " + e.getMessage() );
        }catch (IndexOutOfBoundsException e){
            Log.e(TAG, "displayPhotos: IndexOutOfBoundsException: " + e.getMessage() );
        }
    }

}
