package graph;
// Java imports

import java.util.HashSet;
import java.util.List;

/**
 * Created by Supra on 27/02/2017.
 */
public class CapNode {
    //------------------------------------------------------------------------------------------------------------------
    // Variables--------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
    private int personID;
    public List<CapEdge> edges;
    private Boolean info;

    //------------------------------------------------------------------------------------------------------------------
    // Constructor------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------


    public CapNode(int personID, List<CapEdge> edges, Boolean info) {
        this.personID = personID;
        this.edges = edges;
        this.info = info;
    }

    //------------------------------------------------------------------------------------------------------------------
    // Functions--------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------


    public Boolean getInfo() {
        return info;
    }

    public void setInfo(Boolean info) {
        this.info = info;
    }

    public int getPersonID() {
        return personID;
    }

    public List<CapEdge> getEdges() {
        return this.edges;
    }

    public HashSet<CapNode> getNeighbors() {
        HashSet<CapNode> neighbors = new HashSet<>();
        for (CapEdge edge : edges) {
            neighbors.add(edge.getOtherEnd(this));
        }
        return neighbors;
    }
}
