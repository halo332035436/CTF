package com.bullb.ctf.Model;

public class CipherData {
    private String data, iv;

    public CipherData(String data, String iv) {
        this.data = data;
        this.iv = iv;
    }

    public String getData(){
        return data;
    }

    public String getIV(){
        return iv;
    }
}
