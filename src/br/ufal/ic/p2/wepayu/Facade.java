package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.gerenciador;

public class Facade {

    gerenciador gerente;

    public Facade(){
        this.gerente = new gerenciador();
    }

    public String getNumeroDeEmpregados(){
        return gerente.getNumeroEmpregados();
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws SalarioNaoNumericoException, NomeNuloException, EnderecoNuloException, TipoNaoAplicavelException, TipoInvalidoException, SalarioNegativoException, SalarioNuloException {
        return gerente.criarEmpregado(nome, endereco, tipo, salario);
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws NomeNuloException, ComissaoNaoNumericaException, EnderecoNuloException, ComissaoNegativaException, SalarioNaoNumericoException, TipoNaoAplicavelException, TipoInvalidoException, ComissaoNulaException, SalarioNegativoException, SalarioNuloException {
        return gerente.criarEmpregado(nome, endereco, tipo, salario, comissao);
    }

    public String getAtributoEmpregado(String id, String atributo) throws AtributoNaoExisteException, EmpregadoNaoExisteException, IdEmpregadoNuloException, EmpregadoNaoComissionadoException, EmpregadoNaoRecebeBancoException, EmpregadoNaoSindicalizadoException {
        return gerente.getAtributoEmpregad(id, atributo);
    }

    public void removerEmpregado(String id) throws EmpregadoNaoExisteException, IdEmpregadoNuloException {
        gerente.removerEmpregado(id);
    }

    public void lancaCartao(String id, String data, String horas) throws HorasNaoNumericasException, EmpregadoNaoExisteException, HorasNaoPositivasException, IdEmpregadoNuloException, EmpregadoNaoHoristaException, DataInvalidaException, HorasNulasExcepetion {
        gerente.lancaCartao(id, data, horas);
    }

    public String getHorasNormaisTrabalhadas(String id, String dataInicial, String dataFinal) throws EmpregadoNaoExisteException, IdEmpregadoNuloException, EmpregadoNaoHoristaException, DataInvalidaException, DataInicialPosteriorFinalException, DataInicialInvalidaException, DataFinalInvalidaException {
        return gerente.getHorasNormaisTrabalhadas(id, dataInicial, dataFinal);
    }

    public String getHorasExtrasTrabalhadas(String id, String dataInicial, String dataFinal) throws EmpregadoNaoExisteException, IdEmpregadoNuloException, EmpregadoNaoHoristaException, DataInvalidaException, DataInicialPosteriorFinalException, DataInicialInvalidaException, DataFinalInvalidaException {
        return gerente.getHorasExtrasTrabalhadas(id, dataInicial, dataFinal);
    }

    public void lancaVenda(String id, String data, String valor) throws ValorNuloException, ValorNegativoException, EmpregadoNaoExisteException, IdEmpregadoNuloException, ValorNaoNumericoException, DataInvalidaException, EmpregadoNaoComissionadoException {
        gerente.lancaVenda(id, data, valor);
    }

    public String getVendasRealizadas(String id, String dataInicial, String dataFinal) throws DataInicialInvalidaException, EmpregadoNaoExisteException, IdEmpregadoNuloException, DataFinalInvalidaException, EmpregadoNaoComissionadoException, DataInicialPosteriorFinalException {
        return gerente.getVendasRealizadas(id, dataInicial, dataFinal);
    }

    public String getEmpregadoPorNome(String nome, String indice) throws EmpregadoNaoExisteException, EmpregadoPorNomeNaoExisteException {
        return gerente.getEmpregadoPorNome(nome, indice);
    }

    public void alteraEmpregado(String idEmpregado, String atributo, String valor, String idMembro, String taxa) throws ValorNuloException, EmpregadoNaoExisteException, AtributoNaoExisteException, IdEmpregadoNuloException, ValorNaoNumericoException, MembroJaExisteException, TaxaSindicatoNaoNumericaException, TaxaSindicatoNuloException, TaxaSindicatoNegativoException, IdMembroNuloException, IdSindicatoNuloException {
        gerente.alteraEmpregado(idEmpregado, atributo, valor, idMembro, taxa);
    }

    public void alteraEmpregado(String id, String atributo, String valor, String at_banco, String agencia, String contaCorrente) throws ValorNuloException, EmpregadoNaoExisteException, AtributoNaoExisteException, IdEmpregadoNuloException, BancoNuloException, AgenciaNuloException, ContaCorrenteNuloException {
        gerente.alteraEmpregado(id, atributo, valor, at_banco, agencia, contaCorrente);
    }

    public void  alteraEmpregado(String idEmpregado, String atributo, String valor) throws EmpregadoNaoExisteException, IdEmpregadoNuloException, ValorNuloException, ValorNaoNumericoException, EmpregadoNaoComissionadoException, AtributoNaoExisteException, NomeNuloException, EnderecoNuloException, TipoInvalidoException, SalarioNuloException, SalarioNaoNumericoException, SalarioNegativoException, ComissaoNulaException, ComissaoNaoNumericaException, ComissaoNegativaException, MetodoPagamentoInvalidoException, ValorNaoBooleanException, AgendaDePagamentoNaoDisponivelException {
        gerente.alteraEmpregado(idEmpregado, atributo, valor);
    }

    public void alteraEmpregado(String id, String atributo, String valor1, String valor2) throws EmpregadoNaoExisteException, IdEmpregadoNuloException, ValorNuloException, ValorNaoNumericoException{
        gerente.alteraEmpregado(id, atributo, valor1, valor2);
    }

    public void lancaTaxaServico(String idMembro, String data, String valor) throws ValorNuloException, ValorNegativoException, ValorNaoNumericoException, DataInvalidaException, IdMembroNuloException, MembroNaoExisteException {
        gerente.lancaTaxaServico(idMembro, data, valor);
    }

    public String getTaxasServico(String idEmpregado, String dataInicial, String dataFinal) throws DataInicialInvalidaException, EmpregadoNaoExisteException, IdEmpregadoNuloException, DataFinalInvalidaException, EmpregadoNaoSindicalizadoException, DataInicialPosteriorFinalservicosException {
        return gerente.getTaxasServico(idEmpregado, dataInicial, dataFinal);
    }

    public String totalFolha(String data) throws DataInvalidaException {
        return gerente.totalFolha(data);
    }

    public void rodaFolha(String nomeArquivo, String data) throws DataInvalidaException {
        gerente.rodarFolha(nomeArquivo, data);
    }

    public void zerarSistema(){
        gerente.zerarSistema();
    }

    public void encerrarSistema(){
        gerente.encerrarSistema();
    }

    public void undo() throws NaoHaComandosParaDesfazerException, ComandoAposEncerrarSistemaException {gerente.undo();}

    public void redo() throws ComandoAposEncerrarSistemaException {gerente.redo();}

}
