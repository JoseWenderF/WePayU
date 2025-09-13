package br.ufal.ic.p2.wepayu.models;

import java.time.LocalDate;

public class registroVenda {
    LocalDate data;
    double valor;

    public registroVenda(){}

    public registroVenda(LocalDate data, double valor){
        this.data = data;
        this.valor = valor;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public LocalDate getData() {
        return data;
    }

    public double getValor() {
        return valor;
    }
}
