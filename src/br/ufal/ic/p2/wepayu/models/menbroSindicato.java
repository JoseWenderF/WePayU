package br.ufal.ic.p2.wepayu.models;

import java.util.ArrayList;

public class menbroSindicato {
    int idEmpregado;
    String idMembro;
    ArrayList<servicoSindicato> listaServicos;
    double taxa;

    public menbroSindicato(){}

    public menbroSindicato(int idEmpregado, String idMembro, double taxa){
        this.idEmpregado = idEmpregado;
        this.idMembro = idMembro;
        this.listaServicos = new ArrayList<>();
        this.taxa = taxa;
    }

    public ArrayList<servicoSindicato> getListaServicos() {return listaServicos;}

    public double getTaxa() {return taxa;}

    public int getIdEmpregado() {return idEmpregado;}

    public String getIdMembro() {return idMembro;}

    public void setIdEmpregado(int idEmpregado) {this.idEmpregado = idEmpregado;}

    public void setIdMembro(String idMembro) {this.idMembro = idMembro;}

    public void setListaServicos(ArrayList<servicoSindicato> listaServicos) {this.listaServicos = listaServicos;}

    public void setTaxa(double taxa) {this.taxa = taxa;}

    public void addServico(servicoSindicato novo){
        listaServicos.add(novo);
    }
}
