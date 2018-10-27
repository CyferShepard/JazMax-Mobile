package com.cyfer.jazzmax;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class fragment_Users extends Fragment {

    private OnFragmentInteractionListener mListener;
    private RecyclerView users;
    private DatabaseReference usersdb;

    public fragment_Users() {
        // Required empty public constructor
    }


    public static fragment_Users newInstance() {
        fragment_Users fragment = new fragment_Users();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_users, container, false);
        users=view.findViewById(R.id.alluserslist);
        users.setHasFixedSize(true);
        users.setLayoutManager(new LinearLayoutManager(getActivity()));
        usersdb= FirebaseDatabase.getInstance().getReference().child("Users");
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
//        FirebaseRecyclerAdapter<modelUsers, UsersViewHolder>  firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<modelUsers, UsersViewHolder>(
//                modelUsers.class,
//                R.layout.list_users,
//                UsersViewHolder.class,
//                usersdb
//
//
//        ) {
//            @Override
//            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull modelUsers model) {
//
//            }
//
//            @NonNull
//            @Override
//            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                return null;
//            }
//        };















        ///////////////////////////

        FirebaseRecyclerOptions<modelUsers> options =
                new FirebaseRecyclerOptions.Builder<modelUsers>()
                        .setQuery(usersdb, modelUsers.class)
                        .build();
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<modelUsers, UsersViewHolder>(options) {
            @Override
            public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_users, parent, false);

                return new UsersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(UsersViewHolder holder, int position, modelUsers model) {
                holder.setUName(model.getEmail());

            }
        };

        users.setAdapter(adapter);
    }




    public static class UsersViewHolder extends RecyclerView.ViewHolder
    {

        View mview;
        public UsersViewHolder(View itemView) {
            super(itemView);
            mview=itemView;
        }
        public void setUName(String e)
        {
            TextView username = mview.findViewById(R.id.txt_single_user1);
            username.setText(e);
        }



    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}
