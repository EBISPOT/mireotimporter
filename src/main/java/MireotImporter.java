/**
 * Created by malone on 27/03/2015.
 */


import org.semanticweb.owlapi.model.*;

import java.util.Set;

/**
 * Driver class to run the import code
 */
public class MireotImporter {


    public static void main(String[] args) {

        System.setProperty("entityExpansionLimit", "100000000");

        MireotManager manager = new MireotManager();
        OntologyIO ioManager = new OntologyIO();

        IRI activeOntology = IRI.create("http://www.ebi.ac.uk/efo/efo.owl");
        ioManager.loadOntologyFromFileLocation("/Users/malone/EFO/EFOInternalEBI/ExperimentalFactorOntology/ExFactorInOWL/releasecandidate/efo_ordo_module.owl");
        ioManager.loadOntologyFromFileLocation("/Users/malone/EFO/EFOSourceForge/trunk/src/efoinowl/efo.owl");

        //load test ontologies
        OWLOntologyManager m = ioManager.getManager();

        Set<OWLOntology> loadedOntologies = m.getOntologies();
        System.out.println("Loaded ontologies " + loadedOntologies.toString());

        for (OWLOntology o : loadedOntologies){
            System.out.println(o.getOntologyID().getOntologyIRI());
        }


        //mint test class
        OWLDataFactory factory = m.getOWLDataFactory();
        IRI iri = IRI.create("http://www.ebi.ac.uk/efo/EFO_0000400");
        OWLClass exampleClass = factory.getOWLClass(iri);
        manager.getNamedClassParent(m, activeOntology, exampleClass);


    }



}
