package br.ufal.ic.p2.wepayu.models;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class empregadoHorista extends empregado{
    ArrayList<cartaoPontos> listaPontos;

    public empregadoHorista(){}
    public empregadoHorista(String nome, String endereco, double salario, int id)  {
        super(nome, endereco, salario, id);
        this.listaPontos = new ArrayList<>();
        this.setDataContratacao(null);
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
}
