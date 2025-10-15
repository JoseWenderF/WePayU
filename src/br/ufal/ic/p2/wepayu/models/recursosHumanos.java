package br.ufal.ic.p2.wepayu.models;

import javax.swing.*;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.BreakIterator;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;

public class recursosHumanos {
    ArrayList<empregado> listaEmpregados;
    ArrayList<menbroSindicato> listaMembrosSindicato;
    bancoSindicato sindicado;
    ArrayList<agendaDePagamento> listaAgendasDePagamento;

    public ArrayList<agendaDePagamento> getListaAgendasDePagamento() {
        return listaAgendasDePagamento;
    }

    public void setListaAgendasDePagamento(ArrayList<agendaDePagamento> listaAgendasDePagamento) {
        this.listaAgendasDePagamento = listaAgendasDePagamento;
    }

    public recursosHumanos() {
    }

    public recursosHumanos(ArrayList<empregado> listaEmpregados, ArrayList<menbroSindicato> listaMembrosSindicato, bancoSindicato sindicato) {
        this.listaEmpregados = listaEmpregados;
        this.listaMembrosSindicato = listaMembrosSindicato;
        this.sindicado = sindicato;

        try (XMLDecoder decoder = new XMLDecoder(new FileInputStream("recursos_humanos.xml"))) {
            listaAgendasDePagamento = (ArrayList<agendaDePagamento>) decoder.readObject();
        } catch (IOException e) {
            agendaDePagamento assalariado = new agendaDePagamento("mensal", "$");
            agendaDePagamento comissionado = new agendaDePagamento("semanal", "2", "5");
            agendaDePagamento horista = new agendaDePagamento("semanal", "5");
            listaAgendasDePagamento = new ArrayList<>();
            listaAgendasDePagamento.add(assalariado);
            listaAgendasDePagamento.add(comissionado);
            listaAgendasDePagamento.add(horista);
        }
    }

    public void zerarSistema(){
        agendaDePagamento assalariado = new agendaDePagamento("mensal", "$");
        agendaDePagamento comissionado = new agendaDePagamento("semanal", "2", "5");
        agendaDePagamento horista = new agendaDePagamento("semanal", "5");
        listaAgendasDePagamento = new ArrayList<>();
        listaAgendasDePagamento.add(assalariado);
        listaAgendasDePagamento.add(comissionado);
        listaAgendasDePagamento.add(horista);
    }

    public void encerrarSistema(){
        try (XMLEncoder encoder = new XMLEncoder(new FileOutputStream("sindicato.xml"))) {
            encoder.setPersistenceDelegate(LocalDate.class, new LocalDatePersistenceDelegate());
            encoder.writeObject(listaAgendasDePagamento);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public agendaDePagamento verificarSeAgendaPagamentoExiste(String tipo, String v1, String v2) {
        if (v2 == null) {
            for (agendaDePagamento agenda : this.listaAgendasDePagamento) {
                if (agenda.getTipo().equals(tipo) && agenda.getV1().equals(v1) && agenda.getV2() == null) {
                    return agenda;
                }
            }
        } else {
            for (agendaDePagamento agenda : this.listaAgendasDePagamento) {
                if (agenda.getTipo().equals(tipo) && agenda.getV1().equals(v1) && agenda.getV2().equals(v2)) {
                    return agenda;
                }
            }
        }

        return null;
    }

    public BigDecimal totalFolha(LocalDate data, ArrayList<empregado> listaEmpregados, ArrayList<menbroSindicato> listaMembrosSindicato) {
        this.listaEmpregados = listaEmpregados;
        this.listaMembrosSindicato = listaMembrosSindicato;
        BigDecimal total = BigDecimal.ZERO;
        for (empregado emp : this.listaEmpregados) {
            if (emp instanceof empregadoAssalariado) {
                if (verificarDiaPagamento(data, emp)){
                    empregadoAssalariado empAss = (empregadoAssalariado) emp;
                    total = total.add(totalPagamentoAssalariadoNovo(empAss, data));
                }
            } else if (emp instanceof empregadoHorista) {
                if (verificarDiaPagamento(data, emp)){
                    empregadoHorista empHor = (empregadoHorista) emp;
                    total = total.add(totalPagamentoHoristaNovo(empHor, data).get(0));
                }
            } else if (emp instanceof empregadoComissionado) {
                if (verificarDiaPagamento(data, emp)){
                    //System.out.println("comissionado");
                    empregadoComissionado empCom = (empregadoComissionado) emp;
                    total = total.add(totalPagamentoComissionadoNovo(empCom, data).get(0));
                }
            }
        }
        return total;
    }

    public boolean verificarDiaPagamento(LocalDate dataVerificar, empregado emp) {
        LocalDate dataContratacao;
        if (emp.getDataContratacao() == null) {
            dataContratacao = LocalDate.of(2005,1,1);
        }else {
            dataContratacao = emp.getDataContratacao();
        }



        if (dataVerificar.isBefore(dataContratacao)) {
            return false;
        }

        if (emp.getAgendaPagamento().getTipo().equals("semanal")) {
            DayOfWeek diaDaSemanaReceber;
            if (emp.getAgendaPagamento().getV2() == null) {
                diaDaSemanaReceber = DayOfWeek.of(Integer.parseInt(emp.getAgendaPagamento().getV1()));
            }else {
                diaDaSemanaReceber = DayOfWeek.of(Integer.parseInt(emp.getAgendaPagamento().getV2()));
            }

            DayOfWeek diaDeRecber = dataVerificar.getDayOfWeek();
            if (diaDaSemanaReceber != diaDeRecber) {
                return false;
            }

            if (emp.getAgendaPagamento().getV2() == null) {
                return true;
            }

            LocalDate primeiroDomingo = dataContratacao.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            LocalDate primeiroDia = primeiroDomingo.with(TemporalAdjusters.previousOrSame(diaDeRecber));
            long diferencaEmDias = ChronoUnit.DAYS.between(primeiroDia, dataVerificar);
            long quantidadeSemanas = diferencaEmDias / 7;
            if (quantidadeSemanas % Integer.parseInt(emp.getAgendaPagamento().getV1()) == 0) {
                return true;
            }
            return false;
        } else {
            if (emp.getAgendaPagamento().getV1().equals("$")) {
                LocalDate ultimoDiaDoMes = dataVerificar.with(TemporalAdjusters.lastDayOfMonth());
                if (dataVerificar.isEqual(ultimoDiaDoMes)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                int diaDataVerificar = dataVerificar.getDayOfMonth();
                if (diaDataVerificar == Integer.parseInt(emp.getAgendaPagamento().getV1())) {
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    public ArrayList<BigDecimal> totalPagamentoHoristaNovo(empregadoHorista empHor, LocalDate data) {
        ArrayList<BigDecimal> resposta = new ArrayList<>();
        BigDecimal salarioBruto = BigDecimal.ZERO;
        BigDecimal salarioBig = BigDecimal.valueOf(empHor.getSalario());
        BigDecimal totalHorasNormais = BigDecimal.ZERO;
        BigDecimal totalHorasExtras = BigDecimal.ZERO;

        LocalDate dataInicio = calculoDataIncial(empHor, data);

        for (cartaoPontos cartao : empHor.getListaPontos()) {
            if ((cartao.getData().isBefore(data) || cartao.getData().isEqual(data)) && cartao.getData().isAfter(dataInicio)) {
                totalHorasNormais = totalHorasNormais.add(BigDecimal.valueOf(cartao.getHorasNormais()));
                totalHorasExtras = totalHorasExtras.add(BigDecimal.valueOf(cartao.getHorasExtra()));
                salarioBruto = salarioBruto.add(BigDecimal.valueOf(cartao.getHorasNormais()).multiply(BigDecimal.valueOf(empHor.getSalario())));
                BigDecimal salarioHoraExtra = BigDecimal.valueOf(empHor.getSalario()).multiply(new BigDecimal("1.5"));
                salarioBruto = salarioBruto.add(BigDecimal.valueOf(cartao.getHorasExtra()).multiply(salarioHoraExtra));
            }
        }

        resposta.add(salarioBruto);
        resposta.add(totalHorasNormais);
        resposta.add(totalHorasExtras);

        return resposta;
    }

    public BigDecimal totalPagamentoAssalariadoNovo(empregadoAssalariado empAss, LocalDate data) {
        BigDecimal salarioBruto;
        BigDecimal salarioBig = BigDecimal.valueOf(empAss.getSalario());
        BigDecimal salarioSemana = salarioBig.multiply(new BigDecimal("12")).divide(new BigDecimal("52"), 2, RoundingMode.DOWN);

        if (empAss.getAgendaPagamento().getTipo().equals("mensal")) {
            salarioBruto = salarioBig;
        } else {
            if (empAss.getAgendaPagamento().getV2() == null) {
                salarioBruto = salarioSemana;
            } else {
                salarioBruto = salarioSemana.multiply(new BigDecimal(Integer.parseInt(empAss.getAgendaPagamento().getV1())));
            }
        }

        return salarioBruto;
    }

    public ArrayList<BigDecimal> totalPagamentoComissionadoNovo(empregadoComissionado empCom, LocalDate data) {
        BigDecimal salarioBruto;
        BigDecimal salarioBig = BigDecimal.valueOf(empCom.getSalario());
        BigDecimal fixo = BigDecimal.ZERO;

        if (empCom.getAgendaPagamento().getTipo().equals("mensal")) {
            salarioBruto = salarioBig;
        } else {
            BigDecimal salarioSemana;
            if (empCom.getAgendaPagamento().getV2() == null) {
                salarioSemana = salarioBig.multiply(new BigDecimal("12")).divide(new BigDecimal("52"), 2, RoundingMode.DOWN);
                salarioBruto = salarioSemana;
            } else {
                String numeradorQuanSemanas = String.format("%d", (12*Integer.parseInt(empCom.getAgendaPagamento().getV1())));
                //System.out.println("AQUIIIIIIIIIIIIIIIIIIIII " + (12*Integer.parseInt(empCom.getAgendaPagamento().getV1())));
                salarioBruto = salarioBig.multiply(new BigDecimal(numeradorQuanSemanas)).divide(new BigDecimal("52"), 2, RoundingMode.DOWN);
            }
        }

        fixo = fixo.add(salarioBruto);

        LocalDate dataInicio = calculoDataIncial(empCom, data);

        double totalVendas = 0;
        for (registroVenda venda : empCom.getListaVendas()){
            if (venda.getData().isAfter(dataInicio) && (venda.getData().isBefore(data) || venda.getData().isEqual(data))){
                totalVendas+=venda.getValor();
            }
        }
        BigDecimal totalVendasBig = BigDecimal.valueOf(totalVendas);
        BigDecimal comissaoTaxaBig = BigDecimal.valueOf(empCom.getComissao());
        BigDecimal comissaoBig = totalVendasBig.multiply(comissaoTaxaBig).setScale(2, RoundingMode.DOWN);
        salarioBruto = salarioBruto.add(comissaoBig);

        ArrayList<BigDecimal> res = new ArrayList<>();

        res.add(salarioBruto);
        res.add(fixo);
        res.add(totalVendasBig);
        res.add(comissaoBig);

        return res;
    }

    public LocalDate calculoDataIncial (empregado emp, LocalDate dataFinal){
        LocalDate dataInicio;
        if (emp.getAgendaPagamento().getTipo().equals("mensal")) {
            return dataFinal.minusMonths(1);
        } else {
            if (emp.getAgendaPagamento().getV2() == null) {
                return dataFinal.minusWeeks(1);
            } else {
                return dataFinal.minusWeeks(Integer.parseInt(emp.getAgendaPagamento().getV1()));
            }
        }
    }



    public void rodarFolhaNovo(String nomeArquivo, LocalDate data, ArrayList<empregado> listaEmpregados, ArrayList<menbroSindicato> listaMembrosSindicato) {
        this.listaEmpregados = listaEmpregados;
        this.listaMembrosSindicato = listaMembrosSindicato;

        Collections.sort(this.listaEmpregados, (e1, e2) -> e1.getNome().compareTo(e2.getNome()));

        StringBuilder assalariadosBuilder = new StringBuilder();
        StringBuilder comissionadosBuilder = new StringBuilder();
        StringBuilder horistasBuilder = new StringBuilder();

        String salarioBrutoFormatado;
        String taxaFormatada;
        String salarioLiquidoFormatado;
        String totalFolha;

        // Comissionados:
        BigDecimal totalFixo = BigDecimal.ZERO;
        BigDecimal totalVendas = BigDecimal.ZERO;
        BigDecimal totalComissao = BigDecimal.ZERO;
        BigDecimal totalSalarioBrutoComissionados = BigDecimal.ZERO;
        BigDecimal totalTaxasComissionados = BigDecimal.ZERO;
        BigDecimal totalSalarioLiquidoComissionados = BigDecimal.ZERO;
        String fixoFormatado;
        String vendasFormatado;
        String comissaoFormatada;

        // Horista
        BigDecimal totalHorasNormais = BigDecimal.ZERO;
        BigDecimal totalHorasExtras = BigDecimal.ZERO;
        BigDecimal totalSalarioBrutoHoristas = BigDecimal.ZERO;
        BigDecimal totalTaxasHoristas = BigDecimal.ZERO;
        BigDecimal totalSalarioLiquidoHoristas = BigDecimal.ZERO;
        BigInteger horasNormais;
        BigInteger horasExtras;

        // Assalariado
        BigDecimal totalSalarioBrutoAssalariado = BigDecimal.ZERO;
        BigDecimal totalTaxasAssalariado = BigDecimal.ZERO;
        BigDecimal totalSalarioLiquidoAssalariado = BigDecimal.ZERO;

        for (empregado emp : this.listaEmpregados) {
            if (verificarDiaPagamento(data, emp)) {
                BigDecimal taxa;
                if (emp instanceof empregadoAssalariado) {
                    empregadoAssalariado empAss = (empregadoAssalariado) emp;
                    BigDecimal salarioBrutoAssalariado = totalPagamentoAssalariadoNovo(empAss, data);
                    taxa = taxasSindicais(empAss, data);
                    BigDecimal salarioLiquidoAssalariado = salarioBrutoAssalariado.subtract(taxa);

                    String metodoPagamento = "emMaos";
                    if (empAss.getMetodoPagamento() != null) {
                        metodoPagamento = empAss.getMetodoPagamento();
                        if (empAss.getMetodoPagamento().equals("banco")) {
                            metodoPagamento = String.format("%s, Ag. %s CC %s", empAss.getBanco(), empAss.getAgencia(), empAss.getContaCorrente());
                        } else if (metodoPagamento.equals("emMaos")) {
                            metodoPagamento = "Em maos";
                        }else {
                            metodoPagamento = "Correios, " + empAss.getEndereco();
                        }
                    }

                    salarioBrutoFormatado = String.format("%s", salarioBrutoAssalariado.setScale(2, RoundingMode.HALF_UP)).replace(".", ",");
                    salarioLiquidoFormatado = String.format("%s", salarioLiquidoAssalariado.setScale(2, RoundingMode.HALF_UP)).replace(".", ",");
                    taxaFormatada = String.format("%s", taxa.setScale(2, RoundingMode.HALF_UP)).replace(".", ",");
                    String linha = String.format("%-48s %13s %9s %15s %-38s\n", empAss.getNome(), salarioBrutoFormatado, taxaFormatada, salarioLiquidoFormatado, metodoPagamento);
                    assalariadosBuilder.append(linha);

                    totalSalarioBrutoAssalariado = totalSalarioBrutoAssalariado.add(salarioBrutoAssalariado);
                    totalTaxasAssalariado = totalTaxasAssalariado.add(taxa);
                    totalSalarioLiquidoAssalariado = totalSalarioLiquidoAssalariado.add(salarioLiquidoAssalariado);

                } else if (emp instanceof empregadoComissionado) {
                    empregadoComissionado empCom = (empregadoComissionado) emp;
                    ArrayList<BigDecimal> res = totalPagamentoComissionadoNovo(empCom, data);
                    taxa = taxasSindicais(empCom, data);
                    BigDecimal salarioLiquido = res.get(0).subtract(taxa);
                    String metodoPagamento = "emMaos";
                    if (empCom.getMetodoPagamento() != null) {
                        metodoPagamento = empCom.getMetodoPagamento();
                        if (empCom.getMetodoPagamento().equals("banco")) {
                            metodoPagamento = String.format("%s, Ag. %s CC %s", empCom.getBanco(), empCom.getAgencia(), empCom.getContaCorrente());
                        } else if (metodoPagamento.equals("emMaos")) {
                            metodoPagamento = "Em maos";
                        }else {
                            metodoPagamento = "Correios, " + empCom.getEndereco();
                        }
                    }

                    fixoFormatado = String.format("%s", res.get(1).setScale(2, RoundingMode.HALF_UP)).replace(".", ",");
                    vendasFormatado = String.format("%s", res.get(2).setScale(2, RoundingMode.HALF_UP)).replace(".", ",");
                    comissaoFormatada = String.format("%s", res.get(3).setScale(2, RoundingMode.HALF_UP)).replace(".", ",");
                    salarioBrutoFormatado = String.format("%s", res.get(0).setScale(2, RoundingMode.HALF_UP)).replace(".", ",");
                    salarioLiquidoFormatado = String.format("%s", salarioLiquido.setScale(2, RoundingMode.HALF_UP)).replace(".", ",");
                    taxaFormatada = String.format("%s", taxa.setScale(2, RoundingMode.HALF_UP)).replace(".", ",");

                    String linha = String.format("%-21s %8s %8s %8s %13s %9s %15s %-38s\n", empCom.getNome(), fixoFormatado, vendasFormatado, comissaoFormatada, salarioBrutoFormatado, taxaFormatada, salarioLiquidoFormatado, metodoPagamento);
                    comissionadosBuilder.append(linha);

                    totalFixo = totalFixo.add(res.get(1));
                    totalVendas = totalVendas.add(res.get(2));
                    totalComissao = totalComissao.add(res.get(3));
                    totalSalarioBrutoComissionados = totalSalarioBrutoComissionados.add(res.get(0));
                    totalSalarioLiquidoComissionados = totalSalarioLiquidoComissionados.add(salarioLiquido);
                    totalTaxasComissionados = totalTaxasComissionados.add(taxa);

                } else if (emp instanceof empregadoHorista) {
                    empregadoHorista empHor = (empregadoHorista) emp;
                    ArrayList<BigDecimal> res = totalPagamentoHoristaNovo(empHor, data);
                    taxa = taxasSindicais(empHor, data);
                    taxa = taxa.add(empHor.getDevendoSindicato());

                    BigDecimal salarioLiquidoHorista;
                    if (res.get(0).compareTo(taxa) < 0) {
                        salarioLiquidoHorista = BigDecimal.ZERO;
                        empHor.setDevendoSindicato(taxa.subtract(res.get(0)));
                        taxa = res.get(0);
                    } else {
                        salarioLiquidoHorista = res.get(0).subtract(taxa);
                        empHor.setDevendoSindicato(BigDecimal.ZERO);
                    }

                    String metodoPagamento = "emMaos";
                    if (empHor.getMetodoPagamento() != null) {
                        metodoPagamento = empHor.getMetodoPagamento();
                        if (empHor.getMetodoPagamento().equals("banco")) {
                            metodoPagamento = String.format("%s, Ag. %s CC %s", empHor.getBanco(), empHor.getAgencia(), empHor.getContaCorrente());
                        } else if (metodoPagamento.equals("emMaos")) {
                            metodoPagamento = "Em maos";
                        }else {
                            metodoPagamento = "Correios, " + empHor.getEndereco();
                        }
                    }

                    horasNormais = res.get(1).toBigInteger();
                    horasExtras = res.get(2).toBigInteger();
                    salarioBrutoFormatado = String.format("%s", res.get(0).setScale(2, RoundingMode.HALF_UP)).replace(".", ",");
                    salarioLiquidoFormatado = String.format("%s", salarioLiquidoHorista.setScale(2, RoundingMode.HALF_UP)).replace(".", ",");
                    taxaFormatada = String.format("%s", taxa.setScale(2, RoundingMode.HALF_UP)).replace(".", ",");
                    String linha = String.format("%-36s %5s %5s %13s %9s %15s %-38s\n",
                            empHor.getNome(),
                            horasNormais,
                            horasExtras,
                            salarioBrutoFormatado,
                            taxaFormatada,
                            salarioLiquidoFormatado,
                            metodoPagamento);

                    horistasBuilder.append(linha);

                    totalHorasNormais = totalHorasNormais.add(res.get(1));
                    totalHorasExtras = totalHorasExtras.add(res.get(2));
                    totalSalarioBrutoHoristas = totalSalarioBrutoHoristas.add(res.get(0));
                    totalTaxasHoristas = totalTaxasHoristas.add(taxa);
                    totalSalarioLiquidoHoristas = totalSalarioLiquidoHoristas.add(salarioLiquidoHorista);
                }
            }
        }

        // Geração do conteúdo completo em uma única string
        StringBuilder conteudoArquivo = new StringBuilder();
        conteudoArquivo.append(String.format("FOLHA DE PAGAMENTO DO DIA %d-%02d-%02d\n", data.getYear(), data.getMonthValue(), data.getDayOfMonth()));
        conteudoArquivo.append("====================================\n\n");
        conteudoArquivo.append("===============================================================================================================================\n");
        conteudoArquivo.append("===================== HORISTAS ================================================================================================\n");
        conteudoArquivo.append("===============================================================================================================================\n");
        conteudoArquivo.append(String.format("Nome                                 Horas Extra Salario Bruto Descontos Salario Liquido Metodo\n"));
        conteudoArquivo.append("==================================== ===== ===== ============= ========= =============== ======================================\n");
        conteudoArquivo.append(horistasBuilder);
        horasNormais = totalHorasNormais.toBigInteger();
        horasExtras = totalHorasExtras.toBigInteger();
        salarioBrutoFormatado = String.format("%s", totalSalarioBrutoHoristas.setScale(2, RoundingMode.HALF_UP)).replace(".", ",");
        salarioLiquidoFormatado = String.format("%s", totalSalarioLiquidoHoristas.setScale(2, RoundingMode.HALF_UP)).replace(".", ",");
        taxaFormatada = String.format("%s", totalTaxasHoristas.setScale(2, RoundingMode.HALF_UP)).replace(".", ",");
        conteudoArquivo.append(String.format("\n%-36s %5s %5s %13s %9s %15s\n", "TOTAL HORISTAS", horasNormais, horasExtras, salarioBrutoFormatado, taxaFormatada, salarioLiquidoFormatado));

        conteudoArquivo.append("\n===============================================================================================================================\n");
        conteudoArquivo.append("===================== ASSALARIADOS ============================================================================================\n");
        conteudoArquivo.append("===============================================================================================================================\n");
        conteudoArquivo.append("Nome                                             Salario Bruto Descontos Salario Liquido Metodo\n");
        conteudoArquivo.append("================================================ ============= ========= =============== ======================================\n");
        conteudoArquivo.append(assalariadosBuilder);
        salarioBrutoFormatado = String.format("%s", totalSalarioBrutoAssalariado.setScale(2, RoundingMode.HALF_UP)).replace(".", ",");
        salarioLiquidoFormatado = String.format("%s", totalSalarioLiquidoAssalariado.setScale(2, RoundingMode.HALF_UP)).replace(".", ",");
        taxaFormatada = String.format("%s", totalTaxasAssalariado.setScale(2, RoundingMode.HALF_UP)).replace(".", ",");
        conteudoArquivo.append(String.format("\n%-48s %13s %9s %15s\n", "TOTAL ASSALARIADOS", salarioBrutoFormatado, taxaFormatada, salarioLiquidoFormatado));

        conteudoArquivo.append("\n===============================================================================================================================\n");
        conteudoArquivo.append("===================== COMISSIONADOS ===========================================================================================\n");
        conteudoArquivo.append("===============================================================================================================================\n");
        conteudoArquivo.append(String.format("Nome                  Fixo     Vendas   Comissao Salario Bruto Descontos Salario Liquido Metodo\n"));
        conteudoArquivo.append("===================== ======== ======== ======== ============= ========= =============== ======================================\n");
        conteudoArquivo.append(comissionadosBuilder);

        fixoFormatado = String.format("%s", totalFixo.setScale(2, RoundingMode.HALF_UP)).replace(".", ",");
        vendasFormatado = String.format("%s", totalVendas.setScale(2, RoundingMode.HALF_UP)).replace(".", ",");
        comissaoFormatada = String.format("%s", totalComissao.setScale(2, RoundingMode.HALF_UP)).replace(".", ",");
        salarioBrutoFormatado = String.format("%s", totalSalarioBrutoComissionados.setScale(2, RoundingMode.HALF_UP)).replace(".", ",");
        salarioLiquidoFormatado = String.format("%s", totalSalarioLiquidoComissionados.setScale(2, RoundingMode.HALF_UP)).replace(".", ",");
        taxaFormatada = String.format("%s", totalTaxasComissionados.setScale(2, RoundingMode.HALF_UP)).replace(".", ",");

        conteudoArquivo.append(String.format("\n%-21s %8s %8s %8s %13s %9s %15s\n", "TOTAL COMISSIONADOS", fixoFormatado, vendasFormatado, comissaoFormatada, salarioBrutoFormatado, taxaFormatada, salarioLiquidoFormatado));

        totalFolha = String.format("%s", totalSalarioBrutoComissionados.add(totalSalarioBrutoHoristas).add(totalSalarioBrutoAssalariado).setScale(2, RoundingMode.HALF_UP)).replace(".", ",");

        conteudoArquivo.append("\nTOTAL FOLHA: " + totalFolha);

        // Salva o conteúdo no arquivo
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo))) {
            writer.write(conteudoArquivo.toString());
        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo: " + e.getMessage());
        }
    }

    public BigDecimal taxasSindicais(empregado emp, LocalDate data){
        BigDecimal totalTaxas = BigDecimal.ZERO;
        menbroSindicato membro = sindicado.getMembroPorIdEmpregado(emp.getId());
        if (membro == null){
            return BigDecimal.ZERO;
        }

        if(emp.getAgendaPagamento().getTipo().equals("mensal")){
            BigDecimal quantidadeDias;
            if (emp.getAgendaPagamento().getV1().equals("$")){
                quantidadeDias = BigDecimal.valueOf(data.lengthOfMonth());
            }else {
                LocalDate inicioTaxas = data.minusMonths(1);
                quantidadeDias =  BigDecimal.valueOf(ChronoUnit.DAYS.between(inicioTaxas, data));
            }
            totalTaxas = totalTaxas.add(quantidadeDias.multiply(BigDecimal.valueOf(membro.getTaxa())));
        }else {
            if (emp.getAgendaPagamento().getV2() == null){
                totalTaxas = totalTaxas.add(new BigDecimal("7").multiply(BigDecimal.valueOf(membro.getTaxa())));
            }else {
                totalTaxas = totalTaxas.add(new BigDecimal(emp.getAgendaPagamento().getV1()).multiply(BigDecimal.valueOf(membro.getTaxa())));
                totalTaxas = totalTaxas.multiply(BigDecimal.valueOf(7));
            }
        }

        LocalDate dataInicial = calculoDataIncial(emp, data);

        for(servicoSindicato servico : membro.getListaServicos()){
            if (servico.getData().isAfter(dataInicial) && (servico.getData().isBefore(data) || servico.getData().isEqual(data))){
                totalTaxas = totalTaxas.add(BigDecimal.valueOf(servico.getValor()));
            }
        }
        return totalTaxas;
    }
}