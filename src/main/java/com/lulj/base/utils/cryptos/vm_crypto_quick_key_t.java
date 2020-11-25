package com.lulj.base.utils.cryptos;

public class vm_crypto_quick_key_t {

    private int[] iv = new int[8];
    private int[] rk = new int[8];

    public vm_crypto_quick_key_t() {

    }

    public int[] getIv() {
        return iv;
    }

    public void setIv(int[] iv) {
        this.iv = iv;
    }

    public int[] getRk() {
        return rk;
    }

    public void setRk(int[] rk) {
        this.rk = rk;
    }

}