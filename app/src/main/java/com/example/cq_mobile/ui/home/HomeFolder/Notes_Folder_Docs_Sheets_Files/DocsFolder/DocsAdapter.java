package com.example.cq_mobile.ui.home.HomeFolder.Notes_Folder_Docs_Sheets_Files.DocsFolder;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.cq_mobile.R;

import java.util.List;

public class DocsAdapter extends RecyclerView.Adapter<DocsAdapter.DocsViewHolder> {
    private Context context;
    private List<DocsItem> filesList;
    private String baseUrl;
    private String accessToken;
    private String apiKey;

    public DocsAdapter(Context context, List<DocsItem> filesList, String baseUrl, String accessToken, String apiKey) {
        this.context = context;
        this.filesList = filesList;
        this.baseUrl = baseUrl;
        this.accessToken = accessToken;
        this.apiKey = apiKey;
    }

    @NonNull
    @Override
    public DocsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_file, parent, false);
        return new DocsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocsViewHolder holder, int position) {
        DocsItem file = filesList.get(position);
        String fileUrl = file.getUrl().startsWith("http") ? file.getUrl() : baseUrl + file.getUrl();
        String id = String.valueOf(file.getId());
        String mimeType = file.getMime_type();

        // Skip image files
        if (mimeType != null && (
                mimeType.endsWith("png") ||
                        mimeType.endsWith("jpeg") ||
                        mimeType.endsWith("jpg") ||
                        mimeType.endsWith("svg") ||
                        mimeType.endsWith("webp")
        )) {
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            return;
        }

        holder.fileName.setText(file.getFilename());
        holder.fileSize.setText(String.format("Size: %d KB", file.getFilesize() / 1024));

        Glide.with(holder.itemView.getContext())
                .load(R.drawable.new_document_2)
                .placeholder(R.drawable.circular_background)
                .into(holder.filePreview);

        // Handle item click to open file in dialog
        holder.itemView.setOnClickListener(v -> openFileInDialog(fileUrl));
    }

    private void openFileInDialog(String fileUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_file_viewer, null);
        builder.setView(dialogView);

        ImageView imageView = dialogView.findViewById(R.id.img1);
        TextView downloadButton = dialogView.findViewById(R.id.downloadButton);
        TextView backButton = dialogView.findViewById(R.id.backButton);

        GlideUrl glideUrl = new GlideUrl(fileUrl, new LazyHeaders.Builder()
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("x-api-key", apiKey)
                .addHeader("Accept", "application/json")
                .build());

        Glide.with(context)
                .load(glideUrl)
                .placeholder(R.drawable.circular_background)
                .error(R.drawable.baseline_image_not_supported_24)
                .into(imageView);

        AlertDialog dialog = builder.create();

        downloadButton.setOnClickListener(v -> downloadFile(fileUrl));
        backButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void downloadFile(String fileUrl) {
        String fileName = Uri.parse(fileUrl).getLastPathSegment();
        if (fileName == null || fileName.isEmpty()) {
            fileName = "downloaded_file_" + System.currentTimeMillis();
        } else if (fileName.length() > 100) {
            fileName = fileName.substring(0, 100);
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
        request.setTitle("Downloading File");
        request.setDescription("Downloading " + fileName);
        request.addRequestHeader("Authorization", "Bearer " + accessToken);
        request.addRequestHeader("x-api-key", apiKey);
        request.addRequestHeader("Accept", "application/json");
        request.addRequestHeader("Content-Type", "application/json");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            try {
                downloadManager.enqueue(request);
                Toast.makeText(context, "Download started", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(context, "Download failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Download manager not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return filesList.size();
    }

    public void addData(List<DocsItem> newFiles) {
        int startPosition = filesList.size();
        filesList.addAll(newFiles);
        notifyItemRangeInserted(startPosition, newFiles.size());
    }

    public static class DocsViewHolder extends RecyclerView.ViewHolder {
        TextView fileName, fileSize;
        ImageView filePreview;

        public DocsViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.file_name);
            fileSize = itemView.findViewById(R.id.file_size);
            filePreview = itemView.findViewById(R.id.file_preview);
        }
    }
}
