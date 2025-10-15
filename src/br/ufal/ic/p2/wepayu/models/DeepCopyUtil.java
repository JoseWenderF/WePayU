package br.ufal.ic.p2.wepayu.models;

import java.io.*;

public class DeepCopyUtil {

    public static <T extends Serializable> T deepCopy(T original) {
        try {
            // Serializacao: transforma o objeto em um fluxo de bytes
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(original);

            // Desserializacao: reconstrui o objeto a partir dos bytes
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);

            return (T) ois.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null; // ou lanca uma excecao customizada
        }
    }
}