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
        Set<IRI> classes = new HashSet<IRI>();
        classes.add(IRI.create("http://purl.obolibrary.org/obo/CL_0000109"));
        classes.add(IRI.create("http://purl.obolibrary.org/obo/CL_0000019"));
        classes.add(IRI.create("http://purl.obolibrary.org/obo/CL_0011006"));
        classes.add(IRI.create("http://purl.obolibrary.org/obo/GO_0030017"));
        classes.add(IRI.create("http://www.ebi.ac.uk/efo/EFO_0000400"));
        classes.add(IRI.create("http://www.ebi.ac.uk/efo/EFO_0001185"));

        Set<IRI> sourceOntologies = new HashSet<IRI>();
        //sourceOntologies.add(IRI.create("http://www.ebi.ac.uk/efo"));
        sourceOntologies.add(IRI.create("http://purl.obolibrary.org/obo/cl.owl"));

        Set<String> ontoLocations = new HashSet<String>();
        //ontoLocations.add("/Users/malone/EFO/EFOSourceForge/trunk/src/efoinowl/efo.owl");
        ontoLocations.add("/Users/malone/efo_modified.owl");
        ontoLocations.add("/Users/malone/cl.owl");
        //ontoLocations.add("/Users/malone/cmpo.owl");

        IRI targetOntology = IRI.create("http://www.ebi.ac.uk/efo");
        //IRI targetOntology = IRI.create("http://purl.obolibrary.org/obo/cl.owl");
        //IRI targetOntology = IRI.create("http://www.ebi.ac.uk/cmpo/cmpo.owl");



        //block of code to load ontologies before calling methods
        //load all ontologies
        OntologyIO ioManager = moduleExtractor.loadAllOntologies(ontoLocations, sourceOntologies, false, targetOntology);
        //get handle to ontologies and manager they are loaded in to
        OWLOntologyManager manager = ioManager.getManager();

        //ontologies loaded already
        //OWLOntology ontologyModule = moduleExtractor.getMireotBasic(classes, manager, sourceOntologies);
        //OWLOntology ontologyModule = moduleExtractor.getMireotFull(classes, manager, sourceOntologies);
        //OWLOntology ontologyModule = moduleExtractor.getMireotMerge(classes, manager, sourceOntologies, targetOntology);
        //OWLOntology ontologyModule = moduleExtractor.getPartialClosure(classes, manager, sourceOntologies);
        //OWLOntology ontologyModule = moduleExtractor.getFullClosure(classes, manager, sourceOntologies);

        //load through file location
        //OWLOntology ontologyModule = moduleExtractor.getMireotBasic(classes, ontoLocations, sourceOntologies, false);
        //OWLOntology ontologyModule = moduleExtractor.getMireotFull(classes, ontoLocations, sourceOntologies, false);
        //OWLOntology ontologyModule = moduleExtractor.getMireotMerge(classes, ontoLocations, sourceOntologies, targetOntology, false);
        //OWLOntology ontologyModule = moduleExtractor.getPartialClosure(classes, ontoLocations, sourceOntologies, false);
        OWLOntology ontologyModule = moduleExtractor.getFullClosure(classes, ontoLocations, sourceOntologies, false);
        //OWLOntology ontologyModule = moduleExtractor.getFullClosureImportsAsSource(ontoLocations, targetOntology);


        OntologyIO io = new OntologyIO();
        io.saveOntologyToFileLocation(ontologyModule, "file:/Users/malone/cmpo_basic.owl");


        //Set<IRI> classesInSig = moduleExtractor.getOntologySignature("/Users/malone/cmpo.owl", IRI.create("http://www.ebi.ac.uk/cmpo/cmpo.owl"));
        //System.out.println("No of classes " + classesInSig.size());

    }

}
