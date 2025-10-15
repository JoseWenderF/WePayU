package br.ufal.ic.p2.wepayu.models;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class cartaoPontos implements Serializable {
    double horasNormais;
    double horasExtra;
    LocalDate data;

    public cartaoPontos(){}

    public cartaoPontos(double horas, LocalDate data){
        this.data = data;

        if (horas > 8.0){
            this.horasNormais = 8.0;
            this.horasExtra = horas - 8.0;
        }else {
            this.horasNormais = horas;
        }
    }

    public double getHorasExtra() {
        return horasExtra;
    }

    public double getHorasNormais() {
        return horasNormais;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public void setHorasExtra(double horasExtra) {
        this.horasExtra = horasExtra;
    }

    public void setHorasNormais(double horasNormais) {
        this.horasNormais = horasNormais;
    }
}
