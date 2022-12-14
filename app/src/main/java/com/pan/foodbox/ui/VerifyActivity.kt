package com.pan.foodbox.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.IntegrityTokenRequest
import com.google.android.play.core.integrity.IntegrityTokenResponse
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.pan.foodbox.fragment.ProcessingDialog
import com.pan.foodbox.databinding.ActivityVerifyBinding
import java.util.concurrent.TimeUnit


class VerifyActivity : BaseActivity() {
    private var currentStep = 0
    private lateinit var binding: ActivityVerifyBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var mVerificationId: String? = null
    private var mResendToken: PhoneAuthProvider.ForceResendingToken? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

/*

        //google play integrity

        // Receive the nonce from the secure server.
        val nonce: String =""

        // Create an instance of a manager.
        val integrityManager =
            IntegrityManagerFactory.create(applicationContext)

        // Request the integrity token by providing a nonce.
        val integrityTokenResponse: Task<IntegrityTokenResponse> =
            integrityManager.requestIntegrityToken(
                IntegrityTokenRequest.builder()
                    .setNonce(nonce)
                    .build()
            )
        // base64OfEncodedDecryptionKey is provided through Play Console.
        var decryptionKeyBytes: ByteArray =
            Base64.decode(base64OfEncodedDecryptionKey, Base64.DEFAULT)

        // Deserialized encryption (symmetric) key.
        var decryptionKey: SecretKey = SecretKeySpec(
            decryptionKeyBytes,
            */
/* offset= *//*
 0,
            AES_KEY_SIZE_BYTES,
            AES_KEY_TYPE
        )

        // base64OfEncodedVerificationKey is provided through Play Console.
        var encodedVerificationKey: ByteArray =
            Base64.decode(base64OfEncodedVerificationKey, Base64.DEFAULT)

        // Deserialized verification (public) key.
        var verificationKey: PublicKey = KeyFactory.getInstance(EC_KEY_TYPE)
            .generatePublic(X509EncodedKeySpec(encodedVerificationKey))


*/



        binding.stepView.setStepsNumber(3)
        binding.stepView.go(0, true)
        binding.layout1.visibility = View.VISIBLE

        binding.submit1.setOnClickListener {

            if (checkNetwork()) {
                val phoneNumber = binding.phoneNumber.text.toString()
                binding.phonenumberText.text = phoneNumber

                if (phoneNumber.isBlank()) {
                    binding.phoneLayout.error = "Enter a Phone Number"
                    binding.phoneNumber.requestFocus()
                } else if (phoneNumber.length < 10) {
                    binding.phoneNumber.error = "Please enter a valid Phone Number"
                    binding.phoneNumber.requestFocus()
                } else {
                    if (currentStep < binding.stepView.stepCount - 1) {
                        currentStep++
                        binding.stepView.go(currentStep, true)
                    } else {
                        binding.stepView.done(true)
                    }
                    //  startSmartUserConsent()
                    binding.layout1.visibility = View.GONE
                    binding.layout2.visibility = View.VISIBLE
                    sendVerificationCode(phoneNumber) // code ????????????
                }
            } else {
                Toast.makeText(
                    this@VerifyActivity,
                    "Please check your internet connection!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.submit2.setOnClickListener {
            val verificationCode = binding.pinView.text.toString()
            if (verificationCode.isEmpty()) {
                Toast.makeText(this, "Enter verification code", Toast.LENGTH_SHORT).show()
            } else {
                binding.progress.visibility = View.VISIBLE
                verifyCode(verificationCode)
            }
        }
        binding.submit3.setOnClickListener {
            if (currentStep < binding.stepView.stepCount - 1) {
                currentStep++
                binding.stepView.go(currentStep, true)
            } else {
                binding.stepView.done(true)
            }
            val dialog = ProcessingDialog()
            dialog.show(supportFragmentManager, "customDialog")
        }
    }

    private fun sendVerificationCode(phoneNumber: String) {

        val optionBuilder = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallBack)

        if (mResendToken != null) { // code ???????????????????????????????????? resendToken ?????????
            optionBuilder.setForceResendingToken(mResendToken!!)
        }

        PhoneAuthProvider.verifyPhoneNumber(optionBuilder.build())
    }

    private val mCallBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(phoneAuthCreditial: PhoneAuthCredential) {
            val code: String = phoneAuthCreditial.smsCode.toString()
            Toast.makeText(this@VerifyActivity, code, Toast.LENGTH_SHORT).show()
            binding.pinView.append(code)
            verifyCode(code)
        }

        override fun onVerificationFailed(p0: FirebaseException) {
            Toast.makeText(
                this@VerifyActivity,
                "${p0.message}",
                Toast.LENGTH_SHORT
            )
                .show()
        }

        override fun onCodeSent(
            verificationId: String,
            resendToken: PhoneAuthProvider.ForceResendingToken
        ) {
            mVerificationId = verificationId
            mResendToken = resendToken
            Toast.makeText(
                this@VerifyActivity,
                "Verification code send...",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /* private fun startSmartUserConsent() {
         val client = SmsRetriever.getClient(this@VerifyActivity)
         client.startSmsUserConsent(null)
     }*/


    /*private fun startActivityForResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_USER_CONSENT) {
            if (resultCode == RESULT_OK && data != null) {
                val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                getOtpFromMessage(message)
            }
        }
    }*/

//    private fun getOtpFromMessage(message: String?) {
//
//        val otpPattern = compile("(|^)\\d{6}")
//        val matcher = otpPattern.matcher(message.toString())
//        if (matcher.find()) {
//            binding.pinView.setText(matcher.group(0))
//            Toast.makeText(this@VerifyActivity, message.toString(), Toast.LENGTH_SHORT).show()
//        }
//    }

    /*private fun registerBroadcastReceiver() {
        smsBroadcastReceiver = SmsBroadcastReceiver()
       smsBroadcastReceiver!!.smsBroadcastReceiverListener =
            object : SmsBroadcastReceiver.SmsBroadcastReceiverListener {
                override fun onSuccess(intent: Intent?) {
                    startActivityForResult(intent, REQ_USER_CONSENT)
                }

                override fun onFailure() {
                }
            }

        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsBroadcastReceiver, intentFilter)
    }
*/
    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(mVerificationId.toString(), code)
        signInWithPhoneAuthCredential(credential)
    }

/*    private fun processingDialog() {
        val builder = AlertDialog.Builder(this).create()
        val view = layoutInflater.inflate(R.layout.processing_dialog, null)
        dialogBinding = ProcessingDialogBinding.bind(view)
        builder.setView(view)
        dialogBinding.cancel.setOnClickListener {
            builder.dismiss()
        }
        dialogBinding.btnOk.setOnClickListener {
            dialogBinding.progress.visibility = View.VISIBLE
            val intent = Intent(this@VerifyActivity, RegisterActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        builder.show()
    }*/

    private fun signInWithPhoneAuthCredential(phoneAuthCredential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(phoneAuthCredential)
            .addOnCompleteListener {

                if (it.isSuccessful) {
                    if (currentStep < binding.stepView.stepCount - 1) {
                        currentStep++
                        binding.stepView.go(currentStep, true)
                    } else {
                        binding.stepView.done(true)
                    }
                    binding.layout1.visibility = View.GONE
                    binding.layout2.visibility = View.GONE
                    binding.layout3.visibility = View.VISIBLE
                } else {
                    Toast.makeText(this@VerifyActivity, "Something is wrong!!", Toast.LENGTH_SHORT)
                        .show()
                    binding.progress.progress = 100
                }
            }
    }

    private fun checkNetwork(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)

        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
