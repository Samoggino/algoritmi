
/****
 * NOME:        Samoggia Simone 
 * MATRICOLA:   0000970758
 * EMAIL:       simone.samoggia2@studio.unibo.it
 * 
 * 
 * Considerando n nodi e m archi, la complessità dell'algoritmo è la seguente:
 * 
 * Le operazioni di caricamento del grafo sono in O(n + m).
 * Le operazioni di inizializzazione degli array sono in O(n).
 * Le operazioni sulla coda sono in O(log n) poiché la coda è una PriorityQueue
 * che è implementata con un heap binario.
 * 
 * La ricerca del cammino minimo è basato sull'algoritmo di Dijkstra
 * e ha costo O((n + m) * log n).
 * La stampa del cammino minimo è in O(n).
 * 
 * La complessità temporale totale è quindi O((n + m) * log n).
 * 
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

public class Esercizio3 {
    static Random random = new Random(970758);

    // usa numeri random come attesa per ogni nodo con seed numero di matricola
    // static boolean useRandom = true;

    // usa 5.0 come attesa per ogni nodo
    static boolean useRandom = false;

    static class Node {
        public final int id;
        public double attesa = 5.0;
        public final List<Node> lista_di_adiacenza = new ArrayList<>();

        Node(int key) {
            this.id = key;
        }

        @Override
        public String toString() {

            StringBuilder sb = new StringBuilder();
            sb.append("Node [id=").append(id).append(", attesa=").append(attesa).append(" nodi adiacenti=");
            for (Node node : lista_di_adiacenza) {
                sb.append(node.id).append(" ");
            }

            return sb.append("]").toString();
        }
    }

    // costruisce il grafo, i primi due valori sono il numero di nodi e di archi
    private static Graph buildGraph(String filename) {

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            int numNodes = Integer.parseInt(br.readLine().trim());
            int numEdges = Integer.parseInt(br.readLine().trim());

            Graph graph = new Graph(numNodes, numEdges);

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                int nodo1 = Integer.parseInt(parts[0]);
                int nodo2 = Integer.parseInt(parts[1]);
                double tempo_di_percorrenza = Double.parseDouble(parts[2]);

                graph.insertEdge(nodo1, nodo2, tempo_di_percorrenza);
            }
            return graph;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    static class Graph {

        private final Node[] nodes; // Array accesso in O(1)
        private final Map<String, Double> edgeMap; // implementata con HashMap e quindi accesso in O(1)

        // costruisce un grafo con numNodes nodi e numEdges archi
        Graph(int numNodes, int numEdges) {
            this.nodes = new Node[numNodes];
            this.edgeMap = new HashMap<String, Double>(numEdges);

            for (int j = 0; j < numNodes; j++) {
                insertNode(j);
            }
        }

        // inserisce un nodo nel grafo
        private void insertNode(int nodeID) {
            this.nodes[nodeID] = new Node(nodeID);
        }

        // inserisce un arco tra due nodi
        void insertEdge(int id1, int id2, double tempo_di_percorrenza) {
            Node nodo1 = this.nodes[id1];
            Node nodo2 = this.nodes[id2];

            // dato che gli id sono univoci, posso usarne la concatenazione come chiave
            edgeMap.put(id1 + "-" + id2, tempo_di_percorrenza);

            // aggiungo il nodo2 alla lista di adiacenza di nodo1
            nodo1.lista_di_adiacenza.add(nodo2);
        }

        // stampa il grafo
        void printGraph() { // O(n)
            for (Node node : this.nodes) {
                System.out.println(node);
            }
        }

        // restituisce il tempo di attesa per il nodo i al tempo t
        double attesa(int i, double tempo_t) {

            if (useRandom) {
                double attesa_nodo = random.nextDouble() * 10;
                this.nodes[i].attesa = attesa_nodo;
            }

            return tempo_t + this.nodes[i].attesa;
        }

        // restituisce il tempo di percorrenza tra i nodi nodo1 e nodo2
        double getEdge(int id_nodo1, int id_nodo2) { // accesso alla mappa O(1)
            return edgeMap.get(id_nodo1 + "-" + id_nodo2);
        }

    }

    // ricerca del cammino minimo
    public static void camminiMinimi(Graph graph) {
        int dim = graph.nodes.length;
        Node sorgente = graph.nodes[0];

        int[] percorso = new int[dim];
        double[] tempo = new double[dim];
        boolean[] visitati = new boolean[dim];
        PriorityQueue<Node> queue = new PriorityQueue<Node>(
                dim,
                (n1, n2) -> Double.compare(tempo[n1.id], tempo[n2.id]));

        for (int i = 1; i < dim; i++) {
            tempo[i] = Double.MAX_VALUE;
            visitati[i] = false;
        }

        // se non esiste un percorso dalla sorgente alla destinazione
        percorso[0] = -1;

        // nodo 0, istante 0
        tempo[0] = 0;
        visitati[0] = true;
        queue.add(sorgente);

        while (!queue.isEmpty()) {
            Node nodo = queue.poll();
            int partenza = nodo.id;

            for (Node adiacente : nodo.lista_di_adiacenza) {

                int destinazione = adiacente.id;
                double tempo_t = tempo[partenza] + graph.getEdge(partenza, destinazione);
                double tempo_effettivo = graph.attesa(destinazione, tempo_t);

                if (tempo[destinazione] > tempo_effettivo) {
                    tempo[destinazione] = tempo_effettivo;
                    percorso[destinazione] = partenza;
                    if (!visitati[destinazione]) {
                        queue.add(adiacente);
                        visitati[destinazione] = true;
                    }
                }
            }
        }

        if (tempo[dim - 1] == Double.MAX_VALUE) {
            System.out.println("Non raggiungibile");
            return;
        } else {
            printCamminiMinimi(dim, percorso, tempo);
        }
    }

    // stampa il tempo minimo e lancia il metodo ricorsivo per stampare il percorso
    static void printCamminiMinimi(int dim, int[] percorso, double[] tempo) {
        System.out.println(tempo[dim - 1]);
        recPrint(percorso, dim - 1);
        System.out.println();
    }

    // stampa il percorso minimo
    private static void recPrint(int[] t, int i) {

        StringBuilder sb = new StringBuilder();
        sb.append(i).append(" ");

        if (t[i] == -1) {
            System.out.print(sb.toString());
            return;
        }
        recPrint(t, t[i]);
        System.out.print(sb.toString());
    }

    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("Inserire il nome del file di input");
            return;
        }

        camminiMinimi(buildGraph(args[0]));
    }
}