package br.ufal.ic.p2.wepayu.models;

import javax.swing.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class recursosHumanos {
    ArrayList<empregado> listaEmpregados;
    ArrayList<menbroSindicato> listaMembrosSindicato;

    public recursosHumanos(){}

    public recursosHumanos(ArrayList<empregado> listaEmpregados, ArrayList<menbroSindicato> listaMembrosSindicato){
        this.listaEmpregados = listaEmpregados;
        this.listaMembrosSindicato = listaMembrosSindicato;
    }

    public BigDecimal totalFolha(LocalDate data, ArrayList<empregado> listaEmpregados, ArrayList<menbroSindicato> listaMembrosSindicato){
        this.listaEmpregados = listaEmpregados;
        this.listaMembrosSindicato = listaMembrosSindicato;
        //System.out.println("numero de empregados na lista:" + this.listaEmpregados.size());
        //System.out.println("numero de membros na lista:" + this.listaMembrosSindicato.size());
        //System.out.println(this.listaEmpregados);
        BigDecimal total = BigDecimal.ZERO;
        //System.out.println("Data do lançamento atual" + data);
        for(empregado emp : this.listaEmpregados){
        //    System.out.println(emp.getId());
            if(emp instanceof empregadoAssalariado){
                empregadoAssalariado empAss = (empregadoAssalariado) emp;
                total =  total.add(totalPagamentoAssalariado(empAss, data));
            }else if(emp instanceof empregadoHorista){
                empregadoHorista empHor = (empregadoHorista) emp;
        //        System.out.println("data de contratação do horista e: " + empHor.getDataContratacao());
                total = total.add(totalPagamentoHorista(empHor, data) );
            } else if (emp instanceof empregadoComissionado) {
                empregadoComissionado empCom = (empregadoComissionado) emp;
                total = total.add(totalPagamentoComissionado(empCom, data));
            }
        //    System.out.println("TOTAL: " + total + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        return total;
    }

    public BigDecimal totalPagamentoAssalariado(empregadoAssalariado empAss, LocalDate data){
        int dia = data.getDayOfMonth();
        int mes = data.getMonthValue();
        int ano = data.getYear();

        BigDecimal salarioBruto = BigDecimal.ZERO;
        if (mes==1 || mes==3 || mes==5 || mes==7 || mes==8 || mes==10 || mes==12 ){
            if (dia == 31){
                salarioBruto = salarioBruto.add(BigDecimal.valueOf(empAss.getSalario()));
            }
        } else if (mes == 2) {
            if (ano%100 == 0){
                if(ano%400 == 0) {
                    if (dia == 29) {
                        salarioBruto = salarioBruto.add(BigDecimal.valueOf(empAss.getSalario()));
                    }
                }
            } else if (ano%4 == 0) {
                if (dia == 29){
                    salarioBruto = salarioBruto.add(BigDecimal.valueOf(empAss.getSalario()));
                }
            }else {
                if(dia == 28){
                    salarioBruto = salarioBruto.add(BigDecimal.valueOf(empAss.getSalario()));
                }
            }
        }else{
            if (dia == 30){
                salarioBruto = salarioBruto.add(BigDecimal.valueOf(empAss.getSalario()));
            }
        }

        return salarioBruto;
    }

    public BigDecimal totalPagamentoHorista(empregadoHorista empHor, LocalDate data){
        if (empHor.getDataContratacao() == null){
            //System.out.println("caiu aqui");
            return BigDecimal.ZERO;
        }

        if (empHor.getDataContratacao().isAfter(data)){
            return BigDecimal.ZERO;
        }

        long diferencaEmDias = ChronoUnit.DAYS.between(empHor.getDataContratacao(), data);
        diferencaEmDias++;
    //    System.out.println("Diferenca de dias é igual a: " + diferencaEmDias);
        BigDecimal salarioBruto = BigDecimal.ZERO;

        if (diferencaEmDias%7 == 0){
    //        System.out.println("é dia de pagamento horista");
            LocalDate dataAnterior = data.minusDays(7);
            for(cartaoPontos cartao : empHor.getListaPontos()){
    //            System.out.println("Cartao do dia: " + cartao.getData() );
                if ((cartao.getData().isAfter(data.minusDays(7)) || cartao.getData().isEqual(data.minusDays(7))) && cartao.getData().isBefore(data)){
    //                System.out.println("Contabilizado");
                    salarioBruto = salarioBruto.add(BigDecimal.valueOf(cartao.getHorasNormais()).multiply(BigDecimal.valueOf(empHor.getSalario())));
                    BigDecimal salarioHoraExtra = BigDecimal.valueOf(empHor.getSalario()).multiply(new BigDecimal("1.5"));
                    salarioBruto = salarioBruto.add(BigDecimal.valueOf(cartao.getHorasExtra()).multiply(salarioHoraExtra));
    //                System.out.println("salario bruto: " + salarioBruto);
                }
            }
        }

        //System.out.println("Ao final do calculo do horita salario liqido: " + salarioLiquido);

        return salarioBruto;
    }

    public BigDecimal totalPagamentoComissionado(empregadoComissionado empCom, LocalDate data){
        if (empCom.getDataContratacao() == null){
            return BigDecimal.ZERO;
        }
        long diferencaEmDias = ChronoUnit.DAYS.between(empCom.getDataContratacao(), data);
        diferencaEmDias++;
        BigDecimal salarioBruto = BigDecimal.ZERO;

        if (diferencaEmDias%14 == 0){
            BigDecimal salarioBig = BigDecimal.valueOf(empCom.getSalario());
            BigDecimal salarioFixoBD = salarioBig.multiply(new BigDecimal("12")).divide(new BigDecimal("26"), 2, RoundingMode.DOWN);
            salarioBruto = salarioBruto.add(salarioFixoBD);
            LocalDate dataAnterior = data.minusDays(14);
            double totalVendas = 0;
            for (registroVenda venda : empCom.getListaVendas()){
                if (venda.getData().isAfter(dataAnterior) && (venda.getData().isBefore(data) || venda.getData().isEqual(data))){
                    totalVendas+=venda.getValor();
                }
            }
            BigDecimal totalVendasBig = BigDecimal.valueOf(totalVendas);
            BigDecimal comissaoTaxaBig = BigDecimal.valueOf(empCom.getComissao());
            BigDecimal comissaoBig = totalVendasBig.multiply(comissaoTaxaBig).setScale(2, RoundingMode.DOWN);
            salarioBruto = salarioBruto.add(comissaoBig);
        }
        return salarioBruto;
    }

    public double totalPagamentoAssalariadoComTaxas(empregadoAssalariado empAss, LocalDate data){
        int dia = data.getDayOfMonth();
        int mes = data.getMonthValue();
        int ano = data.getYear();

        double salarioBruto = 0;
        if (mes==1 || mes==3 || mes==5 || mes==7 || mes==8 || mes==10 || mes==12 ){
            if (dia == 31){
                salarioBruto = empAss.getSalario();
            }
        } else if (mes == 2) {
            if (ano%100 == 0){
                if(ano%400 == 0) {
                    if (dia == 29) {
                        salarioBruto = empAss.getSalario();
                    }
                }
            } else if (ano%4 == 0) {
                if (dia == 29){
                    salarioBruto = empAss.getSalario();
                }
            }else {
                    if(dia == 28){
                        salarioBruto = empAss.getSalario();
                    }
                }
        }else{
            if (dia == 30){
                salarioBruto = empAss.getSalario();
            }
        }

        double salarioLiquido = salarioBruto;
        if (empAss.getSindicalizado()){
            for(menbroSindicato mem : listaMembrosSindicato){
                if(mem.getIdEmpregado() == empAss.getId()){
                    salarioLiquido -= (dia*mem.getTaxa());

                    for(servicoSindicato ser : mem.getListaServicos()){
                        if ((mes - 1) == ser.getData().getMonthValue()){
                            salarioLiquido -= ser.getValor();
                        }
                    }
                    break;
                }
            }
        }

        if (salarioLiquido < 0){
            salarioLiquido = 0;
        }

        return salarioLiquido;
    }

    public double totalPagamentoHoristaComTaxas(empregadoHorista empHor, LocalDate data){
        if (empHor.getDataContratacao() == null){
            //System.out.println("caiu aqui");
            return 0;
        }

        if (empHor.getDataContratacao().isAfter(data)){
            return 0;
        }

        long diferencaEmDias = ChronoUnit.DAYS.between(empHor.getDataContratacao(), data);
        diferencaEmDias++;
        System.out.println("Diferenca de dias é igual a: " + diferencaEmDias);
        double salarioBruto = 0;
        double salarioLiquido = salarioBruto;

        if (diferencaEmDias%7 == 0){
            System.out.println("é dia de pagamento horista");
            LocalDate dataAnterior = data.minusDays(7);
            for(cartaoPontos cartao : empHor.getListaPontos()){
                System.out.println("Cartao do dia: " + cartao.getData() );
//                if ((cartao.getData().isAfter(dataAnterior))  && (cartao.getData().isBefore(data) || cartao.getData().isEqual(data)))
                if ((cartao.getData().isAfter(data.minusDays(7)) || cartao.getData().isEqual(data.minusDays(7))) && cartao.getData().isBefore(data)){
                    System.out.println("Contabilizado");
                    salarioBruto += (cartao.getHorasNormais() * empHor.getSalario());
                    salarioBruto += (cartao.getHorasExtra() * (empHor.getSalario() * 1.5));
                    System.out.println("salario bruto: " + salarioBruto);
                }
            }

            salarioLiquido = salarioBruto;
            if(empHor.getSindicalizado()){
                for (menbroSindicato membro: listaMembrosSindicato){
                    if (membro.getIdEmpregado() == empHor.getId()){
                        salarioLiquido -= (7 * membro.getTaxa());

                        LocalDate inicioIntervaloAnterior = dataAnterior.minusDays(7);
                        for (servicoSindicato ser : membro.getListaServicos()){
                            if(ser.getData().isAfter(inicioIntervaloAnterior) && (ser.getData().isBefore(dataAnterior) || ser.getData().isEqual(dataAnterior))){
                                salarioLiquido -= ser.getValor();
                            }
                        }
                    }
                }
            }
        }

        if (salarioLiquido < 0){
            salarioLiquido = 0;
        }

        System.out.println("Ao final do calculo do horita salario liqido: " + salarioLiquido);

        return salarioLiquido;
    }

    public double totalPagamentoComissionadoComTaxas(empregadoComissionado empCom, LocalDate data){
        if (empCom.getDataContratacao() == null){
            return 0;
        }
        long diferencaEmDias = ChronoUnit.DAYS.between(empCom.getDataContratacao(), data);
        diferencaEmDias++;
        double salarioBruto = 0;
        double salarioLiquido = salarioBruto;

        if (diferencaEmDias%14 == 0){
            salarioBruto += empCom.getSalario() * ((double) 24/52);
            LocalDate dataAnterior = data.minusDays(14);
            for (registroVenda venda : empCom.getListaVendas()){
                if (venda.getData().isAfter(dataAnterior) && (venda.getData().isBefore(data) || venda.getData().isEqual(data))){
                    salarioBruto += (venda.getValor() * empCom.getComissao());
                }
            }
            salarioLiquido = salarioBruto;
            if(empCom.getSindicalizado()){
                for (menbroSindicato membro: listaMembrosSindicato){
                    if (membro.getIdEmpregado() == empCom.getId()){
                        salarioLiquido -= (14 * empCom.getSalario());

                        LocalDate inicioIntervaloAnterior = dataAnterior.minusDays(14);
                        for (servicoSindicato ser : membro.getListaServicos()){
                            if(ser.getData().isAfter(inicioIntervaloAnterior) && (ser.getData().isBefore(dataAnterior) || ser.getData().isEqual(dataAnterior))){
                                salarioLiquido -= ser.getValor();
                            }
                        }
                    }
                }
            }
        }
        if (salarioLiquido < 0){
            salarioLiquido = 0;
        }

        return salarioLiquido;
    }

}
