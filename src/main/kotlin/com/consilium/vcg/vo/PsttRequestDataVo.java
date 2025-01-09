package com.consilium.vcg.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PsttRequestDataVo {

    @JsonIgnore
    private byte[] data;
    private String fileName;
    private String userName;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
