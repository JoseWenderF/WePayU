package br.ufal.ic.p2.wepayu.models;
import br.ufal.ic.p2.wepayu.Exception.*;

import java.time.LocalDate;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class bancoEmpregados {
    private ArrayList<empregado> listaEmpregados;
    private int ultimooId;


    public bancoEmpregados(){
        this.ultimooId = 0;
        try (XMLDecoder decoder = new XMLDecoder(new FileInputStream("empregados.xml"))) {
            listaEmpregados = (ArrayList<empregado>) decoder.readObject();
        } catch (IOException e) {
            this.listaEmpregados = new ArrayList<>();
        }
    }

    public void zerarSistema(){
        listaEmpregados = new ArrayList<>();
    }

    public void encerrarSistema(){
        try (XMLEncoder encoder = new XMLEncoder(new FileOutputStream("empregados.xml"))) {
            encoder.setPersistenceDelegate(LocalDate.class, new LocalDatePersistenceDelegate());
            encoder.writeObject(listaEmpregados);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getUltimooId(){return this.ultimooId;}
    public void setUltimooId(int ultimooId){this.ultimooId = ultimooId;}

    public List<empregado> getListaEmpregados(){return this.listaEmpregados;}
    public void setListaEmpregados(ArrayList<empregado> listaEmpregados){this.listaEmpregados = listaEmpregados;}

    public int addEmpregado(String nome, String endereco, String tipo, double salario)  {
        this.ultimooId++;
        empregado novo = null;
        if (tipo.equals("assalariado")){
            novo = new empregadoAssalariado(nome, endereco, salario, ultimooId);
        }
        else if (tipo.equals("horista")){
            novo = new empregadoHorista(nome, endereco, salario, ultimooId);
        }

        if (novo != null){
            listaEmpregados.add(novo);
        }

        return this.ultimooId;
    }

    public int addEmpregado(String nome, String endereco, String tipo, double salario, double comissao)  {
        this.ultimooId++;
        empregado novo = new empregadoComissionado(nome, endereco, salario, comissao, ultimooId);
        listaEmpregados.add(novo);
        return this.ultimooId;
    }


    public empregado getEmp(int id) {
        for (empregado emp : listaEmpregados) {
            if (emp.getId() == id) {
                return emp;
            }
        }
        return null;
    }

    public void removerEmpregado(int id){
        empregado emp = getEmp(id);
        listaEmpregados.remove(emp);
    }

    public int getId(int id){
        empregado emp = getEmp(id);
        return emp.getId();
    }

    public String getNome(int id){
        empregado emp = getEmp(id);
        return emp.getNome();
    }

    public String getEndereco(int id){
        empregado emp = getEmp(id);
        return emp.getEndereco();
    }

    public String getTipo(int id){
        empregado emp = getEmp(id);
        if ( emp instanceof empregadoAssalariado){
            return "assalariado";
        } else if (emp instanceof empregadoComissionado) {
            return "comissionado";
        } else if (emp instanceof empregadoHorista) {
            return "horista";
        }
        return "";
    }

    public double getSalario(int id){
        empregado emp = getEmp(id);
        return emp.getSalario();
    }

    public boolean getSindicalizado(int id){
        empregado emp = getEmp(id);
        return emp.getSindicalizado();
    }
    public double getComissao(int id) throws EmpregadoNaoComissionadoException {
        empregado emp = getEmp(id);
        if (emp instanceof empregadoComissionado){
            empregadoComissionado empCom = (empregadoComissionado) emp;
            return empCom.getComissao();
        }else{
            throw new EmpregadoNaoComissionadoException();
        }
    }

    public void lancaCartao(int id, LocalDate data, double horas) throws HorasNaoPositivasException, EmpregadoNaoHoristaException {
        if (horas <= 0.0){
            throw new HorasNaoPositivasException();
        }

        cartaoPontos cartao = new cartaoPontos(horas, data);
        empregado emp = getEmp(id);
        if (emp instanceof empregadoHorista){
            empregadoHorista emphor = (empregadoHorista) emp;
            emphor.addcartaoPontos(cartao);
        }else{
            throw new EmpregadoNaoHoristaException();
        }
    }

    public double getHorasNormaisTrabalhadas(int id, LocalDate dataInicio, LocalDate dataFinal) throws EmpregadoNaoHoristaException, DataInicialPosteriorFinalException {
        empregado emp = getEmp(id);
        if (!(emp instanceof empregadoHorista)) {
            throw new EmpregadoNaoHoristaException();
        }

        if (dataInicio.isAfter(dataFinal)){
            throw new DataInicialPosteriorFinalException();
        }

        empregadoHorista emphorist = (empregadoHorista) emp;
        ArrayList<cartaoPontos> pontos =  emphorist.getListaPontos();

        double total = 0.0;
        for (cartaoPontos cartao : pontos){
            if ((cartao.getData().isAfter(dataInicio) || cartao.getData().isEqual(dataInicio)) && cartao.getData().isBefore(dataFinal)){
                total += cartao.getHorasNormais();
            }
        }

        return total;
    }

    public double getHorasExtrasTrabalhadas(int id, LocalDate dataInicio, LocalDate dataFinal) throws EmpregadoNaoHoristaException, DataInicialPosteriorFinalException {
        empregado emp = getEmp(id);
        if (!(emp instanceof empregadoHorista)) {
            throw new EmpregadoNaoHoristaException();
        }

        if (dataInicio.isAfter(dataFinal)){
            throw new DataInicialPosteriorFinalException();
        }

        empregadoHorista emphorist = (empregadoHorista) emp;
        ArrayList<cartaoPontos> pontos =  emphorist.getListaPontos();

        double total = 0.0;
        for (cartaoPontos cartao : pontos){
            if ((cartao.getData().isAfter(dataInicio) || cartao.getData().isEqual(dataInicio)) && cartao.getData().isBefore(dataFinal)){
                total += cartao.getHorasExtra();
            }
        }

        return total;
    }

    public void lancaVenda(int id, LocalDate data, double valor) throws ValorNegativoException, EmpregadoNaoComissionadoException {
        if (valor <= 0.0){
            throw new ValorNegativoException();
        }

        registroVenda venda = new registroVenda(data, valor);
        empregado emp = getEmp(id);
        if (emp instanceof empregadoComissionado){
            empregadoComissionado empcom = (empregadoComissionado) emp;
            empcom.addVenda(venda);
        }else{
            throw new EmpregadoNaoComissionadoException();
        }
    }

    public double getVendasRealizadas(int id, LocalDate dataInicio, LocalDate dataFinal) throws EmpregadoNaoComissionadoException, DataInicialPosteriorFinalException {
        empregado emp = getEmp(id);
        if (!(emp instanceof empregadoComissionado  )) {
            throw new EmpregadoNaoComissionadoException();
        }

        if (dataInicio.isAfter(dataFinal)){
            throw new DataInicialPosteriorFinalException();
        }

        empregadoComissionado empcomi = (empregadoComissionado) emp;
        ArrayList<registroVenda> listaVendas =  empcomi.getListaVendas();

        double total = 0.0;
        for (registroVenda venda : listaVendas){
            if ((venda.getData().isAfter(dataInicio) || venda.getData().isEqual(dataInicio)) && venda.getData().isBefore(dataFinal)){
                total += venda.getValor();
            }
        }

        return total;
    }

    public int getEmpregadoPorNome(String nome, int indice) throws EmpregadoNaoExisteException, EmpregadoPorNomeNaoExisteException {
        int i = 0;
        for (empregado emp : listaEmpregados) {
            if (emp.getNome().equals(nome)){
                i++;
                if (i == indice){
                    return emp.getId();
                }
            }
        }
        throw new EmpregadoPorNomeNaoExisteException();
    }

    public void alteraEmpregadoSindicato(int id, boolean estado, String idSindicato){
        empregado emp = getEmp(id);

        if (estado == true){
            emp.setSindicalizado(true);
            emp.setIdSindicato(idSindicato);
        }else{
            emp.setSindicalizado(false);
            emp.setIdSindicato("");
        }
    }

    public void removerSindicatoEmpregado(int id){
        empregado emp = getEmp(id);

        emp.setSindicalizado(false);
        emp.setIdSindicato("");
    }


}
