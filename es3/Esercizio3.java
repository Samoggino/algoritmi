package es3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Grafo orientato pesato
 * 
 */
public class Esercizio3 {

    static class Node implements Comparable<Node> {
        public int id;
        public double attesa;

        Node(int key) {
            this.id = key;
        }

        @Override
        public int compareTo(Node arg0) {
            return Integer.compare(this.id, arg0.id);
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
                    }

                    if (i == 1) {
                        line = line.trim();
                        int numArchi = Integer.parseInt(line);
                        this.edges = new ArrayList<Edge>(numArchi);
                    }

                    if (i >= 2) {
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
        }

        void insertEdge(int id1, int id2, double tempo_di_percorrenza) {
            Node nodo1 = this.nodes.get(id1);
            Node nodo2 = this.nodes.get(id2);
            this.edges.add(new Edge(nodo1, nodo2, tempo_di_percorrenza));
        }

        void printGraph() {

            for (Edge e : this.edges) {
                System.out.println(e.incrocio1.id + " -- "   + e.tempo_di_percorrenza + " --> " + e.incrocio2.id);
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
        double attesa(int i, double t) {
            return 0;
        }

    }

    public static void main(String[] args) {
        Graph g = new Graph("es3/input.txt");

        g.printGraph();
    }
}
