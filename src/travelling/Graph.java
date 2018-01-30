package travelling;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.*;
import graph_entities.IEdge;
import graph_entities.IGraph;
import graph_entities.IVertex;
import graph_entities.Result;

public class Graph<T> implements IGraph<T> {

	private final Map<String, IVertex<T>> vrtc;

	public Graph() {
		vrtc = new HashMap<>();
	}

	@Override
	public void addVertex(String vertexId, IVertex<T> vertex) {
		vrtc.put(vertexId, vertex);
	}

	@Override
	public void addEdge(String vertexSrcId, String vertexTgtId, Float cost) {
		vrtc.get(vertexSrcId).addEdge(new Edge<T>(vrtc.get(vertexTgtId), cost));

	}

	@Override
	public Collection<IVertex<T>> getVertices() {
		return vrtc.values();
	}

	@Override
	public Collection<String> getVertexIds() {
		return vrtc.keySet();
	}

	@Override
	public IVertex<T> getVertex(String vertexId) {
		return vrtc.get(vertexId);
	}

	@Override
	public String toDotRepresentation() {
		StringBuilder result = new StringBuilder();
		result.append("digraph {");
		result.append(System.getProperty("line.separator"));
		Set<String> n = vrtc.keySet();
		List<String> nL = new ArrayList<String>();
		nL.addAll(n);
		int nSize = n.size();
		for (int i = 0; i < nSize; i++) {
			result.append(nL.get(i));

			result.append(System.getProperty("line.separator"));
		}
		Set<Entry<String, IVertex<T>>> vEntry = vrtc.entrySet();
		List<Entry<String, IVertex<T>>> vEntryL = new ArrayList<Entry<String, IVertex<T>>>();
		vEntryL.addAll(vEntry);
		int vEntryS = vEntry.size();

		for (int i = 0; i < vEntryS; i++) {
			//
			List<IEdge<T>> vSuccessorL = new ArrayList<>();
			vSuccessorL.addAll(vEntryL.get(i).getValue().getSuccessors());
			int vSuccessorS = vSuccessorL.size();
			//
			for (int k = 0; k < vSuccessorS; k++) {
				graph_entities.Label<T> tgtLabel = vSuccessorL.get(k).getTgt().getLabel();
				result.append(String.format("%s->%s[label=\"%s\"];%s", vEntryL.get(i).getKey(), tgtLabel.getName(),
						vSuccessorL.get(k).getCost(), System.getProperty("line.separator")));
			}
		}

		result.append('}');
		result.append(System.getProperty("line.separator"));
		// System.out.println(result.toString());
		return result.toString();
	}

	@Override
	public void fromDotRepresentation(String dotFilePath) {
		String line;
		try (BufferedReader in = new BufferedReader(new FileReader(dotFilePath))) {
			while ((line = in.readLine()) != null) {
				System.out.println(line);
				if ((line.matches("(?i).->")) && (line.matches("(?i).*[label=\\\"([\\.+\\-0-9]+)\\\"*"))
						&& (line.matches("(?i).*]*"))) {
					String[] parts = line.split("->");
					String[] partss = parts[1].split("[");
					String[] partsss = partss[1].split("=");
					partsss[1] = partsss[1].substring(1);
					String[] partssss = partsss[1].split("]");
					partssss[0] = partssss[0].substring(0, partssss[0].length() - 1);

					String vertixId = parts[0];
					String tgtVertixId = partss[0];
					String edgeCost = partssss[0];
					addEdge(vertixId, tgtVertixId, Float.valueOf(edgeCost));
				} else {
					String vertexId = line.trim();
					if (vertexId.matches("[\\w]+")) {
						vrtc.put(vertexId, new Vertex<T>(vertexId));
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	  @Override
	  public Result<T> breadthFirstSearchFrom(String vertexId,Predicate<IVertex<T>> pred) {
	   
		  HashMap<String, IVertex<T>> vst = new HashMap<String, IVertex<T>>();
		  
		    Queue<IVertex<T>> qq = new LinkedList<>();

		  
		  if (!vrtc.containsKey(vertexId)){
	    	return new Result<T>();
	    }
	    qq.add(vrtc.get(vertexId));
	    while (!(qq.size() == 0)) {
	      IVertex<T> k = qq.poll();
	      
	      if (!(vst.containsKey(k.getLabel().getName()))) {
	        
	      
	      if (pred.test(k)) {
	    	Result<T> rs = new Result<T>();
	    	rs.setVisitedVertices(new ArrayList<>(vst.values()));
	    	if (!(k == null)){
	    		//
	    	    ArrayList<IVertex<T>> path = new ArrayList<>();
	    	    path.add(k);
	    	    while (k.getLabel().getParentVertex().isPresent()&& !k.getLabel().getName().equals(vertexId)) {
	    	      IVertex<T> interm = k.getLabel().getParentVertex().get();
	    	      path.add(interm);
	    	      k = interm;
	    	    }
	    	    Collections.reverse(path);
	    		rs.setPath(path);

	    	    //
	    		rs.setPathCost(k.getLabel().getCost());
	    	}
	        return rs;
	      }
			List<IEdge<T>> vSuccessorL = new ArrayList<>();
			vSuccessorL.addAll(k.getSuccessors());
			int vSuccessorS = vSuccessorL.size();
	      for (int o = 0; o < vSuccessorS; o++) {
	        if (!vst.containsKey(vSuccessorL.get(o).getTgt().getLabel().getName())&&!qq.contains(vSuccessorL.get(o).getTgt())) {
	        	vSuccessorL.get(o).getTgt().getLabel().setParentVertex(k);
	        	vSuccessorL.get(o).getTgt().getLabel()
	              .setCost(vSuccessorL.get(o).getCost() + k.getLabel().getCost());
	          qq.add(vSuccessorL.get(o).getTgt());
	        }
	      }
	      vst.put(k.getLabel().getName(), k);
	    }
	    }
	    /////////////////
	    Result<T> result = new Result<T>();
    	result.setVisitedVertices(new ArrayList<>(vst.values()));
        return result;
	    
	  }
	  
	  @Override
	  public Result<T> depthFirstSearchFrom(String vertexId,Predicate<IVertex<T>> pred) {
	   
		  HashMap<String, IVertex<T>> vst = new HashMap<String, IVertex<T>>();
		    Deque<IVertex<T>> qq = new LinkedList<>();

		  
		  if (!vrtc.containsKey(vertexId)){
	    	return new Result<T>();
	    }
	    qq.add(vrtc.get(vertexId));
	    while (qq.size() != 0) {
	      IVertex<T> k = qq.pop();
	      if (!(vst.containsKey(k.getLabel().getName()))) {

	      if (pred.test(k)) {
	    	Result<T> rs = new Result<T>();
	    	rs.setVisitedVertices(new ArrayList<>(vst.values()));
	    	if (k != null){
	    		//
	    	    ArrayList<IVertex<T>> path = new ArrayList<>();
	    	    path.add(k);
	    	    while (k.getLabel().getParentVertex().isPresent()&& !k.getLabel().getName().equals(vertexId)) {
	    	      IVertex<T> interm = k.getLabel().getParentVertex().get();
	    	      path.add(interm);
	    	      k = interm;
	    	    }
	    	    Collections.reverse(path);
	    		rs.setPath(path);

	    	    //
	    		rs.setPathCost(k.getLabel().getCost());
	    	}
	        return rs;
	      }
			List<IEdge<T>> vSuccessorL = new ArrayList<>();
			vSuccessorL.addAll(k.getSuccessors());
			int vSuccessorS = vSuccessorL.size();
	      for (int o = 0; o < vSuccessorS; o++) {
	        if (!vst.containsKey(vSuccessorL.get(o).getTgt().getLabel().getName())&&!qq.contains(vSuccessorL.get(o).getTgt())) {
	        	vSuccessorL.get(o).getTgt().getLabel().setParentVertex(k);
	        	vSuccessorL.get(o).getTgt().getLabel()
	              .setCost(vSuccessorL.get(o).getCost() + k.getLabel().getCost());
	          qq.add(vSuccessorL.get(o).getTgt());
	        }
	      }
	      vst.put(k.getLabel().getName(), k);
	    }}
	    Result<T> result = new Result<T>();
    	result.setVisitedVertices(new ArrayList<>(vst.values()));
        return result;
	    
	  }
	  @Override
	  public Result<T> dijkstraFrom(String vertexId, Predicate<IVertex<T>> pred) {
	    Queue<IVertex<T>> qq = new PriorityQueue<>();
	    HashMap<String, IVertex<T>> vst = new HashMap<String, IVertex<T>>();
	    if (!vrtc.containsKey(vertexId)){
	    	return new Result<T>();
	    }
	    qq.add(vrtc.get(vertexId));
	    while (qq.size() != 0) {
	      IVertex<T> k = qq.poll();
	      if (!(vst.containsKey(k.getLabel().getName()))) {
	      
	      if (pred.test(k)) {
		    	Result<T> rs = new Result<T>();
		    	rs.setVisitedVertices(new ArrayList<>(vst.values()));
		    	if (k != null){
		    		//
		    	    ArrayList<IVertex<T>> path = new ArrayList<>();
		    	    path.add(k);
		    	    while (k.getLabel().getParentVertex().isPresent()&& !k.getLabel().getName().equals(vertexId)) {
		    	      IVertex<T> interm = k.getLabel().getParentVertex().get();
		    	      path.add(interm);
		    	      k = interm;
		    	    }
		    	    Collections.reverse(path);
		    		rs.setPath(path);

		    	    //
		    		rs.setPathCost(k.getLabel().getCost());
		    	}
		        return rs;
	      }
			List<IEdge<T>> vSuccessorL = new ArrayList<>();
			vSuccessorL.addAll(k.getSuccessors());
			int vSuccessorS = vSuccessorL.size();
	      for (int o = 0; o < vSuccessorS; o++) {
	        if (!vst.containsKey(vSuccessorL.get(o).getTgt().getLabel().getName())) {
	          Float cost = vSuccessorL.get(o).getCost() + k.getLabel().getCost();
	          if (!((vSuccessorL.get(o).getTgt().getLabel().getCost() != 0.0F && cost > vSuccessorL.get(o).getTgt().getLabel().getCost()))) {
	         
	          vSuccessorL.get(o).getTgt().getLabel().setParentVertex(k);
	          vSuccessorL.get(o).getTgt().getLabel().setCost(cost);
	          qq.add(vSuccessorL.get(o).getTgt());
	        }
	      }}
	      vst.put(k.getLabel().getName(), k);
	    }}
	    Result<T> result = new Result<T>();
    	result.setVisitedVertices(new ArrayList<>(vst.values()));
        return result;
	  }

	  
}


//	 