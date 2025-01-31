package com.example.cq_mobile.ui.home.HomeFolder.Notes_Folder_Docs_Sheets_Files.SheetsFolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cq_mobile.R;

import java.util.List;

public class SheetAdapter extends RecyclerView.Adapter<SheetAdapter.ViewHolder> {
    private List<SheetItem> sheetList;
    private Context context;

    public SheetAdapter(Context context, List<SheetItem> sheetList) {
        this.context = context;
        this.sheetList = sheetList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sheet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SheetItem item = sheetList.get(position);
        holder.title.setText(item.getTitle());
        holder.otherTitle.setText(item.getOtherTitle());
    }

    @Override
    public int getItemCount() {
        return sheetList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, otherTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.sheet_title);
            otherTitle = itemView.findViewById(R.id.sheet_other_title);
        }
    }
}
