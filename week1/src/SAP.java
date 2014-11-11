import java.util.ArrayList;
import java.util.List;

public class SAP {

    private BreadthFirstAncestorSearch bfs;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        this.bfs = new BreadthFirstAncestorSearch(new Digraph(G));
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        List<Integer> vs = new ArrayList<>();
        vs.add(v);
        List<Integer> ws = new ArrayList<>();
        ws.add(w);
        return length(vs, ws);
    }

    // a common ancestor of v and w that participates in a shortest ancestral
    // path; -1 if no such path
    public int ancestor(int v, int w) {
        List<Integer> vs = new ArrayList<>();
        vs.add(v);
        List<Integer> ws = new ArrayList<>();
        ws.add(w);
        return ancestor(vs, ws);
    }

    // length of shortest ancestral path between any vertex in v and any vertex
    // in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        bfs.searchAncestor(v, w);
        return bfs.ancestorPathLength();
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no
    // such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        bfs.searchAncestor(v, w);
        return bfs.ancestor();
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In("wordnet/digraph" + StdIn.readString() + ".txt");
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        for (int v = 0; v < G.V(); v++) {
            for (int w = 0; w < G.V(); w++) {
                int length = sap.length(v, w);
                int ancestor = sap.ancestor(v, w);
                StdOut.printf("v = %d, w = %d, length = %d, ancestor = %d\n",
                        v, w, length, ancestor);
            }
        }
        /*while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }*/
    }
}