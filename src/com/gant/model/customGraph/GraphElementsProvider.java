package com.gant.model.customGraph;

import com.gant.model.Link;
import com.gant.model.Node;
import com.mxgraph.model.mxCell;

/**
 * Created by Andrii on 01.06.2015.
 */
public abstract class GraphElementsProvider {

    public abstract Link buildLink(mxCell cell);
    public abstract Node buildNode(mxCell cell);
    protected abstract int getLinkWeight(mxCell cell);
    protected abstract int getNodeWeight(mxCell cell);

}
