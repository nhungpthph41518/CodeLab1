package and103.ph41518.lab1;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginPhoneNumber extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private EditText otpEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone_number);

        mAuth = FirebaseAuth.getInstance();
        otpEditText = findViewById(R.id.editTextOTP);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential); // Đăng nhập tự động khi xác thực thành công
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                // Xử lý khi xác thực thất bại
                Log.w(TAG, "onVerificationFailed", e);
                Toast.makeText(LoginPhoneNumber.this, "Xác thực thất bại", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // Xử lý khi OTP được gửi thành công
                mVerificationId = verificationId;
                Toast.makeText(LoginPhoneNumber.this, "OTP đã được gửi", Toast.LENGTH_SHORT).show();
            }
        };


        Button btnGetOTP = findViewById(R.id.btnGetOTP);
        Button btnLoginOTP = findViewById(R.id.btnLoginOTP);

        btnGetOTP.setOnClickListener(view -> {
            EditText phoneNumberEditText = findViewById(R.id.editTextPhoneNumber);
            String phoneNumber = phoneNumberEditText.getText().toString();
            getOTP(phoneNumber);
        });

        btnLoginOTP.setOnClickListener(view -> {
            String otp = otpEditText.getText().toString();
            verifyOTP(otp);
        });

    }


    private void getOTP(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+84" + phoneNumber, // Số điện thoại cần xác thực
                60, // Độ dài thời gian để người dùng xác thực (60 giây)
                TimeUnit.SECONDS,
                this,
                mCallbacks);
    }


    private void verifyOTP(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginPhoneNumber.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginPhoneNumber.this, Logout.class));
                            finish(); // Kết thúc hoạt động hiện tại sau khi đăng nhập thành công
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(LoginPhoneNumber.this, "Mã OTP không hợp lệ", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginPhoneNumber.this, "Đăng nhập không thành công", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

    }

}







