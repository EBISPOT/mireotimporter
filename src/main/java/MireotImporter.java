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
        classes.add("http://purl.obolibrary.org/obo/CL_0007004");
        classes.add("http://www.ebi.ac.uk/efo/EFO_0000400");
        classes.add("http://www.ebi.ac.uk/efo/EFO_00004100");

        String sourceOntology = "http://purl.obolibrary.org/obo/cl.owl";
        String targetOntology = "http://www.ebi.ac.uk/efo/efo.owl";
        Set<String> ontoLocations = new HashSet<String>();
        ontoLocations.add("/Users/malone/EFO/EFOSourceForge/trunk/src/efoinowl/efo.owl");
        ontoLocations.add("/Users/malone/cl.owl");

        //OWLOntology ontologyModule = moduleExtractor.getMireotFull(classes,sourceOntology, ontoLocations);
        OWLOntology ontologyModule = moduleExtractor.getMireotAware(classes, sourceOntology, targetOntology, ontoLocations);


        OntologyIO io = new OntologyIO();
        io.saveOntologyToFileLocation(ontologyModule, "file:/Users/malone/efo_mireot.owl");


    }



}
