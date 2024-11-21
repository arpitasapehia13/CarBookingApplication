package com.example.mycabbooking


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar
import kotlin.math.max
import kotlin.math.min

class RegisterActivity : AppCompatActivity() {
    var birthDateEditText: EditText? = null
    var usernameEditText: EditText? = null
    var phoneEditText: EditText? = null
    var registerVehiclePlateNumberEditText: EditText? = null
    var backBtn: Button? = null
    var nextBtn: Button? = null
    var datePickerBtn: Button? = null
    var maleRadioBtn: RadioButton? = null
    var femaleRadioBtn: RadioButton? = null
    var roleGroup: RadioGroup? = null
    var driverRadioBtn: RadioButton? = null
    var customerRadioBtn: RadioButton? = null
    var transportationTypeGroup: RadioGroup? = null
    var registerCarRadioBtn: RadioButton? = null
    var registerBikeRadioBtn: RadioButton? = null
    private var year = 0
    private var month = 0
    private var day = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        linkViewElements()
        setRoleGroupBtnActionHandler()
        setBackBtnAction()
        setDatePickerBtnAction()
        setNextBtnAction()
        setBirthDateEditTextAutoFormat()
    }

    //Get View variables from xml id
    private fun linkViewElements() {
        birthDateEditText = findViewById<View>(R.id.registerBirthEditText) as EditText?
        usernameEditText = findViewById<View>(R.id.registerUsernameEditText) as EditText?
        phoneEditText = findViewById<View>(R.id.registerPhoneEditText) as EditText?
        backBtn = findViewById<View>(R.id.registerBackBtn) as Button?
        nextBtn = findViewById<View>(R.id.registerFinalRegisterBtn) as Button?
        datePickerBtn = findViewById<View>(R.id.registerPickdateBtn) as Button?
        maleRadioBtn = findViewById<View>(R.id.registerMaleRadioBtn) as RadioButton?
        femaleRadioBtn = findViewById<View>(R.id.registerFemaleRadioBtn) as RadioButton?
        roleGroup = findViewById<View>(R.id.roleGroup) as RadioGroup?
        customerRadioBtn = findViewById<View>(R.id.registerCustomerRadioBtn) as RadioButton?
        driverRadioBtn = findViewById<View>(R.id.registerDriverRadioBtn) as RadioButton?
        transportationTypeGroup = findViewById<View>(R.id.transportationTypeGroup) as RadioGroup?
        registerCarRadioBtn = findViewById<View>(R.id.registerCarRadioBtn) as RadioButton?
        registerBikeRadioBtn = findViewById<View>(R.id.registerBikeRadioBtn) as RadioButton?
        registerVehiclePlateNumberEditText =
            findViewById<View>(R.id.registerVehiclePlateNumberEditText) as EditText?
    }

    private fun setRoleGroupBtnActionHandler() {
        roleGroup?.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            @SuppressLint("NonConstantResourceId")
            override fun onCheckedChanged(group: RadioGroup, checkedId: Int) {
                when (checkedId) {
                    R.id.registerCustomerRadioBtn -> {
                        transportationTypeGroup?.setVisibility(View.INVISIBLE)
                        registerVehiclePlateNumberEditText?.setVisibility(View.INVISIBLE)
                    }

                    R.id.registerDriverRadioBtn -> {
                        transportationTypeGroup?.setVisibility(View.VISIBLE)
                        registerVehiclePlateNumberEditText?.setVisibility(View.VISIBLE)
                    }
                }
            }
        })
    }


    //Move back to startActivity when pressing 'back' button
    private fun setBackBtnAction() {
        backBtn!!.setOnClickListener {
            val i: Intent =
                Intent(
                    this@RegisterActivity,
                    StartActivity::class.java
                )
            startActivity(i)
            finish()
        }
    }

    //Move to RegisterFinalActivity when pressing 'next', also passing inputted data of user
    private fun setNextBtnAction() {
        nextBtn!!.setOnClickListener {
            val username: String = usernameEditText?.getText().toString()
            val phone: String = phoneEditText?.getText().toString()
            val birthDate: String = birthDateEditText?.getText().toString()
            val gender = if (maleRadioBtn?.isChecked() == true) "Male" else "Female"
            val role = if (customerRadioBtn?.isChecked() == true) "Customer" else "Driver"
            var transportationType = ""
            if (driverRadioBtn?.isChecked() == true) {
                transportationType = if (registerCarRadioBtn?.isChecked() == true) "car" else "bike"
            }
            val vehiclePlateNumber: String =
                registerVehiclePlateNumberEditText?.getText().toString()

            //Check empty input
            if (checkEmptyInput(username, phone, birthDate)) {
                Toast.makeText(
                    this@RegisterActivity,
                    Constants.ToastMessage.emptyInputError,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                //Transfer data
                val i: Intent =
                    Intent(
                        this@RegisterActivity,
                        RegisterFinalActivity::class.java
                    )
                i.putExtra(Constants.FSUser.usernameField, username)
                i.putExtra(Constants.FSUser.phoneField, phone)
                i.putExtra(Constants.FSUser.birthDateField, birthDate)
                i.putExtra(Constants.FSUser.genderField, gender)
                i.putExtra(Constants.FSUser.roleField, role)
                i.putExtra(
                    Constants.FSUser.transportationType,
                    transportationType
                )
                i.putExtra(
                    Constants.FSUser.vehiclePlateNumber,
                    vehiclePlateNumber
                )
                startActivity(i)
                finish()
            }
        }
    }

    //Check if one of the input is empty
    private fun checkEmptyInput(username: String, phone: String, birthDate: String): Boolean {
        return username.isEmpty() || phone.isEmpty() || birthDate.length < 9 || birthDate.contains("D") || birthDate.contains(
            "M"
        ) || birthDate.contains("Y")
    }

    //date picker dialog for birthday
    private fun setDatePickerBtnAction() {
        val c = Calendar.getInstance()
        year = c[Calendar.YEAR]
        month = c[Calendar.MONTH]
        day = c[Calendar.DAY_OF_MONTH]

        datePickerBtn!!.setOnClickListener {
            val datePickerDialog: DatePickerDialog = DatePickerDialog(
                datePickerBtn!!.context,
                object : DatePickerDialog.OnDateSetListener {
                    @SuppressLint("SetTextI18n")
                    override fun onDateSet(
                        view: DatePicker,
                        year: Int,
                        monthOfYear: Int,
                        dayOfMonth: Int
                    ) {
                        birthDateEditText?.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
                    }
                }, year, month, day
            )
            datePickerDialog.show()
        }
    }

    //Validation after input birth date in the edit text
    private fun setBirthDateEditTextAutoFormat() {
        birthDateEditText?.addTextChangedListener(object : TextWatcher {
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
                    birthDateEditText?.setText(curDateStr)
                    birthDateEditText?.setSelection(
                        min(
                            cursorPos.toDouble(),
                            curDateStr.length.toDouble()
                        ).toInt()
                    )
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
    }
}
