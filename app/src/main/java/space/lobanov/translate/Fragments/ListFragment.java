package space.lobanov.translate.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import space.lobanov.translate.Adapters.AdapterSetter;
import space.lobanov.translate.MainActivity;
import space.lobanov.translate.R;
import space.lobanov.translate.SavedItemsAdapter;
import space.lobanov.translate.Translate;

public class ListFragment extends Fragment {
    private FirebaseAuth mAuth;

    private Translate mActivity;
    private ImageButton ibSignOut;
    private ImageView imAvatar;
    TextView tvEmail;
    private SavedItemsAdapter adapter;
    private RecyclerView savedList;
    private ListFragment(){

    }

    public static ListFragment newInstance(){
        return new ListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        ibSignOut.setOnClickListener(l -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(mActivity, MainActivity.class);
            startActivity(intent);
            mActivity.finish();
        });
        tvEmail.setText(mAuth.getCurrentUser().getEmail());

        LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        savedList.setLayoutManager(manager);
        AdapterSetter adapterSetter = new AdapterSetter(mActivity);
        adapter = adapterSetter.getSavedAdapter();
        savedList.setAdapter(adapter);
    }
    private void init() {
        mActivity = (Translate) getActivity();
        ibSignOut = mActivity.findViewById(R.id.ibSignOut);
        imAvatar = mActivity.findViewById(R.id.imAvatar);
        tvEmail = mActivity.findViewById(R.id.tvEmail);
        savedList = mActivity.findViewById(R.id.savedList);
        mAuth = FirebaseAuth.getInstance();
    }
}