import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

/**
 * Ogni livello dell'albero è una riga della mappa, dove la chiave è il livello
 * nell'albero
 * e il valore è una lista di nodi che si trovano a quel livello.
 * 
 */
public class Esercizio1 {
    static class TreeNode implements Comparable<TreeNode> {
        int value;
        Set<TreeNode> children;
        TreeNode padre;

        TreeNode(int value) {
            this.value = value;
            this.children = new TreeSet<>();
            this.padre = null;
        }

        @Override
        public String toString() {
            return "TreeNode [value=" + value + ", children=" + children + "]";
        }

        @Override
        public int compareTo(TreeNode other) {
            return Integer.compare(this.value, other.value);
        }
    }

    static TreeNode buildTreeFromPairs(String filename) {

        // ricorda che il file parent_child_pairs.txt è nella cartella es1, dovrà essere
        // eseguito da terminale, quindi il path sarà parent_child_pairs.txt
        Map<Integer, TreeNode> nodeMap = new HashMap<>();
        Set<Integer> childrenSet = new TreeSet<>();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader("es1/parent_child_pairs.txt"));
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
                childNode.padre = parentNode;
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

            String line = br.readLine();

            br.close();
            return recNested(line, nodeMap, null, 0);
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

                // FIXME: Trova un modo più efficiente per trovare il numero
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

    static boolean areTreesEqual(TreeNode root1, TreeNode root2) {

        // Sort and compare children by converting them into sets
        TreeSet<TreeNode> childSet1 = new TreeSet<>();
        for (TreeNode child : root1.children) {
            childSet1.add(child);
        }

        TreeSet<TreeNode> childSet2 = new TreeSet<>();
        for (TreeNode child : root2.children) {
            childSet2.add(child);
        }

        return recAreEquals(childSet1, childSet2);
    }

    /**
     * Ricorsivamente controlla se due set di interi sono uguali e poi lo fa per
     * tutti i figli
     * 
     * @param childSet1
     * @param childSet2
     * @return
     */
    static boolean recAreEquals(TreeSet<TreeNode> childSet1, TreeSet<TreeNode> childSet2) {

        if (childSet1.size() != childSet2.size()) {
            return false;
        } else if (childSet1.size() < 1 || childSet2.size() < 1) {
            return true;
        }

        try {

            if (childSet1.equals(childSet2)) {
                if (areTreesEqual(childSet1.first(), childSet2.first())) {
                    return true;
                }
            }
        } catch (NullPointerException e) {
            System.out.println("NullPointer: " + e.getMessage());
            return false;
        } catch (NoSuchElementException e) {
            System.out.println("Set is empty: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public static void main(String[] args) {

        // if (args.length != 2) {
        // System.err.println("Usage: java Esercizio1 <parent_child_pairs_file>
        // <nested_list_file>");
        // System.exit(1);
        // }

        String pairList = "es1/parent_child_pairs.txt";
        String nestedList = "es1/nested2.txt";

        TreeNode tree1 = buildTreeFromPairs(pairList);
        TreeNode tree2 = buildTreeFromNestedList(nestedList);
        printTree(tree1);
        System.out.println();
        System.out.println();
        printTree(tree2);

        if (areTreesEqual(tree1, tree2)) {
            System.out.println("Alberi uguali");
        } else {
            System.out.println("Alberi diversi");
        }

    }

}
