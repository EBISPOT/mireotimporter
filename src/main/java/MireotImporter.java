/**
 * Created by malone on 27/03/2015.
 */


import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.util.Set;

/**
 * Driver class to run the import code
 */
public class MireotImporter {


    public static void main(String[] args) {

        System.setProperty("entityExpansionLimit", "100000000");

        System.out.println("hello welt");
        MireotManager manager = new MireotManager();
        OntologyIO ioManager = new OntologyIO();

        ioManager.loadOntologyFromFileLocation("/Users/malone/EFO/EFOInternalEBI/ExperimentalFactorOntology/ExFactorInOWL/releasecandidate/efo_ordo_module.owl");
        ioManager.loadOntologyFromFileLocation("/Users/malone/EFO/EFOSourceForge/trunk/src/efoinowl/efo.owl");

        OWLOntologyManager m = ioManager.getManager();

        Set<OWLOntology> loadedOntologies = m.getOntologies();
        System.out.println("Loaded ontologies " + loadedOntologies.toString());

    }



}
