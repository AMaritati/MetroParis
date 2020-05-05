package it.polito.tdp.metroparis.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.*;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import it.polito.tdp.metroparis.db.MetroDAO;
public class Model {

	private Graph<Fermata,DefaultEdge> graph;
	private List<Fermata> fermate;
	private Map<Integer,Fermata> fermateIdMap;
	
	public Model() {
		this.graph = new SimpleDirectedGraph<>(DefaultEdge.class);
		MetroDAO dao = new MetroDAO();
		
		// CREAZIONE VERTICI
		this.fermate = dao.getAllFermate();
		this.fermateIdMap = new HashMap<>();
		for (Fermata f : this.fermate) {
			fermateIdMap.put(f.getIdFermata(), f);
		}
		Graphs.addAllVertices(this.graph, this.fermate);
		
		System.out.println(this.graph);
		
		/*// CREAZIONE DEGLI ARCHI -- metodo 1 (coppie di vertici)
		// QUESTO METODO VA BENE PER PICCOLE DIMENSIONI
		// PER LA MAPPA METRO CI VORREBBE TROPPO TEMPO
		for (Fermata fp : this.fermate) {
			for(Fermata fa : this.fermate) {
				//esiste una connessione che va da fa e fp
				if (dao.fermateConnesse(fp, fa) ) {
					this.graph.addEdge(fp, fa);
				}
			}
		}
		
		// CREAZIONE DEGLI ARCHI -- metodo 2 
		// DA UN VERTICE, TROVA TUTTI I CONNESSI
		for (Fermata fp:this.fermate) {
			List<Fermata> connesse = dao.fermateSuccessive(fp, fermateIdMap);//fermate adiacenti ad fp
					
					for (Fermata fa : connesse) {
						this.graph.addEdge(fp, fa);
					}
		}
		*/
		// CREAZIONE DEGLI ARCHI -- metodo 3
		// chiedo al DB l'elenco degli archi
		
		List<CoppiaFermata> coppie = dao.coppieFermate(fermateIdMap);
		
		for (CoppiaFermata c : coppie) {
			this.graph.addEdge(c.getFp(), c.getFa());
		}
		
	
		
		//System.out.println(this.graph);
		System.out.format("Grafo caricato con %d vertici %d archi",
				this.graph.vertexSet().size(),this.graph.edgeSet().size());

	}
	
	public static void main(String args[]) {
		Model m = new Model();
		
	}
}
