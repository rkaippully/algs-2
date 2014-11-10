public class Outcast {

    private WordNet wordnet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int maxDistance = -1;
        String outcast = null;
        for (int i = 0; i < nouns.length; i++) {
            int distance = 0;
            for (int j = 0; j < nouns.length; j++)
                distance += wordnet.distance(nouns[i], nouns[j]);

            if (distance > maxDistance) {
                maxDistance = distance;
                outcast = nouns[i];
            }
        }
        return outcast;
    }

    // see test client below
    public static void main(String[] args) {
        WordNet wordnet = new WordNet("wordnet/synsets.txt",
                "wordnet/hypernyms.txt");
        Outcast outcast = new Outcast(wordnet);
        while (!StdIn.isEmpty()) {
            String line = StdIn.readLine();
            String[] nouns = line.split(" ");
            StdOut.println(line + ": " + outcast.outcast(nouns));
        }
    }
}