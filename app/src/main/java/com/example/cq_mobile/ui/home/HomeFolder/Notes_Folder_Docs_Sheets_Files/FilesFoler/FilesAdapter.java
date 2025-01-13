package com.example.cq_mobile.ui.home.HomeFolder.Notes_Folder_Docs_Sheets_Files.FilesFoler;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
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
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.cq_mobile.MoreActivityFolder.MoreActivity;
import com.example.cq_mobile.R;
import java.util.List;
import com.bumptech.glide.Glide;
import com.example.cq_mobile.TestFolder.WebViewActivity;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.NewBuild;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.FilesViewHolder> {
    private Context context;
    private List<FileItem> filesList;
    private String baseUrl;
    private String accessToken;
    private String apiKey;

    public FilesAdapter(Context context, List<FileItem> filesList, String baseUrl, String accessToken, String apiKey) {
        this.context = context;
        this.filesList = filesList;
        this.baseUrl = baseUrl;
        this.accessToken = accessToken;
        this.apiKey = apiKey;
    }

    @NonNull
    @Override
    public FilesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_file, parent, false);
        return new FilesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilesViewHolder holder, int position) {
        FileItem file = filesList.get(position);
        String fileUrl = file.getUrl().startsWith("http") ? file.getUrl() : baseUrl + file.getUrl();
        String id = String.valueOf(file.getId());

        fileUrl = fileUrl + "?token=" + accessToken;

        holder.fileName.setText(file.getFilename());
        holder.fileSize.setText(String.format("Size: %d KB", file.getFilesize() / 1024));

        Log.d("FilesAdapter", "Preview URL: " + fileUrl + "\nID->" + id);

        String mimeType = file.getMime_type();
        Log.d("FilesAdapter", "MimeType for file " + file.getFilename() + ": " + mimeType);

        if (mimeType == null) {
            mimeType = "unknown";
            Log.d("FilesAdapter", "Fallback mimeType for file " + file.getFilename() + ": " + mimeType);
        }
        holder.filePreview.getLayoutParams().width = 300;
        holder.filePreview.getLayoutParams().height = 300;


        GlideUrl glideUrl = null;
        if (mimeType != null && (
                mimeType.endsWith("png") ||
                        mimeType.endsWith("jpeg") ||
                        mimeType.endsWith("jpg") ||
                        mimeType.endsWith("svg") ||
                        mimeType.endsWith("webp")
        )) {
            Log.d("FilesAdapter", "MimeType is a valid image type: " + mimeType);
            holder.filePreview.setVisibility(View.VISIBLE);  // Make ImageView visible

            // Construct Glide URL with headers
            glideUrl = new GlideUrl(fileUrl, new LazyHeaders.Builder()
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("x-api-key", apiKey)
                    .addHeader("Accept", "application/json")
                    .build());

            // Load image into the ImageView using Glide
            Glide.with(holder.itemView.getContext())
                    .load(fileUrl)  // Use the glideUrl with headers
                    .into(holder.filePreview);  // Load the image into ImageView
        } else if (mimeType.endsWith("rtf")){
            Glide.with(holder.itemView.getContext())
                    .load(fileUrl)
                    .placeholder(R.drawable.circular_background)
                    .error(R.drawable.rtf)
                    .into(holder.filePreview);
        }else  if ((mimeType.endsWith("docx"))){
            Glide.with(holder.itemView.getContext())
                    .load(fileUrl)
                    .placeholder(R.drawable.circular_background)
                    .error(R.drawable.doc_1)
                    .into(holder.filePreview);
        }else  if ((mimeType.endsWith("pdf"))) {
            Glide.with(holder.itemView.getContext())
                    .load(fileUrl)
                    .placeholder(R.drawable.circular_background)
                    .error(R.drawable.pdf)
                    .into(holder.filePreview);
        }else {
            Log.d("FilesAdapter", "MimeType is not a valid image type: " + mimeType);
            Glide.with(holder.itemView.getContext())
                    .load(glideUrl)
                    .placeholder(R.drawable.circular_background)
                    .error(R.drawable.new_document_2)
                    .into(holder.filePreview);
        }

        // Handle item click to open file in dialog
        String finalFileUrl = fileUrl;
        holder.itemView.setOnClickListener(v -> {
            String urlToOpen = finalFileUrl;  // Create a final or effectively final variable
            openFileInDialog(urlToOpen);
        });
    }


    private void openFileInDialog(String fileUrl) {
        Log.d("FilesAdapter", "openFileInDialog: " + fileUrl);

        // Debugging: Log the access token and API key
        Log.d("FilesAdapter", "AccessToken: " + accessToken);
        Log.d("FilesAdapter", "API Key: " + apiKey);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_file_viewer, null);
        builder.setView(dialogView);

        ImageView imageView = dialogView.findViewById(R.id.img1);
        TextView downloadButton = dialogView.findViewById(R.id.downloadButton);
        TextView backButton = dialogView.findViewById(R.id.backButton);

        // Construct the Glide URL with headers
        GlideUrl glideUrl = new GlideUrl(fileUrl, new LazyHeaders.Builder()
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("x-api-key", apiKey)
                .addHeader("Accept", "application/json")
                .build());

        // Debugging: Log the Glide request URL and headers
        Log.d("FilesAdapter", "Glide Request URL: " + glideUrl.toString());

        Glide.with(context)
                .load(glideUrl)
                .placeholder(R.drawable.circular_background)
                .error(R.drawable.new_document_2)
                .into(imageView);

        AlertDialog dialog = builder.create();

        // Handle Download Button Click
        downloadButton.setOnClickListener(v -> {
            Log.d("FilesAdapter", "Download Button Clicked");
            downloadFile(fileUrl);
        });

        // Handle Back Button Click
        backButton.setOnClickListener(v -> {
            Log.d("FilesAdapter", "Back Button Clicked");
            dialog.dismiss();

        });

        dialog.show();
    }

    private void downloadFile(String fileUrl) {
        String fileName = Uri.parse(fileUrl).getLastPathSegment();

        // Fallback if fileName is null or too long
        if (fileName == null || fileName.isEmpty()) {
            fileName = "downloaded_file_" + System.currentTimeMillis();
        } else if (fileName.length() > 100) { // Limit the file name length to 100 characters
            fileName = fileName.substring(0, 100);
        }

        // Debugging: Log the file name and URL before downloading
        Log.d("DownloadFile", "Starting download: " + fileName + " from URL: " + fileUrl);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
        request.setTitle("Downloading File");
        request.setDescription("Downloading " + fileName);
        request.addRequestHeader("Authorization", "Bearer " + accessToken);
        request.addRequestHeader("x-api-key", apiKey);
        request.addRequestHeader("Accept", "application/json");
        request.addRequestHeader("Content-Type", "application/json");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        // Set download destination
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        // Enqueue the download
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            try {
                long downloadId = downloadManager.enqueue(request);
                Log.d("DownloadFile", "Download started with ID: " + downloadId);
                Toast.makeText(context, "Download started", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e("DownloadFile", "Error enqueuing file: " + e.getMessage());
                Toast.makeText(context, "Download failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("DownloadFile", "DownloadManager is null");
            Toast.makeText(context, "Download manager not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return filesList.size();
    }

    public void addData(List<FileItem> newFiles) {
        int startPosition = filesList.size();
        filesList.addAll(newFiles);
        notifyItemRangeInserted(startPosition, newFiles.size());
    }

    public static class FilesViewHolder extends RecyclerView.ViewHolder {
        TextView fileName, fileSize;
        ImageView filePreview;

        public FilesViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.file_name);
            fileSize = itemView.findViewById(R.id.file_size);
            filePreview = itemView.findViewById(R.id.file_preview);
        }
    }
}


/*
         Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra("url", fileUrl);
            context.startActivity(intent);
 */