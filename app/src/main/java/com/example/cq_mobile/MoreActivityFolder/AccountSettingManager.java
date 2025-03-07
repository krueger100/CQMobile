package com.example.cq_mobile.MoreActivityFolder;



import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;


import com.example.cq_mobile.HelperManagers.Animation.ClickAnimationManager;
import com.example.cq_mobile.R;
import com.example.cq_mobile.databinding.BottomSheetAccountSettingBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class AccountSettingManager {
    private final Context context;

    public AccountSettingManager(Context context) {
        this.context = context;
    }

    public void showAccountSettingBottomSheetFragment() {
        if (context == null || (context instanceof Activity && ((Activity) context).isFinishing())) {
            return; // Avoid crash if context is null or activity is finishing
        }

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        BottomSheetAccountSettingBinding binding = BottomSheetAccountSettingBinding.inflate(LayoutInflater.from(context));
        bottomSheetDialog.setContentView(binding.getRoot());

        // Handle Privacy Policy Click
        binding.privacyPolicy.setOnClickListener(v -> {
            ClickAnimationManager.applyClickAnimation(v);
            bottomSheetDialog.dismiss();
            openPdfFromRaw();
        });

        bottomSheetDialog.show();
    }

    private void openPdfFromRaw() {
        try {
            File pdfDir = new File(context.getCacheDir(), "pdfs");
            if (!pdfDir.exists()) pdfDir.mkdirs(); // Ensure directory exists

            File pdfFile = new File(pdfDir, "privacy_policy.pdf");

            if (!pdfFile.exists()) {
                InputStream inputStream = context.getResources().openRawResource(R.raw.privacy_policy);
                FileOutputStream outputStream = new FileOutputStream(pdfFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.close();
                inputStream.close();
            }

            // Use correct FileProvider authority
            Uri pdfUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", pdfFile);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(pdfUri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NO_HISTORY);

            context.startActivity(Intent.createChooser(intent, "Open Privacy Policy"));

        } catch (Exception e) {
            e.printStackTrace();
            new AlertDialog.Builder(context)
                    .setTitle("Error")
                    .setMessage("Unable to open PDF file.")
                    .setPositiveButton("OK", null)
                    .show();
        }
    }
}
