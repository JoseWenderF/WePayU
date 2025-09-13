package br.ufal.ic.p2.wepayu.models;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import br.ufal.ic.p2.wepayu.Exception.*;
import javax.swing.*;
import java.math.BigDecimal;


import java.beans.XMLEncoder;
import java.beans.XMLDecoder;
import java.io.*;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

public class gerenciador {
    bancoEmpregados banco;
    bancoSindicato sindicato;

    public gerenciador(){
        banco = new bancoEmpregados();
        sindicato = new bancoSindicato();
    }

    public bancoEmpregados getBanco(){return this.banco;}
    public void setBanco(bancoEmpregados banco){this.banco = banco;}

    private void validacaoDados(String nome, String endereco, String salario, String tipo)throws EnderecoNuloException, NomeNuloException,
            SalarioNaoNumericoException, SalarioNegativoException, SalarioNuloException, TipoInvalidoException{

        double salarioDouble;

        if (endereco.isEmpty()){
            throw new EnderecoNuloException();
        }

        if (nome.isEmpty()){
            throw new NomeNuloException();
        }

        if (salario.isEmpty()){
            throw new SalarioNuloException();
        }

        try {
            salarioDouble = Double.parseDouble(salario);
        }catch (NumberFormatException e){
            throw new SalarioNaoNumericoException();
        }

        if (salarioDouble < 0){
            throw new SalarioNegativoException();
        }

        if (!tipo.equals("assalariado") && !tipo.equals("comissionado") && !tipo.equals("horista") ){
            throw new TipoInvalidoException();
        }
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws ComissaoNaoNumericaException,
            ComissaoNegativaException, ComissaoNulaException, EnderecoNuloException, NomeNuloException, SalarioNaoNumericoException,
            SalarioNegativoException, SalarioNuloException, TipoNaoAplicavelException, TipoInvalidoException{

        if (comissao.isEmpty() || comissao == null || comissao.isBlank()){
            throw new ComissaoNulaException();
        }

        String formatSalario = salario.replace(",", ".");
        String formatcomissao = comissao.replace(",", ".");

        String limpa = formatcomissao.trim();

        validacaoDados(nome, endereco, formatSalario, tipo);

        double salarioDouble;
        double comissaoDouble;


        try {
            comissaoDouble = Double.parseDouble(limpa);
        }catch (NumberFormatException e){
            throw new ComissaoNaoNumericaException();
        }

        if (comissaoDouble < 0){
            throw new ComissaoNegativaException();
        }

        salarioDouble = Double.parseDouble(formatSalario);

        if (tipo.equals("assalariado") || tipo.equals("horista") ){
            throw new TipoNaoAplicavelException();
        }

        int id = banco.addEmpregado(nome, endereco, tipo, salarioDouble, comissaoDouble);

        return String.format("%d",id);
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws EnderecoNuloException, NomeNuloException, SalarioNaoNumericoException,
            SalarioNegativoException, SalarioNuloException, TipoNaoAplicavelException, TipoInvalidoException{

        String formatSalario = salario.replace(",", ".");

        validacaoDados(nome, endereco, formatSalario, tipo);

        double salarioDouble;

        salarioDouble = Double.parseDouble(formatSalario);

        if (tipo.equals("comissionado")){
            throw new TipoNaoAplicavelException();
        }

        int id = banco.addEmpregado(nome, endereco, tipo, salarioDouble);

        return String.format("%d",id);
    }

    public String getAtributoEmpregad(String id, String atributo) throws AtributoNaoExisteException, EmpregadoNaoExisteException, IdEmpregadoNuloException,
            EmpregadoNaoComissionadoException{
        int idInt = convercaoId(id);

        if (banco.getEmp(idInt) == null){
            throw new EmpregadoNaoExisteException();
        }

        if (atributo.equals("nome")){
            return banco.getNome(idInt);

        } else if (atributo.equals("endereco")){
            return banco.getEndereco(idInt);

        } else if (atributo.equals("salario")){
            return String.format("%.2f", banco.getSalario(idInt)).replace(".", ",");

        } else if (atributo.equals("tipo")){
            return banco.getTipo(idInt);

        } else if (atributo.equals("sindicalizado")){
            return String.valueOf(banco.getSindicalizado(idInt));

        } else if (atributo.equals("comissao")){
            return String.format("%.2f", banco.getComissao(idInt)).replace(".", ",");

        }else {
            throw new AtributoNaoExisteException();
        }

    }


    public void removerEmpregado(String id) throws IdEmpregadoNuloException, EmpregadoNaoExisteException {
        int idInt = convercaoId(id);

        if (banco.getEmp(idInt) == null){
            throw new EmpregadoNaoExisteException();
        }

        banco.removerEmpregado(idInt);
    }

    public void lancaCartao(String id, String data, String horas) throws EmpregadoNaoExisteException, IdEmpregadoNuloException, HorasNulasExcepetion, HorasNaoNumericasException, HorasNaoPositivasException, DataInvalidaException, EmpregadoNaoHoristaException {
        int idInt = convercaoId(id);

        if (banco.getEmp(idInt) == null){
            throw new EmpregadoNaoExisteException();
        }

        double horasDouble;
        if (horas.isEmpty()){
            throw new HorasNulasExcepetion();
        }

        try {
            horasDouble = stringForDouble(horas);
        }catch (Exception e){
            throw new HorasNaoNumericasException();
        }

        if (horasDouble <= 0){
            throw new HorasNaoPositivasException();
        }

        LocalDate dataEmData = conversaoData(data);

        banco.lancaCartao(idInt, dataEmData, horasDouble);

    }

    public String getHorasNormaisTrabalhadas(String id, String dataInicial, String dataFinal) throws IdEmpregadoNuloException, EmpregadoNaoExisteException, DataInvalidaException, EmpregadoNaoHoristaException, DataInicialPosteriorFinalException, DataInicialInvalidaException, DataFinalInvalidaException {
        int idInt = convercaoId(id);

        if (banco.getEmp(idInt) == null){
            throw new EmpregadoNaoExisteException();
        }

        LocalDate dataInicialEmData;
        LocalDate dataFinalEmData;

        try {
            dataInicialEmData = conversaoData(dataInicial);
        }catch (DataInvalidaException e){
            throw new DataInicialInvalidaException();
        }

        try {
            dataFinalEmData = conversaoData(dataFinal);
        }catch (DataInvalidaException e){
            throw new DataFinalInvalidaException();
        }

        double totalHoras = banco.getHorasNormaisTrabalhadas(idInt, dataInicialEmData, dataFinalEmData);

        String res =  new BigDecimal(totalHoras).stripTrailingZeros().toPlainString().replace(".", ",");

        return res;
    }

    public String getHorasExtrasTrabalhadas(String id, String dataInicial, String dataFinal) throws IdEmpregadoNuloException, EmpregadoNaoExisteException, DataInvalidaException, EmpregadoNaoHoristaException, DataInicialPosteriorFinalException, DataInicialInvalidaException, DataFinalInvalidaException {
        int idInt = convercaoId(id);

        if (banco.getEmp(idInt) == null){
            throw new EmpregadoNaoExisteException();
        }

        LocalDate dataInicialEmData;
        LocalDate dataFinalEmData;

        try {
            dataInicialEmData = conversaoData(dataInicial);
        }catch (DataInvalidaException e){
            throw new DataInicialInvalidaException();
        }

        try {
            dataFinalEmData = conversaoData(dataFinal);
        }catch (DataInvalidaException e){
            throw new DataFinalInvalidaException();
        }

        double totalHoras = banco.getHorasExtrasTrabalhadas(idInt, dataInicialEmData, dataFinalEmData);

        String res =  new BigDecimal(totalHoras).stripTrailingZeros().toPlainString().replace(".", ",");;

        return res;
    }

    public void lancaVenda(String id, String data, String valor) throws IdEmpregadoNuloException, EmpregadoNaoExisteException, ValorNuloException, ValorNaoNumericoException, ValorNegativoException, DataInvalidaException, EmpregadoNaoComissionadoException {
        int idInt= convercaoId(id);

        if (banco.getEmp(idInt) == null){
            throw new EmpregadoNaoExisteException();
        }

        double valorDouble = stringForDouble(valor);

        if (valorDouble <= 0){
            throw new ValorNegativoException();
        }

        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("d/M/yyyy");

        LocalDate dataEmData;
        try {
            dataEmData = LocalDate.parse(data, formatador);
        } catch (DateTimeParseException e) {
            throw new DataInvalidaException();
        }

        banco.lancaVenda(idInt, dataEmData, valorDouble);
    }

    public String getVendasRealizadas(String id, String dataInicial, String dataFinal) throws IdEmpregadoNuloException, EmpregadoNaoExisteException, DataInicialInvalidaException, DataFinalInvalidaException, EmpregadoNaoComissionadoException, DataInicialPosteriorFinalException {
        int idInt = convercaoId(id);

        if (banco.getEmp(idInt) == null){
            throw new EmpregadoNaoExisteException();
        }

        LocalDate dataInicialEmData;
        LocalDate dataFinalEmData;

        try {
            dataInicialEmData = conversaoData(dataInicial);
        }catch (DataInvalidaException e){
            throw new DataInicialInvalidaException();
        }

        try {
            dataFinalEmData = conversaoData(dataFinal);
        }catch (DataInvalidaException e){
            throw new DataFinalInvalidaException();
        }

        double totalVendas = banco.getVendasRealizadas(idInt, dataInicialEmData, dataFinalEmData);

        String res =  String.format("%.2f", totalVendas).replace(".", ",");;

        return res;
    }

    public String getEmpregadoPorNome(String nome, String indice) throws EmpregadoNaoExisteException, EmpregadoPorNomeNaoExisteException {
        int indiceInt = Integer.parseInt(indice);

        return String.format("%d", banco.getEmpregadoPorNome(nome, indiceInt));
    }

    public void alteraEmpregado(String idEmpregado, String atributo, String valor, String idMembro, String taxa) throws IdEmpregadoNuloException, EmpregadoNaoExisteException, AtributoNaoExisteException, ValorNuloException, ValorNaoNumericoException, MembroJaExisteException {
        int idInt = convercaoId(idEmpregado);

        if (banco.getEmp(idInt) == null){
            throw new EmpregadoNaoExisteException();
        }

        if (!atributo.equals("sindicalizado")){
            throw new AtributoNaoExisteException();
        }

        double taxaDoube = stringForDouble(taxa);

        boolean valorBoolean;
        if (valor.equals("true")){
            valorBoolean = true;
        }else {
            valorBoolean = false;
        }

        banco.alteraEmpregadoSindicato(idInt, valorBoolean, idMembro);

        sindicato.criarMembro(idInt, idMembro, taxaDoube);
    }

    public void alteraEmpregado(String idEmpregado, String atributo, String valor) throws EmpregadoNaoExisteException, IdEmpregadoNuloException {
        int idInt = convercaoId(idEmpregado);

        banco.removerSindicatoEmpregado(idInt);
        sindicato.removerMembro(idInt);
    }

    public void lancaTaxaServico(String idMembro, String data, String valor) throws DataInvalidaException, ValorNuloException, ValorNaoNumericoException, ValorNegativoException, IdMembroNuloException, MembroNaoExisteException {
        if (idMembro.isEmpty()){
            throw new IdMembroNuloException();
        }

        LocalDate dataEmData = conversaoData(data);

        double valorDouble = stringForDouble(valor);

        if (valorDouble <= 0){
            throw new ValorNegativoException();
        }

        sindicato.lancaTaxaServico(idMembro, dataEmData, valorDouble);
    }

    public String getTaxasServico(String id, String dataInicial, String dataFinal) throws EmpregadoNaoExisteException, IdEmpregadoNuloException, DataInicialInvalidaException, DataFinalInvalidaException, EmpregadoNaoSindicalizadoException, DataInicialPosteriorFinalservicosException {
        int idInt = convercaoId(id);

        if (banco.getEmp(idInt) == null){
            throw new EmpregadoNaoExisteException();
        }

        LocalDate dataInicialEmData;
        LocalDate dataFinalEmData;

        try {
            dataInicialEmData = conversaoData(dataInicial);
        }catch (DataInvalidaException e){
            throw new DataInicialInvalidaException();
        }

        try {
            dataFinalEmData = conversaoData(dataFinal);
        }catch (DataInvalidaException e){
            throw new DataFinalInvalidaException();
        }

        if (!banco.getEmp(idInt).getSindicalizado()){
            throw new EmpregadoNaoSindicalizadoException();
        }

        double totalValorServicos = sindicato.getTaxasServico(idInt, dataInicialEmData, dataFinalEmData);

        String res =  String.format("%.2f", totalValorServicos).replace(".", ",");;

        return res;
    }

    private double stringForDouble(String src) throws ValorNuloException, ValorNaoNumericoException {
        double valor;
        if (src.isEmpty()){
            throw new ValorNuloException();
        }

        String formatValor = src.replace(",", ".");

        try {
            valor = Double.parseDouble(formatValor);
        }catch (NumberFormatException e){
            throw new ValorNaoNumericoException();
        }

        return valor;
    }

    private int convercaoId(String src) throws IdEmpregadoNuloException, EmpregadoNaoExisteException {
        if (src.isEmpty()){
            throw new IdEmpregadoNuloException();
        }

        int idInt;
        try {
            idInt = Integer.parseInt(src);
        } catch (NumberFormatException e) {
            throw new EmpregadoNaoExisteException();
        }

        return idInt;
    }

    private LocalDate conversaoData(String src) throws DataInvalidaException {
        int d;
        int m;
        int a;
        String[] partes = src.split("/");
        try{
            d = Integer.parseInt(partes[0]);
            m = Integer.parseInt(partes[1]);
            a = Integer.parseInt(partes[2]);
        }catch (Exception e){
            throw new DataInvalidaException();
        }

        if (m < 1 || m > 12){
            throw new DataInvalidaException();
        }
        if (m == 2 && (d < 1 || d>28)){
            throw new DataInvalidaException();
        }

        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("d/M/yyyy");
        LocalDate data;

        try {
            data = LocalDate.parse(src, formatador);
        } catch (DateTimeParseException e) {
            throw new DataInvalidaException();
        }

        return data;
    }

    public void zerarSistema() {
        banco.zerarSistema();
        sindicato.zerarSistema();
    }

    public void encerrarSistema() {
        banco.encerrarSistema();
        sindicato.encerrarSistema();
    }
}