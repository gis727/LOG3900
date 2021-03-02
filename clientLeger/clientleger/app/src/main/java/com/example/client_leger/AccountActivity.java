//package com.example.client_leger;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.Base64;
//import android.util.Log;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.TextView;
//
//import com.example.client_leger.authentication.AuthenticationActivity;
//import com.google.android.material.button.MaterialButton;
//
//import java.io.ByteArrayOutputStream;
//import java.util.concurrent.Callable;
//
//public class AccountActivity extends Activity {
//
//    String errorMessage1 = "Some fields have an invalid format";
//    String errorMessage2 = "An internal problem has occurred. Please try again later.";
//    String errorMessage3 = "This username is already taken";
//    String errorMessage4 = "Passwords do not match";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.sign_up_page);
//
//        ((EditText)findViewById(R.id.name)).addTextChangedListener(new TextWatcher() {
//            public void afterTextChanged(Editable s) {}
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                onCredentialsChanged();
//            }
//        });
//        ((EditText)findViewById(R.id.surname)).addTextChangedListener(new TextWatcher() {
//            public void afterTextChanged(Editable s) {}
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                onCredentialsChanged();
//            }
//        });
//        ((EditText)findViewById(R.id.username_layout)).addTextChangedListener(new TextWatcher() {
//            public void afterTextChanged(Editable s) {}
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                onCredentialsChanged();
//            }
//        });
//        ((EditText)findViewById(R.id.password_layout)).addTextChangedListener(new TextWatcher() {
//            public void afterTextChanged(Editable s) {}
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                onCredentialsChanged();
//            }
//        });
//        ((EditText)findViewById(R.id.confirmPassword)).addTextChangedListener(new TextWatcher() {
//            public void afterTextChanged(Editable s) {}
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                onCredentialsChanged();
//            }
//        });
//    }
//
//    public void onCredentialsChanged() {
//        MaterialButton button = findViewById(R.id.signUpButton);
//        TextView alert = findViewById(R.id.alert);
//        final String name = ((EditText)findViewById(R.id.name)).getText().toString().trim();
//        final String surname = ((EditText)findViewById(R.id.surname)).getText().toString().trim();
//        final String username = ((EditText)findViewById(R.id.username_layout)).getText().toString().trim();
//        final String password = ((EditText)findViewById(R.id.password_layout)).getText().toString().trim();
//        final String confirmPassword = ((EditText)findViewById(R.id.confirmPassword)).getText().toString().trim();
//        button.setBackgroundTintList(getResources().getColorStateList(R.color.button_grayed_out));
//        boolean canRegister = username.length()>0 && password.length()>0 && name.length()>0 && surname.length()>0 && confirmPassword.length()>0 && password.matches(confirmPassword);
//
//        if (!password.matches(confirmPassword))
//            alert.setText(errorMessage4);
//        else {
//            alert.setText("");
//        }
//        if (!canRegister) {
//            button.setBackgroundTintList(getResources().getColorStateList(R.color.button_grayed_out));
//            button.setEnabled(false);
//        }
//        else {
//            alert.setText("");
//            button.setEnabled(true);
//            button.setBackgroundTintList(getResources().getColorStateList(R.color.button_colored));
//        }
//    }
//
//    @Override
//    public void onBackPressed() {
//        SocketSingleton.get(getApplicationContext()).dispose();
//        Intent intent = new Intent(AccountActivity.this, AuthenticationActivity.class);
//        startActivity(intent);
//    }
//
//    public void register(View view) {
//        EditText nameField = findViewById(R.id.name);
//        final String name = nameField.getText().toString().trim();
//
//        EditText surnameField = findViewById(R.id.surname);
//        final String surname = surnameField.getText().toString().trim();
//
//        EditText passwordField = findViewById(R.id.password_layout);
//        final String password = passwordField.getText().toString().trim();
//
//        EditText usernameField = findViewById(R.id.username_layout);
//        final String username = usernameField.getText().toString().trim();
//
//        //encode image to base64 string
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.realavatar);
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        byte[] imageBytes = baos.toByteArray();
//        String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
//
////        SocketSingleton.get(getApplicationContext()).OnLogin(
////                new Callable() { // SUCCESS
////                    @Override
////                    public Object call() throws Exception {
////                        Intent intent = new Intent(AccountActivity.this, MainActivity.class);
////                        intent.putExtra("username", username);
////                        intent.putExtra("password", password);
////                        startActivity(intent);
////                        return null;
////                    }
////                }, new Callable() { // STATE == 1
////                    @Override
////                    public Object call() throws Exception {
////                        TextView alert = findViewById(R.id.alert);
////                        alert.setText(errorMessage1);
////                        return null;
////                    }
////                }, new Callable() { // STATE == 2
////                    @Override
////                    public Object call() throws Exception {
////                        TextView alert = findViewById(R.id.alert);
////                        alert.setText(errorMessage2);
////                        return null;
////                    }
////                }, new Callable() { // STATE == 3
////                    @Override
////                    public Object call() throws Exception {
////                        TextView alert = findViewById(R.id.alert);
////                        alert.setText(errorMessage3);
////                        return null;
////                    }
////                });
//
//        SocketSingleton.get(getApplicationContext()).OnCreateAccount(
//               new Callable() { // STATE == 1
//                    @Override
//                    public Object call() throws Exception {
//                        TextView alert = findViewById(R.id.alert);
//                        alert.setText(errorMessage1);
//                        Log.i("ALERT1: ", alert.getText().toString().trim());
//                        return null;
//                    }
//                }, new Callable() { // STATE == 2
//                    @Override
//                    public Object call() throws Exception {
//                        TextView alert = findViewById(R.id.alert);
//                        alert.setText(errorMessage2);
//                        Log.i("ALERT2: ", alert.getText().toString().trim());
//                        return null;
//                    }
//                }, new Callable() { // STATE == 3
//                    @Override
//                    public Object call() throws Exception {
//                        TextView alert = findViewById(R.id.alert);
//                        alert.setText(errorMessage3);
//                        Log.i("ALERT3", alert.getText().toString().trim());
//                        return null;
//                    }
//                });
//        SocketSingleton.get(getApplicationContext()).CreateAccount(name, surname, password, username, "data:image/png;base64," + imageString);
//    }
//}
