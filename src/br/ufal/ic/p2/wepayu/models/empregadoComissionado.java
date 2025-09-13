package br.ufal.ic.p2.wepayu.models;


import java.util.ArrayList;

public class empregadoComissionado extends empregado{
    private double comissao;
    private ArrayList<registroVenda> listaVendas;

    public empregadoComissionado(){}
    public empregadoComissionado(String nome, String endereco, double salario, double comissao, int id ) {
        super(nome, endereco, salario, id);
        this.comissao = comissao;
        listaVendas = new ArrayList<>();
    }

    public double getComissao() {return comissao;}

    public void setComissao(double comissao) {this.comissao = comissao;}

    public void addVenda(registroVenda v){
        listaVendas.add(v);
    }

    public ArrayList<registroVenda> getListaVendas() {
        return listaVendas;
    }

    public void setListaVendas(ArrayList<registroVenda> listaVendas) {
        this.listaVendas = listaVendas;
    }
}
