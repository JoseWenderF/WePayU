package br.ufal.ic.p2.wepayu.models;

import java.util.ArrayList;

public class estado {

    private ArrayList<empregado> copiaEmpregados;
    private ArrayList<menbroSindicato> copiaMembrosSindicato;
    private ArrayList<agendaDePagamento> copiaAgendasDePagamentos;

    public estado (){}

    public estado (ArrayList<empregado> listaEmpregados, ArrayList<menbroSindicato> listaMembrosSindicato, ArrayList<agendaDePagamento> listaAgendasPagamento){
        this.copiaEmpregados = DeepCopyUtil.deepCopy(listaEmpregados);
        this.copiaMembrosSindicato = DeepCopyUtil.deepCopy(listaMembrosSindicato);
        this.copiaAgendasDePagamentos = DeepCopyUtil.deepCopy(listaAgendasPagamento);
    }

    public ArrayList<empregado> getCopiaEmpregados() {
        return copiaEmpregados;
    }

    public void setCopiaEmpregados(ArrayList<empregado> copiaEmpregados) {
        this.copiaEmpregados = copiaEmpregados;
    }

    public ArrayList<menbroSindicato> getCopiaMembrosSindicato() {
        return copiaMembrosSindicato;
    }

    public void setCopiaMembrosSindicato(ArrayList<menbroSindicato> copiaMembrosSindicato) {
        this.copiaMembrosSindicato = copiaMembrosSindicato;
    }

    public ArrayList<agendaDePagamento> getCopiaAgendasDePagamentos() {
        return copiaAgendasDePagamentos;
    }

    public void setCopiaAgendasDePagamentos(ArrayList<agendaDePagamento> copiaAgendasDePagamentos) {
        this.copiaAgendasDePagamentos = copiaAgendasDePagamentos;
    }
}
