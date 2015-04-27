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
        //classes.add("http://purl.obolibrary.org/obo/CL_0000019");
        classes.add("http://www.ebi.ac.uk/efo/EFO_0000400");
        //classes.add("http://www.ebi.ac.uk/efo/EFO_0001185");
        //classes.add("http://www.ebi.ac.uk/efo/EFO_00004100");   //does not exist test

        //String sourceOntology = "http://purl.obolibrary.org/obo/cl.owl";
        String sourceOntology = "http://www.ebi.ac.uk/efo/efo.owl";
        Set<String> ontoLocations = new HashSet<String>();
        ontoLocations.add("/Users/malone/EFO/EFOSourceForge/trunk/src/efoinowl/efo.owl");
        ontoLocations.add("/Users/malone/cl.owl");

        //OWLOntology ontologyModule = moduleExtractor.getMireotFull(classes,sourceOntology, ontoLocations);
        //OWLOntology ontologyModule = moduleExtractor.getMireotMerge(classes, sourceOntology, targetOntology, ontoLocations);
        //OWLOntology ontologyModule = moduleExtractor.getPartialClosure(classes, sourceOntology, ontoLocations);
        OWLOntology ontologyModule = moduleExtractor.getFullClosure(classes, sourceOntology, ontoLocations);


        //OntologyIO io = new OntologyIO();
        //io.saveOntologyToFileLocation(ontologyModule, "file:/Users/malone/efo_mireot.owl");


    }



}
