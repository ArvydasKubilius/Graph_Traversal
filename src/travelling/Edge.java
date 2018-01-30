package travelling;

import graph_entities.IEdge;
import graph_entities.IVertex;

public class Edge<T> implements IEdge<T> {

  private final IVertex<T> tgt;
  private final Float cst;

  public Edge(IVertex<T> tgt, Float cst) {
    this.tgt = tgt;
    this.cst = cst;
  }
  @Override
  public IVertex<T> getTgt() {
    return tgt;
  }
  @Override
  public Float getCost() {
    return cst;
  }
}