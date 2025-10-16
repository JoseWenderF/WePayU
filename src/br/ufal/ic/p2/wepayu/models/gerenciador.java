package br.ufal.ic.p2.wepayu.models;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import br.ufal.ic.p2.wepayu.Exception.*;

import java.math.BigDecimal;


import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CompletionException;

public class gerenciador {
    bancoEmpregados banco;
    bancoSindicato sindicato;
    recursosHumanos rh;
    Stack<estado> pilha_undo;
    Stack<estado> pilha_redo;
    boolean estadoSistema;

    public gerenciador(){
        banco = new bancoEmpregados();
        sindicato = new bancoSindicato();
        rh = new recursosHumanos(banco.getListaEmpregados(), sindicato.getListaMebrosSindicatos(), sindicato);
        this.pilha_undo = new Stack<>();
        this.pilha_redo = new Stack<>();
        estadoSistema = true;
    }

    public bancoEmpregados getBanco(){return this.banco;}
    public void setBanco(bancoEmpregados banco){this.banco = banco;}

    public String getNumeroEmpregados(){
        return String.format("%d", banco.getNumeroEmpregados());
    }

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

        this.criar_undo();

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

        this.criar_undo();

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
            EmpregadoNaoComissionadoException, EmpregadoNaoRecebeBancoException, EmpregadoNaoSindicalizadoException {
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

        }else if(atributo.equals("metodoPagamento")){
            return banco.getMetodoPagamento(idInt);
        }else if(atributo.equals("banco")){
            return banco.getBanco(idInt);
        }else if(atributo.equals("agencia")){
            return banco.getAgencia(idInt);
        }else if(atributo.equals("contaCorrente")){
            return banco.getContaCorrente(idInt);
        }else if(atributo.equals("idSindicato")){
            empregado emp = banco.getEmp(idInt);
            if (!emp.getSindicalizado()){
                throw new EmpregadoNaoSindicalizadoException();
            }
            return sindicato.getIdSindicato(idInt);
        }else if(atributo.equals("taxaSindical")){
            empregado emp = banco.getEmp(idInt);
            if (!emp.getSindicalizado()){
                throw new EmpregadoNaoSindicalizadoException();
            }
            return String.format("%.2f", sindicato.getTaxa(idInt)).replace(".", ",");
        } else if (atributo.equals("agendaPagamento")) {
            agendaDePagamento agenda = banco.getAgendaDePagamento(idInt);
            if (agenda.getV2() == null){
                return String.format(agenda.getTipo() + " " + agenda.getV1());
            }else {
                return String.format(agenda.getTipo() + " " + agenda.getV1() + " " + agenda.getV2());
            }
        } else {
            throw new AtributoNaoExisteException();
        }

    }


    public void removerEmpregado(String id) throws IdEmpregadoNuloException, EmpregadoNaoExisteException {

        this.criar_undo();

        int idInt = convercaoId(id);

        if (banco.getEmp(idInt) == null){
            throw new EmpregadoNaoExisteException();
        }

        banco.removerEmpregado(idInt);
    }

    public void lancaCartao(String id, String data, String horas) throws EmpregadoNaoExisteException, IdEmpregadoNuloException, HorasNulasExcepetion, HorasNaoNumericasException, HorasNaoPositivasException, DataInvalidaException, EmpregadoNaoHoristaException {
        this.criar_undo();

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

        this.criar_undo();

        try {
            banco.lancaVenda(idInt, dataEmData, valorDouble);
        }catch (Exception e){
            pilha_undo.pop();
            throw e;
        }

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

    public void alteraEmpregado(String idEmpregado, String atributo, String valor, String idMembro, String taxa) throws IdEmpregadoNuloException, EmpregadoNaoExisteException, AtributoNaoExisteException, ValorNuloException, ValorNaoNumericoException, MembroJaExisteException, IdMembroNuloException, TaxaSindicatoNuloException, TaxaSindicatoNaoNumericaException, TaxaSindicatoNegativoException, IdSindicatoNuloException {
        this.criar_undo();

        int idInt = convercaoId(idEmpregado);

        if (banco.getEmp(idInt) == null){
            throw new EmpregadoNaoExisteException();
        }

        if (!atributo.equals("sindicalizado")){
            throw new AtributoNaoExisteException();
        }

        double taxaDoube;
        try {
            taxaDoube = stringForDouble(taxa);
        }catch (ValorNuloException e){
            throw new TaxaSindicatoNuloException();
        }catch (ValorNaoNumericoException e){
            throw new TaxaSindicatoNaoNumericaException();
        }

        if (taxaDoube < 0){
            throw new TaxaSindicatoNegativoException();
        }

        boolean valorBoolean;
        if (valor.equals("true")){
            valorBoolean = true;
        }else {
            valorBoolean = false;
        }

        if(idMembro.isBlank()){
            throw new IdSindicatoNuloException();
        }

        banco.alteraEmpregadoSindicato(idInt, valorBoolean, idMembro);

        sindicato.criarMembro(idInt, idMembro, taxaDoube);
    }

    public void alteraEmpregado(String idEmpregado, String atributo, String valor) throws EmpregadoNaoExisteException, IdEmpregadoNuloException, ValorNuloException, ValorNaoNumericoException, EmpregadoNaoComissionadoException, AtributoNaoExisteException, NomeNuloException, EnderecoNuloException, TipoInvalidoException, SalarioNuloException, SalarioNaoNumericoException, SalarioNegativoException, ComissaoNulaException, ComissaoNaoNumericaException, ComissaoNegativaException, MetodoPagamentoInvalidoException, ValorNaoBooleanException, AgendaDePagamentoNaoDisponivelException {
        this.criar_undo();

        int idInt = convercaoId(idEmpregado);

        if (atributo.equals("sindicalizado")){
            if (!valor.equals("true") && !valor.equals("false")){
                throw new ValorNaoBooleanException();
            }
            banco.removerSindicatoEmpregado(idInt);
            sindicato.removerMembro(idInt);
        }else if(atributo.equals("comissao")){
            if (valor.isBlank()){
                throw new ComissaoNulaException();
            }

            double valorDouble;
            try {
                valorDouble = stringForDouble(valor);
            } catch (Exception e) {
                throw new ComissaoNaoNumericaException();
            }

            if(valorDouble < 0){
                throw new ComissaoNegativaException();
            }

            banco.alterarEmpregadoComissao(idInt, valorDouble);
        }else if(atributo.equals("nome")){
            if (valor.isBlank()){
                throw new NomeNuloException();
            }
            banco.alterarEmpregadoNome(idInt, valor);
        }else if (atributo.equals("endereco")){
            if (valor.isBlank()){
                throw new EnderecoNuloException();
            }
            banco.alterarEmpregadoEndereco(idInt, valor);
        }else if(atributo.equals("tipo")){
            if (!valor.equals("assalariado")){
                throw new TipoInvalidoException();
            }
            banco.alterarEmpregadoTipoParaAssalariado(idInt);
        }else if(atributo.equals("salario")){
            if (valor.isBlank()){
                throw new SalarioNuloException();
            }

            double valorDouble;
            try {
                valorDouble = stringForDouble(valor);
            } catch (Exception e) {
                throw new SalarioNaoNumericoException();
            }

            if(valorDouble < 0){
                throw new SalarioNegativoException();
            }
            banco.alterarEmpregadoSalario(idInt, valorDouble);
        }else if (atributo.equals("metodoPagamento")){
            if (!valor.equals("emMaos") && !valor.equals("correios")){
                throw new MetodoPagamentoInvalidoException();
            }

            banco.alteraEmpregadoMetodoPagamento(idInt, valor);
        } else if (atributo.equals("agendaPagamento")) {
            String[] palavras = valor.split(" ");
            agendaDePagamento cont;

            if (palavras.length < 2 || palavras.length > 3){
                throw new AgendaDePagamentoNaoDisponivelException();
            }

            if (palavras.length == 2){
                cont = rh.verificarSeAgendaPagamentoExiste(palavras[0], palavras[1], null);
            }else {
                cont = rh.verificarSeAgendaPagamentoExiste(palavras[0], palavras[1], palavras[2]);
            }

            if (cont == null){
                throw new AgendaDePagamentoNaoDisponivelException();
            }

            banco.alterarEmpregadoAgendaDePagamento(idInt, cont);

        } else{
            throw new AtributoNaoExisteException();
        }
    }

    public void alteraEmpregado(String id, String atributo, String valor, String at_banco, String agencia, String contaCorrente) throws EmpregadoNaoExisteException, IdEmpregadoNuloException, AtributoNaoExisteException, ValorNuloException, BancoNuloException, AgenciaNuloException, ContaCorrenteNuloException {
        this.criar_undo();

        int idInt = convercaoId(id);

        if (banco.getEmp(idInt) == null){
            throw new EmpregadoNaoExisteException();
        }

        if(!atributo.equals("metodoPagamento")){
            throw new AtributoNaoExisteException();
        }

        if(!valor.equals("banco")){
            throw new AtributoNaoExisteException();
        }

        if(at_banco.isBlank()){
            throw new BancoNuloException();
        }

        if(agencia.isBlank()){
            throw new AgenciaNuloException();
        }

        if(contaCorrente.isBlank()){
            throw new ContaCorrenteNuloException();
        }

        banco.alteraEmpregadoMetodoPagamento(idInt, atributo, valor, at_banco, agencia, contaCorrente);
    }

    public void alteraEmpregado(String id, String atributo, String valor1, String valor2) throws EmpregadoNaoExisteException, IdEmpregadoNuloException, ValorNuloException, ValorNaoNumericoException {
        this.criar_undo();

        int idInt = convercaoId(id);

        if (banco.getEmp(idInt) == null){
            throw new EmpregadoNaoExisteException();
        }

        if (atributo.equals("tipo")){
            double sal_ou_com = stringForDouble(valor2);
            if(valor1.equals("horista")){
                banco.alterarEmpregadoTipoParaHorista(idInt, sal_ou_com);
            }else if(valor1.equals("comissionado")){
                banco.alterarEmpregadoTipoParaComissionado(idInt, sal_ou_com);
            }
        }
    }

    public void lancaTaxaServico(String idMembro, String data, String valor) throws DataInvalidaException, ValorNuloException, ValorNaoNumericoException, ValorNegativoException, IdMembroNuloException, MembroNaoExisteException {
        this.criar_undo();

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

    public String totalFolha(String data_str) throws DataInvalidaException {
            //System.out.println(banco.getListaEmpregados().size());
            //System.out.println(sindicato.getListaMebrosSindicatos().size());

            LocalDate data = conversaoData(data_str);

            return String.format("%.2f", rh.totalFolha(data, banco.getListaEmpregados(), sindicato.getListaMebrosSindicatos()));
    }

    public void rodarFolha(String data_str, String nomeArquivo) throws DataInvalidaException {
        this.criar_undo();
        LocalDate data = conversaoData(data_str);
        rh.rodarFolhaNovo(nomeArquivo, data, banco.getListaEmpregados(), sindicato.getListaMebrosSindicatos());
    }

    public void criarAgendarDePagamento(String agendaPagamentoStr) throws DescricaoDeAgendaIncalidaException, AgendaDePagamentoNaoDisponivelException, AgendaJaExisteException {
        String[] palavras = agendaPagamentoStr.split(" ");
        agendaDePagamento cont;

        if (palavras.length < 2 || palavras.length > 3){
            throw new DescricaoDeAgendaIncalidaException();
        }

        if (palavras.length == 2){
            cont = rh.verificarSeAgendaPagamentoExiste(palavras[0], palavras[1], null);
        }else {
            cont = rh.verificarSeAgendaPagamentoExiste(palavras[0], palavras[1], palavras[2]);
        }

        if (cont != null){
            throw new AgendaJaExisteException();
        }

        if (palavras.length == 2){
            rh.criarAgendaDePagamento(palavras[0], palavras[1], null);
        }else {
            rh.criarAgendaDePagamento(palavras[0], palavras[1], palavras[2]);
        }
    }

    public void zerarSistema() {
        criar_undo();
        banco.zerarSistema();
        sindicato.zerarSistema();
        rh.zerarSistema();
    }

    public void encerrarSistema() {
        banco.encerrarSistema();
        sindicato.encerrarSistema();
        rh.encerrarSistema();
        pilha_undo.clear();
        pilha_redo.clear();
        estadoSistema = false;
    }

    public void undo() throws NaoHaComandosParaDesfazerException, ComandoAposEncerrarSistemaException {
        if (!this.estadoSistema){
            throw new ComandoAposEncerrarSistemaException();
        }
        if (pilha_undo.size() == 0){
            throw new NaoHaComandosParaDesfazerException();
        }
        estado atual = new estado(banco.getListaEmpregados(), sindicato.getListaMebrosSindicatos(), rh.listaAgendasDePagamento);
        pilha_redo.push(atual);
        estado anterior = pilha_undo.pop();
        banco.setListaEmpregados(anterior.getCopiaEmpregados());
        sindicato.setListaMebrosSindicatos(anterior.getCopiaMembrosSindicato());
        rh.setListaAgendasDePagamento(anterior.getCopiaAgendasDePagamentos());
    }

    public void redo() throws ComandoAposEncerrarSistemaException {
        if (!this.estadoSistema){
            throw new ComandoAposEncerrarSistemaException();
        }
        estado atual = new estado(banco.getListaEmpregados(), sindicato.getListaMebrosSindicatos(), rh.listaAgendasDePagamento);
        pilha_undo.push(atual);
        estado anterior = pilha_redo.pop();
        banco.setListaEmpregados(anterior.getCopiaEmpregados());
        sindicato.setListaMebrosSindicatos(anterior.getCopiaMembrosSindicato());
        rh.setListaAgendasDePagamento(anterior.getCopiaAgendasDePagamentos());
    }

    public void criar_undo(){
        estado atual = new estado(banco.getListaEmpregados(), sindicato.getListaMebrosSindicatos(), rh.listaAgendasDePagamento);
        pilha_undo.push(atual);
        this.pilha_redo.clear();
        //System.out.println(pilha_undo.size());
    }

}