package space.lobanov.translate.Login;

import android.content.Context;
import android.widget.Toast;

public class ErrorHandler {
    private final String errorCode;
    private final Context context;

    public ErrorHandler(Context context, String errorCode) {
        this.context = context;
        this.errorCode = errorCode;
    }

    public void showErrorMessage() {
        switch (errorCode) {

            case "ERROR_INVALID_CUSTOM_TOKEN":
                Toast.makeText(context, "The custom token format is incorrect. Please check the documentation.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_CUSTOM_TOKEN_MISMATCH":
                Toast.makeText(context, "The custom token corresponds to a different audience.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_CREDENTIAL":
                Toast.makeText(context, "The supplied auth credential is malformed or has expired.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_EMAIL":
                Toast.makeText(context, "The email address is badly formatted.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_WRONG_PASSWORD":
                Toast.makeText(context, "The password is invalid.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_MISMATCH":
                Toast.makeText(context, "The supplied credentials do not correspond to the previously signed in user.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_REQUIRES_RECENT_LOGIN":
                Toast.makeText(context, "This operation is sensitive and requires recent authentication. Log in again before retrying this request.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                Toast.makeText(context, "An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_EMAIL_ALREADY_IN_USE":
                Toast.makeText(context, "The email address is already in use by another account.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                Toast.makeText(context, "This credential is already associated with a different user account.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_DISABLED":
                Toast.makeText(context, "The user account has been disabled by an administrator.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_TOKEN_EXPIRED":

            case "ERROR_INVALID_USER_TOKEN":
                Toast.makeText(context, "The user's credential is no longer valid. The user must sign in again.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_NOT_FOUND":
                Toast.makeText(context, "There is no user record corresponding to this identifier.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_OPERATION_NOT_ALLOWED":
                Toast.makeText(context, "This operation is not allowed. You must enable this service in the console.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_WEAK_PASSWORD":
                Toast.makeText(context, "The given password is invalid1.", Toast.LENGTH_LONG).show();
                break;
        }
    }
}
