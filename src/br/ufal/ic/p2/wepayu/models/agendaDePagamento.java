package br.ufal.ic.p2.wepayu.models;

import java.io.Serializable;

public class agendaDePagamento implements Serializable {
    private String tipo;
    private String v1;
    private String v2;

    public agendaDePagamento(){}

    public agendaDePagamento(String tipo, String v1, String v2){
        this.tipo = tipo;
        this.v1 = v1;
        this.v2 = v2;
    }

    public agendaDePagamento(String tipo, String v1){
        this.tipo = tipo;
        this.v1 = v1;
        this.v2 = null;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getV1() {
        return v1;
    }

    public void setV1(String v1) {
        this.v1 = v1;
    }

    public String getV2() {
        return v2;
    }

    public void setV2(String v2) {
        this.v2 = v2;
    }
}
