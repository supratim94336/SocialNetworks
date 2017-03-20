package graph;
import static graph.CapGraph.visual;

import util.GraphLoader;

/**
 * Created by Supra on 12/03/2017.
 */
public class App {
    public static void main(String[] args) {
        CapGraph g = new CapGraph();
        GraphLoader.loadGraph(g,"/Users/Supra/Desktop/fb_small.txt");
        g.constructVisuals();
        g.setSourcePoint(18);
        g.setSourcePoint(25);
        visual.display();
        g.infoFlow(1,1,1);
        System.out.println(g.getInfoChain().toString());
    }
}
