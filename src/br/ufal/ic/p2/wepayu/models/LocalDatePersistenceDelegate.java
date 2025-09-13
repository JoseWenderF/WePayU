package br.ufal.ic.p2.wepayu.models;

import java.beans.Encoder;
import java.beans.Expression;
import java.beans.PersistenceDelegate;
import java.time.LocalDate;

// Classe que ensina a serializar/desserializar LocalDate
public class LocalDatePersistenceDelegate extends PersistenceDelegate {

    @Override
    protected Expression instantiate(Object oldInstance, Encoder out) {
        LocalDate date = (LocalDate) oldInstance;
        // Diz ao XMLEncoder para usar LocalDate.of() para criar o objeto
        return new Expression(date, LocalDate.class, "of",
                new Object[] { date.getYear(), date.getMonthValue(), date.getDayOfMonth() });
    }
}