package com.dongnao.james.rabbit.body;

import java.io.Serializable;

/**
 * @Author by fyin.
 * @date 2018/06/06.
 * @description
 */
public class FileBlockBody implements Serializable {
    private String fileId;
    private Long filelength;
    private Long blockindex;
    private Long start;
    private int limit;
    private byte[] content;
    private String fileTargetPath;

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }


    public Long getFilelength() {
        return filelength;
    }

    public void setFilelength(Long filelength) {
        this.filelength = filelength;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Long getBlockindex() {
        return blockindex;
    }

    public void setBlockindex(Long blockindex) {
        this.blockindex = blockindex;
    }

    public String getFileTargetPath() {
        return fileTargetPath;
    }

    public void setFileTargetPath(String fileTargetPath) {
        this.fileTargetPath = fileTargetPath;
    }

    @Override
    public String toString() {
        return "FileBlockBody{" +
                "fileId='" + fileId + '\'' +
                ", filelength=" + filelength +
                ", blockindex=" + blockindex +
                ", start=" + start +
                ", limit=" + limit +
                ", fileTargetPath='" + fileTargetPath + '\'' +
                '}';
    }
}
