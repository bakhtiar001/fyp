package com.example.appscandiycrafts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private Button registerbutton;
    private EditText nameEdtText, emailEdtText, passwordEdtText, birthdayEdtText;
    private RadioGroup sexRadioG;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();


        DatePickerDialog datePicker;
        registerbutton = findViewById(R.id.registerButton);
        nameEdtText = findViewById(R.id.nameEditText);
        emailEdtText = findViewById(R.id.emailEditText);
        passwordEdtText = findViewById(R.id.passwordEditText);
        birthdayEdtText = findViewById(R.id.birthdayEditText);
        sexRadioG = findViewById(R.id.sexRadioGroup);

        // set an OnClickListener to the EditText
        birthdayEdtText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the current date
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                // create a DatePickerDialog and show it
                DatePickerDialog datePickerDialog = new DatePickerDialog(RegistrationActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // set the selected date to the EditText
                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        birthdayEdtText.setText(selectedDate);
                    }
                }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedId = sexRadioG.getCheckedRadioButtonId();
                String strGender="";

                if (selectedId == R.id.maleRadioButton) {
                    strGender = "Male";
                } else if (selectedId == R.id.femaleRadioButton) {
                    strGender = "Female";
                } else { Toast.makeText(RegistrationActivity.this, "Choose gender", Toast.LENGTH_SHORT).show();}



                String name = nameEdtText.getText().toString();
                String email = emailEdtText.getText().toString();
                String password = passwordEdtText.getText().toString();
                String birthday = birthdayEdtText.getText().toString();
                String sex = strGender;


                if(TextUtils.isEmpty(name)||TextUtils.isEmpty(email)||TextUtils.isEmpty(password)||TextUtils.isEmpty(birthday)||TextUtils.isEmpty(sex)){
                    Toast.makeText(RegistrationActivity.this, "Please fill all the field", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // User registration successful
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        String uid = user.getUid();

                                        // Create a new document in the users collection of Firestore with the user's information
                                        Map<String, Object> userData = new HashMap<>();
                                        userData.put("name", name);
                                        userData.put("email", email);
                                        userData.put("password", password);
                                        userData.put("birthday", birthday);
                                        userData.put("sex", sex);
                                        db.collection("users").document(uid).set(userData);
                                                /*.addOnSuccessListener(aVoid -> {
                                                    // User document created successfully, now create the "favourite" subcollection
                                                    CollectionReference favouriteRef = db.collection("users").document(uid).collection("favourite");

                                                    Intent intent = new Intent(RegistrationActivity.this, SignInActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                })
                                                .addOnFailureListener(e -> {
                                                    // Failed to create user document
                                                    Toast.makeText(RegistrationActivity.this, "Failed to create user document", Toast.LENGTH_SHORT).show();
                                                });*/

                                        Intent intent = new Intent(RegistrationActivity.this, SignInActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // User registration failed
                                        Toast.makeText(RegistrationActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }

            }
        });
    }
}