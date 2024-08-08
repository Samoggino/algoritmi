import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Ogni livello dell'albero è una riga della mappa, dove la chiave è il livello
 * nell'albero
 * e il valore è una lista di nodi che si trovano a quel livello.
 * 
 */
public class Esercizio1 {
    static class TreeNode {
        int value;
        List<TreeNode> children;
        TreeNode padre;

        TreeNode(int value) {
            this.value = value;
            this.children = new ArrayList<>();
            this.padre = null;
        }

        @Override
        public String toString() {
            return "TreeNode [value=" + value + ", children=" + children + "]";
        }
    }

    static TreeNode buildTreeFromPairs(String filename) {

        // ricorda che il file parent_child_pairs.txt è nella cartella es1, dovrà essere
        // eseguito da terminale, quindi il path sarà parent_child_pairs.txt
        Map<Integer, TreeNode> nodeMap = new HashMap<>();
        Set<Integer> childrenSet = new HashSet<>();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader("algoritmi-esercizi/es1/parent_child_pairs.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty())
                    continue;
                String[] parts = line.split(",");
                int parentValue = Integer.parseInt(parts[0].trim());
                int childValue = Integer.parseInt(parts[1].trim());

                TreeNode parentNode = nodeMap.computeIfAbsent(parentValue, k -> new TreeNode(k));
                TreeNode childNode = nodeMap.computeIfAbsent(childValue, k -> new TreeNode(k));

                parentNode.children.add(childNode);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Find root node (node that is not a child of any other node)
        for (int nodeValue : nodeMap.keySet()) {
            if (!childrenSet.contains(nodeValue)) {
                return nodeMap.get(nodeValue);
            }
        }

        return null;
    }

    static void printTree(TreeNode root) {
        if (root == null) {
            System.out.println("Root is null");
            return;
        }

        if (root.children.isEmpty()) {
            return;
        } else {
            System.out.print(root.value + " -> ");
        }
        for (TreeNode child : root.children) {
            System.out.print(child.value + " ");
        }
        System.out.println();
        for (TreeNode child : root.children) {
            printTree(child);
        }
    }

    static TreeNode buildTreeFromNestedList(String filename) {

        Map<Integer, TreeNode[]> nodeMap = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader("es1/nested2.txt"));
            return recNested(br.readLine(), nodeMap, null, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static TreeNode recNested(String line, Map<Integer, TreeNode[]> nodeMap, TreeNode root, int livello) {

        if (line == null || line.isEmpty()) {
            return null;
        }
        switch (line.charAt(0)) {
            case '[':
                // aumenta un livello
                livello++;

                // Trova il numero che segue l'apertura della parentesi
                int end = 1;
                while (end < line.length() && Character.isDigit(line.charAt(end))) {
                    end++;
                }

                // Crea un nuovo nodo con il numero trovato
                int value = Integer.parseInt(line.substring(1, end));
                TreeNode nodo = new TreeNode(value);

                // se il nodo padre è null, allora è la radice
                if (root == null) {
                    root = nodo;
                } else {
                    // altrimenti aggiungi il nodo alla lista dei figli del nodo padre
                    root.children.add(nodo);
                    // setta il padre del nodo
                    nodo.padre = root;
                }
                // aggiungi il nodo alla mappa
                nodeMap.put(livello, new TreeNode[] { nodo });
                root = nodo;

                // Esegui la ricorsione dal nodo appena creato
                recNested(line.substring(end), nodeMap, nodo, livello);
                break;

            case ']':
                // Diminuisci il livello e ritorna al padre
                livello--;
                if (root != null) {
                    root = root.padre;
                }
                // Esegui la ricorsione dal carattere successivo
                recNested(line.substring(1), nodeMap, root, livello);
                break;

            case ',':
                // Continua con la ricorsione ignorando la virgola
                recNested(line.substring(1), nodeMap, root, livello);
                break;

            default:
                // Ignora altri caratteri e continua la ricorsione
                recNested(line.substring(1), nodeMap, root, livello);
                break;
        }
        return root;
    }

    public static void main(String[] args) {

        // if (args.length != 2) {
        // System.err.println("Usage: java Esercizio1 <parent_child_pairs_file>
        // <nested_list_file>");
        // System.exit(1);
        // }

        String pairList = "parent_child_pairs.txt";
        String nestedList = "es1/nested2.txt";

        try {
            // TreeNode tree1 = buildTreeFromPairs(pairList);
            TreeNode tree2 = buildTreeFromNestedList(nestedList);
            printTree(tree2);
            // TreeNode nodo = tree2.children.get(0).children.get(0).children.get(1);
            // System.out.println("Nodo: " + nodo.value);
            // System.out.println("Padre: " + nodo.padre.value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
