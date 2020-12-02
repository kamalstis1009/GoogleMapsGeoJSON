package com.subrasystems.geojson.dialogs;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.subrasystems.geojson.R;

import java.util.ArrayList;
import java.util.Collections;

//implements EasyPermissions.PermissionCallbacks
public class SearchableSpinnerDialog {

    /*
    private static final int ACTION_PICK_REQUEST_CODE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 4;
    private static final int REQUEST_CAMERA = 202;
    private static final int REQUEST_GALLERY = 203;
    private String[] CAMERA_PERMISSIONS = { android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, };
    private String[] GALLERY_PERMISSIONS = { android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,};
    private File mFile;
    private String currentPhotoPath;
    */


    private static SearchableSpinnerDialog mInstance;
    private Context mContext;
    private AlertDialog mDialog;

    public SearchableSpinnerDialog(Context context) {
        this.mContext = context;
    }

    public static synchronized SearchableSpinnerDialog getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SearchableSpinnerDialog(context);
        }
        return mInstance;
    }

    public interface CallBackPosition {
        void onPositionItem(int position, String name);
    }

    public void getAlertDialog(final CallBackPosition mCallback, ArrayList<String> items, boolean IsSorted) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        //AlertDialog.Builder builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_searchable_spinner, null, false);
        builder.setView(view);
        builder.setCancelable(true);
        builder.create();
        mDialog = builder.show();

        if (IsSorted) {
            Collections.sort(items);
        }

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.item_search_recycler_view);
        ItemSearchAdapter mAdapter = new ItemSearchAdapter(items, mCallback);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        //mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(5 /*px spacing*/));
        mAdapter.notifyDataSetChanged();

        ((EditText) view.findViewById(R.id.search_item)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mAdapter != null){
                    mAdapter.getFilter().filter(s);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        //NestedScrollView mRootView = (NestedScrollView) view.findViewById(R.id.root_view);
    }


    private class ItemSearchAdapter extends RecyclerView.Adapter<ItemSearchAdapter.MyViewHolder> implements Filterable {

        //private FragmentActivity mActivity;
        private ArrayList<String> mArrayList;
        private ArrayList<String> mArrayList1;
        private CallBackPosition mListener;

        public ItemSearchAdapter(ArrayList<String> arrayList, CallBackPosition mCallback) {
            if (mArrayList != null) {
                mArrayList.clear();
                mArrayList1.clear();
            }
            this.mArrayList = arrayList;
            this.mArrayList1 = arrayList;
            this.mListener = mCallback;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_searchable_spinner, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            String name = mArrayList.get(position);

            holder.textView.setText(name);
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onPositionItem(position, name);
                    mDialog.dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mArrayList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            LinearLayout layout;
            TextView textView;
            MyViewHolder(@NonNull View itemView) {
                super(itemView);
                layout = (LinearLayout) itemView.findViewById(R.id.item_layout);
                textView = (TextView) itemView.findViewById(R.id.item_name);
            }
        }

        //http://programmingroot.com/android-recyclerview-search-filter-tutorial-with-example/
        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    String charString = constraint.toString();
                    if (charString.isEmpty()){
                        mArrayList = mArrayList1;
                    } else {
                        ArrayList<String> filterList = new ArrayList<>();
                        for (String data : mArrayList1){
                            if (data.toLowerCase().contains(charString.toLowerCase())){
                                filterList.add(data);
                            }
                        }
                        mArrayList = filterList;
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = mArrayList;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    mArrayList = (ArrayList<String>) results.values;
                    notifyDataSetChanged();
                }
            };
        }

    }

    /*
    //====================================================| Camera and Gallery Dialog
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_photo_upload_option, null, false);
        builder.setView(view);
        builder.setCancelable(true);
        builder.create();
        final AlertDialog dialog = builder.show();
        ((ImageButton) view.findViewById(R.id.camera_id)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraRequestPermissions();
                dialog.dismiss();
            }
        });
        ((ImageButton) view.findViewById(R.id.gallery_id)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryRequestPermissions();
                dialog.dismiss();
            }
        });
    }
    //@AfterPermissionGranted(REQUEST_CAMERA)
    private void cameraRequestPermissions() {
        if (EasyPermissions.hasPermissions(getActivity(), CAMERA_PERMISSIONS)) {
            // Already have permission, do the thing
            getCamera();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "This app needs access to your camera to capture photo", REQUEST_CAMERA, CAMERA_PERMISSIONS);
        }
    }
    //@AfterPermissionGranted(REQUEST_GALLERY)
    private void galleryRequestPermissions() {
        if (EasyPermissions.hasPermissions(getActivity(), GALLERY_PERMISSIONS)) {
            // Already have permission, do the thing
            getGallery();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "This app needs access to your gallery to access images", REQUEST_GALLERY, GALLERY_PERMISSIONS);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> permissions) {
        // Some permissions have been granted
        if (Arrays.equals(permissions.toArray(new String[0]), CAMERA_PERMISSIONS) && requestCode == REQUEST_CAMERA) {
            getCamera();
        }
        if (Arrays.equals(permissions.toArray(new String[0]), GALLERY_PERMISSIONS) && requestCode == REQUEST_GALLERY) {
            getGallery();
        }
    }
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        // Some permissions have been denied
    }
    //====================================================| Permissions Result for Camera, Gallery
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String mImgName = "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";
        if (requestCode == ACTION_PICK_REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null) {
            Uri uri = data.getData();
            Bitmap bitmap = Utility.getInstance().getDownBitmap(getActivity(), uri, 250, 250);
            contactPhoto.setImageBitmap(bitmap);
            String mImagePath = Utility.getInstance().saveToInternalStorage(getActivity(), bitmap, mImgName, "persons");
            mFile = new File(mImagePath, mImgName);
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && currentPhotoPath != null) {
            Uri uri = Uri.fromFile(new File(currentPhotoPath));
            Bitmap bitmap = Utility.getInstance().getDownBitmap(getActivity(), uri, 250, 250);
            contactPhoto.setImageBitmap(bitmap);
            String mImagePath = Utility.getInstance().saveToInternalStorage(getActivity(), bitmap, mImgName, "persons");
            mFile = new File(mImagePath, mImgName);
        }
    }
    private void getCamera() {
        if (getActivity() != null) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                try {
                    File photoFile = createImageFile();
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", photoFile); //BuildConfig.APPLICATION_ID || getActivity().getOpPackageName()
                        //Log.d(TAG, "Image Uri: " + photoURI);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    private File createImageFile() throws IOException {
        if (getActivity() != null) {
            String imageFileName = "JPEG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_";
            File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            //File image = File.createTempFile(imageFileName, prefix ".jpg", suffix storageDir directory );
            currentPhotoPath = image.getAbsolutePath();
            return image;
        }
        return null;
    }
    private void getGallery() {
        startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), ACTION_PICK_REQUEST_CODE);
    }
    */

}
