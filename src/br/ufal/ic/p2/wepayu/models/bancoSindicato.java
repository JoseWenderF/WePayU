package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.DataInicialPosteriorFinalservicosException;
import br.ufal.ic.p2.wepayu.Exception.MembroJaExisteException;
import br.ufal.ic.p2.wepayu.Exception.MembroNaoExisteException;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class bancoSindicato {
    ArrayList<menbroSindicato> listaMebrosSindicatos;

    public bancoSindicato(){
        try (XMLDecoder decoder = new XMLDecoder(new FileInputStream("sindicato.xml"))) {
            listaMebrosSindicatos = (ArrayList<menbroSindicato>) decoder.readObject();
        } catch (IOException e) {
            this.listaMebrosSindicatos = new ArrayList<>();
        }
    }

    public void zerarSistema(){
        listaMebrosSindicatos = new ArrayList<>();
    }

    public void encerrarSistema(){
        try (XMLEncoder encoder = new XMLEncoder(new FileOutputStream("sindicato.xml"))) {
            encoder.setPersistenceDelegate(LocalDate.class, new LocalDatePersistenceDelegate());
            encoder.writeObject(listaMebrosSindicatos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void criarMembro(int idEmpregado, String idMembro, double taxa) throws MembroJaExisteException {
        menbroSindicato novo = new menbroSindicato(idEmpregado, idMembro, taxa);
        for(menbroSindicato mem : listaMebrosSindicatos){
            if(mem.getIdMembro().equals(idMembro)){
                throw new MembroJaExisteException();
            }
        }

        listaMebrosSindicatos.add(novo);
    }

    public menbroSindicato getMembroPorIdEmpregado(int idEmpregado) {
        for (menbroSindicato mem : listaMebrosSindicatos) {
            if (mem.getIdEmpregado() == idEmpregado) {
                return mem;
            }
        }
        return null;
    }

    public menbroSindicato getMembroPorIdMembro(String idMembro) throws MembroNaoExisteException {
        for (menbroSindicato mem : listaMebrosSindicatos) {
            if (mem.getIdMembro().equals(idMembro)) {
                return mem;
            }
        }
        throw new MembroNaoExisteException();
    }

    public void removerMembro(int idEmpregado){
        menbroSindicato mem = this.getMembroPorIdEmpregado(idEmpregado);
        listaMebrosSindicatos.remove(mem);
    }

    public void lancaTaxaServico(String idMembro, LocalDate data, double valor) throws MembroNaoExisteException {
        servicoSindicato novoServico = new servicoSindicato(data, valor);
        menbroSindicato mem = this.getMembroPorIdMembro(idMembro);

        mem.addServico(novoServico);
    }

    public double getTaxasServico(int idEmpregado, LocalDate dataInicial, LocalDate dataFinal) throws DataInicialPosteriorFinalservicosException {
        menbroSindicato mem = this.getMembroPorIdEmpregado(idEmpregado);
        ArrayList<servicoSindicato> servicos = mem.getListaServicos();

        if (dataInicial.isAfter(dataFinal)){
            throw new DataInicialPosteriorFinalservicosException();
        }

        double total = 0.0;
        for (servicoSindicato servico : servicos){
            if((servico.getData().isAfter(dataInicial) || servico.getData().isEqual(dataInicial)) && servico.getData().isBefore(dataFinal)){
                total += servico.getValor();
            }
        }

        return total;
    }


}
