package com.example.cq_mobile.ui.home.HomeFolder.Notes_Folder_Docs_Sheets_Files.FilesFoler;

public class FileItem {
    private int id;
    private String url;
    private String filename;
    private int filesize;
    private String mime_type;

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getFilename() {
        return filename;
    }

    public int getFilesize() {
        return filesize;
    }

    public String getMime_type() {
        return mime_type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setFilesize(int filesize) {
        this.filesize = filesize;
    }

    public void setMime_type(String mime_type) {
        this.mime_type = mime_type;
    }
}
