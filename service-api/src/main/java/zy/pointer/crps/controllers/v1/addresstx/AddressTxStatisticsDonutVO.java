package zy.pointer.crps.controllers.v1.addresstx;

import lombok.Data;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class AddressTxStatisticsDonutVO {

    private List<Node> nodes;

    private List<Edge> edges;

    private Set<String> nodeIdSet = Collections.synchronizedSet(new HashSet<>());

    public void addNode( Node node ){
        if ( ! nodeIdSet.contains( node.getId() ) ){
            nodes.add(node);
            nodeIdSet.add( node.getId() );
        }
    }

    @Data
    public static class Node {

        private String id;

        private String label;

        private DonutAttrs donutAttrs = new DonutAttrs();

        @Data
        private static class DonutAttrs {
            private Integer income = 50;
            private Integer outcome = 50;
            private Integer unknown = 50;
        }
    }

    @Data
    public static class Edge {

        private String source;

        private String target;

        private Double size;

    }


}
