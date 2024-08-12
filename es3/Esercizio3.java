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
import java.util.Stack;

/**
 * Grafo orientato pesato
 * 
 */
public class Esercizio3 {

    static int num_matricola_seed = 10000;
    // static int num_matricola_seed = 970758;

    static class Node implements Comparable<Node> {
        public int id;
        public double attesa = 5;
        public List<Node> lista_di_adiacenza = new ArrayList<Node>();

        Node(int key) {
            this.id = key;
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

        public Double setRandomAttesa() {

            // caso 1: il valore di attesa è impostato a 5
            // caso 2: il valore di attesa è impostato a un valore casuale con seed 10000

            return this.attesa = new Random(num_matricola_seed).nextDouble();
        }

    }

    static class Graph {
        public List<Node> nodes;
        public Map<String, Double> tempiMap = new HashMap<>();

        Graph(String filename) {
            // leggi il file e crea il grafo

            /**
             * la prima riga del file contiene il numero di nodi
             * la seconda riga contiene il numero di archi
             * e le successive righe contengono nodo1 nodo2 attesa
             * 
             */

            try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

                String line;
                int i = 0;
                while ((line = br.readLine()) != null) {

                    if (i == 0) { // prendi numero di nodi
                        line = line.trim();
                        int numNodi = Integer.parseInt(line);
                        this.nodes = new ArrayList<Node>(numNodi);
                        for (int j = 0; j < numNodi; j++) {
                            this.insertNode(j);
                        }
                    } else if (i == 1) { // prendi numero di archi
                        line = line.trim();
                        int numArchi = Integer.parseInt(line);
                        tempiMap = new HashMap<>(numArchi);

                    } else if (i >= 2) { // crea il grafo
                        String[] parts = line.split(" ");
                        int nodo1 = Integer.parseInt(parts[0]);
                        int nodo2 = Integer.parseInt(parts[1]);
                        line = line.trim();
                        double tempo_di_percorrenza = Double.parseDouble(parts[2]);

                        this.insertEdge(nodo1, nodo2, tempo_di_percorrenza);
                    }

                    i++;

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        };

        void insertNode(int nodeID) {
            this.nodes.add(new Node(nodeID));

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

        public Double getArco(int id_nodo1, int id_nodo2) {
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
        PriorityQueue<Node> queue = new PriorityQueue<Node>();

        // inizializza gli array
        int[] t = new int[dim];
        double[] dist = new double[dim];
        boolean[] visitati = new boolean[dim];

        for (int i = 1; i < dim; i++) {
            t[i] = sorgente.id;
            dist[i] = Double.MAX_VALUE;
            visitati[i] = false;
        }

        t[0] = -1;
        dist[0] = 0;
        visitati[0] = true;
        queue.add(sorgente);

        while (!queue.isEmpty()) {
            Node u = queue.poll();
            int i = u.id;
            visitati[i] = false;

            for (Node adiacente : u.lista_di_adiacenza) {

                int j = adiacente.id;
                double tempo = graph.getArco(i, j);
                if (dist[j] > dist[i] + tempo) {
                    dist[j] = dist[i] + tempo;
                    t[j] = i;
                    if (!visitati[j]) {
                        queue.add(adiacente);
                        visitati[j] = true;
                    }
                }

            }

        }

        // stampa il risultato
        System.out.println(dist[dim - 1]);

        // stampa il percorso
        int i = dim - 1;
        Stack<Integer> percorso = new Stack<Integer>();
        while (i != -1) {
            percorso.push(i);
            i = t[i];
        }

        while (!percorso.isEmpty()) {
            System.out.print(percorso.pop() + " ");
        }

        System.out.println();

    }

    public static void main(String[] args) {
        Graph g = new Graph("es3/input.txt");

        // g.printGraph();
        camminiMinimi(g);
    }
}
