package com.example.quickcash.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.quickcash.R;
import com.example.quickcash.models.Job;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/**
 * ComposeFragment
 *
 * This is our Compose Fragment. In here, the user can compose
 * a job post into the Parse DB.
 *
 * @author Patrick Amaro Rivera
 */

public class ComposeFragment extends Fragment {

    public static final String TAG = "ComposeFragment";

    private ImageView homeImage;
    private EditText etName;
    private EditText etDescription;
    private EditText etAddress;
    private EditText etPrice;
    private EditText etJobDate;
    private EditText etJobTime;
    private Button btnCompose;
    private Button btnTakeImage;
    private ImageView ivImage;
    private File photoFile;
    private String photoFileName = "photo.jpg";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;


    public ComposeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        homeImage = view.findViewById(R.id.cf_homeImage);
        etName = view.findViewById(R.id.et_name);
        etDescription = view.findViewById(R.id.et_description);
        etJobDate = view.findViewById(R.id.et_date);
        etJobTime = view.findViewById(R.id.et_time);
        etAddress = view.findViewById(R.id.et_address);
        etPrice = view.findViewById(R.id.et_price);
        btnCompose = view.findViewById(R.id.btn_composeJob);
        btnTakeImage = view.findViewById(R.id.btn_takeJobPic);
        ivImage = view.findViewById(R.id.iv_optionImage);

        Glide.with(getContext()).load(R.drawable.logo).placeholder(R.drawable.ic_launcher_foreground).into(homeImage);

        etJobDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        etJobTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);
                TimePickerDialog dialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                                etJobTime.setText(hourOfDay + ":" + minute);
                            }
                        }, hourOfDay, minute, true);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Log.d(TAG, "onDateSet date: mm/dd/yyyy: " + month + "/" +day + "/" + year);
                String date = month + "/" + day + "/" + year;
                etJobDate.setText(date);
            }
        };


        btnTakeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });
        /**
         * This entire method creates a new job post once user has filled all necessary information.
         * This gets the information provided in the editTexts and sends a request to create a new
         * Job post into the Parse DB.
         */
        btnCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etName.getText().toString();
                String description = etDescription.getText().toString();
                Date date = null;
                try {
                    date = convertStringtoDate(etJobDate.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String address = etAddress.getText().toString();
                Double price = Double.parseDouble(etPrice.getText().toString());
                if(name.isEmpty() || description.isEmpty() || date.equals(null) || address.isEmpty() || price.equals(null)){
                    Toast.makeText(getContext(), " Missing required parts", Toast.LENGTH_SHORT).show();
                    return;
                }
                ParseUser currentUser = ParseUser.getCurrentUser();
                Job job = new Job();
                job.setName(name);
                job.setDescription(description);
                job.setAddress(address);
                job.setPrice(price.doubleValue());
                job.setJobDate(date);
                job.setUser(currentUser);
                if(photoFile != null){
                    job.setImage(new ParseFile(photoFile));
                }
                job.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(com.parse.ParseException e) {
                        if(e!= null){
                            Log.e(TAG, "Error while saving", e);
                        }
                        Log.i(TAG, "Job was successful!");
                        etName.setText("");
                        etDescription.setText("");
                        etAddress.setText("");
                        etJobDate.setText("");
                        etPrice.setText("");
                        ivImage.setImageResource(0);
                    }
                });
            }
        });

    }

    /**
     * This methods launches the Android Camera in our application. This is used if the user
     * wants to a put an optional Image for their Job.
     */
    public void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider.QuickCash", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivImage.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * This method converts a date string of format mm/dd/yyyy to a Date object.
     * @param stringDate
     * @return Date
     * @throws ParseException
     */
    public Date convertStringtoDate(String stringDate) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        Date date= format.parse(stringDate);
        return date;
    }
}