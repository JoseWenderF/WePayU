package br.ufal.ic.p2.wepayu.models;

import java.time.LocalDate;

public class servicoSindicato {
    LocalDate data;
    double valor;

    public servicoSindicato(){}
    public servicoSindicato(LocalDate data, double valor){
        this.data = data;
        this.valor = valor;
    }

    public LocalDate getData() {return data;}

    public double getValor() {return valor;}

    public void setData(LocalDate data) {this.data = data;}

    public void setValor(double valor) {this.valor = valor;}


}
