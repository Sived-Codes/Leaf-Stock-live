package com.prashant.stockmarketadviser.ui.auth;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.prashant.stockmarketadviser.util.CProgressDialog;
import com.prashant.stockmarketadviser.R;
import com.prashant.stockmarketadviser.databinding.ActivityRegistrationBinding;
import com.prashant.stockmarketadviser.firebase.AuthManager;
import com.prashant.stockmarketadviser.firebase.Constant;
import com.prashant.stockmarketadviser.model.UserModel;
import com.prashant.stockmarketadviser.ui.BaseActivity;

import java.util.Calendar;

public class RegistrationActivity extends BaseActivity {

    ActivityRegistrationBinding binding;
    String selectedGender, selectedPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        setupViews();
        setupListeners();


    }

    private void setupListeners() {
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CProgressDialog.mShow(RegistrationActivity.this);
                String firstName = binding.firstNameEd.getText().toString().trim();
                String lastName = binding.lastNameEd.getText().toString().trim();
                String gender = selectedGender;
                String dateOfBirth = binding.dobEd.getText().toString().trim();
                String mobile = binding.mobileEd.getText().toString().trim();
                String stockPlan = selectedPlan;
                String address = binding.postalAddressEd.getText().toString().trim();
                String email = binding.emailEd.getText().toString().trim();
                String password = binding.passwordEd.getText().toString();
                String confirmPassword = binding.confirmPasswordEd.getText().toString();


                if (firstName.isEmpty()) {
                    binding.firstNameEd.setError("First name cannot be empty");
                    binding.firstNameEd.requestFocus();

                    return;
                }

                if (lastName.isEmpty()) {
                    binding.lastNameEd.setError("Last name cannot be empty");
                    binding.lastNameEd.requestFocus();
                    return;
                }

                if (gender.equals("Select Gender")) {
                    Toast.makeText(RegistrationActivity.this, "Please Select Gender", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (stockPlan.equals("Select Stock Plan")) {
                    Toast.makeText(RegistrationActivity.this, "Please Select Gender", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (dateOfBirth.isEmpty()) {
                    binding.dobEd.setError("Date of birth cannot be empty");
                    binding.dobEd.requestFocus();
                    return;
                }

                if (mobile.isEmpty()) {
                    binding.mobileEd.setError("Mobile number cannot be empty");
                    binding.mobileEd.requestFocus();
                    return;
                }

                if (mobile.length() != 10) {
                    binding.mobileEd.setError("Mobile number should be 10 digits");
                    binding.mobileEd.requestFocus();
                    return;
                }

                if (!email.endsWith("@gmail.com") && !email.endsWith("@yahoo.com") && !email.endsWith("@outlook.com") && !email.endsWith("@hotmail.com")) {
                    binding.emailEd.setError("Invalid email domain");
                    binding.emailEd.requestFocus();
                    return;
                }

                if (email.isEmpty()) {
                    binding.emailEd.setError("Email address cannot be empty");
                    binding.emailEd.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    binding.passwordEd.setError("Password cannot be empty");
                    binding.passwordEd.requestFocus();
                    return;
                }


                if (address.isEmpty()) {
                    binding.postalAddressEd.setError("Address cannot be empty");
                    binding.postalAddressEd.requestFocus();
                    return;
                }

                if (confirmPassword.isEmpty()) {
                    binding.confirmPasswordEd.setError("Confirm password cannot be empty");
                    binding.confirmPasswordEd.requestFocus();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    binding.confirmPasswordEd.setError("Passwords do not match");
                    binding.confirmPasswordEd.requestFocus();
                    return;
                }


                new AuthManager().registerUser(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            String uid = task.getResult().getUser().getUid();
                            UserModel model = new UserModel();
                            model.setFullName(firstName + " " + lastName);
                            model.setGender(gender);
                            model.setMobile(mobile);
                            model.setEmail(email);
                            model.setDateOfBirth(dateOfBirth);
                            model.setAddress(address);
                            model.setUserPlan(stockPlan);
                            model.setUserType("user");
                            model.setUserImage("");
                            model.setUserUid(uid);

                            Constant.userDB.child(uid).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(RegistrationActivity.this, "Registration completed", Toast.LENGTH_SHORT).show();
                                    CProgressDialog.mDismiss();
                                    CProgressDialog.mDismiss();


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(RegistrationActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                    CProgressDialog.mDismiss();
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegistrationActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        CProgressDialog.mDismiss();

                    }
                });


            }
        });


        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        binding.genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selectedGender = getResources().getStringArray(R.array.gender_options)[position];
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    });

        binding.stockPlanEd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selectedPlan = getResources().getStringArray(R.array.plan_options)[position];
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    });

        binding.dobEd.setOnClickListener(v -> showDatePickerDialog());
        binding.dobEd.setOnClickListener(v -> showDatePickerDialog());
    }


    private void setupViews() {
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.gender_options));
        ArrayAdapter<String> planAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.plan_options));
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        planAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.genderSpinner.setAdapter(genderAdapter);
        binding.stockPlanEd.setAdapter(planAdapter);
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear);
                    binding.dobEd.setText(selectedDate);
                },
                year, month, day);

        datePickerDialog.show();
    }

}