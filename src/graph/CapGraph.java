/**
 * 
 */
package graph;

import org.graphstream.graph.implementations.SingleGraph;
import com.google.common.collect.HashBasedTable;


import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Supratim Das
 * For the warm up assignment, you must implement your Graph in a class
 * named CapGraph.  Here is the stub file.
 *
 */
public class CapGraph implements Graph {

	/* (non-Javadoc)
	 * @see graph.Graph#addVertex(int)
	 */
	//------------------------------------------------------------------------------------------------------------------
    // Variables--------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
	private HashMap<Integer,CapNode> vertices;
	private HashSet<CapEdge> edges;

	// Week 3
    private HashMap<Integer,Boolean> infoChain;
    public static SingleGraph visual;

    //------------------------------------------------------------------------------------------------------------------
    // Constructor------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
	public CapGraph() {
        vertices = new HashMap<>();
        edges = new HashSet<>();

        // Week 3
        infoChain = new HashMap<>();

        // Visual
        visual = new SingleGraph("Visuals");
    }

    //------------------------------------------------------------------------------------------------------------------
    // Functions--------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
	@Override
    // Verbose
	public void addVertex(int num) {
		// TODO Auto-generated method stub
        CapNode cp = new CapNode(num,new ArrayList<>(),false);
        vertices.put(num,cp);

        // Week 3
        infoChain.put(num,false);

	}

	/* (non-Javadoc)
	 * @see graph.Graph#addEdge(int, int)
	 */
	// Verbose
	@Override
	public void addEdge(int from, int to) {
		// TODO Auto-generated method stub
        CapNode fromNode = vertices.get(from);
        CapNode toNode = vertices.get(to);
        CapEdge ce = new CapEdge(fromNode,toNode);
        edges.add(ce);
        fromNode.getEdges().add(ce);
	}

	public void constructVisuals() {
	    for(int v: vertices.keySet()) {
	        visual.addNode(Integer.toString(v));
        }

        int count = 0;

	    HashBasedTable<String,String,Integer> edgTable = HashBasedTable.create();

	    for(CapEdge e: edges) {
	        if(!edgTable.contains(Integer.toString(e.getFromID().getPersonID()),Integer.toString(e.getToID().getPersonID()))) {
	            if(!edgTable.contains(Integer.toString(e.getToID().getPersonID()),Integer.toString(e.getFromID().getPersonID()))) {
                    count++;
                    edgTable.put(Integer.toString(e.getFromID().getPersonID()), Integer.toString(e.getToID().getPersonID()), count);
                    visual.addEdge("id" + count, Integer.toString(e.getFromID().getPersonID()), Integer.toString(e.getToID().getPersonID()));
                }
            }
        }
//        visual.addAttribute("ui.stylesheet","/Users/Supra/Downloads/SocialNetworks/ui.css");
    }
    /**
     * This function returns all the vertices present in the graph
     * @return vertices
     */
    @Override
    public HashMap<Integer,CapNode> getVertices() {
        return this.vertices;
    }

    // Week 3-----------------------------------------------------------------------------------------------------------

    /**
     * This function returns the current state of information in the graph
     * @return infoChain
     */
    public HashMap<Integer, Boolean> getInfoChain() {
        return infoChain;
    }

    /**
     * This function sets information to a node
     * @param source
     */
    public void setSourcePoint(int source) {

        if(!infoChain.containsKey(source)) {
            throw new IllegalArgumentException();
        }

        CapNode cp = vertices.get(source);
        cp.setInfo(true);
        infoChain.put(source,true);
        visual.getNode(Integer.toString(source)).addAttribute("ui.style", "fill-color: rgb(0,100,255);");
        visual.getNode(Integer.toString(source)).addAttribute("ui.label", Integer.toString(source));
    }

    private double calDecisionValue(int a, int b) {
        return ((double)b/(double)(a+b));
    }

    public void infoFlow(int gen,int a,int b) throws InterruptedException{

        int countGen = 0;
        HashMap<Integer,Integer> haveInfo = new HashMap<>();
        double threshold = calDecisionValue(a,b);

        while(countGen < gen) {
            // First part: Information collection
            for (int vertex : vertices.keySet()) {
                CapNode cp = vertices.get(vertex);
                int count = 0;
                for (CapNode np : cp.getNeighbors()) {
                    if (np.getInfo()) {
                        count++;
                    }
                }
                haveInfo.put(vertex, count);
            }

            // Second part: Information load
            for (int vertex : vertices.keySet()) {
                CapNode cp = vertices.get(vertex);
                int denom = cp.getNeighbors().size();
                int count = haveInfo.get(vertex);
                double coeffChange = (double) count / (double) denom;
                if (coeffChange > threshold) {
                    setSourcePoint(cp.getPersonID());
                }
            }
            countGen++;
        }
    }

    // Week 3-----------------------------------------------------------------------------------------------------------
    /**
     * @param center
     * @return egoNet
     */
    public Graph getEgonet(int center) {
		// TODO Auto-generated method stub
        Graph egoNet = new CapGraph();
        ArrayList<CapNode> netNodes = new ArrayList<>();
        CapNode centerNode = vertices.get(center);
        netNodes.add(centerNode);
        // Vertices-----------------------------------------------------------------------------------------------------
        if(!vertices.containsKey(center)) {
            System.out.println("The vertex doesn't exist!");
            return null;
        }

        HashSet<CapNode> neighborsOfCenter = centerNode.getNeighbors();

        egoNet.addVertex(center);

        for (CapNode node : neighborsOfCenter) {
            egoNet.addVertex(node.getPersonID());
            netNodes.add(node);
            egoNet.addEdge(center, node.getPersonID());
        }

        for(CapNode node: neighborsOfCenter) {
            for(CapNode node2: node.getNeighbors()) {
                if(netNodes.contains(node2)) {
                    egoNet.addEdge(node.getPersonID(),node2.getPersonID());
                }
            }
        }
		return egoNet;
	}

	/* (non-Javadoc)
	 * @see graph.Graph#getSCCs()
	 */
    /**
     * this function overrides the method from it's parent class
     * @return strongConComp: List of Strongly connected components
     */
	@Override
	public List<Graph> getSCCs() {
		// TODO Auto-generated method stub
        // Initialization-----------------------------------------------------------------------------------------------
        List<Graph> strongConComp = new ArrayList<>();
        Stack<Integer> finished = new Stack<>();
        Stack<Integer> verticesList = new Stack<>();
        // Putting the vertices into a stack
        for(int vertex: vertices.keySet()) {
            verticesList.add(vertex);
        }
        // Main Algorithm-----------------------------------------------------------------------------------------------
        Graph Gt = new CapGraph();

        // Step 1: Do DFS on graph G
        try {
            finished = DFS(this, verticesList);
            //System.out.println(finished.toString());
        } catch(Exception e) {
            e.printStackTrace();
        }

        // Step 2: Do G transpose
        try {
            Gt = transpose(this);
        } catch(Exception e) {
            e.printStackTrace();
        }

        // Step 3: Do DFS on graph G transposed
        try {
            strongConComp = DFS_SP(Gt, finished);
        } catch(Exception e) {
            e.printStackTrace();
        }
		return strongConComp;
	}

    /**
     * This function performs DFS algorithm recursively
     * @param G
     * @param verticesList
     * @return finished
     */
    private Stack<Integer> DFS(Graph G, Stack<Integer> verticesList) {
        Set<Integer> visited = new HashSet<>();
        Stack<Integer> finished = new Stack();
        while(!verticesList.isEmpty()) {
            int tempVertex = verticesList.pop();
            if(!visited.contains(tempVertex)) {
                ReturningValues rv = DFS_VISIT(G,tempVertex,visited,finished);
                finished = rv.getFin();
                visited = rv.getVisit();
            }
        }
        return finished;
    }

    /**
     * This function performs DFS recursively in the 3rd step to extract all the strongly connected components
     * @param G
     * @param verticesList
     * @return SCC
     */
    private ArrayList<Graph> DFS_SP(Graph G, Stack<Integer> verticesList) {
	    Set<Integer> visited = new HashSet<>();
	    ArrayList<Graph> SCC = new ArrayList<>();
        Stack<Integer> tempFinished = new Stack<>();
	    while(!verticesList.isEmpty()) {
	        int tempVertex = verticesList.pop();
	        if(!visited.contains(tempVertex)) {
	            ReturningValues rv = DFS_VISIT(G,tempVertex,visited,tempFinished);
	            System.out.println(tempFinished.toString());
                SCC.add(buildGraph(G,tempFinished));
                visited = rv.getVisit();
            }
        }
	    return SCC;
    }

    /**
     * This function performs the recursion for DFS in order to put vertices in stack
     * @param G
     * @param tempVertex
     * @param visited
     * @param finished
     * @return rv
     */
    private ReturningValues DFS_VISIT(Graph G, Integer tempVertex, Set<Integer> visited, Stack<Integer> finished) {
        visited.add(tempVertex);
        HashMap<Integer,CapNode> localVertices = G.getVertices();
        CapNode vNode = localVertices.get(tempVertex);
        for(CapNode node: vNode.getNeighbors()){
            if(!visited.contains(node.getPersonID())){
                DFS_VISIT(G,node.getPersonID(),visited,finished);
            }
        }
        finished.push(tempVertex);
        ReturningValues rv = new ReturningValues(visited,finished);
        return rv;
    }

    /**
     * This function changes the direction of edges of the Graph and transposes it
     * @param G
     * @return Gt
     */
    private Graph transpose(Graph G) {
	    Graph Gt = new CapGraph();
	    HashMap<Integer,CapNode> localVertices = G.getVertices();
	    for(int i: localVertices.keySet()) {
	        CapNode temp = localVertices.get(i);
	        if(!Gt.getVertices().containsKey(i)) {
                Gt.addVertex(i);
            }
	        for(CapNode node: temp.getNeighbors()) {
                if(!Gt.getVertices().containsKey(node.getPersonID())) {
                    Gt.addVertex(node.getPersonID());
                }
	            Gt.addEdge(node.getPersonID(),i);
            }
        }
	    return Gt;
    }

    /**
     * This function builds a graph given a set of vertices
     * @param G
     * @param stack
     * @return localGraph
     */
    private Graph buildGraph(Graph G,Stack<Integer> stack) {
        HashMap<Integer,CapNode> localVertices = G.getVertices();
	    Graph localGraph = new CapGraph();
	    while(!stack.isEmpty()) {
            int vertex = stack.pop();
            if(!localGraph.getVertices().containsKey(vertex)) {
                localGraph.addVertex(vertex);
            }
            CapNode node = localVertices.get(vertex);
            for (CapNode cp: node.getNeighbors()) {
                if (stack.contains(cp.getPersonID())) {
                    if (!localGraph.getVertices().containsKey(cp.getPersonID())) {
                        localGraph.addVertex(cp.getPersonID());
                    }
                    localGraph.addEdge(vertex, cp.getPersonID());
                }
            }
        }
	    return localGraph;
    }

	/* (non-Javadoc)
	 * @see graph.Graph#exportGraph()
	 */

    /**
     * this function takes care of exporting the graph in required format
     * @return graphExpo
     */
	@Override
	public HashMap<Integer, HashSet<Integer>> exportGraph() {
		// TODO Auto-generated method stub
        HashMap<Integer, HashSet<Integer>> graphExpo = new HashMap<>();
        for(int vertex: vertices.keySet()) {
            CapNode cp = vertices.get(vertex);
            HashSet<CapNode> neighbors = cp.getNeighbors();
            HashSet<Integer> neighborsInt = new HashSet<>();
            for(CapNode neighbor: neighbors) {
                neighborsInt.add(neighbor.getPersonID());
            }
            graphExpo.put(vertex,neighborsInt);
        }
		return graphExpo;
	}

    /**
     * The class does the job for returning two different kinds of values out of the same function
     */
    private class ReturningValues {
        private Set<Integer> visit;
        private Stack<Integer> fin;

        private ReturningValues(Set<Integer> visit, Stack<Integer> fin) {
            this.visit = visit;
            this.fin = fin;
        }

        private Set<Integer> getVisit() {
            return visit;
        }

        private Stack<Integer> getFin() {
            return fin;
        }
    }

    /**
     * This function does the job of printing a graph as specified by the input data
     * @param g
     */
    public static void printGraph(Graph g) {
        HashMap<Integer, HashSet<Integer>> result = g.exportGraph();
        for(int i: result.keySet()){
            System.out.println(i+" : "+result.get(i).toString());
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    // Week 3-----------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------


}
