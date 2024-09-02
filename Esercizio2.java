
/****
 * SIMONE SAMOGGIA 970758
 * simone.samoggia2@studio.unibo.it
 * 
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * La mappa delle codifiche di Huffman ha grandezza m, dove m è il numero di
 * decodifiche possibili, ed essendo una mappa, l'accesso è in O(1).
 *
 * La decodifica avviene tramite programmazione dinamica con memoization,
 * evitando calcoli ripetuti e riducendo la complessità temporale.
 * 
 * Poichè ogni codifica può essere decodificata in più modi, la complessità
 * dell'algoritmo è esponenziale, con una complessità teorica di O(2^n), dove n
 * è la lunghezza della stringa binaria in input.
 * Questo è vero per ogni carattere della stringa binaria, quindi la complessità
 * totale è O(n * 2^n).
 * 
 * La memoization riduce significativamente la complessità esponenziale
 * dell'algoritmo, avvicinandola in alcuni casi a una complessità molto
 * inferiore, che potrebbe potenzialmente avvicinarsi a O(n^2) in scenari
 * con condizioni ottimali.
 * Generalmente, però, rimane sotto O(n * 2^n).
 *
 * La complessità dell'algoritmo dipende anche dalla lunghezza massima dei
 * prefissi presenti nella mappa delle codifiche, calcolata in O(m). In questo
 * caso, il MAX_PREFIX_LENGTH è 4, il che limita il numero di iterazioni
 * necessarie per trovare le decodifiche, ottimizzando le prestazioni.
 *
 * La classe utilizza StringBuilder per costruire le stringhe decodificate,
 * poiché è più efficiente rispetto alla concatenazione di stringhe.
 *
 */
public class Esercizio2 {

    private static final Map<String, String> huffmanMap;
    private static final int MAX_PREFIX_LENGTH;

    static {

        huffmanMap = new HashMap<>();

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

        if (args.length == 0) {
            System.err.println("Usage: java Esercizio2 <input_file>");
            System.exit(1);
        }

        BufferedReader reader = new BufferedReader(new FileReader(args[0]));
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

    // Decodifica la stringa binaria usando la programmazione dinamica con
    // memoization
    static List<String> decodeUsingDP(String binaryInput) {
        Map<String, List<String>> memo = new HashMap<>(binaryInput.length());

        return decode(binaryInput, 0, memo);
    }

    // Parte ricorsiva per decodificare la stringa binaria
    private static List<String> decode(String binaryInput, int start, Map<String, List<String>> memo) {

        // Usa StringBuilder per costruire la chiave della memoization
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(start).append('-').append(binaryInput.length());
        String key = keyBuilder.toString();

        // Verifica se il risultato è già memorizzato
        if (memo.containsKey(key)) {
            return memo.get(key);
        }

        // Lista per contenere tutte le possibili decodifiche
        List<String> sequences = new ArrayList<>();

        // Lunghezza della stringa binaria
        int length = binaryInput.length();

        // Prova tutti i prefissi possibili che possono essere decodificati
        for (int i = start + 1; i <= length && i <= start + MAX_PREFIX_LENGTH; i++) {
            // Controlla se l'intervallo [start, i) è valido
            if (start < i && i <= length) {
                // Ottieni il prefisso usando StringBuilder
                StringBuilder prefixBuilder = new StringBuilder();
                prefixBuilder.append(binaryInput, start, i);
                String prefix = prefixBuilder.toString();

                // Verifica se il prefisso è presente nella mappa di Huffman
                if (huffmanMap.containsKey(prefix)) {
                    String letter = huffmanMap.get(prefix); // Lettera associata al prefisso
                    int nextStart = i; // Nuovo punto di inizio per la sottostringa rimanente

                    // Se la sottostringa rimanente è vuota, aggiungi la lettera alla lista
                    if (nextStart == length) {
                        sequences.add(letter);
                    } else {
                        // Decodifica ricorsiva della sottostringa rimanente
                        List<String> suffixDecodings = decode(binaryInput, nextStart, memo);

                        // Costruisci le stringhe decodificate usando StringBuilder
                        for (String decoding : suffixDecodings) {
                            StringBuilder sb = new StringBuilder();
                            sb.append(letter).append(decoding);
                            sequences.add(sb.toString());
                        }
                    }
                }
            }
        }

        // Memorizza la lista di decodifiche trovate per la sottostringa corrente
        memo.put(key, sequences);
        return sequences;
    }

    // Restituisce la lunghezza massima dei prefissi presenti nella huuffmanMap
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

    // stampa le sequenze decodificate
    static void printSequences(List<String> sequences) {
        StringBuilder sb = new StringBuilder();
        if (sequences.isEmpty()) {
            System.out.println(0);
        } else {
            sb.append(sequences.size()).append(',').append('\n');

            for (String seq : sequences) {
                sb.append(seq).append(',').append('\n');
            }
            System.out.println(sb.toString().trim());
        }
    }

}
