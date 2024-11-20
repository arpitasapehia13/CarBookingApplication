package com.example.mycabbooking.ui.user_profile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.cabbooking.Constants
import com.example.cabbooking.model.User
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Objects
import kotlin.math.max
import kotlin.math.min

class UserProfileFragment : Fragment() {
    private var mViewModel: UserProfileViewModel? = null

    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var currentUser: FirebaseUser? = null
    private var mStorageRef: StorageReference? = null
    var currentUserObject: User? = null


    private var profileImgView: ImageView? = null
    private var nameEditText: EditText? = null
    private var emailEditText: EditText? = null
    private var phoneEditText: EditText? = null
    private var dateOfBirthEditText: EditText? = null
    private var maleRadioBtn: RadioButton? = null
    private var femaleRadioBtn: RadioButton? = null
    private var updateBtn: Button? = null
    private var pickerBtn: Button? = null
    private var changePassBtn: Button? = null
    private var year = 0
    private var month = 0
    private var day = 0


    private fun linkViewElements(rootView: View) {
        profileImgView = rootView.findViewById<ImageView>(R.id.image_userAva)
        nameEditText = rootView.findViewById<EditText>(R.id.editText_name)
        emailEditText = rootView.findViewById<EditText>(R.id.editText_email)
        phoneEditText = rootView.findViewById<EditText>(R.id.editText_phone)
        dateOfBirthEditText = rootView.findViewById<EditText>(R.id.editText_DOB)
        maleRadioBtn = rootView.findViewById<RadioButton>(R.id.radioButton_genderMale)
        femaleRadioBtn = rootView.findViewById<RadioButton>(R.id.radioButton_genderFemale)
        updateBtn = rootView.findViewById<Button>(R.id.btn_updateProfle)
        pickerBtn = rootView.findViewById<Button>(R.id.btn_pickDate)
        changePassBtn = rootView.findViewById<Button>(R.id.btn_changePass)
    }

    //date picker dialog for birthday
    private fun setDatePickerBtnAction() {
        val c = Calendar.getInstance()
        year = c[Calendar.YEAR]
        month = c[Calendar.MONTH]
        day = c[Calendar.DAY_OF_MONTH]

        pickerBtn!!.setOnClickListener {
            val datePickerDialog: DatePickerDialog = DatePickerDialog(
                pickerBtn!!.context,
                object : OnDateSetListener {
                    @SuppressLint("SetTextI18n")
                    override fun onDateSet(
                        view: DatePicker,
                        year: Int,
                        monthOfYear: Int,
                        dayOfMonth: Int
                    ) {
                        dateOfBirthEditText.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
                    }
                }, year, month, day
            )
            datePickerDialog.show()
        }
    }

    //Validation after input birth date in the edit text
    private fun setBirthDateEditTextAutoFormat() {
        dateOfBirthEditText.addTextChangedListener(object : TextWatcher {
            private var curDateStr = ""
            private val calendar: Calendar = Calendar.getInstance()
            private val tempYear = calendar[Calendar.YEAR]

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            @SuppressLint("DefaultLocale")
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //Take action at most 1 number is changed at a time.
                if (s.toString() != curDateStr && count == 1) {
                    //Current date string in the edit text, after latest change, without the "/" character
                    var curDateStrAfterChangedWithoutSlash =
                        s.toString().replace("[^\\d.]|\\.".toRegex(), "")
                    //Current date string in the edit text, before the latest change, without the "/" character
                    val curDateStrBeforeChangedWithoutSlash =
                        curDateStr.replace("[^\\d.]|\\.".toRegex(), "")

                    val dateStrAfterChangedLen = curDateStrAfterChangedWithoutSlash.length
                    var cursorPos = dateStrAfterChangedLen //Cursor position

                    var i = 2
                    while (i <= dateStrAfterChangedLen && i < 6) {
                        cursorPos++
                        i += 2
                    }

                    //If delete the slash character "/", move cursor back 1 position
                    if (curDateStrAfterChangedWithoutSlash == curDateStrBeforeChangedWithoutSlash) cursorPos--

                    //If the current date string, after latest change, without slash, is not fully filled
                    if (curDateStrAfterChangedWithoutSlash.length < 8) {
                        val dateFormat = "DDMMYYYY"
                        //
                        curDateStrAfterChangedWithoutSlash = (curDateStrAfterChangedWithoutSlash
                                + dateFormat.substring(curDateStrAfterChangedWithoutSlash.length))
                    } else {
                        //Validate and fix the input date if necessary
                        var day = curDateStrAfterChangedWithoutSlash.substring(0, 2).toInt()
                        var month = curDateStrAfterChangedWithoutSlash.substring(2, 4).toInt()
                        var year = curDateStrAfterChangedWithoutSlash.substring(4, 8).toInt()

                        month = if (month < 1) 1 else min(
                            month.toDouble(),
                            12.0
                        ).toInt() //Max month is 12
                        calendar[Calendar.MONTH] = month - 1

                        year = if ((year < 1900)) 1900 else min(
                            year.toDouble(),
                            tempYear.toDouble()
                        ).toInt() //Max year for birthday is this year
                        calendar[Calendar.YEAR] = year

                        //Get the right day according to the input year and month
                        day = min(
                            day.toDouble(),
                            calendar.getActualMaximum(Calendar.DATE).toDouble()
                        ).toInt()
                        curDateStrAfterChangedWithoutSlash =
                            String.format("%02d%02d%02d", day, month, year)
                    }

                    //finalize the form of displayed date string
                    curDateStrAfterChangedWithoutSlash = String.format(
                        "%s/%s/%s", curDateStrAfterChangedWithoutSlash.substring(0, 2),
                        curDateStrAfterChangedWithoutSlash.substring(2, 4),
                        curDateStrAfterChangedWithoutSlash.substring(4, 8)
                    )

                    //Set date string as text in the EditText view and set the cursor position, update current date string
                    cursorPos = max(cursorPos.toDouble(), 0.0).toInt()
                    curDateStr = curDateStrAfterChangedWithoutSlash
                    dateOfBirthEditText.setText(curDateStr)
                    dateOfBirthEditText.setSelection(
                        min(
                            cursorPos.toDouble(),
                            curDateStr.length.toDouble()
                        )
                    )
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    /**
     * Send intent to open photo gallery
     */
    private fun handleProfileImageClick() {
        profileImgView!!.setOnClickListener {
            val openGalleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(openGalleryIntent, 1000)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                val imgUri = data!!.data
                profileImgView!!.setImageURI(imgUri)
                uploadImageFirebase(imgUri)
            }
        }
    }

    /**
     * Get profile image from firebase
     */
    private fun setImageFromFirebase() {
        val fref: StorageReference =
            mStorageRef.child("profileImages").child(currentUserObject!!.docId + ".jpeg")

        fref.getDownloadUrl().addOnSuccessListener(object : OnSuccessListener<Uri?>() {
            override fun onSuccess(uri: Uri?) {
                Picasso.get().load(uri).into(profileImgView)
            }
        }).addOnFailureListener(object : OnFailureListener() {
            override fun onFailure(exception: Exception) {
            }
        })
    }

    /**
     * Upload profile image
     * @param uri
     */
    private fun uploadImageFirebase(uri: Uri?) {
        val fref: StorageReference =
            mStorageRef.child("profileImages").child(currentUserObject!!.docId + ".jpeg")
        fref.putFile(uri).addOnSuccessListener(
            object : OnSuccessListener<UploadTask.TaskSnapshot?>() {
                override fun onSuccess(taskSnapshot: UploadTask.TaskSnapshot?) {
                    Log.d("USER PROFILE", "onSuccess: upload success")
                    Toast.makeText(activity, "Image Uploaded", Toast.LENGTH_LONG).show()
                }
            }).addOnFailureListener(
            object : OnFailureListener() {
                override fun onFailure(e: Exception) {
                    Log.d("USER PROFILE", "onFalure: upload failed" + e.message)
                }
            }
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_user_profile, container, false)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        mStorageRef = FirebaseStorage.getInstance().getReference()
        currentUser = mAuth.getCurrentUser()

        linkViewElements(view)
        setDatePickerBtnAction()
        setBirthDateEditTextAutoFormat()
        return view
    }

    /**
     * Render user info to view
     */
    private fun renderUserDetails() {
        @SuppressLint("SimpleDateFormat") val df = SimpleDateFormat("MM/dd/yyyy")

        nameEditText.setText(currentUserObject!!.username)
        emailEditText.setText(currentUserObject!!.email)
        phoneEditText.setText(currentUserObject!!.phone)
        dateOfBirthEditText.setText(df.format(currentUserObject!!.birthDate))
        if (currentUserObject!!.gender === "Male") {
            maleRadioBtn.toggle()
        } else {
            femaleRadioBtn.toggle()
        }
    }


    /**
     * Set change password button event listener
     */
    private fun setChangePasswordBtnHandler() {
        changePassBtn!!.setOnClickListener {
            mAuth.sendPasswordResetEmail(Objects.requireNonNull<T>(currentUser.getEmail()))
                .addOnCompleteListener(object : OnCompleteListener<Void?>() {
                    override fun onComplete(task: Task<Void?>) {
                        if (task.isSuccessful()) {
                            Toast.makeText(
                                activity,
                                "Please check your email to receive further instruction",
                                Toast.LENGTH_LONG
                            ).show()
                            Log.d("USER PROFILE", "Email sent.")
                        }
                    }
                })
        }
    }

    /**
     * set update button event handler
     */
    private fun setUpdateBtnHandler() {
        updateBtn!!.setOnClickListener {
            val gender = if (maleRadioBtn.isChecked()) "Male" else "Female"
            val splitBirthDateStr: Array<String> =
                dateOfBirthEditText.getText().toString().split("/".toRegex())
                    .dropLastWhile { it.isEmpty() }.toTypedArray()
            val day = splitBirthDateStr[0].toInt()
            val month = splitBirthDateStr[1].toInt()
            val year = splitBirthDateStr[2].toInt()
            @SuppressLint("SimpleDateFormat") val df =
                SimpleDateFormat("MM/dd/yyyy")
            var birthDateNew: Date? = null
            try {
                birthDateNew = df.parse("$month/$day/$year")
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            val userData: MutableMap<String, Any?> =
                HashMap()
            userData[Constants.FSUser.usernameField] = nameEditText.getText().toString()
            userData[Constants.FSUser.phoneField] = phoneEditText.getText().toString()
            userData[Constants.FSUser.genderField] = gender
            userData[Constants.FSUser.birthDateField] = birthDateNew
            db.collection(Constants.FSUser.userCollection).document(currentUserObject!!.docId)
                .update(userData)
                .addOnCompleteListener(object : OnCompleteListener<Void?>() {
                    override fun onComplete(task: Task<Void?>) {
                        if (task.isSuccessful()) {
                            Toast.makeText(
                                activity!!.applicationContext,
                                "User profile updates",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                        }
                    }
                })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //        setImageFromFirebase();
        handleProfileImageClick()
        setChangePasswordBtnHandler()
        setUpdateBtnHandler()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(requireActivity()).get<UserProfileViewModel>(
            UserProfileViewModel::class.java
        )
        mViewModel!!.currentUserObject.observe(viewLifecycleOwner, object : Observer<User?> {
            override fun onChanged(user: User) {
                currentUserObject = user
                renderUserDetails()
                setImageFromFirebase()
            }
        })
    }

    companion object {
        fun newInstance(): UserProfileFragment {
            return UserProfileFragment()
        }
    }
}
