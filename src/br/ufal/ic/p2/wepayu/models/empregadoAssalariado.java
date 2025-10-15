package br.ufal.ic.p2.wepayu.models;

import java.io.Serializable;
import java.time.LocalDate;

public class empregadoAssalariado extends empregado implements Serializable {

    public empregadoAssalariado(){}

    public empregadoAssalariado(String nome, String endereco, double salario, int id) {
        super(nome, endereco, salario, id);
        LocalDate dataContratacao = LocalDate.of(2005,1, 1);
        this.setDataContratacao(dataContratacao);
        agendaDePagamento agenda = new agendaDePagamento("mensal", "$");
        this.setAgendaPagamento(agenda);
    }
}
