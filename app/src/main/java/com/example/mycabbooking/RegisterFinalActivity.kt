package com.example.mycabbooking


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.android.gms.tasks.OnCompleteListener
import java.text.ParseException
import java.text.SimpleDateFormat

class RegisterFinalActivity : AppCompatActivity() {
    var backBtn: Button? = null
    var registerBtn: Button? = null
    var emailEditText: EditText? = null
    var passwordEditText: EditText? = null

    private var username: String? = null
    private var phone: String? = null
    private var birthDate: String? = null
    private var gender: String? = null
    private var role: String? = null
    private var transportationType: String? = null
    private var vehiclePlateNumber: String? = null
    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_final)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        linkViewElements()
        previousRegisterFormInfo
        setBackBtnAction()
        setRegisterBtnAction()
    }

    //Get View variables from xml id
    private fun linkViewElements() {
        backBtn = findViewById<View>(R.id.registerFinalBackBtn) as Button?
        registerBtn = findViewById<View>(R.id.registerFinalRegisterBtn) as Button?
        emailEditText = findViewById<View>(R.id.registerFinalEmailEditText) as EditText?
        passwordEditText = findViewById<View>(R.id.registerFinalPasswordEditText) as EditText?
    }

    //Go back to RegisterActivity when pressing 'back' button
    private fun setBackBtnAction() {
        backBtn!!.setOnClickListener {
            val i: Intent =
                Intent(
                    this@RegisterFinalActivity,
                    RegisterActivity::class.java
                )
            startActivity(i)
            finish()
        }
    }

    private val previousRegisterFormInfo: Unit
        //Get user input data from previous register activity
        get() {
            val intent: Intent = getIntent()
            username = intent.extras
                .get(Constants.FSUser.usernameField) as String?
            phone = intent.extras
                .get(Constants.FSUser.phoneField) as String?
            birthDate = intent.extras
                .get(Constants.FSUser.birthDateField) as String?
            gender = intent.extras
                .get(Constants.FSUser.genderField) as String?
            role = intent.extras
                .get(Constants.FSUser.roleField) as String?
            transportationType = intent.extras
                .get(Constants.FSUser.transportationType) as String?
            vehiclePlateNumber = intent.extras
                .get(Constants.FSUser.vehiclePlateNumber) as String?
        }

    //Save user data to 'users' collection on firebase
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Throws(ParseException::class)
    private fun saveUserInfo() {
        val splitBirthDateStr =
            birthDate!!.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        val day = splitBirthDateStr[0].toInt()
        val month = splitBirthDateStr[1].toInt()
        val year = splitBirthDateStr[2].toInt()

        @SuppressLint("SimpleDateFormat") val df = SimpleDateFormat("MM/dd/yyyy")
        val birthDateNew = df.parse("$month/$day/$year")

        //Create data hashmap to push to FireStore db
        val data: MutableMap<String, Any?> = HashMap()
        data[Constants.FSUser.usernameField] = username
        data[Constants.FSUser.phoneField] = phone
        data[Constants.FSUser.birthDateField] = birthDateNew
        data[Constants.FSUser.genderField] = gender
        data[Constants.FSUser.emailField] =
            emailEditText.getText().toString()
        data[Constants.FSUser.roleField] = role
        data[Constants.FSUser.transportationType] = transportationType
        data[Constants.FSUser.vehiclePlateNumber] = vehiclePlateNumber
        data[Constants.FSUser.currentPositionLatitude] = 0.0
        data[Constants.FSUser.currentPositionLongitude] = 0.0
        //        data.put(Constants.FSUser.rating, 5.0);
        val rating = ArrayList<Int>()
        rating.add(5)
        data[Constants.FSUser.rating] = rating

        db.collection(Constants.FSUser.userCollection).add(data)
    }

    //Redirect to LoginActivity
    private fun moveToLoginActivity() {
        val i: Intent = Intent(
            this@RegisterFinalActivity,
            LoginActivity::class.java
        )
        startActivity(i)
        finish()
    }

    //
    private fun setRegisterBtnAction() {
        registerBtn!!.setOnClickListener(View.OnClickListener {
            val email: String = emailEditText.getText().toString()
            val password: String = passwordEditText.getText().toString()

            //Check if username and password fields are empty
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    this@RegisterFinalActivity,
                    Constants.ToastMessage.emptyInputError,
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }

            //Call FireStoreAuth for authentication process
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    this@RegisterFinalActivity,
                    OnCompleteListener<Any?> { task ->
                        if (task.isSuccessful) { //If successful
                            Toast.makeText(
                                this@RegisterFinalActivity,
                                Constants.ToastMessage.registerSuccess,
                                Toast.LENGTH_SHORT
                            ).show()
                            try {
                                saveUserInfo()
                            } catch (e: ParseException) {
                                e.printStackTrace()
                            }
                            moveToLoginActivity() // go to login activity
                        } else {
                            Toast.makeText(
                                this@RegisterFinalActivity,
                                Constants.ToastMessage.registerFailure,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
        })
    }
}