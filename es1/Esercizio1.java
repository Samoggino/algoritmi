import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

/**
 * - k numero di caratteri della stringa
 * - n numero di nodi dell'albero
 * - m numero che ha più cifre
 * 
 * 
 */
public class Esercizio1 {
    static class TreeNode implements Comparable<TreeNode> {
        int value;
        Set<TreeNode> children;

        TreeNode(int value) { // O(1)
            this.value = value;
            this.children = new HashSet<>(); // O(1)
        }

        @Override
        public String toString() { // O(n) poichè la root.children potrebbe contenere tutti i nodi dell'albero
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
        Map<Integer, TreeNode> nodeMap = new HashMap<>(); // tutte le operazioni su map sono O(1) considerando il basso
                                                          // numero di nodi
        Set<Integer> parentSet = new HashSet<>(); // tutte le operazioni su hashset sono O(1)
        Set<Integer> childSet = new HashSet<>(); // tutte le operazioni su hashset sono O(1)

        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader("es1/parent_child_pairs.txt"));
            String line;
            while ((line = br.readLine()) != null) { // le righe sono n - 1, perchè la root non ha una riga in cui sta a
                                                     // destra della virgola
                line = line.trim();
                if (line.isEmpty())
                    continue;
                String[] parts = line.split(",");
                int parentValue = Integer.parseInt(parts[0].trim());
                int childValue = Integer.parseInt(parts[1].trim());

                TreeNode parentNode = nodeMap.computeIfAbsent(parentValue, k -> new TreeNode(k)); // O(1)
                TreeNode childNode = nodeMap.computeIfAbsent(childValue, k -> new TreeNode(k)); // O(1)

                parentNode.children.add(childNode); // O(1)
                parentSet.add(parentValue); // O(1)
                childSet.add(childValue); // O(1)
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        parentSet.removeAll(childSet); // O(n) dove n è il numero di nodi
        if (parentSet.size() != 1) {
            throw new IllegalStateException("L'albero dovrebbe avere esattamente una radice.");
        }

        return nodeMap.get(parentSet.iterator().next()); // O(1)
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

        try {
            BufferedReader br = new BufferedReader(new FileReader("es1/nested_list.txt"));
            String line = br.readLine().trim();
            br.close();

            Stack<TreeNode> stack = new Stack<>();
            int i = 0;
            TreeNode root = null;

            while (i < line.length()) { // O(k) dove k è il numero di caratteri della stringa

                switch (line.charAt(i)) {
                    case '[':
                        // Trova il numero che segue l'apertura della parentesi
                        int start = i + 1;

                        // costo O(m) dove m è il numero che ha più cifre
                        while (i + 1 < line.length() && Character.isDigit(line.charAt(i + 1))) {
                            i++;
                        }
                        int num = Integer.parseInt(line.substring(start, i + 1));
                        TreeNode newNode = new TreeNode(num);

                        // Se lo stack non è vuoto, aggiungi il nuovo nodo come figlio del nodo in cima
                        // allo stack
                        if (!stack.isEmpty()) { // O(1)
                            stack.peek().children.add(newNode); // O(1)
                        }

                        // Se il root non è stato ancora impostato, impostalo al primo nodo trovato
                        if (root == null) {
                            root = newNode;
                        }

                        // Aggiungi il nuovo nodo allo stack
                        stack.push(newNode); // O(1)
                        break;

                    case ']':
                        // Rimuovi il nodo in cima allo stack
                        stack.pop(); // O(1)
                        break;

                    default:
                        break;
                }

                // se il carattere successivo è una virgola o uno spazio, fai i+2
                if (i + 1 < line.length() && (line.charAt(i + 1) == ',')) {
                    // questo caso è quando ho una virgola e poi uno spazio, quindi devo fare i+3,
                    // ovvero l'incremento base + 2
                    i += 3;
                } else {
                    i++;
                }
            }
            return root;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Ricorsione per costruire l'albero da una stringa nested
     * 
     * Il costo computazionale di questo metodo è O(n) dove n è il numero di nodi,
     * poichè i caratteri della stringa vengono letti solo se sono numeri, grazie al
     * salto in avanti quando i caratteri sono virgola o spazio.
     * 
     * @param root1
     * @param root2
     * @return sono uguali o no
     */
    public static boolean areTreesEqual(TreeNode root1, TreeNode root2) {

        Map<TreeNode, TreeNode> visited = new HashMap<>();

        if (root1 == null || root2 == null) {
            return root1 == root2;
        }

        if (root1.compareTo(root2) != 0) {
            return false;
        }

        return recAreEquals(root1, root2, visited);
    }

    /**
     * Questo metodo viene richiamato ricorsivamente n - 1, poichè il numero di nodi
     * è
     * n, e il costo di ogni chiamata è O(log n) poichè si fa un'operazione di
     * confronto tra due TreeSet.
     * 
     * @param root1
     * @param root2
     * @return
     */
    private static boolean recAreEquals(TreeNode root1, TreeNode root2, Map<TreeNode, TreeNode> visited) { // eseguito n
                                                                                                           // volte

        // prova ad usare una mappa per segnare se hai già verificato i nodi, in modo da
        // eseguire la ricorsione solo una volta per ogni nodo
        TreeSet<TreeNode> childSet1 = new TreeSet<>(root1.children); // log n
        TreeSet<TreeNode> childSet2 = new TreeSet<>(root2.children); // log n

        if (childSet1.size() != childSet2.size()) { // O(1)
            System.out.println("Different number of children");
            return false;
        }

        if (root1.value != root2.value) { // O(1)
            System.out.println("Different value");
            return false;
        }

        Iterator<TreeNode> iter1 = childSet1.iterator(); // O(1)
        Iterator<TreeNode> iter2 = childSet2.iterator(); // O(1)

        // il while cicla su tutti i figli di root1 e root2, quindi il costo medio è
        // O(n) * O(log n) = O(n log n)
        while (iter1.hasNext() && iter2.hasNext()) {
            TreeNode child1 = iter1.next(); // O(log n) nel caso pessimo, O(1) nel caso medio
            TreeNode child2 = iter2.next(); // O(log n) nel caso pessimo, O(1) nel caso medio

            if (visited.containsKey(child1) || visited.containsKey(child2)) { // O(1)
                return false;
            } else {
                visited.put(child1, child2); // O(1)
                visited.put(child2, child1); // O(1)
            }

            System.out.println("Comparing " + child1.value + " and " + child2.value);

            if (!recAreEquals(child1, child2, visited)) { // O(log n)
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Usage: java Esercizio1 <pairList> <nestedList>");
            return;
        }

        TreeNode tree1 = buildTreeFromPairs(args[0]); // O(n) dove n è il numero di nodi
        TreeNode tree2 = buildTreeFromNestedList(args[1]); // O(k) dove k è il numero di caratteri della stringa

        // printTree(tree1);
        // System.out.println();
        // printTree(tree2);

        if (areTreesEqual(tree1, tree2)) { // O(n log n)
            System.out.println("Alberi uguali");
        } else {
            System.out.println("Alberi diversi");
        }

    }

}
