package br.ufal.ic.p2.wepayu.models;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;

public class empregadoHorista extends empregado implements Serializable {
    private ArrayList<cartaoPontos> listaPontos;
    private BigDecimal devendoSindicato;

    public empregadoHorista(){this.devendoSindicato = BigDecimal.ZERO;}
    public empregadoHorista(String nome, String endereco, double salario, int id)  {
        super(nome, endereco, salario, id);
        this.listaPontos = new ArrayList<>();
        this.setDataContratacao(null);
        agendaDePagamento agenda = new agendaDePagamento("semanal", "5");
        this.setAgendaPagamento(agenda);
        this.devendoSindicato = BigDecimal.ZERO;
    }

    public void addcartaoPontos(cartaoPontos novoCartao){
        listaPontos.add(novoCartao);
    }

    public ArrayList<cartaoPontos> getListaPontos() {
        return listaPontos;
    }

    public void setListaPontos(ArrayList<cartaoPontos> listaPontos) {
        this.listaPontos = listaPontos;
    }

    public BigDecimal getDevendoSindicato() {return devendoSindicato;}

    public void setDevendoSindicato(BigDecimal devendoSindicato) {this.devendoSindicato = devendoSindicato;}
}
