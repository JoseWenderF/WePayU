package br.ufal.ic.p2.wepayu.models;


import java.io.Serializable;
import java.time.LocalDate;

public abstract class empregado implements Serializable {
    private String nome;
    private String endereco;
    private double salario;
    int id;
    private boolean sindicalizado;
    private String metodoPagamento;
    private String idSindicato;
    private String banco;
    private String agencia;
    private String contaCorrente;
    private LocalDate dataContratacao;
    private agendaDePagamento agendaPagamento;



    public empregado(){}

    public empregado(String nome, String endereco, double salario, int id) {
        this.nome = nome;
        this.endereco = endereco;
        this.salario = salario;
        this.id = id;
        this.metodoPagamento = "emMaos";
        this.sindicalizado = false;
        this.idSindicato = "";
        this.banco = "";
        this.agencia = "";
        this.contaCorrente = "";
    }

    public String getIdSindicato() {return idSindicato;}

    public String getMetodoPagamento() {return metodoPagamento;}

    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public double getSalario() {
        return salario;
    }

    public boolean getSindicalizado (){return this.sindicalizado;}

    public int getId() {return id;}

    public boolean isSindicalizado() {return sindicalizado;}

    public String getBanco() {return banco;}

    public void setBanco(String banco) {this.banco = banco;}

    public String getAgencia() {return agencia;}

    public void setAgencia(String agencia) {this.agencia = agencia;}

    public String getContaCorrente() {return contaCorrente;}

    public void setContaCorrente(String contaCorrente) {this.contaCorrente = contaCorrente;}

    public void setEndereco(String endereco) { this.endereco = endereco;}

    public void setNome(String nome) {this.nome = nome;}

    public void setMetodoPagamento(String metodoPagamento) {this.metodoPagamento = metodoPagamento;}

    public void setSalario(double salario) {this.salario = salario;}

    public void setSindicalizado(boolean sindicalizado) {this.sindicalizado = sindicalizado;}

    public void setId(int id) {this.id = id;}

    public void setIdSindicato(String idSindicato) {this.idSindicato = idSindicato;}

    public LocalDate getDataContratacao() {return dataContratacao;}

    public void setDataContratacao(LocalDate dataContratacao) {this.dataContratacao = dataContratacao;}

    public agendaDePagamento getAgendaPagamento() {return agendaPagamento;}

    public void setAgendaPagamento(agendaDePagamento agendaPagamento) {this.agendaPagamento = agendaPagamento;}

    @Override
    public String toString() {
        return String.format("Id:" + this.id + " Nome: " + this.nome);
    }
}
