package com.MwandoJrTechnologies.the_smart_parent.Profile;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.MwandoJrTechnologies.the_smart_parent.NewsFeed.MainActivity;
import com.MwandoJrTechnologies.the_smart_parent.R;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    final static int galleryPick = 1;
    String currentUserID;
    private EditText editTextName;
    private EditText editTextDOB;
    private EditText editTextPhoneNumber;
    private CircleImageView profileImage;
    private Button buttonSave;
    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference usersReference;
    private StorageReference userProfileImageRef;
    private ProgressDialog progressDialog;
    private String downloadImageUrl;
    private AutoCompleteTextView selectYourGenderTextView;
    private ImageView resetSelectedGender;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit profile");

        //getting the user who is logged in as CurrentUser
        mAuth = FirebaseAuth.getInstance();
        //get unique user id
        currentUserID = mAuth.getCurrentUser().getUid();

        usersReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        //specify path in fireBase storage
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("ProfilePictures");

        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        profileImage = findViewById(R.id.profile_image_view);
        editTextName = findViewById(R.id.edit_text_name);
        editTextDOB = findViewById(R.id.edit_text_DOB);
        editTextPhoneNumber = findViewById(R.id.edit_text_phone_number);
        selectYourGenderTextView = findViewById(R.id.edit_profile_gender_selector);
        resetSelectedGender = findViewById(R.id.delete_selected_gender_button);
        buttonSave = findViewById(R.id.buttonSave);

        progressDialog = new ProgressDialog(this);

        //for gender selection
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, getResources()
                .getStringArray(R.array.gender_names));
        final String[] selection = new String[1];
        selectYourGenderTextView.setAdapter(arrayAdapter);
        selectYourGenderTextView.setCursorVisible(false);
        selectYourGenderTextView.setOnItemClickListener((parent, view, position, id) -> {

            selectYourGenderTextView.showDropDown();

            resetSelectedGender.setAlpha(.8f);
        });

        selectYourGenderTextView.setOnClickListener(arg0 -> selectYourGenderTextView.showDropDown());

        resetSelectedGender.setOnClickListener(view -> {
            selectYourGenderTextView.setText(null);
            resetSelectedGender.setAlpha(.2f);
            selection[0] = null;
        });

        //initialize calendar
        Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = (view, year, month, dayOfMonth) -> {

            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);


            String myFormat = "dd/MM/yyyy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);

            editTextDOB.setText(sdf.format(myCalendar.getTime()));

        };

        editTextDOB.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                new DatePickerDialog(EditProfileActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }

            return true;
        });

        buttonSave.setOnClickListener(v -> {

            //show progress dialog
            progressDialog.setTitle("Profile Details");
            progressDialog.setMessage("Updating profile details, Please wait...");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();

            saveUserInformation();

        });
        profileImage.setOnClickListener(v -> {
            //method for choosing an image file
            imageFileChooser();
        });
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //load image from fireBase
                if (dataSnapshot.exists()) {

                    if (dataSnapshot.hasChild("profileImage")) {
                        String image = dataSnapshot.child("profileImage").getValue().toString();
                        Picasso.get()
                                .load(image)
                                .placeholder(R.drawable.profile_image_placeholder)
                                .into(profileImage);

                    } else {
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                                "Please select a profile picture first", Snackbar.LENGTH_LONG);
                        snackbar.show();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * helper method for choosing new image, from camera or gallery
     */

    private void imageFileChooser() {

        CropImage.activity().start(EditProfileActivity.this);
    }

    /**
     * adding a profile image to fireBase storage
     * <p>
     * method for picking the chosen image from my gallery
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                assert result != null;

                //show progress dialog
                progressDialog.setTitle("Profile Image");
                progressDialog.setMessage("Updating profile image, Please wait...");
                progressDialog.setCanceledOnTouchOutside(true);
                progressDialog.show();

                Uri resultUri = result.getUri();

                //Adding to storage by User ID
                final StorageReference filePath = userProfileImageRef
                        .child(resultUri
                                .getLastPathSegment() + currentUserID + ".jpg");

                //now store in fireBase storage
                final UploadTask uploadTask = filePath.putFile(resultUri);

                uploadTask.addOnSuccessListener(taskSnapshot -> {

                    Task<Uri> uri = uploadTask.continueWithTask(task -> {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        //get the url
                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();


                    }).addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            //get the link
                            downloadImageUrl = task.getResult().toString();
                            addLinkToFireBaseDatabase();
                            progressDialog.dismiss();
                        }
                    });
                });
            }
        }
    }

    private void addLinkToFireBaseDatabase() {
        usersReference
                .child("profileImage")
                .setValue(downloadImageUrl)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Snackbar snackbar = Snackbar
                                .make(findViewById(android.R.id.content),
                                        "profile image uploaded successfully uploaded...",
                                        Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else {
                        progressDialog.dismiss();
                        String message = task.getException().getMessage();
                        Snackbar snackbar = Snackbar
                                .make(findViewById(android.R.id.content),
                                        "Error occurred  " + message,
                                        Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    //Validation where both details must be filled and saved to fireBase database
    private void saveUserInformation() {
        final String gender = selectYourGenderTextView.getText().toString().trim();
        String fullName = editTextName.getText().toString().trim();
        String dob = editTextDOB.getText().toString();
        String phoneNumber = editTextPhoneNumber.getText().toString();

        //creating a username and checking if any other exists
        if (gender.isEmpty()) {
            selectYourGenderTextView.setError("Please select your gender");
            selectYourGenderTextView.requestFocus();
            progressDialog.dismiss();
        } else if (fullName.isEmpty()) {
            editTextName.setError("Name is required");
            editTextName.requestFocus();
            progressDialog.dismiss();
        } else if (dob.isEmpty()) {
            editTextDOB.setError("Please select your date of birth");
            progressDialog.dismiss();
        } else if (phoneNumber.isEmpty()) {
            editTextPhoneNumber.setError("Please enter your phone number");
            progressDialog.dismiss();
        } else if (phoneNumber.length() < 10 || phoneNumber.length() > 13) {

            editTextPhoneNumber.setError("Enter a valid phone number");
            progressDialog.dismiss();
        } else {

            progressDialog.setTitle("Uploading Details...");
            progressDialog.setMessage("Saving your information, Please wait...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(true);


            final HashMap<String, Object> userMap = new HashMap<>();
            userMap.put("uid", currentUserID);
            userMap.put("status", "Hey there, I am using this informative SMART PARENT App!!!");
            userMap.put("fullName", fullName);
            userMap.put("phoneNumber", phoneNumber);
            userMap.put("dob", dob);
            userMap.put("gender", gender);
            userMap.put("numberOfChildren", "Insert");
            usersReference.updateChildren(userMap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    SendUserToMainActivity();
                    Snackbar snackbar = Snackbar
                            .make(findViewById(android.R.id.content),
                                    "Your details saved successfully",
                                    Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    progressDialog.dismiss();

                } else {
                    String message = task.getException().getMessage();
                    Snackbar snackbar = Snackbar
                            .make(findViewById(android.R.id.content),
                                    "An error occurred,please try again " + message,
                                    Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    progressDialog.dismiss();
                }
            });
            //start main activity
            finish();
            SendUserToMainActivity();
        }
    }

    //open main activity
    private void SendUserToMainActivity() {
        Intent registerIntent = new Intent(EditProfileActivity.this, MainActivity.class);
        finish();
        startActivity(registerIntent);
    }
}


