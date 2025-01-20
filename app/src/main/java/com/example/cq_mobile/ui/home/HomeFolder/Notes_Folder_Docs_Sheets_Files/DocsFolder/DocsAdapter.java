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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.cq_mobile.R;


import java.util.List;

public class DocsAdapter extends RecyclerView.Adapter<DocsAdapter.DocsViewHolder> {
    private Context context;
    private List<DocsItem> docsList;
    private String baseUrl;
    private String accessToken;
    private String apiKey;

    public DocsAdapter(Context context, List<DocsItem> docsList, String baseUrl, String accessToken, String apiKey) {
        this.context = context;
        this.docsList = docsList;
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
        DocsItem file = docsList.get(position);
        String fileUrl = file.getUrl().startsWith("http") ? file.getUrl() : baseUrl + file.getUrl();
        String id = String.valueOf(file.getId());
        String mimeType = file.getMime_type();

        fileUrl = fileUrl + "?token=" + accessToken;

        holder.fileName.setText(file.getFilename());
        holder.fileSize.setText(String.format("Size: %d KB", file.getFilesize() / 1024));

        Log.d("DocsAdapter", "Preview URL: " + fileUrl + "\nID->" + id);
        Log.d("DocsAdapter", "Preview id: "+id);

        Log.d("DocsAdapter", "MimeType for file " + file.getFilename() + ": " + mimeType);
        String finalFileUrl = fileUrl;


        if (mimeType.endsWith("rtf")) {
            Glide.with(holder.itemView.getContext())
                    .load(fileUrl)
                    .placeholder(R.drawable.circular_background)
                    .error(R.drawable.rtf)
                    .into(holder.filePreview);

            holder.itemView.setOnClickListener(v -> {
                String urlToOpen = finalFileUrl; // Create a final or effectively final variable
                openDocInDialog(urlToOpen, mimeType);

            });
        } else if (mimeType.endsWith("docx")) {
            Glide.with(holder.itemView.getContext())
                    .load(fileUrl)
                    .placeholder(R.drawable.circular_background)
                    .error(R.drawable.doc_1)
                    .into(holder.filePreview);

            holder.itemView.setOnClickListener(v -> {
                String urlToOpen = finalFileUrl; // Create a final or effectively final variable
                openDocInDialog(urlToOpen, mimeType);

            });
        } else if (mimeType.endsWith("pdf")) {
            Glide.with(holder.itemView.getContext())
                    .load(fileUrl)
                    .placeholder(R.drawable.circular_background)
                    .error(R.drawable.pdf)
                    .into(holder.filePreview);


            holder.itemView.setOnClickListener(v -> {
                String urlToOpen = finalFileUrl; // Create a final or effectively final variable
                openDocInDialog(urlToOpen,mimeType);

            });

        }else {
            Glide.with(holder.itemView.getContext())
                    .load(fileUrl)
                    .into(holder.filePreview);
            holder.itemView.setOnClickListener(v -> {
                String urlToOpen = finalFileUrl; // Create a final or effectively final variable
                openFileInDialog(urlToOpen);
            });
        }


    }

    private void openDocInDialog(String fileUrl, String mimeType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_file_doc_viewer, null);
        builder.setView(dialogView);
        TextView file_name = dialogView.findViewById(R.id.file_name);
        TextView file_type = dialogView.findViewById(R.id.file_type);
        TextView downloadButton = dialogView.findViewById(R.id.downloadButton);
        TextView backButton = dialogView.findViewById(R.id.backButton);

        AlertDialog dialog = builder.create();

        file_name.setText(fileUrl);
        file_type.setText("TYPE: "+mimeType);
        // Handle Download Button Click
        downloadButton.setOnClickListener(v -> {
            Log.d("DocsAdapter", "Download Button Clicked");
            downloadFile(fileUrl);
        });

        // Handle Back Button Click
        backButton.setOnClickListener(v -> {
            Log.d("DocsAdapter", "Back Button Clicked");
            dialog.dismiss();

        });

        dialog.show();
    }


    private void openFileInDialog(String fileUrl) {
        Log.d("DocsAdapter", "openFileInDialog: " + fileUrl);

        // Debugging: Log the access token and API key
        Log.d("DocsAdapter", "AccessToken: " + accessToken);
        Log.d("DocsAdapter", "API Key: " + apiKey);

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
        Log.d("DocsAdapter", "Glide Request URL: " + glideUrl.toString());

        Glide.with(context)
                .load(glideUrl)
                .placeholder(R.drawable.circular_background)
                .error(R.drawable.new_document_2)
                .into(imageView);

        AlertDialog dialog = builder.create();

        // Handle Download Button Click
        downloadButton.setOnClickListener(v -> {
            Log.d("DocsAdapter", "Download Button Clicked");
            downloadFile(fileUrl);
        });

        // Handle Back Button Click
        backButton.setOnClickListener(v -> {
            Log.d("DocsAdapter", "Back Button Clicked");
            dialog.dismiss();

        });

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
        return docsList.size();
    }



    public void addData(List<DocsItem> newFiles) {
        int startPosition = docsList.size();
        docsList.addAll(newFiles);
        notifyItemRangeInserted(startPosition, newFiles.size());
    }

    public static class DocsViewHolder extends RecyclerView.ViewHolder {
        TextView fileName, fileSize;
        ImageView filePreview;
CardView cardView;
        public DocsViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.file_name);
            fileSize = itemView.findViewById(R.id.file_size);
            filePreview = itemView.findViewById(R.id.file_preview);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
