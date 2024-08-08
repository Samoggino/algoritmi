import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class Esercizio1_GPT {

    // TreeNode class to represent each node in the tree
    static class TreeNode {
        int value;
        List<TreeNode> children;

        TreeNode(int value) {
            this.value = value;
            this.children = new ArrayList<>();
        }
    }

    static TreeNode buildTreeFromPairs(String filename) throws IOException, NumberFormatException, Exception {

        // ricorda che il file parent_child_pairs.txt è nella cartella es1, dovrà essere
        // eseguito da terminale, quindi il path sarà parent_child_pairs.txt
        Map<Integer, TreeNode> nodeMap = new HashMap<>();
        Set<Integer> childrenSet = new HashSet<>();
        BufferedReader br = new BufferedReader(new FileReader("es1/parent_child_pairs.txt"));
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

        // Find root node (node that is not a child of any other node)
        for (int nodeValue : nodeMap.keySet()) {
            if (!childrenSet.contains(nodeValue)) {
                return nodeMap.get(nodeValue);
            }
        }

        return null;
    }

    // Method to build a tree from a nested list
    static TreeNode buildTreeFromNestedList(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line = br.readLine().trim();
        br.close();

        // Strip surrounding brackets and parse
        return parseNestedList(new StringTokenizer(line, "[],", true));
    }

    // Helper method to parse nested list format
    static TreeNode parseNestedList(StringTokenizer tokenizer) {
        if (!tokenizer.hasMoreTokens())
            return null;

        // Read the node value
        String token = tokenizer.nextToken().trim();
        if (!token.matches("\\d+"))
            return null;

        int value = Integer.parseInt(token);
        TreeNode node = new TreeNode(value);

        while (tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken().trim();
            if (token.equals("[")) {
                // Start a new list (children)
                while (!token.equals("]")) {
                    TreeNode child = parseNestedList(tokenizer);
                    if (child != null)
                        node.children.add(child);
                    token = tokenizer.nextToken().trim();
                }
            } else if (token.equals("]") || token.equals(",")) {
                continue;
            }
        }

        return node;
    }

    // Method to compare two trees
    static boolean areTreesEqual(TreeNode root1, TreeNode root2) {
        if (root1 == null && root2 == null)
            return true;
        if (root1 == null || root2 == null) {
            System.out.println("c'è un null");
            return false;
        }
        if (root1.value != root2.value) {
            System.out.println("Valori diversi");
            return false;
        }

        // Compare children ignoring the order
        if (root1.children.size() != root2.children.size())
            return false;

        // Sort and compare children by converting them into sets
        Set<Integer> childSet1 = new HashSet<>();
        for (TreeNode child : root1.children) {
            childSet1.add(child.value);
        }

        Set<Integer> childSet2 = new HashSet<>();
        for (TreeNode child : root2.children) {
            childSet2.add(child.value);
        }

        return childSet1.equals(childSet2);
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

    public static void main(String[] args) {
        // if (args.length != 2) {
        // System.err.println("Usage: java Esercizio1 <parent_child_pairs_file>
        // <nested_list_file>");
        // System.exit(1);
        // }

        // String pairsFile = args[0];
        // String nestedFile = args[1];

        String pairsFile = "es1/parent_child_pairs.txt";
        String nestedFile = "es1/nested_list.txt";

        try {
            TreeNode tree1 = buildTreeFromPairs(pairsFile);
            TreeNode tree2 = buildTreeFromNestedList(nestedFile);

            if (areTreesEqual(tree1, tree2)) {
                System.out.println("I due alberi sono uguali.");
            } else {
                System.out.println("I due alberi non sono uguali.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
