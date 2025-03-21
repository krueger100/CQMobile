package com.example.cq_mobile.ui.home.HomeFolder.TaskFolder.FilesINTaskFolder;

import android.annotation.SuppressLint;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.HomeFolder.NotesNFilesAPI_folder.FilesFoler.FileItem;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileInnerTaskAdapter extends RecyclerView.Adapter<FileInnerTaskAdapter.FileViewHolder> {
    private Context context;
    private List<FileItem> fileList;
    private String baseUrl;
    private String accessToken;
    private String apiKey;
    private View emptyStateView;

    public FileInnerTaskAdapter(Context context, List<FileItem> fileList, String baseUrl, String accessToken, String apiKey, LinearLayout emptyStateView) {
        this.context = context;
        this.fileList = fileList;
        this.baseUrl = baseUrl;
        this.accessToken = accessToken;
        this.emptyStateView = emptyStateView;
        this.apiKey = apiKey;

        checkEmptyState();

        checkEmptyState();
    }

    private void checkEmptyState() {
        if (emptyStateView != null) {
            emptyStateView.setVisibility(fileList.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.file_item, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        FileItem fileItem = fileList.get(position);
        String fileUrlWithToken = fileItem.getUrl() + "?token=" + accessToken;

        holder.fileName.setText(fileItem.getFilename());
        holder.fileSize.setText(String.format("Size: %d KB", fileItem.getFilesize() / 1024));

        String mimeType = fileItem.getMime_type();
        if (mimeType == null) mimeType = "unknown";
        holder.mimeType.setText(mimeType);

        Glide.with(holder.itemView.getContext())
                .load(fileUrlWithToken)
                .placeholder(R.drawable.circular_background)
                .error(getErrorDrawable(mimeType))
                .into(holder.fileImage);

        holder.itemView.setOnClickListener(v -> openFileInDialog(fileUrlWithToken));
    }

    private int getErrorDrawable(String mimeType) {
        if (mimeType.endsWith("png") || mimeType.endsWith("jpeg") || mimeType.endsWith("jpg") ||
                mimeType.endsWith("svg") || mimeType.endsWith("webp")) {
            return R.drawable.new_document_2;
        } else if (mimeType.endsWith("pdf")) {
            return R.drawable.pdf;
        } else if (mimeType.endsWith("docx")) {
            return R.drawable.doc_1;
        } else if (mimeType.endsWith("rtf")) {
            return R.drawable.rtf;
        } else {
            return R.drawable.new_document_2;
        }
    }

    private void openFileInDialog(String fileUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_file_viewer, null);
        builder.setView(dialogView);

        ImageView imageView = dialogView.findViewById(R.id.img1);
        TextView downloadButton = dialogView.findViewById(R.id.downloadButton);
        TextView backButton = dialogView.findViewById(R.id.backButton);

        Glide.with(context)
                .load(fileUrl)
                .placeholder(R.drawable.circular_background)
                .error(R.drawable.new_document_2)
                .into(imageView);

        AlertDialog dialog = builder.create();
        downloadButton.setOnClickListener(v -> downloadFile(fileUrl));
        backButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void downloadFile(String fileUrl) {
        String fileName = Uri.parse(fileUrl).getLastPathSegment();
        if (fileName == null || fileName.isEmpty()) fileName = "downloaded_file_" + System.currentTimeMillis();

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
        request.setTitle("Downloading File");
        request.setDescription("Downloading " + fileName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadManager.enqueue(request);
            Toast.makeText(context, "Download started", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Download manager not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        checkEmptyState();
        return fileList.size();
    }

    public void addData(List<FileItem> newFiles) {
        Set<FileItem> uniqueFiles = new HashSet<>(fileList);
        uniqueFiles.addAll(newFiles);
        fileList.clear();
        fileList.addAll(uniqueFiles);
        notifyDataSetChanged();
        checkEmptyState();
    }

    public void setData(List<FileItem> newFiles) {
        fileList.clear();
        fileList.addAll(newFiles);
        notifyDataSetChanged();
        checkEmptyState();
    }

    static class FileViewHolder extends RecyclerView.ViewHolder {
        TextView fileName, fileSize, mimeType;
        ImageView fileImage;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.file_name);
            fileSize = itemView.findViewById(R.id.file_size);
            mimeType = itemView.findViewById(R.id.file_mime_type);
            fileImage = itemView.findViewById(R.id.file_image);
        }
    }
}
