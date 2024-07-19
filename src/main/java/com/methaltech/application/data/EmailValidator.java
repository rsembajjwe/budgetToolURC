
package com.methaltech.application.data;

import java.util.regex.*;
import org.springframework.stereotype.Component;

@Component
public class EmailValidator {

    String email_regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    String pass_regex = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,20}$";

    public boolean ValidateEmail(String email) {

        Pattern pattern = Pattern.compile(email_regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean isValidPassword(String password) {
        Pattern pattern = Pattern.compile(pass_regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
