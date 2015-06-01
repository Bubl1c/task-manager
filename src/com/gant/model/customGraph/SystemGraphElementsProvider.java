package com.gant.model.customGraph;

import com.gant.model.Link;
import com.gant.model.Node;
import com.mxgraph.model.mxCell;

/**
 * Created by Andrii on 01.06.2015.
 */
public class SystemGraphElementsProvider extends GraphElementsProvider {
    @Override
    public Link buildLink(mxCell cell) {
        Link link;
        try {
            int sourceId = Integer.parseInt(cell.getSource().getId()) - 1;
            int targetId = Integer.parseInt(cell.getTarget().getId()) - 1;
            link = new Link(getLinkWeight(cell), sourceId, targetId, cell);
        } catch(Exception e){
            throw new RuntimeException("Filed to create link for cell: " + cell.getId() + " [" + cell.getValue() + "]");
        }
        return link;
    }

    @Override
    public Node buildNode(mxCell cell) {
        Node node;
        try {
            node = new Node(Integer.parseInt(cell.getId()) - 1, getNodeWeight(cell), cell);
        } catch(Exception e){
            throw new RuntimeException("Filed to create node for cell: " + cell.getId()+ " [" + cell.getValue() + "]");
        }
        return node;
    }

    @Override
    protected int getLinkWeight(mxCell cell) {
        Integer weight = 0;
        try {
            String str = (String) cell.getValue();
            weight = str.equals("") ? 1 : Integer.parseInt(str);
        } catch (Exception e){
            throw new IllegalArgumentException("Invalid cell value! Cannot get link weight.");
        }
        return weight;
    }

    @Override
    protected int getNodeWeight(mxCell cell) {
        Integer weight = 0;
        try {
            String str = (String) cell.getValue();
            weight = Integer.parseInt(str.substring(str.indexOf("[") + 1, str.indexOf("]")));
        } catch (Exception e){
            throw new IllegalArgumentException("Invalid cell value! Cannot get node weight.");
        }
        return weight;
    }
}
