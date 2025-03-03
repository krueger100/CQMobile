package com.example.cq_mobile.ui.home.HomeFolder.TaskFolder.FilesINTaskFolder;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.HomeFolder.NotesNFilesAPI_folder.FilesFoler.FileItem;

import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder> {
    private Context context;
    private List<FileItem> fileList;
    private String token;

    public FileAdapter(Context context, List<FileItem> fileList, String baseUrl, String token, String apiKey) {
        this.context = context;
        this.fileList = fileList;
        this.token = token;
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

        // Set the file details into the view
        holder.fileName.setText(fileItem.getFilename());
        holder.fileSize.setText(String.valueOf(fileItem.getFilesize()) + " KB"); // Display size in KB
        holder.mimeType.setText(fileItem.getMime_type());

        // Append the token to the file URL
        String fileUrlWithToken = fileItem.getUrl() + "?token=" + token;
        String mimeType_file = fileItem.getMime_type();
        Log.d("FileAdapter", "mimeType_file:  "+  mimeType_file);


        if (mimeType_file != null && (mimeType_file.endsWith("png") || mimeType_file.endsWith("jpeg") || mimeType_file.endsWith("jpg") || mimeType_file.endsWith("svg") || mimeType_file.endsWith("webp"))) {
            Glide.with(context)
                    .load(fileUrlWithToken)
                    .into(holder.fileImage);
        } else if (mimeType_file.endsWith("rtf")){
            Glide.with(holder.itemView.getContext())
                    .load(fileUrlWithToken)
                    .placeholder(R.drawable.circular_background)
                    .error(R.drawable.rtf)
                    .into(holder.fileImage);
        }else  if ((mimeType_file.endsWith("docx"))){
            Glide.with(holder.itemView.getContext())
                    .load(fileUrlWithToken)
                    .placeholder(R.drawable.circular_background)
                    .error(R.drawable.doc_1)
                    .into(holder.fileImage);
        }else  if ((mimeType_file.endsWith("pdf"))){
            Glide.with(holder.itemView.getContext())
                    .load(fileUrlWithToken)
                    .placeholder(R.drawable.circular_background)
                    .error(R.drawable.pdf)
                    .into(holder.fileImage);
        }else {
            Glide.with(holder.itemView.getContext())
                    .load(fileUrlWithToken)
                    .placeholder(R.drawable.circular_background)
                    .error(R.drawable.new_document_2)
                    .into(holder.fileImage);
        }

    }  /// mimeType_file.endsWith("rtf")

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public void addData(List<FileItem> newFiles) {
        fileList.addAll(newFiles);
        notifyDataSetChanged();
    }

    static class FileViewHolder extends RecyclerView.ViewHolder {
        TextView fileName, fileSize, mimeType;
        ImageView fileImage;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.file_name);
            fileSize = itemView.findViewById(R.id.file_size);
            mimeType = itemView.findViewById(R.id.file_mime_type);
            fileImage = itemView.findViewById(R.id.file_image);  // Initialize ImageView
        }
    }
}
