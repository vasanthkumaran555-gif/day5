class Solution {

    // Intuition:
    // Since n <= 15, we can enumerate every possible subset of nodes using a bitmask.
    // For each subset, we need to:
    // 1. Check if the selected nodes form a connected subgraph.
    // 2. If connected, compute its diameter.
    //
    // To avoid repeatedly computing shortest paths for every subset,
    // we first precompute the distance between every pair of nodes
    // by running a BFS from each node.
    //
    // Then for every connected subset, the diameter is simply the
    // maximum precomputed distance among all pairs of selected nodes.
    //
    // Time Complexity:
    // Precomputing distances : O(n^2)
    // Enumerating subsets    : O(2^n * n^2)
    // Overall                : O(2^n * n^2)
    //
    // Space Complexity: O(n^2)

    List<Integer> adj[];

    public int[] countSubgraphsForEachDiameter(int n, int[][] edges) {

        // Build the adjacency list of the tree.
        adj = new ArrayList[n];
        for (int i = 0; i < n; i++)
            adj[i] = new ArrayList<>();

        for (int e[] : edges) {
            int u = e[0] - 1, v = e[1] - 1;
            adj[u].add(v);
            adj[v].add(u);
        }

        // dist[i][j] = shortest distance between nodes i and j.
        // Since the graph is a tree, running BFS from every node
        // computes all pairwise distances.
        int dist[][] = new int[n][n];

        for (int i = 0; i < n; i++)
            Arrays.fill(dist[i], -1);

        for (int i = 0; i < n; i++) {
            dist[i][i] = 0;

            Queue<int[]> q = new LinkedList<>();
            q.offer(new int[]{i, -1, 0});

            while (!q.isEmpty()) {
                int arr[] = q.poll();
                int u = arr[0], p = arr[1], d = arr[2];

                for (int v : adj[u]) {
                    if (v == p)
                        continue;

                    dist[i][v] = d + 1;
                    q.offer(new int[]{v, u, d + 1});
                }
            }
        }

        // res[d - 1] stores the number of connected subgraphs
        // having diameter d.
        int res[] = new int[n - 1];

        // Enumerate every non-empty subset of nodes.
        for (int mask = 1; mask < (1 << n); mask++) {

            boolean nodes[] = new boolean[n];
            int start = n;
            int count = 0;

            // Decode the bitmask.
            // Also remember one selected node to start the BFS.
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {
                    nodes[i] = true;
                    start = Math.min(start, i);
                    count++;
                }
            }

            // Check whether the selected nodes form a connected subgraph.
            Queue<Integer> q = new LinkedList<>();
            Set<Integer> visited = new HashSet<>();

            visited.add(start);
            q.offer(start);

            while (!q.isEmpty()) {
                int u = q.poll();

                for (int v : adj[u]) {
                    if (nodes[v] && !visited.contains(v)) {
                        visited.add(v);
                        q.offer(v);
                    }
                }
            }

            // Ignore disconnected subsets.
            if (visited.size() != count)
                continue;

            // Compute the diameter by taking the maximum distance
            // among every pair of selected nodes.
            int maxD = 0;

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (nodes[i] && nodes[j]) {
                        maxD = Math.max(maxD, dist[i][j]);
                    }
                }
            }

            // Diameter 0 corresponds to a single-node subset,
            // which is not counted in the answer.
            if (maxD >= 1)
                res[maxD - 1]++;
        }

        return res;
    }
}