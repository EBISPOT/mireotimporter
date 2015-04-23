/**
 * Created by malone on 27/03/2015.
 */


import org.semanticweb.owlapi.model.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Driver class to run the import code
 */
public class MireotImporter {


    public static void main(String[] args) {

        System.setProperty("entityExpansionLimit", "100000000");

        ModuleProfileExtractor moduleExtractor = new ModuleProfileExtractor();

        //some test data
        Set<String> classes = new HashSet<String>();
        classes.add("http://www.ebi.ac.uk/efo/EFO_0000400");
        String activeOntology = "http://www.ebi.ac.uk/efo/efo.owl";
        Set<String> ontoLocations = new HashSet<String>();
        ontoLocations.add("/Users/malone/EFO/EFOSourceForge/trunk/src/efoinowl/efo.owl");
        //ontoLocations.add("/Users/malone/EFO/EFOInternalEBI/ExperimentalFactorOntology/ExFactorInOWL/releasecandidate/efo_ordo_module.owl");

        OWLOntology ontologyModule = moduleExtractor.getMireotBasic(classes, activeOntology, ontoLocations);

        OntologyIO io = new OntologyIO();
        io.saveOntologyToFileLocation(ontologyModule, "file:/Users/malone/efo_mireot.owl");


    }



}
