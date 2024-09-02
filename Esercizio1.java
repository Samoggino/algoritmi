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
 * Il metodo `buildTreeFromPairs` costruisce un albero in O(n) dove n è il
 * numero di figli.
 *
 * Il metodo `buildTreeFromNestedList` costruisce un albero a partire da una
 * stringa che rappresenta una lista annidata. Il costo di questo metodo è O(m),
 * dove m è il numero di caratteri della stringa. La costruzione dell'albero
 * avviene attraverso una lettura carattere per carattere e l'uso di uno stack.
 *
 * Il metodo `areTreesEqual` dà il via alla ricorsione per verificare
 * l'uguaglianza, che avviene nel metodo `recAreEquals`.
 * Il metodo `recAreEquals` costa O(log n) per ogni nodo, poichè ordina i figli
 * dei nodi in TreeSet, che ha complessità logaritmica per l'inserimento e
 * l'accesso.
 * Quindi la complessità totale del metodo è O(n * k log k) nel caso peggiore,
 * dove ho tanti figli per ogni nodo, e O(n) quando l'albero è molto semplice
 * e non ha molti figli per nodo.
 * Nel caso specifico, k = 4, quindi la complessità è nell'ordine di O(n).
 * 
 * Il metodo `printTree` stampa ogni nodo dell'albero, a partire dal nodo
 * radice, e ha costo O(n).
 * Utilizza `StringBuilder` per ottimizzare la creazione della stringa da
 * stampare.
 */
public class Esercizio1 {
    static class TreeNode implements Comparable<TreeNode> {
        int value;
        Set<TreeNode> children;

        TreeNode(int value) {
            this.value = value;
            this.children = new HashSet<>();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("TreeNode [value=").append(value).append(", children=");
            for (TreeNode child : children) {
                sb.append(child.value).append(" ");
            }
            return sb.append("]").toString();
        }

        @Override
        public int compareTo(TreeNode other) {
            return Integer.compare(this.value, other.value);
        }
    }

    // costruisce il pair tree
    static TreeNode buildTreeFromPairs(String filename) {

        Map<Integer, TreeNode> nodeMap = new HashMap<>();
        Set<Integer> parentSet = new HashSet<>();
        Set<Integer> childSet = new HashSet<>();

        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(filename));
            String line;
            // le righe sono n - 1, perchè la root non ha una riga in cui sta a
            // destra della virgola
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty())
                    continue;
                String[] parts = line.split(",");
                int parentValue = Integer.parseInt(parts[0].trim());
                int childValue = Integer.parseInt(parts[1].trim());

                // Se il nodo non esiste nella mappa, crealo
                TreeNode parentNode = nodeMap.computeIfAbsent(parentValue, k -> new TreeNode(k)); // O(1)
                TreeNode childNode = nodeMap.computeIfAbsent(childValue, k -> new TreeNode(k)); // O(1)

                parentNode.children.add(childNode);
                parentSet.add(parentValue);
                childSet.add(childValue);
            }
        } catch (FileNotFoundException e) {
            // problema con il path
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return nodeMap.get(getRoot(parentSet, childSet)); // O(1)
    }

    private static int getRoot(Set<Integer> parentSet, Set<Integer> childSet) {
        parentSet.removeAll(childSet); // O(n)
        if (parentSet.size() != 1) {
            throw new IllegalStateException("L'albero dovrebbe avere esattamente una radice.");
        }

        return parentSet.iterator().next();

    }

    // costruisce il nested tree
    static TreeNode buildTreeFromNestedList(String filename) {

        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line = br.readLine().trim();
            br.close();

            Stack<TreeNode> stack = new Stack<>();
            int i = 0;
            TreeNode root = null;

            while (i < line.length()) { // O(m) dove m è il numero di caratteri della stringa

                switch (line.charAt(i)) {
                    case '[':

                        StringBuilder sb = new StringBuilder();

                        while (i + 1 < line.length() && Character.isDigit(line.charAt(i + 1))) {
                            i++;
                            sb.append(line.charAt(i));
                        }

                        int num = Integer.parseInt(sb.toString());
                        TreeNode newNode = new TreeNode(num);

                        // Se lo stack non è vuoto, aggiungi il nuovo nodo come figlio del nodo in cima
                        // allo stack
                        if (!stack.isEmpty()) { // O(1)
                            stack.peek().children.add(newNode); // O(1)
                        }

                        // Se la root non è stato ancora impostata, impostala al primo nodo trovato
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

                i++;

            }
            return root;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    // lancia la ricorsione
    public static boolean areTreesEqual(TreeNode root1, TreeNode root2) {

        // Se entrambi gli alberi sono nulli, sono uguali
        if (root1 == null || root2 == null) {
            return root1 == root2;
        }

        // Se i valori delle radici sono diversi, gli alberi non sono uguali
        if (root1.compareTo(root2) != 0) {
            return false;
        }

        return recAreEquals(root1, root2);
    }

    // metodo ricorsivo eseguito n volte
    private static boolean recAreEquals(TreeNode root1, TreeNode root2) {

        if (root1.children.isEmpty() && root2.children.isEmpty()) {
            return true;
        }

        TreeSet<TreeNode> childSet1 = new TreeSet<>(root1.children); // O(k log k)
        TreeSet<TreeNode> childSet2 = new TreeSet<>(root2.children); // O(k log k)

        if (childSet1.size() != childSet2.size()) {
            return false;
        }

        if (root1.value != root2.value) {
            return false;
        }

        Iterator<TreeNode> iter1 = childSet1.iterator(); // O(1)
        Iterator<TreeNode> iter2 = childSet2.iterator(); // O(1)

        while (iter1.hasNext() && iter2.hasNext()) {
            TreeNode child1 = iter1.next(); // O(log k) nel caso pessimo, O(1) nel caso medio
            TreeNode child2 = iter2.next(); // O(log k) nel caso pessimo, O(1) nel caso medio

            if (!recAreEquals(child1, child2)) {
                return false;
            }
        }

        return true;
    }

    // stampa un albero ricorsivamente
    static void printTree(TreeNode root) {
        // Verifica se il nodo radice è nullo
        if (root == null) {
            System.out.println("Root is null");
            return;
        }

        StringBuilder sb = new StringBuilder();

        // Aggiungi il valore del nodo radice se ha figli
        if (!root.children.isEmpty()) {
            sb.append(root.value).append(" -> ");

            // Aggiungi i valori dei figli
            for (TreeNode child : root.children) {
                sb.append(child.value).append(" ");
            }

            // Stampa il risultato
            System.out.println(sb.toString().trim());
        }

        // Stampa ricorsivamente gli alberi dei figli
        for (TreeNode child : root.children) {
            printTree(child);
        }
    }

    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Usage: java Esercizio1 <pairList> <nestedList>");
            return;
        }

        TreeNode tree1 = buildTreeFromPairs(args[0]); // O(n) dove n è il numero di nodi
        TreeNode tree2 = buildTreeFromNestedList(args[1]); // O(m) dove m è il numero di caratteri della stringa

        // printTree(tree1);
        // System.out.println();
        // printTree(tree2);

        if (areTreesEqual(tree1, tree2)) { 
            System.out.println("Alberi uguali");
        } else {
            System.out.println("Alberi diversi");
        }

    }

}
