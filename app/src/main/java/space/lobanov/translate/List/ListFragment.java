package space.lobanov.translate.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import space.lobanov.translate.AdapterSetter;
import space.lobanov.translate.Home.HomeFragment;
import space.lobanov.translate.Login.MainActivity;
import space.lobanov.translate.R;
import space.lobanov.translate.SavedItemsAdapter;
import space.lobanov.translate.Translate;

public class ListFragment extends Fragment {
    private FirebaseAuth mAuth;

    private Translate mActivity;
    private ImageButton ibSignOut, imAvatar;
    public static SavedItemsAdapter adapter;
    public RecyclerView savedList;
    private ActivityResultLauncher<Intent> resultLauncher;
    private StorageReference mStorageRef;
    private Uri uploadUri;

    public ListFragment() {

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
        getImage();
        setSavedLayoutManager();
        setAdapter();
        setButtonsActions();
    }

    private void init() {
        mActivity = (Translate) getActivity();
        ibSignOut = mActivity.findViewById(R.id.ibSignOut);
        imAvatar = mActivity.findViewById(R.id.imAvatar);
        TextView tvEmail = mActivity.findViewById(R.id.tvEmail);
        savedList = mActivity.findViewById(R.id.savedList);
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("ImageDB");
        resultLauncher = createResultLauncher();
        tvEmail.setText(mAuth.getCurrentUser().getEmail());
    }

    private ActivityResultLauncher<Intent> createResultLauncher() {
        ActivityResultCallback<ActivityResult> callback = result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                imAvatar.setBackgroundResource(R.color.pitbull_black);
                imAvatar.setImageURI(data.getData());
                uploadImage();
            }
        };
        return registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), callback);
    }

    private void uploadImage() {
        BitmapDrawable drawable = (BitmapDrawable) imAvatar.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] byteArray = baos.toByteArray();
        StorageReference mRef = mStorageRef.child(mAuth.getCurrentUser().getUid());
        mRef.putBytes(byteArray);
    }

    private void getImage() {
        StorageReference mRef = mStorageRef.child(mAuth.getCurrentUser().getUid());
        mRef.getDownloadUrl().addOnSuccessListener(uri -> {
            uploadUri = uri;
            imAvatar.setBackgroundResource(R.color.pitbull_black);
            Picasso.get().load(uploadUri).into(imAvatar);
        });
    }

    private void setSavedLayoutManager() {
        WrapContentLinearLayoutManager manager = new WrapContentLinearLayoutManager(mActivity);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        savedList.setLayoutManager(manager);
    }

    private void setAdapter() {
        AdapterSetter adapterSetter = new AdapterSetter(mActivity);
        adapter = adapterSetter.getSavedAdapter();
        savedList.setAdapter(adapter);
    }

    private void setButtonsActions() {
        ibSignOut.setOnClickListener(l -> onClickSignOut());
        imAvatar.setOnClickListener(l -> onClickSetImage());
    }

    private void onClickSignOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(mActivity, MainActivity.class);
        startActivity(intent);
        mActivity.finish();
    }

    private void onClickSetImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        resultLauncher.launch(intent);
    }
}