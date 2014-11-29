public class BaseballElimination {

    private int N;
    private ST<String, Integer> teams;
    private int[] wins;
    private int[] losses;
    private int[] remaining;
    private int[][] against;

    private ST<Integer, SET<String>> certificates = new ST<>();

    public BaseballElimination(String filename) {
        readData(filename);
    }

    private void readData(String filename) {
        // Read the data
        In in = new In(filename);
        N = in.readInt();

        teams = new ST<>();
        wins = new int[N];
        losses = new int[N];
        remaining = new int[N];
        against = new int[N][N];
        for (int i = 0; i < N; i++) {
            teams.put(in.readString(), i);
            wins[i] = in.readInt();
            losses[i] = in.readInt();
            remaining[i] = in.readInt();
            for (int j = 0; j < N; j++)
                against[i][j] = in.readInt();
        }
        in.close();
    }

    // number of teams
    public int numberOfTeams() {
        return N;
    }

    // all teams
    public Iterable<String> teams() {
        return teams.keys();
    }

    private int getTeamIndex(String team) {
        Integer idx = teams.get(team);
        if (idx == null)
            throw new IllegalArgumentException();
        return idx;
    }

    // number of wins for given team
    public int wins(String team) {
        return wins[getTeamIndex(team)];
    }

    // number of losses for given team
    public int losses(String team) {
        return losses[getTeamIndex(team)];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        return remaining[getTeamIndex(team)];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        return against[getTeamIndex(team1)][getTeamIndex(team2)];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        int idx = getTeamIndex(team);
        computeElimination(idx);
        return !certificates.get(idx).isEmpty();
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        int idx = getTeamIndex(team);
        computeElimination(idx);
        return certificates.get(idx);
    }

    private void computeElimination(int team) {
        if (certificates.contains(team))
            return;

        // Check for trivial elimination first
        int best = wins[team] + remaining[team];
        for (String t : teams()) {
            int i = getTeamIndex(t);
            if (i != team && best < wins[i]) {
                SET<String> certs = new SET<>();
                certs.add(t);
                certificates.put(team, certs);
                return;
            }
        }

        // Solve using mincut algorithm
        computeMinCut(team);
    }

    private void computeMinCut(int team) {
        /*
         * To simplify things, we create vertices for all teams
         * 
         * vertex 0 - source
         * vertices 1..(N^2-N)/2 - games
         * vertices (N^2-N)/2 + 1..(N^2+N)/2 - teams
         * vertex (N^2+N)/2+1 - target
         */
        int numGames = N * (N - 1) / 2;
        int V = numGames + N + 2;
        FlowNetwork nw = new FlowNetwork(V);

        int x = 1;
        int teamVertexStart = numGames + 1;
        for (int i = 0; i < N; i++) {
            if (i == team)
                continue;
            for (int j = i + 1; j < N; j++) {
                if (j == team)
                    continue;

                // Edge from source to game
                nw.addEdge(new FlowEdge(0, x, against[i][j]));

                // Edge from game to teams
                nw.addEdge(new FlowEdge(x, teamVertexStart + i,
                        Double.POSITIVE_INFINITY));
                nw.addEdge(new FlowEdge(x, teamVertexStart + j,
                        Double.POSITIVE_INFINITY));

                x++;
            }
        }

        int bestForTeam = wins[team] + remaining[team];
        // Edges from teams to sink
        for (int i = 0; i < N; i++) {
            if (i == team)
                continue;
            nw.addEdge(new FlowEdge(teamVertexStart + i, V - 1, bestForTeam
                    - wins[i]));
        }

        SET<String> certs = new SET<>();
        FordFulkerson ff = new FordFulkerson(nw, 0, V - 1);
        for (String t : teams()) {
            int idx = getTeamIndex(t);
            if (idx == team)
                continue;
            if (ff.inCut(teamVertexStart + idx))
                certs.add(t);
        }
        certificates.put(team, certs);
    }
}
