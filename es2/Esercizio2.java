package es2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * n -> il numero di caratteri della stringa S
 * m -> il numero di lettere della mappa di Huffman
 * maxLetter -> la lunghezza massima di una codifica
 */
public class Esercizio2 {
    private static final Map<String, Character> huffmanMap = new TreeMap<>(new BinaryCodeComparator());
    private static final Map<Character, String> reversedHuffmanMap = new HashMap<>();
    private static final Map<Character, String> combinationMap = new HashMap<>();

    private static int maxLetter = 0;

    /**
     * Idea:
     * 
     * dato che ho la mappa di codifica, posso fare un ciclo su tutti i possibili
     * prefissi di lunghezza massima e vedere se sono presenti nella stringa
     * 
     * 0 + 001 corrisponde a AC, ma 0001 corrisponde a H, quindi devo fare un
     * controllo su tutti i possibili prefissi
     * in modo da ridurre il numero di iterazioni su tutte le entry della mappa
     */

    static { // O(m log m + m^2), contando che le lettere dell'alfabeto al massimo sono 26 è
             // O(26 log 26 + 26^2) ~ 800, quindi è un'operazione accettabile
        Character[] letters = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H' };
        String[] prefixes = { "0", "00", "001", "010", "0010", "0100", "0110", "0001" };

        for (int i = 0; i < letters.length; i++) { // O(m log m)
            huffmanMap.put(prefixes[i], letters[i]); // O(log m)
            reversedHuffmanMap.put(letters[i], prefixes[i]); // O(1)
        }

        for (Map.Entry<String, Character> entry1 : huffmanMap.entrySet()) { // O(m^2)
            for (Map.Entry<String, Character> entry2 : huffmanMap.entrySet()) {
                String combined = entry1.getKey() + entry2.getKey();
                if (huffmanMap.containsKey(combined)) { // O(log m)
                    combinationMap.put(huffmanMap.get(combined), "" + entry1.getValue() + entry2.getValue()); // O(1)
                }
            }
        }

        // stampa la mappa in modo carino
        // combinationMap.forEach((key, value) -> System.out.println(key + " -> " +
        // value));

    }

    // Comparator per ordinare le chiavi in base alla lunghezza
    private static class BinaryCodeComparator implements Comparator<String> {
        @Override
        public int compare(String code1, String code2) {
            // se sono uguali, ordina per valore
            if (code1.length() == code2.length()) {
                // dato che code1 e code2 sono numeri binari, posso fare il confronto come se
                // fossero numeri interi
                return code1.compareTo(code2);

            } else {
                return code1.length() - code2.length();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        String fileName = "es2/input.txt";
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String S = reader.readLine().trim();
        reader.close();

        // Calcola la lunghezza massima della codifica
        for (String key : huffmanMap.keySet()) {
            maxLetter = Math.max(maxLetter, key.length());
        }

        System.out.println("\n" + S + "\n");

        // Risolvi usando la programmazione dinamica
        List<String> sequences = decodeUsingDP(S);

        printDecodedSequence(sequences);
    }

    /**
     * Decodifica la stringa S usando la programmazione dinamica
     * 
     * @param S
     * @return lista di sequenze decodificate
     */
    private static List<String> decodeUsingDP(String S) {

        // Inizializza la lista delle sequenze decodificate
        List<String> sequences = new ArrayList<>();

        // Inizializza la tabella delle soluzioni parziali
        Map<Integer, List<String>> partialSolutions = new HashMap<>();
        partialSolutions.put(0, new ArrayList<>());
        partialSolutions.get(0).add("");

        // Calcola la lunghezza della stringa
        int n = S.length();

        // Calcola le sequenze decodificate
        // dato che ci sono lettere che combinano due lettere, mi conviene controllare
        // prima quelle grandi, ovvero quelle che stanno in fondo alla mappa

        Iterator<Map.Entry<String, Character>> iterator = ((TreeMap<String, Character>) huffmanMap)
                .descendingMap()
                .entrySet()
                .iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Character> entry = iterator.next();

            int count = 0;
            for (int i = 0; i < n; i++) {

                count++;
                // stampa il blocco di codice che sto analizzando
                // per evitare di andare out of bounds
                // verifico che il blocco di codice che sto analizzando non vada oltre la fine
                // della stringa
                if (i + entry.getKey().length() > n) {
                    break;
                } else {
                    // stampa il blocco di codice che sto analizzando
                    System.out.println("Count: " + count + " \t" + S.substring(i, entry.getKey().length() + i));
                }

            }
            System.out.println();
        }

        return sequences;

    }

    /**
     * Helper per stampare le decodifiche
     * 
     * @param sequences
     */
    private static void printDecodedSequence(List<String> sequences) {
        // Stampa il numero di decodifiche
        System.out.println(sequences.size());

        // Stampa in griglia le decodifiche
        int i = 0;
        for (String seq : sequences) {
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
