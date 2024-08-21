package es2;

import java.io.*;
import java.util.*;

public class Esercizio2 {
    private static final Map<String, String> huffmanMap = new HashMap<>();

    static {
        huffmanMap.put("0", "A");
        huffmanMap.put("00", "B");
        huffmanMap.put("001", "C");
        huffmanMap.put("010", "D");
        huffmanMap.put("0010", "E");
        huffmanMap.put("0100", "F");
        huffmanMap.put("0110", "G");
        huffmanMap.put("0001", "H");
    }

    public static void main(String[] args) throws IOException {
        String fileName = "es2/input.txt";
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String S = reader.readLine().trim();
        reader.close();

        System.out.println(S);
        // Costruisci il DAG
        Map<Integer, List<Integer>> graph = buildDAG(S);

        // Trova tutte le decodifiche usando DFS sul DAG
        List<String> sequences = new ArrayList<>();
        dfs(graph, 0, S, new StringBuilder(), sequences);

        // Stampa il numero di decodifiche
        System.out.println(sequences.size());

        // Stampa tutte le decodifiche
        for (String seq : sequences) {
            System.out.println(seq);
        }
    }

    // Costruisce il DAG
    private static Map<Integer, List<Integer>> buildDAG(String S) {
        int n = S.length();
        Map<Integer, List<Integer>> graph = new HashMap<>();

        for (int i = 0; i <= n; i++) {
            graph.put(i, new ArrayList<>());
        }

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j <= n; j++) {
                String prefix = S.substring(i, j);
                if (huffmanMap.containsKey(prefix)) {
                    graph.get(i).add(j);
                }
            }
        }

        return graph;
    }

    // DFS per trovare tutte le decodifiche nel DAG
    private static void dfs(Map<Integer, List<Integer>> graph, int node, String S, StringBuilder currentPath,
            List<String> sequences) {
        int n = S.length();
        if (node == n) {
            sequences.add(currentPath.toString());
            return;
        }

        for (int nextNode : graph.get(node)) {
            String decodedChar = huffmanMap.get(S.substring(node, nextNode));
            currentPath.append(decodedChar);
            dfs(graph, nextNode, S, currentPath, sequences);
            currentPath.setLength(currentPath.length() - decodedChar.length()); // Backtrack
        }
    }
}
