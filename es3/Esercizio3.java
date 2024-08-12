package es3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * Grafo orientato pesato
 * 
 */
public class Esercizio3 {

    // static int num_matricola_seed = 970758;
    static Double random = new Random(10000).nextDouble();

    static class Node implements Comparable<Node> {
        public final int id;
        // public final double attesa = 5;
        public final double attesa = random;
        public final List<Node> lista_di_adiacenza = new ArrayList<>();

        Node(int key) {
            this.id = key;
            // random.setSeed(10000); // da commentare in Caso 1
            // this.attesa = random.nextDouble(); // da commentare in Caso 1
        }

        @Override
        public int compareTo(Node o) {
            return Double.compare(this.attesa, o.attesa);
        }

        @Override
        public String toString() {
            return "Node [id=" + id + ", attesa=" + attesa + " nodi adiacenti=" + lista_di_adiacenza + "]";
        }

        public double getAttesa() {
            return this.attesa;
        }

    }

    static class Graph {
        private final List<Node> nodes;
        private Map<String, Double> tempiMap;

        Graph(String filename) {
            nodes = new ArrayList<>();

            buildGraph(filename);
        }

        /**
         * la prima riga del file contiene il numero di nodi
         * la seconda riga contiene il numero di archi
         * e le successive righe contengono nodo1 nodo2 attesa
         * 
         * @param filename
         */
        private void buildGraph(String filename) {
            try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
                int numNodi = Integer.parseInt(br.readLine().trim());
                int numArchi = Integer.parseInt(br.readLine().trim());
                tempiMap = new HashMap<String, Double>(numArchi);

                for (int j = 0; j < numNodi; j++) {
                    insertNode(j);
                }

                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(" ");
                    int nodo1 = Integer.parseInt(parts[0]);
                    int nodo2 = Integer.parseInt(parts[1]);
                    double tempo_di_percorrenza = Double.parseDouble(parts[2]);

                    insertEdge(nodo1, nodo2, tempo_di_percorrenza);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Node insertNode(int nodeID) {
            Node node = new Node(nodeID);
            this.nodes.add(node);
            return node;
            // Node node = new Node(nodeID);
            // node.setRandomAttesa();
            // this.nodes.add(node);
        }

        void insertEdge(int id1, int id2, double tempo_di_percorrenza) {
            Node nodo1 = this.nodes.get(id1);
            Node nodo2 = this.nodes.get(id2);

            // predo nodo1 e nodo2, faccio l'hash e lo metto in tempiMap
            tempiMap.put(id1 + "-" + id2, tempo_di_percorrenza);

            // aggiungo il nodo2 alla lista di adiacenza di nodo1
            nodo1.lista_di_adiacenza.add(nodo2);
        }

        void printGraph() {

            for (Node node : this.nodes) {
                System.out.println(node);
            }
        }

        // restituisce il tempo di attesa per il nodo i
        double attesa(int i, double tempo_corrente) {
            return tempo_corrente + this.nodes.get(i).getAttesa();
            // return tempo_corrente + this.nodes.get(i).getAttesa(this.num_matricola_seed);
        }

        public List<Node> getNodes() {
            return nodes;
        }

        public Double getArco(int id_nodo1, int id_nodo2) { // O(1)
            return tempiMap.get(id_nodo1 + "-" + id_nodo2) + this.nodes.get(id_nodo2).getAttesa();
        }

    }

    /**
     * La soluzione per tempo di attesa = 5.0 è:
     * 99.14
     * 0 3 2 4
     * 
     * @param graph
     */
    public static void camminiMinimi(Graph graph) {
        // nodo sorgente e destinazione
        int dim = graph.nodes.size();
        Node sorgente = graph.nodes.get(0);

        // inizializza la coda dei nodi da visitare
        PriorityQueue<Node> queue = new PriorityQueue<Node>(graph.nodes.size());

        // inizializza gli array
        int[] t = new int[dim];
        double[] dist = new double[dim];
        boolean[] visitati = new boolean[dim];

        for (int i = 1; i < dim; i++) { // O(n)
            dist[i] = Double.MAX_VALUE;
            visitati[i] = false;
        }

        t[0] = -1;
        dist[0] = 0;
        visitati[0] = true;
        queue.add(sorgente);

        while (!queue.isEmpty()) {
            Node u = queue.poll(); // O(log n)
            int i = u.id;
            visitati[i] = false;

            for (Node adiacente : u.lista_di_adiacenza) {

                int j = adiacente.id;
                double tempo = graph.getArco(i, j); // O(1)
                if (dist[j] > dist[i] + tempo) {
                    dist[j] = dist[i] + tempo;
                    t[j] = i;
                    if (!visitati[j]) {
                        queue.add(adiacente); // O(log n)
                        visitati[j] = true;
                    }
                }

            }

        } // O((n + m) * log n)

        // // stampa il risultato
        // System.out.println(dist[dim - 1]);

        // // stampa il percorso
        // int i = dim - 1;
        // Stack<Integer> percorso = new Stack<Integer>();
        // while (i != -1) {
        // percorso.push(i);
        // i = t[i];
        // }

        // while (!percorso.isEmpty()) {
        // System.out.print(percorso.pop() + " ");
        // }

        // System.out.println();

        // stampa il risultato
        System.out.println(dist[dim - 1]);
        printCamminiMinimi(t, dim - 1);
        System.out.println();
    }

    static void printCamminiMinimi(int[] t, int i) {
        if (t[i] == -1) {
            System.out.print(i + " ");
            return;
        }
        printCamminiMinimi(t, t[i]);
        System.out.print(i + " ");
    }

    public static void main(String[] args) {
        Graph g = new Graph("es3/input.txt");

        // g.printGraph();
        camminiMinimi(g);
    }
}
