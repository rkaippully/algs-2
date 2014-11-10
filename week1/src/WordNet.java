import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordNet {

    private Map<Integer, String> idToNouns = new HashMap<>();
    private Map<String, List<Integer>> nounToIds = new HashMap<>();
    private Digraph G;

    // constructor takes the name of the two input files
    public WordNet(String synsetsFile, String hypernymsFile) {
        int vertices = 0;
        In synsetsIn = new In(synsetsFile);
        String line = synsetsIn.readLine();
        while (line != null) {
            String[] parts = line.split(",");
            int id = Integer.parseInt(parts[0]);
            String[] nouns = parts[1].split(" ");
            idToNouns.put(id, parts[1]);
            for (String noun : nouns) {
                List<Integer> ids = nounToIds.get(noun);
                if (ids == null) {
                    ids = new ArrayList<>();
                    nounToIds.put(noun, ids);
                }
                ids.add(id);
            }
            vertices++;
            line = synsetsIn.readLine();
        }
        synsetsIn.close();

        G = new Digraph(vertices);
        In hypernymsIn = new In(hypernymsFile);
        line = hypernymsIn.readLine();
        while (line != null) {
            String[] parts = line.split(",");
            int id = Integer.parseInt(parts[0]);

            // Form the edges
            for (int i = 1; i < parts.length; i++) {
                int parent = Integer.parseInt(parts[i]);
                G.addEdge(id, parent);
            }

            line = hypernymsIn.readLine();
        }
        hypernymsIn.close();

        // Is this a rooted DAG?
        boolean rootFound = false;
        for (int i = 0; i < vertices; i++) {
            if (!G.adj(i).iterator().hasNext()) {
                if (!rootFound)
                    rootFound = true;
                else
                    throw new IllegalArgumentException("Not connected");
            }
        }
        if (!rootFound)
            throw new IllegalArgumentException("No root found");
        DirectedCycle dc = new DirectedCycle(G);
        if (dc.hasCycle())
            throw new IllegalArgumentException("Has cycles");
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nounToIds.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null)
            throw new NullPointerException();
        return nounToIds.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException();
        SAP sap = new SAP(G);
        return sap.length(nounToIds.get(nounA), nounToIds.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of
    // nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException();
        SAP sap = new SAP(G);
        int ancestor = sap.ancestor(nounToIds.get(nounA), nounToIds.get(nounB));
        return idToNouns.get(ancestor);
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wn = new WordNet(args[0], args[1]);
        for (String nounA : wn.nouns()) {
            for (String nounB : wn.nouns())
                StdOut.printf("nounA: %s, nounB: %s, distance: %d\n", nounA,
                        nounB, wn.distance(nounA, nounB));
        }
    }
}