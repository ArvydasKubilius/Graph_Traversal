package student_solution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import graph_entities.IEdge;
import graph_entities.IVertex;
import graph_entities.Label;

public class Vertex<T> implements IVertex<T> {
	 private final List<IEdge<T>> edges;
  private Label<T> lbl;
  


  public Vertex() {
    edges = new ArrayList<>();
    
  }
  public Vertex(String vertexId) {
    this();
    this.lbl = new Label<T>();
    this.lbl.setName(vertexId);
  }
  @Override
  public void addEdge(IEdge<T> edge) {
    edges.add(edge);
  }
  @Override
  public Collection<IEdge<T>> getSuccessors() {
    return edges;
  }
  @Override
  public Label<T> getLabel() {
    return lbl;
  }

  @Override
  public void setLabel(Label<T> label) {
    this.lbl = label;
  }
  @Override
  public int compareTo(IVertex<T> other) {
    assert (other.getLabel().getCost() != null&& other.getLabel().getName() != null&&other != null && other.getLabel() != null);
    //alio alio
    assert (this.getLabel().getName() != null&&this.getLabel() != null && this.getLabel().getCost() != null);
    // reik padaryti
    if (other == this)
      return 0;
    int k = this.getLabel().getCost().compareTo(other.getLabel().getCost());
    if (this.getLabel().getCost().compareTo(other.getLabel().getCost()) == 0) {
      k = this.getLabel().getName().compareTo(other.getLabel().getName());
    }
    return k;
  }
}
