package br.ufal.ic.p2.wepayu.models;

import java.time.LocalDate;

public class empregadoAssalariado extends empregado{

    public empregadoAssalariado(){}

    public empregadoAssalariado(String nome, String endereco, double salario, int id) {
        super(nome, endereco, salario, id);
        LocalDate dataContratacao = LocalDate.of(2005,1, 1);
        this.setDataContratacao(dataContratacao);
    }
}
