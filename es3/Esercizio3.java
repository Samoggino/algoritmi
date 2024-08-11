package es3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Grafo orientato pesato
 * 
 */
public class Esercizio3 {

    static int num_matricola_seed = 10000;

    static class Node implements Comparable<Node> {
        public int id;
        public double attesa = 5;

        Node(int key) {
            this.id = key;
        }

        @Override
        public int compareTo(Node arg0) {
            return Integer.compare(this.id, arg0.id);
        }

        @Override
        public String toString() {
            return "Node [id=" + id + ", attesa=" + attesa + "]";
        }

        public double getAttesa() {
            return this.attesa;
        }

        public Node setRandomAttesa() {

            // caso 1: il valore di attesa è impostato a 5
            // caso 2: il valore di attesa è impostato a un valore casuale con seed 10000

            Random rand = new Random(num_matricola_seed);
            this.attesa = rand.nextDouble();
            return this;
        }

    }

    static class Edge {
        public Node incrocio1;
        public Node incrocio2;
        public double tempo_di_percorrenza;

        Edge(Node incrocio1, Node incrocio2, double tempo_di_percorrenza) {
            this.incrocio1 = incrocio1;
            this.incrocio2 = incrocio2;
            this.tempo_di_percorrenza = tempo_di_percorrenza;
        }
    }

    static class Graph {
        public List<Node> nodes;
        public List<Edge> edges;

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

                    if (i == 0) {
                        // trimma e ignora i tab
                        line = line.trim();
                        int numNodi = Integer.parseInt(line);
                        this.nodes = new ArrayList<Node>(numNodi);
                        for (int j = 0; j < numNodi; j++) {
                            this.insertNode(j);
                        }
                    } else if (i == 1) {
                        line = line.trim();
                        int numArchi = Integer.parseInt(line);
                        this.edges = new ArrayList<Edge>(numArchi);
                    } else if (i >= 2) {
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
            // this.nodes.add(new Node(nodeID).setRandomAttesa());
        }

        void insertEdge(int id1, int id2, double tempo_di_percorrenza) {
            Node nodo1 = this.nodes.get(id1);
            Node nodo2 = this.nodes.get(id2);
            this.edges.add(new Edge(nodo1, nodo2, tempo_di_percorrenza));
        }

        void printGraph() {

            for (Node n : this.nodes) {
                System.out.println(n);
            }

            for (Edge e : this.edges) {
                System.out.println(e.incrocio1.id + " -- " + e.tempo_di_percorrenza + " --> " + e.incrocio2.id);
            }
        }

        List<Node> getNodiDUscita(Node nodo) {
            List<Node> nodiUscita = new ArrayList<Node>();
            for (Edge e : this.edges) {
                if (e.incrocio1 == nodo) {
                    nodiUscita.add(e.incrocio2);
                }
            }
            return nodiUscita;
        }

        // restituisce il tempo di attesa per il nodo i
        double attesa(int i, double tempo_corrente) {
            return tempo_corrente + this.nodes.get(i).getAttesa();
            // return tempo_corrente + this.nodes.get(i).getAttesa(this.num_matricola_seed);
        }

        void camminiMinimi() {
            // nodo sorgente e destinazione
            Node sorgente = this.nodes.get(0);
            Node destinazione = this.nodes.get(this.nodes.size() - 1);

            // se dal nodo sorgente non si può raggiungere la destinazione, il programma
            // stampa "NON RAGGIUNGIBILE"
            // altrimenti stampa il tempo minimo per raggiungere la destinazione
            // e il percorso minimo

            // inizializza i tempi di arrivo a infinito
            double[] tempi = new double[this.nodes.size()];
            for (int i = 0; i < this.nodes.size(); i++) {
                tempi[i] = Double.POSITIVE_INFINITY;
            }

            // il tempo di arrivo al nodo sorgente è il tempo di attesa
            tempi[sorgente.id] = this.attesa(sorgente.id, 0);

        }

    }

    public static void main(String[] args) {
        Graph g = new Graph("es3/input.txt");

        g.printGraph();
        g.camminiMinimi();
    }
}
