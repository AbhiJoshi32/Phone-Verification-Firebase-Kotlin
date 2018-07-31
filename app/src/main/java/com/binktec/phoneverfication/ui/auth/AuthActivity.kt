package com.binktec.phoneverfication.ui.auth

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.Toast
import com.binktec.phoneverfication.R
import kotlinx.android.synthetic.main.activity_auth.*
import java.util.concurrent.TimeUnit
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthCredential


class AuthActivity : AppCompatActivity(),View.OnClickListener {
    private val STATE_INITIALIZED = 1
    private val STATE_CODE_SENT = 2
    private val STATE_VERIFY_FAILED = 3
    private val KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress"

    private var a:AlertDialog?=null

    private lateinit var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    private var mVerificationInProgress = false

    private var mVerificationId: String? = null
    private var mResendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var mAuth:FirebaseAuth?=null
    private var phoneNo = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        mAuth = FirebaseAuth.getInstance()
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState)
        }
        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d("Main", "onVerificationCompleted:$credential")

                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
//                Log.w(FragmentActivity.TAG, "onVerificationFailed", e)

                mVerificationInProgress = false
                var errorMsg = ""
                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                    errorMsg = "Wrong Number format"
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    errorMsg = "Try Again Later"
                }
                updateUI(STATE_VERIFY_FAILED,errorMsg)
                // Show a message and update the UI
                // ...
            }

            override fun onCodeSent(verificationId: String?,
                                    token: PhoneAuthProvider.ForceResendingToken?) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("Main", "onCodeSent:" + verificationId!!)

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId
                mResendToken = token
                updateUI(STATE_CODE_SENT)
                // ...
            }
        }
        next.setOnClickListener(this)
        new_number.setOnClickListener(this)
        verify_btn.setOnClickListener(this)
        resend_btn.setOnClickListener(this)
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth?.currentUser
        if (currentUser != null) {
            startActivity(Intent(this,PrefernceActivity::class.java))
            finish()
        }
        // [START_EXCLUDE]
        if (mVerificationInProgress) {
            startPhoneNumberVerification(phone_no.text.toString())
        }
        // [END_EXCLUDE]
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        // [START start_phone_auth]
        Log.d("Main","Strart phoen  number veri")

        buildAlertDialog("Sending OTP to \n" + phone_no.text.toString())
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber, // Phone number to verify
                60, // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                this, // Activity (for callback binding)
                mCallbacks)        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true
    }


    private fun updateUI(state: Int,msg: String = "") {
        Log.d("Main","Update UI ")
        a?.dismiss()
        when(state) {
            STATE_INITIALIZED ->{
                showViews(phone_text_input,terms_check_box,next)
                hideViews(otp_text,number_text,code_text_input,verify_btn,resend_btn,new_number)
            }
            STATE_CODE_SENT -> {
                hideViews(phone_text_input,terms_check_box,next,new_number)
                number_text.text = phone_no.text.toString()
                showViews(otp_text,number_text,code_text_input,verify_btn,resend_btn)
            }
            STATE_VERIFY_FAILED -> {
                showError(msg)
            }
        }
    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        // [START verify_with_code]
        if (verificationId != null) {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            // [END verify_with_code]
            signInWithPhoneAuthCredential(credential)
        }
    }

    // [START resend_verification]
    private fun resendVerificationCode(phoneNumber: String,
                                       token: PhoneAuthProvider.ForceResendingToken?) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber, // Phone number to verify
                60, // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                this, // Activity (for callback binding)
                mCallbacks, // OnVerificationStateChangedCallbacks
                token)             // ForceResendingToken from callbacks
    }



    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        buildAlertDialog("Veryfying Number")
        mAuth?.signInWithCredential(credential)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
//                         Sign in success, update UI with the signed-in user's information
//                                Log.d(FragmentActivity.TAG, "signInWithCredential:success")
//                        val user = task.result.user
                        startActivity(Intent(this,PrefernceActivity::class.java))
                        finish()
                    } else {
                        // Sign in failed, display a message and update the UI
        //                        Log.w(FragmentActivity.TAG, "signInWithCredential:failure", task.exception)
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
//                            showError("Invalid Code")
                            updateUI(STATE_VERIFY_FAILED,"Invalid Code")
                        }
                    }
                }
    }

    private fun showError(s:String) {
        Toast.makeText(this,s,Toast.LENGTH_LONG).show()
    }

    private fun hideViews(vararg views: View) {
        for (v in views) {
            v.visibility = View.GONE
        }
    }

    private fun showViews(vararg views: View) {
        for (v in views) {
            v.visibility = View.VISIBLE
        }
    }

    override fun onClick(v: View?) {
//        Log.d("Main",view.id)
        when(v?.id) {
            R.id.next -> {
                Log.d("Main","Nexr clicked")
                startPhoneNumberVerification(phone_no.text.toString())
            }
            R.id.verify_btn -> {
                val code = code.text.toString()
                verifyPhoneNumberWithCode(mVerificationId,code)
            }
            R.id.resend_btn -> {
                resendVerificationCode(phoneNo, mResendToken)
            }
            R.id.new_number -> {
                updateUI(STATE_INITIALIZED)
            }
        }
    }

    private fun buildAlertDialog(title:String) {
        val b = AlertDialog.Builder(this)
        b.setTitle(title).setCancelable(false)
//        b.setMessage("")
        a = b.create()
        a?.show()
    }
}
