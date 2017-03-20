package graph;
import static graph.CapGraph.visual;
import util.GraphLoader;

/**
 * Created by Supra on 12/03/2017.
 */
public class App {
    public static void main(String[] args) throws InterruptedException{
        CapGraph g = new CapGraph();
        GraphLoader.loadGraph(g,"facebook_combined.txt");
        g.constructVisuals();
        visual.display();
        for(int i=200;i<400;i++) {
            g.setSourcePoint(i);
        }
        g.infoFlow(10,300,1);
        System.out.println(g.getInfoChain().toString());
        visual.display();
    }
}
