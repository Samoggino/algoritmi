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
 * 
 * Considerando n nodi e m archi, la complessità dell'algoritmo è la seguente:
 * 
 * Le operazioni di caricamento del grafo sono in O(n + m)
 * 
 * Le operazioni di inizializzazione degli array sono in O(n)
 * Le operazioni sulla coda sono in O(log n) perchè la coda è una PriorityQueue
 * ed è implementata con un heap binario
 * 
 * La ricerca del cammino minimo è in O((n + m) * log n)
 * La stampa del cammino minimo è in O(n)
 * 
 * La complessità totale è O((n + m) * log n)
 * 
 */
public class Esercizio3 {

    static int num_matricola_seed = 970758;
    static Double random = new Random(10000).nextDouble();
    // static Random random2 = new Random(10000); // attese diverse per tutti i nodi

    static class Node implements Comparable<Node> {
        public final int id;
        // public final double attesa = 5;
        public final double attesa = random; // da commentare in Caso 1
        public final List<Node> lista_di_adiacenza = new ArrayList<>();

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
    }

    static class Graph {
        private final List<Node> nodes; // implemetata con ArrayList e quindi accesso in O(1)
        private Map<String, Double> archiMap; // implementata con HashMap e quindi accesso in O(1)

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
                archiMap = new HashMap<String, Double>(numArchi);

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
        }

        void insertEdge(int id1, int id2, double tempo_di_percorrenza) {
            Node nodo1 = this.nodes.get(id1);
            Node nodo2 = this.nodes.get(id2);

            // predo nodo1 e nodo2, faccio l'hash e lo metto in tempiMap
            archiMap.put(id1 + "-" + id2, tempo_di_percorrenza);

            // aggiungo il nodo2 alla lista di adiacenza di nodo1
            nodo1.lista_di_adiacenza.add(nodo2);
        }

        void printGraph() {
            for (Node node : this.nodes) {
                System.out.println(node);
            }
        }

        // restituisce il tempo di attesa per il nodo i
        public double attesa(int i, double tempo_corrente) { // accesso in ArrayList O(1)
            return tempo_corrente + this.nodes.get(i).attesa;
        }

        public Double getArco(int id_nodo1, int id_nodo2) { // accesso alla mappa O(1)
            return archiMap.get(id_nodo1 + "-" + id_nodo2);
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

        /**
         * Inizializzazione delle strutture dati
         * 
         * La PriorityQueue è implementata con un min-heap binario e quindi l'accesso è
         * in O(log n), mentre la sua inizializzazione è in O(1) poichè viene
         * inizializzata con la dimensione della lista di nodi del grafo.
         * 
         * Gli array t, dist e visitati sono inizializzati in O(n)
         * 
         * L'aggiunta del nodo sorgente alla coda è in O(log n)
         * 
         * La complessità totale dell'inizializzazione è in O(n)
         */

        int[] percorso = new int[dim];
        double[] dist = new double[dim];
        boolean[] visitati = new boolean[dim];
        PriorityQueue<Node> queue = new PriorityQueue<Node>(dim);

        for (int i = 1; i < dim; i++) {
            dist[i] = Double.MAX_VALUE;
            visitati[i] = false;
        }

        // se non esiste un percorso dalla sorgente alla destinazione
        // stampa -1
        percorso[0] = -1;
        dist[0] = 0;
        visitati[0] = true;
        queue.add(sorgente);

        /**
         * La ricerca del cammino minimo è in O((n + m) * log n),
         * poichè il ciclo while scorre tutti i nodi del grafo e per ogni nodo
         * scorre tutti i suoi archi.
         * Ad ogni iterazione del ciclo while, viene estratto un nodo dalla coda
         * e questo è in O(log n).
         * 
         * Quindi itero n + m volte e per ogni iterazione faccio un'operazione in
         * O(log n), quindi la complessità totale è in O((n + m) * log n)
         * 
         */
        while (!queue.isEmpty()) {
            Node nodo = queue.poll();
            int partenza = nodo.id;

            for (Node adiacente : nodo.lista_di_adiacenza) {

                int destinazione = adiacente.id;
                double tempo_corrente = dist[partenza] + graph.getArco(partenza, destinazione);
                double tempo_effettivo = graph.attesa(destinazione, tempo_corrente);

                if (dist[destinazione] > tempo_effettivo) {
                    dist[destinazione] = tempo_effettivo;
                    percorso[destinazione] = partenza;
                    if (!visitati[destinazione]) {
                        queue.add(adiacente);
                        visitati[destinazione] = true;
                    }
                }
            }
        }

        if (dist[dim - 1] == Double.MAX_VALUE) {
            System.out.println("Non esiste un percorso dalla sorgente alla destinazione");
            return;
        } else {
            System.out.println(dist[dim - 1]);
            printCamminiMinimi(percorso, dim - 1);
            System.out.println();
        }
    }

    /**
     * La stampa del cammino minimo è in O(n),
     * poichè stampa il cammino minimo dalla destinazione alla sorgente
     */
    static void printCamminiMinimi(int[] t, int i) {
        if (t[i] == -1) {
            System.out.print(i + " ");
            return;
        }
        printCamminiMinimi(t, t[i]);
        System.out.print(i + " ");
    }

    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("Inserire il nome del file di input");
            return;
        }
        // "es3/input.txt"
        Graph g = new Graph(args[0]);

        // g.printGraph();
        camminiMinimi(g);
    }
}
