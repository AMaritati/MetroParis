package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.*;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

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
		 * 
		 * 
		 * 
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
		System.out.format("Grafo caricato con %d vertici %d archi\n",
				this.graph.vertexSet().size(),this.graph.edgeSet().size());

	}
	
	/**
	 * Visita l'intero grafo con la strategia Breadth First
	 * e ritorna l'insieme dei vertici incontrati
	 * @param source vertice di partenza della visita
	 * @return insieme dei vertici incontrati
	 */
	public List<Fermata> visitaAmpiezza(Fermata source) {
		
		List<Fermata> visita = new ArrayList<>();
		
		BreadthFirstIterator<Fermata, DefaultEdge> bfv = new BreadthFirstIterator<>(graph,source);
		
		while (bfv.hasNext()) {
		    visita.add(bfv.next());	
		}
		
		return visita;
	}
	
	public Map<Fermata, Fermata> alberoVisita (Fermata source) {
		Map<Fermata,Fermata> albero = new HashMap<>();
		albero.put(source, null); // conosco la sorgente conosciuto da null
		
		BreadthFirstIterator<Fermata, DefaultEdge> bfv = new BreadthFirstIterator<>(graph,source);
		bfv.addTraversalListener(new TraversalListener<Fermata, DefaultEdge>() {
			
			@Override
			public void vertexTraversed(VertexTraversalEvent<Fermata> e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void vertexFinished(VertexTraversalEvent<Fermata> e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> e) {
				// la visita sta considerando un nuovo arco.
				// questo arco ha scoperto un nuovo vertice?
				// Se sì, da dove?
				DefaultEdge edge = e.getEdge(); // (a,b) : ho scoperto 'a' partendo da 'b' o viceversa
				Fermata a = graph.getEdgeSource(edge);
				Fermata b = graph.getEdgeTarget(edge);
				
				if(albero.containsKey(a)) {
					albero.put(b, a);
				}
				else {
					albero.put(a, b);
				}
			}
			
			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {
		
				
			}
			
			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {
				
				
			}
		});
		
		while (bfv.hasNext()) {
			bfv.next();
		}
		
		return albero;
	}
	
public List<Fermata> visitaProfondita(Fermata source) {
		
		List<Fermata> visita = new ArrayList<>();
		
		DepthFirstIterator<Fermata, DefaultEdge> dfv = new DepthFirstIterator<>(graph,source);
		
		while (dfv.hasNext()) {
		    visita.add(dfv.next());	
		}
		
		return visita;
	}
	public static void main(String args[]) {
		Model m = new Model();
		
		List<Fermata> visita1 = m.visitaAmpiezza(m.fermate.get(0));
		System.out.println(visita1);
		List<Fermata> visita2 = m.visitaProfondita(m.fermate.get(0));
		System.out.println(visita2);
		
		Map<Fermata,Fermata> albero = m.alberoVisita(m.fermate.get(0));
		
		for(Fermata f: albero.keySet()) {
			System.out.format("%s --> %s\n", f,albero.get(f));
		}
	}
}
