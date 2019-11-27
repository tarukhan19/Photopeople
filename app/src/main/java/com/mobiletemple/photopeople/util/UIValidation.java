package com.mobiletemple.photopeople.util;

/**
 * Created by gourav on 9/10/17.
 */

public class UIValidation
{
    public static final String SUCCESS = "success";
    public static final String BLANK_MSG = "Blank Not Allowed !";
    public static final String NAME_LENGTH_MSG = "Length Must be  3-50 !";
    public static final String ADDRESS_LENGTH_MSG = "Length Must be  5-100 !";
    public static final String PASSWORD_LENGTH_MSG = "Length Must be  6-12 !";
    public static final String CPASSWORD_MSG = "Password Not Match !";
    public static final String MOBILE_LENGTH_MSG = "Length Must be  10 !";
    public static final String NUMBER_MSG = "Only Number is Allowed !";
    public static final String EMAIL_MSG = "Invalid Email Format !";
    // Name
    public static String nameValidate(String name, boolean isBlankCheck)
    {
        String msg = SUCCESS;

        if(isBlankCheck) {
            if (name == null || name.isEmpty()) {
                msg = BLANK_MSG;
                return msg;
            }
        }
//        int length = name.length();
//                if(length<3 || length>50)
//                {
//                    msg = NAME_LENGTH_MSG;
//                }
        return msg;
    }

    // Address
    public static String addressValidate(String address, boolean isBlankCheck)
    {
        String msg = SUCCESS;

        if(isBlankCheck) {
            if (address == null || address.isEmpty()) {
                msg = BLANK_MSG;
                return msg;
            }
        }
        int length = address.length();
            if(length<5 || length>100)
            {
                msg = ADDRESS_LENGTH_MSG;
            }
        return msg;
    }

    // Mobile
    public static String mobileValidate(String mobile, boolean isBlankCheck)
    {
        String msg = SUCCESS;

        if(isBlankCheck) {
            if (mobile == null || mobile.isEmpty()) {
                msg = BLANK_MSG;
            }
        }
            if (!mobile.matches("[0-9]+")) {
                msg = NUMBER_MSG;;
            }
            else {
                int length = mobile.length();
                if (length <6) {
                    msg = MOBILE_LENGTH_MSG;
                }
            }
        return msg;
    }

    // Number
    public static String numberValidate(String number, boolean isBlankCheck)
    {
        String msg = SUCCESS;

        if(isBlankCheck) {
            if (number == null || number.isEmpty()) {
                msg = BLANK_MSG;
                return msg;
            }
        }

        if (!number.matches("[0-9]+")) {
            msg = NUMBER_MSG;;
        }

        return msg;
    }

    // Number
    public static String emailValidate(String email, boolean isBlankCheck)
    {
        String msg = SUCCESS;
//
//        if(isBlankCheck) {
//            if (email == null || email.isEmpty()) {
//                msg = BLANK_MSG;
//                return msg;
//            }
//        }

        String emailPattern = "\\S+@\\S+";
        if (!email.matches(emailPattern)) {
            msg = EMAIL_MSG;;
        }

        return msg;
    }


    // Password
    public static String passwordValidate(String password, boolean isBlankCheck)
    {
        String msg = SUCCESS;

        if(isBlankCheck) {
            if (password == null || password.isEmpty()) {
                msg = BLANK_MSG;
                return msg;
            }
        }
        int length = password.length();
            if(length<6 || length>12)
            {
                msg = PASSWORD_LENGTH_MSG;
            }
        return msg;
    }

    // Confirm Password
    public static String confirmPasswordValidate(String cpassword, String password, boolean isBlankCheck)
    {
        String msg = SUCCESS;

        if(isBlankCheck)
        {
            if (cpassword == null || cpassword.isEmpty()) {
                msg = BLANK_MSG;
                return msg;
            }
        }
        int length = cpassword.length();
        if(length<6 || length>12)
        {
            msg = PASSWORD_LENGTH_MSG;
            return msg;
        }

        if(!cpassword.equals(password))
        {
            msg = CPASSWORD_MSG;
            return msg;
        }
        return msg;
    }
}
