package it.polito.tdp.extflightdelays.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {
	
	private Map<Integer, Airport> idMap;
	private SimpleWeightedGraph<Airport, DefaultWeightedEdge> grafo;
	
	public Model() {
		idMap = new HashMap<>();
	}

	public boolean isDigit(String distanza) {
		if(distanza.matches("\\d+")) {
			return true;
		}
		return false;
	}

	public String creaGrafo(String distanza) {
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		ExtFlightDelaysDAO dao = new ExtFlightDelaysDAO();
		String risultato="";
		dao.loadAllAirports(idMap);
		Graphs.addAllVertices(grafo, idMap.values());
		List<Rotta> rotte = dao.getRotte(idMap, distanza);
		for(Rotta r: rotte) {
			DefaultWeightedEdge edge = grafo.getEdge(r.getA1(), r.getA2());
			if(edge==null) {
				Graphs.addEdgeWithVertices(grafo, r.getA1(), r.getA2(), r.getPeso());
			}else {
				double peso = grafo.getEdgeWeight(edge);
				double newPeso = (peso+r.getPeso())/2;
				grafo.setEdgeWeight(edge, newPeso);
			}
		}
		risultato="Grafo Creato! Vertici: "+grafo.vertexSet().size()+" Archi: "+grafo.edgeSet().size()+"\n";
		return risultato;
	}

	public List<Airport> getVertici() {
		List<Airport> vertici = new LinkedList<>();
		for(Airport a : grafo.vertexSet()) {
			vertici.add(a);
		}
		return vertici;
	}

	public String getConnessioni(Airport a) {
		String risultato="";
		List<Airport> vicini = Graphs.neighborListOf(grafo, a);
		Collections.sort(vicini, new Comparator<Airport>() {

			@Override
			public int compare(Airport a1, Airport a2) {
				DefaultWeightedEdge edge1 = grafo.getEdge(a, a1);
				double peso1 = grafo.getEdgeWeight(edge1);
				DefaultWeightedEdge edge2 = grafo.getEdge(a, a2);
				double peso2 = grafo.getEdgeWeight(edge2);
				return (int) (peso2-peso1);
			}
		});
		for(Airport a3: vicini) {
			DefaultWeightedEdge edge = grafo.getEdge(a, a3);
			risultato+=a3.getAirportName()+" Distanza media: "+grafo.getEdgeWeight(edge)+"\n";
		}
		return risultato;
	}

}
