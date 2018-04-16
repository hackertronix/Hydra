package io.execube.monotype.deimos.photo_feed;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import io.execube.monotype.deimos.R;
import io.execube.monotype.deimos.Utils.AnimUtilsKt;
import io.execube.monotype.deimos.model.Photo;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;

public class PhotosFragment extends Fragment implements EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks {

  public static final String TAG = PhotosFragment.class.getSimpleName();
  private ImageButton fab;
  private FirebaseFirestore db = FirebaseFirestore.getInstance();
  private FirestoreRecyclerAdapter<Photo, PhotosHolder> adapter;
  private RecyclerView photosRecyclerview;
  private ImageView mImageView;
  private int width;
  private int height;
  private static final String[] CAMERA_AND_STORAGE =
      { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

  private static final int RC_CAMERA_PERM = 123;
  private static final int RC_CAMERA_AND_STORAGE_PERM = 124;

  @Nullable @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_photos, container, false);
    getDisplaySize();
    fab = view.findViewById(R.id.add_photo);
    mImageView = view.findViewById(R.id.photo);
    photosRecyclerview = view.findViewById(R.id.photos_recycler_view);
    initRecyclerView();
    getPhotos();
    return view;
  }

  private void initRecyclerView() {

    LinearLayoutManager linearLayoutManager =
        new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    photosRecyclerview.setLayoutManager(linearLayoutManager);
  }


private void getDisplaySize(){
  DisplayMetrics displayMetrics = new DisplayMetrics();
  getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
   height = displayMetrics.heightPixels;
   width = displayMetrics.widthPixels;
}
  private void getPhotos() {

    Query query = db.collection("Photos");
    FirestoreRecyclerOptions<Photo> response = new FirestoreRecyclerOptions.Builder<Photo>()
        .setQuery(query, Photo.class)
        .build();

    adapter = new FirestoreRecyclerAdapter<Photo, PhotosHolder>(response) {

      @NonNull @Override
      public PhotosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.photo_entry_item, parent, false);
        return new PhotosHolder(view);
      }

      @Override protected void onBindViewHolder(@NonNull PhotosHolder holder, int position,
          @NonNull Photo model) {
        holder.bind(model,width,height);
      }

      @Override public void onError(@NonNull FirebaseFirestoreException e) {
        super.onError(e);
        Log.e("Error",e.getMessage());
      }
    };

        adapter.notifyDataSetChanged();
        photosRecyclerview.setAdapter( adapter);

  }

  @Override public void onStop() {
    super.onStop();
    adapter.stopListening();
  }

  @Override public void onStart() {
    super.onStart();
    adapter.startListening();
  }

  @Override public void onResume() {
    super.onResume();
    animateFab();
    fab.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        //dispatchTakePictureIntent();
        cameraTask();
      }
    });
  }

  @AfterPermissionGranted(RC_CAMERA_AND_STORAGE_PERM)
  private void cameraTask() {

    if (hasCameraAndStoragePermission()) {
      // Have permissions, do the thing!
      dispatchTakePictureIntent();
    } else {
      // Ask for both permissions
      EasyPermissions.requestPermissions(
          this,
          getString(R.string.rationale_camera),
          RC_CAMERA_AND_STORAGE_PERM,
          CAMERA_AND_STORAGE);
    }
  }

  private boolean hasCameraAndStoragePermission() {
    return EasyPermissions.hasPermissions(this.requireContext(), CAMERA_AND_STORAGE);
  }

  String mCurrentPhotoPath;

  private File createImageFile() throws IOException {
    // Create an image file name
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String imageFileName = "JPEG_" + timeStamp + "_";
    File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    File image = File.createTempFile(
        imageFileName,  /* prefix */
        ".jpg",         /* suffix */
        storageDir      /* directory */
    );

    // Save a file: path for use with ACTION_VIEW intents
    mCurrentPhotoPath = image.getAbsolutePath();
    return image;
  }

  static final int REQUEST_TAKE_PHOTO = 1;

  private void dispatchTakePictureIntent() {
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    // Ensure that there's a camera activity to handle the intent
    if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
      // Create the File where the photo should go
      File photoFile = null;
      try {
        photoFile = createImageFile();
      } catch (IOException ex) {
        // Error occurred while creating the File
        Log.e("Error", ex.getMessage());
      }
      // Continue only if the File was successfully created
      if (photoFile != null) {
        Uri photoURI = FileProvider.getUriForFile(getContext(),
            "io.execube.monotype.deimos.fileprovider",
            photoFile);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
      }
    }
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
      //Bundle extras = data.getExtras();
      //assert extras != null;
      //Bitmap imageBitmap = (Bitmap) extras.get("data");
      //mImageView.setImageBitmap(imageBitmap);
      Intent intent = new Intent(getContext(), PhotoUploadActivity.class);
      intent.putExtra("IMAGE_PATH", mCurrentPhotoPath);
      startActivity(intent);
    }

    if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
      String yes = "Yes";
      String no = "No";

      // Do something after user returned from app settings screen, like showing a Toast.
     Toast.makeText(
                   this.requireContext(), getString(R.string.returned_from_app_settings_to_activity,hasCameraAndStoragePermission()? yes:no), Toast.LENGTH_LONG)
                   .show();
    }
  }

  private void animateFab() {

    fab.setVisibility(View.VISIBLE);
    fab.setAlpha(0f);
    fab.setScaleX(0f);
    fab.setScaleY(0f);
    fab.setTranslationY( fab.getHeight() / 2f);
    fab.animate()
        .alpha(1f)
        .scaleX(1f)
        .scaleY(1f)
        .translationY(0f)
        .setDuration(500L)
        .setInterpolator(AnimUtilsKt.getLinearOutSlowInInterpolator(getContext()))
        .start();
  }

  @Override public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
    Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
    dispatchTakePictureIntent();
  }

  @Override public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());

    // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
    // This will display a dialog directing them to enable the permission in app settings.
    if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
      new AppSettingsDialog.Builder(this).build().show();
    }
  }

  @Override public void onRationaleAccepted(int requestCode) {
    Log.d(TAG, "onRationaleAccepted:" + requestCode);
  }

  @Override public void onRationaleDenied(int requestCode) {
    Log.d(TAG, "onRationaleDenied:" + requestCode);
  }
}