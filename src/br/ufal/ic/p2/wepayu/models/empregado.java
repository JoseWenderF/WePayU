package br.ufal.ic.p2.wepayu.models;


public abstract class empregado {
    private String nome;
    private String endereco;
    private double salario;
    int id;
    private boolean sindicalizado;
    private String metodoPagamento;
    private String idSindicato;



    public empregado(){}

    public empregado(String nome, String endereco, double salario, int id) {
        this.nome = nome;
        this.endereco = endereco;
        this.salario = salario;
        this.id = id;
        this.metodoPagamento = "emMaos";
        this.sindicalizado = false;
        this.idSindicato = "";
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

    public void setEndereco(String endereco) { this.endereco = endereco;}

    public void setNome(String nome) {this.nome = nome;}

    public void setMetodoPagamento(String metodoPagamento) {this.metodoPagamento = metodoPagamento;}

    public void setSalario(double salario) {this.salario = salario;}

    public void setSindicalizado(boolean sindicalizado) {this.sindicalizado = sindicalizado;}

    public void setId(int id) {this.id = id;}

    public void setIdSindicato(String idSindicato) {this.idSindicato = idSindicato;}
}
