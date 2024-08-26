package es2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Esercizio2 {
    /**
     * La mappa di codifica Huffman, che è di dimensione piccola (m = 8), quindi
     * le operazioni di accesso e ricerca sono O(1).
     */
    private static final Map<String, String> huffmanMap = new HashMap<>();
    private static final int MAX_PREFIX_LENGTH;

    static { // O(m) con m = 8
        huffmanMap.put("0", "A");
        huffmanMap.put("00", "B");
        huffmanMap.put("001", "C");
        huffmanMap.put("010", "D");
        huffmanMap.put("0010", "E");
        huffmanMap.put("0100", "F");
        huffmanMap.put("0110", "G");
        huffmanMap.put("0001", "H");

        MAX_PREFIX_LENGTH = getMaxPrefixLength();
    }

    public static void main(String[] args) throws IOException {
        String fileName = "es2/input.txt";
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String binaryInput = reader.readLine().trim();
        reader.close();

        if (binaryInput.isEmpty()) {
            System.out.println(0);
            return;
        }

        // Risolvi usando la programmazione dinamica con memoization
        List<String> sequences = decodeUsingDP(binaryInput);

        // Stampa i risultati
        printSequences(sequences);
    }

    /**
     * Decodifica la stringa binaria usando la programmazione dinamica con
     * memoization, per evitare di ricalcolare le stesse soluzioni più volte.
     * 
     * La complessità temporale è compresa tra O(n^2) e O(n*2^n), dove n è la
     * lunghezza della stringa binaria in input.
     * 
     * La complessità deriva dal fatto che si tenta di decodificare la stringa
     * binaria per ogni possibile prefisso (m prefissi) e, per ciascun prefisso, si
     * prova a decodificare la sottostringa restante, fino a raggiungere la fine
     * della stringa binaria.
     * 
     * La memoization permette di evitare di ricalcolare le stesse soluzioni più
     * volte, riducendo così la complessità dell'algoritmo.
     * 
     * La complessità è influenzata anche dalla lunghezza massima dei prefissi
     * presenti nella mappa delle codifiche, che è 4 nel mio caso.
     * 
     * Grazie alla memoization e al MAX_PREFIX_LENGTH, l'algoritmo si avvicina alla
     * complessità media di O(n^2), ma può variare a seconda della stringa binaria.
     * 
     * Senza memoization, la complessità sarebbe O(n*2^n), poiché per ogni prefisso
     * della stringa binaria, si prova a decodificarlo, e per ogni prefisso si prova
     * a decodificare la sottostringa rimanente, fino a quando non si arriva alla
     * fine della stringa binaria.
     * 
     * @param binaryInput La stringa binaria da decodificare
     * @return Lista di tutte le possibili decodifiche della stringa binaria
     */
    private static List<String> decodeUsingDP(String binaryInput) {
        Map<String, List<String>> memo = new HashMap<>(binaryInput.length());

        return decode(binaryInput, memo);
    }

    private static List<String> decode(String codice, Map<String, List<String>> memo) {

        // Se la stringa è già stata calcolata, ritorna la soluzione memorizzata,
        // in modo da evitare di ricalcolare la stessa soluzione più volte.
        if (memo.containsKey(codice)) { // O(1) per accesso alla mappa
            return memo.get(codice);
        }

        // Lista per contenere tutte le possibili decodifiche
        List<String> sequences = new ArrayList<>();

        /**
         * Prova tutte le possibili decodifiche, ovvero più piccole della dimensione
         * massima del prefisso e della stringa corrente.
         * MAX_PREFIX_LENGTH non è necessario per il funzionamento dell'algoritmo, ma
         * serve per evitare di provare a decodificare prefissi più lunghi di quelli
         * presenti nella mappa delle codifiche, ottimizzando così le prestazioni,
         * riducendo
         * il numero di iterazioni necessarie per trovare le decodifiche, da
         * codice.length() a MAX_PREFIX_LENGTH, che nel mio caso è 4.
         */
        for (int i = 1; i <= codice.length() && i <= MAX_PREFIX_LENGTH; i++) {
            String prefix = codice.substring(0, i);
            /**
             * Se il prefisso è presente nella mappa delle codifiche trovate, allora
             * prosegui con la decodifica della sottostringa rimanente, ignorando quella
             * corrente (prefix).
             */
            if (huffmanMap.containsKey(prefix)) { // O(1) per accesso alla mappa
                String letter = huffmanMap.get(prefix); // O(1) per accesso alla mappa
                String suffix = codice.substring(i);

                /**
                 * Se la sottostringa rimanente è vuota, allora la decodifica è completa e
                 * letter è la lettera finale della soluzione corrente.
                 * 
                 * Ad esempio se ho una stringa 0010, dato che le soluzioni sono:
                 * 0010 -> [AD, CA, E]
                 * 
                 * le lettere che chiudono il ciclo sono D, A ed E.
                 * 
                 * Chiudono il ciclo per ogni sequenza possibile.
                 * 
                 */
                if (suffix.isEmpty()) {
                    sequences.add(letter);
                    System.out.println(letter);
                } else {
                    // Decodifica ricorsiva della sottostringa rimanente
                    List<String> suffixDecodings = decode(suffix, memo);

                    // Costruisci le stringhe decodificate
                    for (String decoding : suffixDecodings) {
                        /**
                         * Utilizzare StringBuilder per costruire le stringhe decodificate è più
                         * efficiente rispetto alla concatenazione di stringhe, ed essendo una
                         * operazione frequente, è importante ottimizzarla.
                         */
                        StringBuilder sb = new StringBuilder();
                        sb.append(letter).append(decoding);

                        // sequences è la lista di tutte le stringhe che traducono il binario in lettere
                        sequences.add(sb.toString());
                    }
                }
            }
        }

        // Memorizza la lista di decodifiche trovate per la stringa corrente
        memo.put(codice, sequences); // O(1) per operazione di inserimento nella mappa
        return sequences;
    }

    /**
     * Calcola la lunghezza massima dei prefissi presenti nella mappa delle
     * codifiche. Poiché la mappa è piccola, l'operazione è eseguita una sola volta
     * ed è accettabile in termini di prestazioni.
     * 
     * Complessità: O(m) con m = 8, dove m è il numero di codifiche nella mappa.
     * 
     * @return lunghezza massima dei prefissi
     */
    private static int getMaxPrefixLength() {
        int maxLength = 0;
        for (String key : huffmanMap.keySet()) {
            int length = key.length();
            if (length > maxLength) {
                maxLength = length;
            }
        }
        return maxLength; // in questo caso è 4, ma può variare a seconda della mappa
    }

    /**
     * Stampa le sequenze decodificate
     * 
     * @param sequences Lista delle sequenze decodificate
     */
    private static void printSequences(List<String> sequences) {
        if (sequences.isEmpty()) {
            System.out.println(0);
        } else {
            // Stampa in griglia le decodifiche
            int i = 0;
            System.out.println(sequences.size());
            for (String seq : sequences) { // O(k) con k = numero di sequenze
                System.out.print(seq);
                if (++i % 10 == 0) {
                    System.out.println();
                } else {
                    System.out.print("\t");
                }
            }
            System.out.println();
        }
    }

}
