package com.apps.fatima.sealocation.manager;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Button;
import android.widget.TextView;


import com.apps.fatima.sealocation.R;

import java.util.Locale;

public class AppErrorsManager {


    public static void showErrorDialog(Activity context, String error) {
        if (context != null && !context.isFinishing()) {

            AlertDialog alertDialogBuilder = new AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.error))
                    .setMessage(error)
                    .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();

            TextView textView = alertDialogBuilder.findViewById(android.R.id.message);
            TextView alertTitle = alertDialogBuilder.findViewById(R.id.alertTitle);
            Button button1 = alertDialogBuilder.findViewById(android.R.id.button1);
            Button button2 = alertDialogBuilder.findViewById(android.R.id.button2);
            if (textView != null) {
                textView.setLineSpacing(0, 1.5f);
            }
            if (Locale.getDefault().getLanguage().equals("ar")) {
                FontManager.applyFont(context, textView);
                FontManager.applyFont(context, alertTitle);
                FontManager.applyFont(context, button1);
                FontManager.applyFont(context, button2);
            }
        }
    }

//    public static void showErrorDialog(Activity context, String error, DialogInterface.OnClickListener okClickListener) {
//        if (context != null && !context.isFinishing()) {
//            AlertDialog alertDialogBuilder = new AlertDialog.Builder(context)
//                    .setTitle(R.string.error).setMessage(error).setPositiveButton(R.string.ok, okClickListener).show();
//
//            TextView textView = (TextView) alertDialogBuilder.findViewById(android.R.id.message);
//            TextView alertTitle = (TextView) alertDialogBuilder.findViewById(R.id.alertTitle);
//            Button button1 = (Button) alertDialogBuilder.findViewById(android.R.id.button1);
//            Button button2 = (Button) alertDialogBuilder.findViewById(android.R.id.button2);
//            if (textView != null) {
//                textView.setLineSpacing(0, 1.5f);
//            }
//            Utils.applyFont(context, textView);
//            Utils.applyFont(context, alertTitle);
//            Utils.applyFont(context, button1);
//            Utils.applyFont(context, button2);
//        }
//    }

//    public static void showSuccessDialog(Activity context, String error) {
//        if (context != null && !context.isFinishing()) {
//            AlertDialog alertDialogBuilder = new AlertDialog.Builder(context)
//                    .setTitle(context.getString(R.string.info)).setMessage(error).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//
//                            dialog.dismiss();
//                        }
//                    }).show();
//
//            TextView textView = (TextView) alertDialogBuilder.findViewById(android.R.id.message);
//            TextView alertTitle = (TextView) alertDialogBuilder.findViewById(R.id.alertTitle);
//            Button button1 = (Button) alertDialogBuilder.findViewById(android.R.id.button1);
//            Button button2 = (Button) alertDialogBuilder.findViewById(android.R.id.button2);
//            if (textView != null) {
//                textView.setLineSpacing(0, 1.5f);
//            }
//            Utils.applyFont(context, textView);
//            Utils.applyFont(context, alertTitle);
//            Utils.applyFont(context, button1);
//            Utils.applyFont(context, button2);
//        }
//    }
//
//    public static void showSuccessDialogReport(Activity context, String error) {
//        if (context != null && !context.isFinishing()) {
//            AlertDialog alertDialogBuilder = new AlertDialog.Builder(context)
//                    .setMessage(error).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//
//                            dialog.dismiss();
//                        }
//                    }).show();
//
//            TextView textView = (TextView) alertDialogBuilder.findViewById(android.R.id.message);
//            TextView alertTitle = (TextView) alertDialogBuilder.findViewById(R.id.alertTitle);
//            Button button1 = (Button) alertDialogBuilder.findViewById(android.R.id.button1);
//            Button button2 = (Button) alertDialogBuilder.findViewById(android.R.id.button2);
//            if (textView != null) {
//                textView.setLineSpacing(0, 1.5f);
//            }
//            Utils.applyFont(context, textView);
//            Utils.applyFont(context, alertTitle);
//            Utils.applyFont(context, button1);
//            Utils.applyFont(context, button2);
//        }
//    }
//
//    public static void showSuccessDialog(Activity context, String title, String error) {
//        if (context != null && !context.isFinishing()) {
//            AlertDialog alertDialogBuilder = new AlertDialog.Builder(context)
//                    .setTitle(title).setMessage(error).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//
//                            dialog.dismiss();
//                        }
//                    }).show();
//
//            TextView textView = (TextView) alertDialogBuilder.findViewById(android.R.id.message);
//            TextView alertTitle = (TextView) alertDialogBuilder.findViewById(R.id.alertTitle);
//            Button button1 = (Button) alertDialogBuilder.findViewById(android.R.id.button1);
//            Button button2 = (Button) alertDialogBuilder.findViewById(android.R.id.button2);
//            if (textView != null) {
//                textView.setLineSpacing(0, 1.5f);
//            }
//            Utils.applyFont(context, textView);
//            Utils.applyFont(context, alertTitle);
//            Utils.applyFont(context, button1);
//            Utils.applyFont(context, button2);
//        }
//    }
//
//    public static void showSuccessDialog(Activity context, String error, DialogInterface.OnClickListener okClickListener) {
//        if (context != null && !context.isFinishing()) {
//            AlertDialog alertDialogBuilder = new AlertDialog.Builder(context)
//                    .setTitle(context.getString(R.string.info)).setMessage(error).setPositiveButton(R.string.ok, okClickListener)
//                    .show();
//
//            TextView textView = (TextView) alertDialogBuilder.findViewById(android.R.id.message);
//            TextView alertTitle = (TextView) alertDialogBuilder.findViewById(R.id.alertTitle);
//            Button button1 = (Button) alertDialogBuilder.findViewById(android.R.id.button1);
//            Button button2 = (Button) alertDialogBuilder.findViewById(android.R.id.button2);
//            if (textView != null) {
//                textView.setLineSpacing(0, 1.5f);
//            }
//            Utils.applyFont(context, textView);
//            Utils.applyFont(context, alertTitle);
//            Utils.applyFont(context, button1);
//            Utils.applyFont(context, button2);
//        }
//    }

    public static void showSuccessDialog(Activity context, String title, String error,
                                         DialogInterface.OnClickListener okClickListener,
                                         DialogInterface.OnClickListener cancelClickListener) {
        if (context != null && !context.isFinishing()) {
            AlertDialog alertDialogBuilder = new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(error)
                    .setPositiveButton(context.getString(R.string.confirm), okClickListener).setNegativeButton(context.getString(R.string.cancel), cancelClickListener)
                    .show();

            TextView textView = alertDialogBuilder.findViewById(android.R.id.message);
            TextView alertTitle = alertDialogBuilder.findViewById(R.id.alertTitle);
            Button button1 = alertDialogBuilder.findViewById(android.R.id.button1);
            Button button2 = alertDialogBuilder.findViewById(android.R.id.button2);
            if (textView != null) {
                textView.setLineSpacing(0, 1.5f);
            }
            if (Locale.getDefault().getLanguage().equals("ar")) {
                FontManager.applyFont(context, textView);
                FontManager.applyFont(context, alertTitle);
                FontManager.applyFont(context, button1);
                FontManager.applyFont(context, button2);
            }
        }
    }

    public static void showSuccessDialog(Activity context, String error,
                                         DialogInterface.OnClickListener okClickListener) {
        if (context != null && !context.isFinishing()) {
            AlertDialog alertDialogBuilder = new AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.info))
                    .setMessage(error)
                    .setPositiveButton(context.getString(R.string.ok), okClickListener)
                    .show();

            TextView textView = alertDialogBuilder.findViewById(android.R.id.message);
            TextView alertTitle = alertDialogBuilder.findViewById(R.id.alertTitle);
            Button button1 = alertDialogBuilder.findViewById(android.R.id.button1);
            Button button2 = alertDialogBuilder.findViewById(android.R.id.button2);
            if (textView != null) {
                textView.setLineSpacing(0, 1.5f);
            }
            if (Locale.getDefault().getLanguage().equals("ar")) {
                FontManager.applyFont(context, textView);
                FontManager.applyFont(context, alertTitle);
                FontManager.applyFont(context, button1);
                FontManager.applyFont(context, button2);
            }
        }
    }
}

//    public static void deleteSuccessDialog(Activity context, String error, DialogInterface.OnClickListener okClickListener, DialogInterface.OnClickListener cancelClickListener) {
//        if (context != null && !context.isFinishing()) {
//            AlertDialog alertDialogBuilder = new AlertDialog.Builder(context)
//                    .setTitle(context.getString(R.string.error))
//                    .setMessage(error)
//                    .setPositiveButton(R.string.ok, okClickListener)
//                    .setNegativeButton(R.string.cancel, cancelClickListener).show();
//
//            TextView textView = (TextView) alertDialogBuilder.findViewById(android.R.id.message);
//            TextView alertTitle = (TextView) alertDialogBuilder.findViewById(R.id.alertTitle);
//            Button button1 = (Button) alertDialogBuilder.findViewById(android.R.id.button1);
//            Button button2 = (Button) alertDialogBuilder.findViewById(android.R.id.button2);
//            if (textView != null) {
//                textView.setLineSpacing(0, 1.5f);
//            }
//            Utils.applyFont(context, textView);
//            Utils.applyFont(context, alertTitle);
//            Utils.applyFont(context, button1);
//            Utils.applyFont(context, button2);
//        }
//    }
//
//    public static void showSuccessSnack(View view, String message) {
//        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
//    }
//
//    public static void showErrorSnack(View view, String message) {
//        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
//    }
//}
